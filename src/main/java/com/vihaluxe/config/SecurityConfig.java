package com.vihaluxe.config;

import com.vihaluxe.security.CustomLoginSuccessHandler;
import com.vihaluxe.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final CustomLoginSuccessHandler successHandler;

    public SecurityConfig(
            CustomUserDetailsService customUserDetailsService,
            CustomLoginSuccessHandler successHandler) {

        this.customUserDetailsService = customUserDetailsService;
        this.successHandler = successHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http

                .csrf(csrf -> csrf.disable())

                .userDetailsService(customUserDetailsService)

                .authorizeHttpRequests(auth -> auth

                        // Public Pages
                        .requestMatchers(
                                "/",
                                "/products",
                                "/product/**",
                                "/about",
                                "/contact",
                                "/register",
                                "/login",
                                "/forgot-password",
                                "/reset-password",
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        ).permitAll()

                        .requestMatchers(
                                org.springframework.http.HttpMethod.GET,
                                "/ai"
                        ).permitAll()

                        .requestMatchers(
                                org.springframework.http.HttpMethod.POST,
                                "/ai"
                        ).permitAll()

                        // Admin Pages
                        .requestMatchers("/admin/**")
                        .hasRole("ADMIN")

                        // User Pages
                        .requestMatchers(
                                "/cart/**",
                                "/checkout",
                                "/place-order",
                                "/orders",
                                "/order-success",
                                "/profile"
                        )
                        .hasAnyRole("USER", "ADMIN")

                        // Any other request
                        .anyRequest().authenticated()
                )

                .formLogin(login -> login

                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler(successHandler)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )

                .logout(logout -> logout

                        .logoutSuccessUrl("/")
                        .permitAll()
                );

        return http.build();
    }
}