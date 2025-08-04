package com.bestflix.movie.security.config;

import com.bestflix.movie.security.userService.filter.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


/**
 * Configures Spring Security for the application.
 * Defines custom JWT-based authentication, disables form login,
 * and enables stateless session management.
 */
@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    /**
     * Custom JWT filter used for authentication and token validation.
     */
    private final JwtFilter jwtFilter;


    /**
     * Configures the HTTP security filter chain.
     * - Enables CORS and disables CSRF
     * - Whitelists publicly accessible endpoints
     * - Disables default form login
     * - Adds {@link JwtFilter} before the {@link UsernamePasswordAuthenticationFilter}
     * - Enforces stateless session management
     *
     * @param http the {@link HttpSecurity} builder
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception in case of configuration errors
     */

    @Bean
     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

         return http
                 .cors(Customizer.withDefaults())
                 .csrf(customizer -> customizer.disable())

                 .authorizeHttpRequests(request->request

                         .requestMatchers(HttpMethod.OPTIONS, "/**")
                         .permitAll()

                         .requestMatchers("/forgot-password", "/reset-password", "/login", "/h2-console/**",
                                 "/register","/rest/api/movie/all","/rest/api/movie/image/**","/rest/api/movie/**",
                                 "/swagger-ui/**",
                                 "/swagger-ui.html",
                                 "/api/v1/auth/**",
                                 "/v2/api-docs",
                                 "/v3/api-docs/**",
                                 "/v3/api-docs",
                                 "/swagger-resources",
                                 "/swagger-resources/**",
                                 "/configuration/ui",
                                 "/configuration/security",
                                 "/webjars/**")
                         .permitAll()
                         .anyRequest().authenticated())
                 .formLogin(form -> form.disable())
                 .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                 .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                 .build();

     }


    /**
     * Provides a BCrypt password encoder with strength 12.
     *
     * @return password encoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * Retrieves the {@link AuthenticationManager} from the configuration context.
     *
     * @param config the authentication configuration bean
     * @return configured {@link AuthenticationManager}
     * @throws Exception if retrieval fails
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception   {
         return config.getAuthenticationManager();
     }


    //    @Bean
//    public UserDetailsService userDetailsService() {
//        return username -> {
//            Users user = userRepository.findByUsername(username);
//            if(user==null){
//                throw new UsernameNotFoundException("User Not Found");
//            }
//
//            return new CustomUserDetails(user);
//        };
//    }


}
