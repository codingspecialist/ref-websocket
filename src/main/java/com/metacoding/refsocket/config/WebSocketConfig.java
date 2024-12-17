package com.metacoding.refsocket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

// SRP : 마트 점원 (메시지브로커) 세팅
@EnableWebSocketMessageBroker
@Configuration
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // sub(브라우저) : /바나나
    // pub(브라우저) : /바나나

    // 웹소켓 연결 앤드포인트 설정
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/connect")
                .setAllowedOriginPatterns("*");
    }

    // 구독, 발행 앤드포인트 설정
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/sub"); // sub로 시작하는 모든 주소
        registry.setApplicationDestinationPrefixes("/pub"); // pub로 시작하는 모든 주소
    }

}
