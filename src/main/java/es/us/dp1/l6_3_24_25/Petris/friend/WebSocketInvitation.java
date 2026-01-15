package es.us.dp1.l6_3_24_25.Petris.friend;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketInvitation {

    @MessageMapping("/invite/{id}")
    @SendTo("/topic/invitations/{id}")
    public String sendInvitation(@DestinationVariable String id , @Payload String message){
        return message;
    }
}
