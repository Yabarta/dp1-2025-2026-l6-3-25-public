package es.us.dp1.l6_3_24_25.Petris.configuration;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.StompWebSocketEndpointRegistration;

@ExtendWith(MockitoExtension.class)
class WebSocketConfigTest {

    private final WebSocketConfig config = new WebSocketConfig();

    @Mock
    private StompEndpointRegistry endpointRegistry;

    @Mock
    private StompWebSocketEndpointRegistration endpointRegistration;

    @Mock
    private MessageBrokerRegistry brokerRegistry;

    @Test
    @SuppressWarnings("null")
    void registerStompEndpoints_registersClientEndpoint() {
        when(endpointRegistry.addEndpoint("/ws")).thenReturn(endpointRegistration);
        when(endpointRegistration.setAllowedOrigins("http://localhost:3000")).thenReturn(endpointRegistration);

        config.registerStompEndpoints(endpointRegistry);

        verify(endpointRegistry).addEndpoint("/ws");
        verify(endpointRegistration).setAllowedOrigins("http://localhost:3000");
        verify(endpointRegistration).withSockJS();
    }

    @Test
    @SuppressWarnings("null")
    void configureMessageBroker_configuresTopicAndAppPrefix() {
        config.configureMessageBroker(brokerRegistry);

        verify(brokerRegistry).enableSimpleBroker(eq("/topic"));
        verify(brokerRegistry).setApplicationDestinationPrefixes(eq("/app"));
    }
}
