package com.example.testingLogIn.WebsiteSecurityConfiguration;

import com.example.testingLogIn.Enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    final CustomUserDetailsService userService;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    @Autowired
    public SecurityConfig(CustomUserDetailsService userService,
                          CustomAuthenticationFailureHandler customAuthenticationFailureHandler) {
        this.customAuthenticationFailureHandler = customAuthenticationFailureHandler;
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return  http.csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(req -> req
                        .requestMatchers("/css/**", "/website-logo", "/website-cover",
                                            "/images/**","/js/*","/signing","/register/add").permitAll()
                        .requestMatchers("/maintenance/**","/settings/**","/accounts/**").hasAnyAuthority("ADMIN")
                            .requestMatchers("/reports/*","/enrollment/**","/transaction/**","/accounts/my-account").hasAnyAuthority("ADMIN","ENROLLMENT_STAFF")
                            .requestMatchers("/schedule/**","/grade-management/**","/class-list/**","/accounts/my-account").hasAnyAuthority("ADMIN","TEACHER")
                            .requestMatchers("/class-schedule","/personal-profile","/grades").hasAnyAuthority("STUDENT")
                        .anyRequest().authenticated()
                    )
                    .formLogin(form -> form.loginPage("/login").permitAll()
                            .defaultSuccessUrl("/home", true)
                            .failureHandler(customAuthenticationFailureHandler))  // Custom login page
                    //.httpBasic(Customizer.withDefaults())  // Enable basic authentication (for Postman)
                    .logout(c -> c.invalidateHttpSession(true))
                    .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(new BCryptPasswordEncoder(11));
        provider.setUserDetailsService(userService);
        return provider;
    }
}
