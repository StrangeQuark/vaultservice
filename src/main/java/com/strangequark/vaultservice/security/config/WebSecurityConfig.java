package com.strangequark.vaultservice.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf().disable()//Disable CSRF
                .authorizeHttpRequests().requestMatchers("**").permitAll()//List of strings (URLs) which are whitelisted and don't need to be authenticated
                .anyRequest().authenticated()//All other requests need to be authenticated
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);//Spring will create a new session for each request

        return httpSecurity.build();
    }
}
