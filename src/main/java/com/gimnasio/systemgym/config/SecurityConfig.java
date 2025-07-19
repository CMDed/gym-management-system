package com.gimnasio.systemgym.config;

import com.gimnasio.systemgym.service.CombinedUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CombinedUserDetailsService combinedUserDetailsService;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    public SecurityConfig(CombinedUserDetailsService combinedUserDetailsService,
                          CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler) {
        this.combinedUserDetailsService = combinedUserDetailsService;
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(combinedUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.sameOrigin())
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/css/**", "/js/**", "/images/**",
                                "/",
                                "/membresias",
                                "/registro-miembro",
                                "/login",
                                "/error",
                                "/h2-console/**",
                                "/api/membresias/activas"
                        ).permitAll()
                        .requestMatchers("/api/miembros/registrar").permitAll()
                        .requestMatchers("/api/miembros/**").hasAnyRole("ADMIN", "RECEPCIONISTA", "MIEMBRO")
                        .requestMatchers("/api/membresias/**").hasAnyRole("ADMIN", "RECEPCIONISTA", "MIEMBRO")
                        .requestMatchers("/api/inscripciones/**").hasAnyRole("ADMIN", "RECEPCIONISTA", "MIEMBRO")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/dashboard").authenticated()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/perform_login")
                        .successHandler(customAuthenticationSuccessHandler)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/login?logout=true")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                        .permitAll()
                );

        return http.build();
    }
}