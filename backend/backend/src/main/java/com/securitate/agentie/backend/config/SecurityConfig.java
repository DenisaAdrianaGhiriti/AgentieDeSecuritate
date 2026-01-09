package com.securitate.agentie.backend.config;

import com.securitate.agentie.backend.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            CorsConfigurationSource corsConfigurationSource,
            AuthenticationProvider authenticationProvider,
            JwtAuthenticationFilter jwtAuthFilter
    ) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // ✅ Preflight (CORS)
                        .requestMatchers(antMatcher(HttpMethod.OPTIONS, "/**")).permitAll()

                        // ✅ Auth public
                        .requestMatchers(antMatcher("/api/auth/**")).permitAll()

                        // ✅ PDF / fișiere statice (link <a href> fără JWT)
                        .requestMatchers(antMatcher(HttpMethod.GET, "/uploads/**")).permitAll()

                        // =========================================================
                        // ✅ USERS
                        // =========================================================

                        // Beneficiar: profilul lui
                        .requestMatchers(antMatcher(HttpMethod.GET, "/api/users/profile"))
                        .hasAnyAuthority("ROLE_BENEFICIAR", "BENEFICIAR")
                        .requestMatchers(antMatcher(HttpMethod.GET, "/api/users/profile/**"))
                        .hasAnyAuthority("ROLE_BENEFICIAR", "BENEFICIAR")

                        // Beneficiar: angajați alocați
                        .requestMatchers(antMatcher(HttpMethod.GET, "/api/users/beneficiar/angajati"))
                        .hasAnyAuthority("ROLE_BENEFICIAR", "BENEFICIAR")

                        // Create / list admin stuff
                        .requestMatchers(antMatcher(HttpMethod.POST, "/api/users/create"))
                        .hasAnyAuthority("ROLE_ADMIN", "ADMIN", "ROLE_ADMINISTRATOR", "ADMINISTRATOR")
                        .requestMatchers(antMatcher(HttpMethod.GET, "/api/users/list/**"))
                        .hasAnyAuthority("ROLE_ADMIN", "ADMIN", "ROLE_ADMINISTRATOR", "ADMINISTRATOR")
                        .requestMatchers(antMatcher(HttpMethod.POST, "/api/users/create-admin"))
                        .hasAnyAuthority("ROLE_ADMINISTRATOR", "ADMINISTRATOR")

                        // Update / delete
                        .requestMatchers(antMatcher(HttpMethod.PUT, "/api/users/**"))
                        .hasAnyAuthority("ROLE_ADMIN", "ADMIN", "ROLE_ADMINISTRATOR", "ADMINISTRATOR")
                        .requestMatchers(antMatcher(HttpMethod.DELETE, "/api/users/**"))
                        .hasAnyAuthority("ROLE_ADMIN", "ADMIN", "ROLE_ADMINISTRATOR", "ADMINISTRATOR")

                        // Liste simple
                        .requestMatchers(antMatcher(HttpMethod.GET, "/api/users/paznici"))
                        .hasAnyAuthority("ROLE_ADMIN", "ADMIN", "ROLE_ADMINISTRATOR", "ADMINISTRATOR", "ROLE_PAZNIC", "PAZNIC")
                        .requestMatchers(antMatcher(HttpMethod.GET, "/api/users/beneficiari"))
                        .hasAnyAuthority("ROLE_ADMIN", "ADMIN", "ROLE_ADMINISTRATOR", "ADMINISTRATOR", "ROLE_PAZNIC", "PAZNIC")

                        // ✅ User by id - DOAR numeric (evită conflictul cu /profile)
                        .requestMatchers(new RegexRequestMatcher("^/api/users/\\d+$", "GET"))
                        .hasAnyAuthority("ROLE_ADMIN", "ADMIN", "ROLE_ADMINISTRATOR", "ADMINISTRATOR", "ROLE_BENEFICIAR", "BENEFICIAR")

                        // =========================================================
                        // ✅ POSTS
                        // =========================================================
                        .requestMatchers(antMatcher(HttpMethod.GET, "/api/posts/my-assigned-workpoints"))
                        .hasAnyAuthority("ROLE_PAZNIC", "PAZNIC", "ROLE_ADMINISTRATOR", "ADMINISTRATOR")
                        .requestMatchers(antMatcher("/api/posts/**"))
                        .hasAnyAuthority("ROLE_ADMIN", "ADMIN", "ROLE_ADMINISTRATOR", "ADMINISTRATOR")

                        // =========================================================
                        // ✅ PONTAJ (SPECIFICE înainte de /**)
                        // =========================================================
                        .requestMatchers(antMatcher(HttpMethod.POST, "/api/pontaj/update-location"))
                        .hasAnyAuthority("ROLE_PAZNIC", "PAZNIC")

                        .requestMatchers(antMatcher(HttpMethod.GET, "/api/pontaj/locatie/**"))
                        .hasAnyAuthority("ROLE_ADMIN", "ADMIN", "ROLE_ADMINISTRATOR", "ADMINISTRATOR", "ROLE_BENEFICIAR", "BENEFICIAR")

                        .requestMatchers(antMatcher(HttpMethod.GET, "/api/pontaj/angajati-activi"))
                        .hasAnyAuthority("ROLE_ADMIN", "ADMIN", "ROLE_ADMINISTRATOR", "ADMINISTRATOR")

                        .requestMatchers(antMatcher(HttpMethod.GET, "/api/pontaj/istoric-60zile"))
                        .hasAnyAuthority("ROLE_ADMIN", "ADMIN", "ROLE_ADMINISTRATOR", "ADMINISTRATOR")

                        .requestMatchers(antMatcher(HttpMethod.GET, "/api/pontaj/angajati-activi-beneficiar"))
                        .hasAnyAuthority("ROLE_BENEFICIAR", "BENEFICIAR")

                        .requestMatchers(antMatcher(HttpMethod.GET, "/api/pontaj/istoric-60zile-beneficiar"))
                        .hasAnyAuthority("ROLE_BENEFICIAR", "BENEFICIAR")

                        .requestMatchers(antMatcher(HttpMethod.GET, "/api/pontaj/active"))
                        .hasAnyAuthority("ROLE_PAZNIC", "PAZNIC", "ROLE_ADMINISTRATOR", "ADMINISTRATOR")

                        .requestMatchers(antMatcher(HttpMethod.POST, "/api/pontaj/check-in"))
                        .hasAnyAuthority("ROLE_PAZNIC", "PAZNIC", "ROLE_ADMINISTRATOR", "ADMINISTRATOR")
                        .requestMatchers(antMatcher(HttpMethod.POST, "/api/pontaj/check-out"))
                        .hasAnyAuthority("ROLE_PAZNIC", "PAZNIC", "ROLE_ADMINISTRATOR", "ADMINISTRATOR")

                        // fallback pontaj
                        .requestMatchers(antMatcher("/api/pontaj/**"))
                        .hasAnyAuthority(
                                "ROLE_PAZNIC", "PAZNIC",
                                "ROLE_ADMIN", "ADMIN",
                                "ROLE_ADMINISTRATOR", "ADMINISTRATOR",
                                "ROLE_BENEFICIAR", "BENEFICIAR"
                        )

                        // =========================================================
                        // ✅ PROCESE / RAPOARTE
                        // =========================================================
                        .requestMatchers(antMatcher(HttpMethod.GET, "/api/proces-verbal/documente"))
                        .hasAnyAuthority("ROLE_ADMIN", "ADMIN", "ROLE_ADMINISTRATOR", "ADMINISTRATOR")
                        .requestMatchers(antMatcher("/api/proces-verbal/**"))
                        .hasAnyAuthority("ROLE_PAZNIC", "PAZNIC", "ROLE_ADMINISTRATOR", "ADMINISTRATOR")

                        .requestMatchers(antMatcher(HttpMethod.POST, "/api/proces-verbal-predare/create"))
                        .hasAnyAuthority("ROLE_PAZNIC", "PAZNIC", "ROLE_ADMINISTRATOR", "ADMINISTRATOR")
                        .requestMatchers(antMatcher(HttpMethod.GET, "/api/proces-verbal-predare/documente"))
                        .hasAnyAuthority("ROLE_ADMIN", "ADMIN", "ROLE_ADMINISTRATOR", "ADMINISTRATOR")

                        .requestMatchers(antMatcher(HttpMethod.POST, "/api/raport-eveniment/create"))
                        .hasAnyAuthority("ROLE_PAZNIC", "PAZNIC", "ROLE_ADMINISTRATOR", "ADMINISTRATOR")
                        .requestMatchers(antMatcher(HttpMethod.GET, "/api/raport-eveniment/documente"))
                        .hasAnyAuthority("ROLE_ADMIN", "ADMIN", "ROLE_ADMINISTRATOR", "ADMINISTRATOR")

                        // =========================================================
                        // ✅ SESIZARI / INCIDENTE / CLEANUP
                        // =========================================================
                        .requestMatchers(antMatcher(HttpMethod.POST, "/api/sesizari"))
                        .hasAnyAuthority("ROLE_BENEFICIAR", "BENEFICIAR")
                        .requestMatchers(antMatcher(HttpMethod.GET, "/api/sesizari/beneficiar"))
                        .hasAnyAuthority("ROLE_BENEFICIAR", "BENEFICIAR")
                        .requestMatchers(antMatcher("/api/sesizari/**"))
                        .hasAnyAuthority("ROLE_ADMIN", "ADMIN", "ROLE_ADMINISTRATOR", "ADMINISTRATOR")

                        .requestMatchers(antMatcher(HttpMethod.GET, "/api/incidente/beneficiar"))
                        .hasAnyAuthority("ROLE_BENEFICIAR", "BENEFICIAR")

                        .requestMatchers(antMatcher(HttpMethod.GET, "/api/incidente"))
                        .hasAnyAuthority("ROLE_ADMIN", "ADMIN", "ROLE_ADMINISTRATOR", "ADMINISTRATOR", "ROLE_PAZNIC", "PAZNIC")
                        .requestMatchers(antMatcher(HttpMethod.POST, "/api/incidente"))
                        .hasAnyAuthority("ROLE_ADMIN", "ADMIN", "ROLE_ADMINISTRATOR", "ADMINISTRATOR", "ROLE_PAZNIC", "PAZNIC")

                        .requestMatchers(antMatcher("/api/incidente/**"))
                        .hasAnyAuthority("ROLE_ADMIN", "ADMIN", "ROLE_ADMINISTRATOR", "ADMINISTRATOR", "ROLE_PAZNIC", "PAZNIC")

                        .requestMatchers(antMatcher("/api/cleanup/**"))
                        .hasAnyAuthority("ROLE_ADMIN", "ADMIN", "ROLE_ADMINISTRATOR", "ADMINISTRATOR")

                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Origin", "Accept"));

        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
