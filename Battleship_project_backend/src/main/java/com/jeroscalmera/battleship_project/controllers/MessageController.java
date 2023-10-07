package com.jeroscalmera.battleship_project.controllers;

import com.jeroscalmera.battleship_project.websocket.Greeting;
import com.jeroscalmera.battleship_project.websocket.Connection;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Controller
public class MessageController {
    @MessageMapping("/hello")
    @SendTo("/topic/connect")
    public Greeting greeting(Connection message) throws Exception{
        System.out.println("Client Connected");
    return new Greeting("Confirming: " + HtmlUtils.htmlEscape(message.getMessage()));
    }
}
