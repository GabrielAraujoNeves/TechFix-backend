package AuthAssistencia.AuthAssitencia.Security;

import AuthAssistencia.AuthAssitencia.Service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*")); // Permitir qualquer origem
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(false);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/api/products/all").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/products/{id}").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/products/marca/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/products/modelo/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/products/search").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/products/low-stock").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/products/out-of-stock").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/admin/users/**").hasRole("ADMIN")

                        // Endpoints de Ordem de Serviço (USER e ADMIN podem criar e visualizar)
                        .requestMatchers("/api/os/create").hasAnyRole("USER", "ADMIN")  // Criar OS
                        .requestMatchers("/api/os/all").hasAnyRole("USER", "ADMIN")     // Listar OS
                        .requestMatchers("/api/os/{id}").hasAnyRole("USER", "ADMIN")    // Buscar OS
                        .requestMatchers("/api/os/numero/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/os/status/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/os/cliente/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/os/update-status/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/os/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/products/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}