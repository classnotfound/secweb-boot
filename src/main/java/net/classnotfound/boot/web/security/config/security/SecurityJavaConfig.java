package net.classnotfound.boot.web.security.config.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityJavaConfig extends WebSecurityConfigurerAdapter {
	
	private static final Logger LOG = LoggerFactory.getLogger(SecurityJavaConfig.class);
	

	//This object is responsible to the redirection in case on attempt of access without authentication
	//In our web service use case, we do not want to be redirected to the login form, we just send 
	//an HTTP code which must be managed by the client
	@Autowired
	private AuthenticationEntryPoint authenticationEntryPoint;

	@Autowired
	private AuthenticationFailureHandler authenticationFailureHandler;

	@Autowired
	private AuthenticationSuccessHandler authenticationSuccessHandler;
 
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().
          withUser("admin").password("password").roles("ADMIN", "USER").and().
          withUser("user").password("password").roles("USER");
    }
 
    @Override
    protected void configure(HttpSecurity http) throws Exception { 
		
        http
        //.csrf().disable()
        .authorizeRequests()
        .antMatchers(HttpMethod.GET, "/secured/services/**").access("hasRole('SERVICE_USER')");
        
        http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint);
        
        //indicate that we are user login form authentication
        FormLoginConfigurer<HttpSecurity> formLogin = http.formLogin();
        //by default, in case of sucessfull login
        formLogin.successHandler(authenticationSuccessHandler);
        formLogin.failureHandler(authenticationFailureHandler);
    }

}
