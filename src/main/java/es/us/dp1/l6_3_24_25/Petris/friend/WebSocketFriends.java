package es.us.dp1.l6_3_24_25.Petris.friend;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketFriends {

    @MessageMapping("/friend")
    @SendTo("/topic/friends")
    public String sendFriend(@Payload String message){
        return "refresh";
    }
}
