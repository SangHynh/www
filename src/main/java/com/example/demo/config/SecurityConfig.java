package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class SecurityConfig {

    private static final String ADMIN = "ADMIN";
    private static final String USER = "USER";

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Directly define AuthenticationManager bean here
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .inMemoryAuthentication()
                .withUser("sang")
                .password(passwordEncoder().encode("123456"))
                .roles(USER)
                .and()
                .withUser("admin")
                .password(passwordEncoder().encode("admin123"))
                .roles(ADMIN);
        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/public/**")  // Tắt CSRF cho các trang công khai
                        .disable()
                )
                .authorizeRequests(auth -> auth
                        .requestMatchers("/public/**").permitAll()  // Các trang công khai
                        .requestMatchers("/admin/**").hasAuthority(ADMIN)  // Các trang dành cho ADMIN
                        .anyRequest().authenticated()  // Các trang còn lại yêu cầu xác thực
                )
                .formLogin(form -> form
                        .permitAll()
                        .loginProcessingUrl("/login")
                        .failureUrl("/login?error=true")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .successHandler(authenticationSuccessHandler())  // Sử dụng custom success handler
                )
                .exceptionHandling(exceptions -> exceptions
                        .accessDeniedPage("/public/access-denied")  // Trang khi người dùng bị từ chối truy cập
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")  // Sau khi logout, chuyển hướng về trang chủ
                );
        return http.build();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        return new InMemoryUserDetailsManager(
                User.builder()
                        .username("sang")
                        .password(passwordEncoder().encode("123456"))
                        .roles(USER)
                        .build(),
                User.builder()
                        .username("admin")
                        .password(passwordEncoder().encode("admin123"))
                        .roles(ADMIN)
                        .build()
        );
    }

    // Thay vì dùng @Bean, tạo trực tiếp trong phương thức này để tránh vòng lặp
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            response.sendRedirect("/");
        };
    }
}
