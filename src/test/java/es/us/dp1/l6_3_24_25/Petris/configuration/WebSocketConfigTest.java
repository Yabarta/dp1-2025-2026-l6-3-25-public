package es.us.dp1.l6_3_24_25.Petris.configuration;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.StompWebSocketEndpointRegistration;

@Epic("WebSocket configuration")
@Feature("STOMP endpoint and broker setup")
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
    @Story("STOMP endpoint registration")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("registerStompEndpoints registra /ws y SockJS")
    @Description("Verifies that the /ws endpoint is registered with allowed origins and SockJS support.")
    @Owner("DiegoVicenteCamara(RXW1249)")
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
    @Story("Broker configuration")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("configureMessageBroker configura /topic y /app")
    @Description("Verifies that the message broker is configured with /topic and /app prefixes.")
    @Owner("DiegoVicenteCamara(RXW1249)")
    @SuppressWarnings("null")
    void configureMessageBroker_configuresTopicAndAppPrefix() {
        config.configureMessageBroker(brokerRegistry);

        verify(brokerRegistry).enableSimpleBroker(eq("/topic"));
        verify(brokerRegistry).setApplicationDestinationPrefixes(eq("/app"));
    }
}
