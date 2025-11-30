package com.securitate.agentie.backend.config;

// IMPORURI NOI (asigură-te că le ai)
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider; // <-- ASIGURĂ-TE CĂ AI IMPORTUL
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import java.util.List;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // <-- IMPORT NOU

// IMPORURI EXISTENTE
import com.securitate.agentie.backend.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   CorsConfigurationSource corsConfigurationSource,
                                                   AuthenticationProvider authenticationProvider,
                                                   JwtAuthenticationFilter jwtAuthFilter) throws Exception { // <-- INJECTĂM FILTRUL
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.disable())
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // --- AICI ÎNCEP REGULILE DE AUTORIZARE (Echivalentul `authorize`) ---
                .authorizeHttpRequests(auth -> auth
                        // Permite explicit TOATE cererile OPTIONS (pentru CORS preflight)
                        .requestMatchers(antMatcher(HttpMethod.OPTIONS, "/**")).permitAll()

                        // Permite explicit TOATE cererile pe /api/auth/
                        .requestMatchers(antMatcher("/api/auth/**")).permitAll()

                        // --- RUTELE NOI (Traduse din Node.js) ---

                        // /api/users
                        .requestMatchers(antMatcher(HttpMethod.POST, "/api/users/create")).hasAnyRole("ADMIN", "ADMINISTRATOR")
                        .requestMatchers(antMatcher(HttpMethod.GET, "/api/users/list/**")).hasAnyRole("ADMIN", "ADMINISTRATOR")
                        .requestMatchers(antMatcher(HttpMethod.POST, "/api/users/create-admin")).hasRole("ADMINISTRATOR")

                        // /api/posts
                        .requestMatchers(antMatcher("/api/posts/**")).hasAnyRole("ADMIN", "ADMINISTRATOR")

                        // /api/pontaj
                        .requestMatchers(antMatcher("/api/pontaj/**")).hasAnyRole("PAZNIC", "ADMINISTRATOR")

                        // /api/proces-verbal
                        .requestMatchers(antMatcher("/api/proces-verbal/**")).hasAnyRole("PAZNIC", "ADMINISTRATOR")

                        // Toate celelalte necesită autentificare
                        .anyRequest().authenticated()
                )
                // --- SFÂRȘIT REGULI ---

                .authenticationProvider(authenticationProvider)
                // --- MODIFICAREA 3: Adaugă filtrul JWT înainte de filtrul de login standard ---
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Bean pentru a încărca user-ul din baza de date (Rămâne neschimbat)
    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
    }

    // Bean pentru hash-uirea și verificarea parolelor (Rămâne neschimbat)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Bean care gestionează procesul de autentificare (Rămâne neschimbat)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // Provider-ul care leagă UserDetailsService și PasswordEncoder (Rămâne neschimbat)
    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    // BEAN-UL PENTRU CORS (Rămâne neschimbat)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}