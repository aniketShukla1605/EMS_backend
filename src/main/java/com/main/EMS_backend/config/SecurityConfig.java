package com.main.EMS_backend.config;

import com.main.EMS_backend.service.CustomUserDetails;
import com.main.EMS_backend.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetails customUserDetails;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    public SecurityConfig(CustomUserDetails customUserDetails,JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.customUserDetails = customUserDetails;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(customUserDetails);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> {})
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers("/otp/**").permitAll()
                        .requestMatchers(HttpMethod.PUT,"/api/profile/reset-password").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/registrations/organiser-registrations").hasAnyRole("ADMIN","ORGANISER")
                        .requestMatchers(HttpMethod.GET,"/api/registrations/pending-count").hasAnyRole("ADMIN","ORGANISER")
                        .requestMatchers(HttpMethod.PUT,"/api/registrations/approve/**").hasAnyRole("ADMIN","ORGANISER")
                        .requestMatchers(HttpMethod.PUT,"/api/registrations/reject/**").hasAnyRole("ADMIN","ORGANISER")
                        .requestMatchers(HttpMethod.GET,"/api/registrations/event/**").hasAnyRole("ADMIN","ORGANISER")
                        .requestMatchers(HttpMethod.POST,"/api/registrations/**").hasRole("USER")
                        .requestMatchers(HttpMethod.GET,"/api/registrations/**").hasRole("USER")
                        .requestMatchers(HttpMethod.PUT,"/api/registrations/cancel/**").hasRole("USER")
                        .requestMatchers("/api/organiser-requests/request").hasRole("USER")
                        .requestMatchers("/api/organiser-requests/**").hasRole("ADMIN")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/events/admin/events/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,"/api/events/**").hasAnyRole("ADMIN","ORGANISER")
                        .requestMatchers("/api/announcements/global/**").hasAnyRole("ADMIN","ORGANISER")
                        .requestMatchers("/api/announcements/event/**").hasAnyRole("ADMIN","ORGANISER")
                        .requestMatchers(HttpMethod.GET,"/api/events/**").authenticated()
                        .requestMatchers("/api/profile/**").authenticated()
                        .requestMatchers("/api/announcements/user").authenticated()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
