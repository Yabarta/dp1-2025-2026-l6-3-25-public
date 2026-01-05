package es.us.dp1.l6_3_24_25.Petris.messagechat.controller;


import es.us.dp1.l6_3_24_25.Petris.messagechat.model.ChatMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.Date;

@Controller
public class ChatController {
    @Operation(
        summary = "Send a chat message",
        description = "Sends a chat message to all subscribed clients.",
        tags = { "chat", "send message" }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Message sent successfully", content = { @Content(schema = @Schema(implementation = ChatMessage.class),
                mediaType = "application/json")}),
        @ApiResponse(responseCode = "400", description = "Invalid message format", content = @Content(schema = @Schema()))
    })
    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage){
        chatMessage.setTimeStamp(new Date());
        return chatMessage;
    }
}
