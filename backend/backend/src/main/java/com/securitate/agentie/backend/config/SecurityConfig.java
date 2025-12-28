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
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   CorsConfigurationSource corsConfigurationSource,
                                                   AuthenticationProvider authenticationProvider,
                                                   JwtAuthenticationFilter jwtAuthFilter) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.disable())
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // Permite explicit TOATE cererile OPTIONS (preflight)
                        .requestMatchers(antMatcher(HttpMethod.OPTIONS, "/**")).permitAll()

                        // Auth public
                        .requestMatchers(antMatcher("/api/auth/**")).permitAll()

                        // /api/users
                        .requestMatchers(antMatcher(HttpMethod.POST, "/api/users/create")).hasAnyRole("ADMIN", "ADMINISTRATOR")
                        .requestMatchers(antMatcher(HttpMethod.GET, "/api/users/list/**")).hasAnyRole("ADMIN", "ADMINISTRATOR")
                        .requestMatchers(antMatcher(HttpMethod.POST, "/api/users/create-admin")).hasRole("ADMINISTRATOR")

                        // /api/posts
                        .requestMatchers(antMatcher("/api/posts/**")).hasAnyRole("ADMIN", "ADMINISTRATOR")

                        // /api/pontaj
                        .requestMatchers(antMatcher(HttpMethod.POST, "/api/pontaj/check-in")).hasAnyRole("PAZNIC", "ADMINISTRATOR")
                        .requestMatchers(antMatcher(HttpMethod.POST, "/api/pontaj/check-out")).hasAnyRole("PAZNIC", "ADMINISTRATOR")
                        .requestMatchers(antMatcher("/api/pontaj/**")).hasAnyRole("PAZNIC", "ADMINISTRATOR")

                        // /api/proces-verbal
                        .requestMatchers(antMatcher("/api/proces-verbal/**")).hasAnyRole("PAZNIC", "ADMINISTRATOR")

                        // /api/users (update/delete)
                        .requestMatchers(antMatcher(HttpMethod.PUT, "/api/users/**")).hasAnyRole("ADMIN", "ADMINISTRATOR")
                        .requestMatchers(antMatcher(HttpMethod.DELETE, "/api/users/**")).hasAnyRole("ADMIN", "ADMINISTRATOR")
                        .requestMatchers(antMatcher(HttpMethod.GET, "/api/users/paznici")).hasAnyRole("ADMIN", "ADMINISTRATOR", "PAZNIC")
                        .requestMatchers(antMatcher(HttpMethod.GET, "/api/users/beneficiari")).hasAnyRole("ADMIN", "ADMINISTRATOR", "PAZNIC")
                        .requestMatchers(antMatcher(HttpMethod.GET, "/api/users/beneficiar/angajati")).hasRole("BENEFICIAR")
                        .requestMatchers(antMatcher(HttpMethod.GET, "/api/users/{id}")).hasAnyRole("ADMIN", "ADMINISTRATOR", "BENEFICIAR")

                        // /api/assignments
                        .requestMatchers(antMatcher("/api/assignments/**")).hasAnyRole("ADMIN", "ADMINISTRATOR")

                        // /api/pontaj (tracking/istoric)
                        .requestMatchers(antMatcher(HttpMethod.POST, "/api/pontaj/update-location")).hasRole("PAZNIC")
                        .requestMatchers(antMatcher(HttpMethod.GET, "/api/pontaj/locatie/**")).hasAnyRole("ADMIN", "ADMINISTRATOR", "BENEFICIAR")
                        .requestMatchers(antMatcher("/api/pontaj/angajati-activi")).hasAnyRole("ADMIN", "ADMINISTRATOR")
                        .requestMatchers(antMatcher("/api/pontaj/angajati-activi-beneficiar")).hasRole("BENEFICIAR")
                        .requestMatchers(antMatcher("/api/pontaj/istoric-60zile")).hasAnyRole("ADMIN", "ADMINISTRATOR")
                        .requestMatchers(antMatcher("/api/pontaj/istoric-60zile-beneficiar")).hasRole("BENEFICIAR")
                        .requestMatchers(antMatcher("/api/pontaj/active")).hasAnyRole("PAZNIC", "ADMINISTRATOR")

                        // /api/proces-verbal-predare
                        .requestMatchers(antMatcher(HttpMethod.POST, "/api/proces-verbal-predare/create")).hasAnyRole("PAZNIC", "ADMINISTRATOR")
                        .requestMatchers(antMatcher(HttpMethod.GET, "/api/proces-verbal-predare/documente")).hasAnyRole("ADMIN", "ADMINISTRATOR")

                        // /api/raport-eveniment
                        .requestMatchers(antMatcher(HttpMethod.POST, "/api/raport-eveniment/create")).hasAnyRole("PAZNIC", "ADMINISTRATOR")
                        .requestMatchers(antMatcher(HttpMethod.GET, "/api/raport-eveniment/documente")).hasAnyRole("ADMIN", "ADMINISTRATOR")

                        // /api/sesizari
                        .requestMatchers(antMatcher(HttpMethod.POST, "/api/sesizari")).hasRole("BENEFICIAR")
                        .requestMatchers(antMatcher(HttpMethod.GET, "/api/sesizari/beneficiar")).hasRole("BENEFICIAR")
                        .requestMatchers(antMatcher("/api/sesizari/**")).hasAnyRole("ADMIN", "ADMINISTRATOR")

                        // /api/incidente
                        .requestMatchers(antMatcher(HttpMethod.GET, "/api/incidente/beneficiar")).hasRole("BENEFICIAR")

                        // IMPORTANT: /api/incidente (fără slash) trebuie permis separat
                        .requestMatchers(antMatcher(HttpMethod.GET, "/api/incidente")).hasAnyRole("ADMIN", "ADMINISTRATOR", "PAZNIC")
                        .requestMatchers(antMatcher(HttpMethod.POST, "/api/incidente")).hasAnyRole("ADMIN", "ADMINISTRATOR", "PAZNIC")

                        // restul sub-rutelor (/api/incidente/..., ex: /istoric, /{id}/restabilire, etc.)
                        .requestMatchers(antMatcher("/api/incidente/**")).hasAnyRole("ADMIN", "ADMINISTRATOR", "PAZNIC")
                        // /api/cleanup
                        .requestMatchers(antMatcher("/api/cleanup/**")).hasAnyRole("ADMIN", "ADMINISTRATOR")

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
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    // ✅ CORS FIX: adăugat PATCH + headers pentru preflight
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:5173"));

        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"
        ));

        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization", "Content-Type", "X-Requested-With", "Origin", "Accept"
        ));

        // opțional: dacă vrei să citești header-ul Authorization din response
        configuration.setExposedHeaders(List.of("Authorization"));

        configuration.setAllowCredentials(true);

        // opțional: cache pentru preflight (1 oră)
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
