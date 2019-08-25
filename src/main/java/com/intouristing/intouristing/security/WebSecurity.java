package com.intouristing.intouristing.security;

import com.intouristing.intouristing.repository.UserRepository;
import com.intouristing.intouristing.service.UserDetailsServiceImpl;
import com.intouristing.intouristing.service.account.AccountService;
import com.intouristing.intouristing.service.account.AccountServiceImpl;
import com.intouristing.intouristing.service.account.AccountWsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static com.intouristing.intouristing.security.SecurityConstants.SIGN_UP_URL;

/**
 * Created by Marcelo Lacroix on 11/08/2019.
 */
@EnableWebSecurity
@CrossOrigin
@Configuration
public class WebSecurity extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {

    private UserDetailsServiceImpl userDetailsService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private UserRepository userRepository;

    public WebSecurity(UserDetailsServiceImpl userDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder, UserRepository userRepository) {
        this.userDetailsService = userDetailsService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().authorizeRequests()
                .antMatchers(HttpMethod.POST, SIGN_UP_URL).permitAll()
                .antMatchers(HttpMethod.GET, "/ping").permitAll()
                .antMatchers(HttpMethod.GET, "/sockjs/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilter(new JWTAuthenticationFilter(authenticationManager(), userRepository))
                .addFilter(new JWTAuthorizationFilter(authenticationManager()))
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Bean
    @Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
    public AccountService accountService() {
        return new AccountServiceImpl();
    }

    @Bean
    @Scope(value = "websocket", proxyMode = ScopedProxyMode.INTERFACES)
    public AccountWsService accountWsService() {
        return new AccountServiceImpl();
    }

}
