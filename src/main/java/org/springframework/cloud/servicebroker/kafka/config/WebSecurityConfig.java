package org.springframework.cloud.servicebroker.kafka.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import java.util.UUID;

/**
 * Created by rhardt on 11/20/16.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private Environment env;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests().antMatchers("/**").hasRole("USER").and().httpBasic();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {

        if(env.getProperty("security.user.name") != null && env.getProperty("security.user.password") != null) {
            auth
                    .inMemoryAuthentication()
                    .withUser(env.getProperty("security.user.name")).password(env.getProperty("security.user.password")).roles("USER");
        }
        else {
            auth.inMemoryAuthentication().withUser(UUID.randomUUID().toString()).password(UUID.randomUUID().toString()).roles("USER");
        }
    }
}