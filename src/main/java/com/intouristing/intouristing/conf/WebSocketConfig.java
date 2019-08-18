package com.intouristing.intouristing.conf;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.intouristing.intouristing.model.entity.User;
import com.intouristing.intouristing.model.repository.UserRepository;
import com.intouristing.intouristing.security.token.TokenService;
import com.intouristing.intouristing.service.account.AccountWsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ExecutorChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.Collections;
import java.util.Optional;

import static com.intouristing.intouristing.security.SecurityConstants.*;
import static java.util.Objects.nonNull;

/**
 * Created by Marcelo Lacroix on 14/08/2019.
 */
@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final AccountWsService accountWsService;
    private final UserRepository userRepository;

    @Autowired
    public WebSocketConfig(AccountWsService accountWsService, UserRepository userRepository) {
        this.accountWsService = accountWsService;
        this.userRepository = userRepository;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/ws");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/sockjs").setAllowedOrigins("*").withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ExecutorChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                String token = Optional.ofNullable(accessor.getNativeHeader(HEADER_STRING))
                        .map(list -> list.stream().findFirst().orElse(null))
                        .orElse(null);
                String username = null;
                if (!StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                    try {
                        username = verifyToken(token);
                    } catch (Exception ex) {
                        throw new MessageDeliveryException(message, ex.getMessage());
                    }
                    setAccount(accessor, token, username);
                }
                return message;
            }
        });
    }

    private void setAccount(StompHeaderAccessor accessor, String token, String username) {
        Optional<User> optUser = userRepository.findByUsername(username);
        if (nonNull(username) && optUser.isPresent()) {
            if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                setContextAuthentication(accessor, username);
            }

            if (!StompCommand.DISCONNECT.equals(accessor.getCommand())) {
                accountWsService.setAccount(TokenService.parseToken(token));
                accountWsService.setIsSearchCancelled(nonNull(optUser.get().getUserSearchControl().getCancelledAt()));
            }
        }
    }

    private void setContextAuthentication(StompHeaderAccessor accessor, String username) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null, Collections.singleton((GrantedAuthority) () -> "USER"));
        accessor.setUser(authentication);
    }

    private String verifyToken(String token) {
        token = Optional.ofNullable(token).orElseThrow(RuntimeException::new);
        String username = JWT.require(Algorithm.HMAC512(SECRET.getBytes()))
                .build()
                .verify(token.replace(TOKEN_PREFIX, ""))
                .getSubject();

        if (nonNull(username)) {
            return username;
        }

        return null;
    }

}
