package org.example.userapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
/**
 * Configuration class for customizing Spring Security settings.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * Configures HTTP security settings.
     *
     * @param http The HttpSecurity object to be configured.
     * @throws Exception If any security-related exception occurs.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // TODO for testing - Allow all requests without authentication - not for production!!!
        http.csrf().disable()
                .authorizeRequests()
                .anyRequest().permitAll();
    }

}
