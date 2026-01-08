package es.us.dp1.l6_3_24_25.Petris.messagechat.controller;


import es.us.dp1.l6_3_24_25.Petris.messagechat.model.ChatMessage;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.Date;

@Controller
public class ChatController {
    @MessageMapping("/chat/{id}")
    @SendTo("/topic/messages/{id}")
    public ChatMessage sendMessage(@DestinationVariable String id, @Payload ChatMessage chatMessage){
        chatMessage.setTimeStamp(new Date());
        return chatMessage;
    }
}
