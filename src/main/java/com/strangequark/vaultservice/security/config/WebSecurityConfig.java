package com.strangequark.vaultservice.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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

    //Configure the CORS policy, allow the ReactService through
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                boolean dockerDeployment = Boolean.parseBoolean(System.getenv("DOCKER_DEPLOYMENT"));

                //Allow the reactService through the CORS policy
                registry.addMapping("/**")
                        .allowedOrigins(
                                "http://react-service:5173", "http://localhost:5173",
                                "http://gateway-service:8080", "http://localhost:8080"
                        )
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowCredentials(true);
            }
        };
    }
}
