package com.intouristing.intouristing.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.intouristing.intouristing.exceptions.NotFoundException;
import com.intouristing.intouristing.model.entity.User;
import com.intouristing.intouristing.model.repository.UserRepository;
import com.intouristing.intouristing.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

import static com.intouristing.intouristing.security.SecurityConstants.*;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Created by Marcelo Lacroix on 11/08/2019.
 */
@Slf4j
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private AccountService accountService;

    private UserRepository userRepository;

    public JWTAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {
        String header = request.getHeader(HEADER_STRING);

        if (isNull(header) || !header.startsWith(TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authenticationToken = this.getAuthentication(request);

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        if (nonNull(authenticationToken)) {
            resolveAccountInfo(request);
        }

        filterChain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(HEADER_STRING);

        if (nonNull(token)) {
            String user = JWT.require(Algorithm.HMAC512(SECRET.getBytes()))
                    .build()
                    .verify(token.replace(TOKEN_PREFIX, ""))
                    .getSubject();

            if (nonNull(user)) {
                return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
            }
            return null;
        }
        return null;
    }

    private void resolveAccountInfo(HttpServletRequest request) {
        resolveAccountServiceBean(request);
        String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        if (isNull(userRepository)) {
            ServletContext servletContext = request.getServletContext();
            WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
            userRepository = webApplicationContext.getBean(UserRepository.class);
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(User.class, username));
        accountService.setUser(user);
    }

    private void resolveAccountServiceBean(HttpServletRequest request) {
        if (isNull(accountService)) {
            ServletContext servletContext = request.getServletContext();
            WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
            accountService = webApplicationContext.getBean(AccountService.class);
        }
    }
}
