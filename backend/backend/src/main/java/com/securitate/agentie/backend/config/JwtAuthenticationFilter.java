package com.securitate.agentie.backend.config;

import com.securitate.agentie.backend.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // Dacă nu există header-ul sau nu începe cu "Bearer ", trecem la următorul filtru
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extragem token-ul (fără "Bearer ")
        jwt = authHeader.substring(7);

        try {
            userEmail = jwtService.extractUsername(jwt);

            // Verificăm dacă email-ul e extras și dacă user-ul NU este deja autentificat
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Încărcăm user-ul din baza de date
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                logger.info("JWT user=" + userEmail + " authorities=" + userDetails.getAuthorities()
                        + " enabled=" + userDetails.isEnabled());

                // Validăm token-ul
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    // Creăm un obiect de autentificare
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null, // Nu avem nevoie de parolă aici
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    // Setăm user-ul ca fiind autentificat în contextul Spring Security
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Dacă token-ul e invalid (expirat, malformat etc.), lăsăm contextul gol
            // Spring Security va arunca 401 sau 403 mai târziu
            logger.warn("Nu s-a putut procesa token-ul JWT: " + e.getMessage());
        }

        // Trecem la următorul filtru din lanț
        filterChain.doFilter(request, response);
    }
}