package neo.com.br.CitMobi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private static final String[] SWAGGER_WHITELIST = {
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "swagger-resources"
    };
    private static final String[] WHITELIST = {

    };



    // Antigo "WHITELIST"
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF
//                .authorizeHttpRequests(auth -> auth
//                        .anyRequest().permitAll() // Allow all requests
//                )
//                .httpBasic(AbstractHttpConfigurer::disable) // Disable HTTP Basic Auth
//                .formLogin(AbstractHttpConfigurer::disable); // Disable Form-based login
//
//        return http.build();
//    }
}

