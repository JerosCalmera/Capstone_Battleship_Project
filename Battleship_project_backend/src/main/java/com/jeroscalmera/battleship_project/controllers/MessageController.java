package com.jeroscalmera.battleship_project.controllers;

import com.jeroscalmera.battleship_project.websocket.Chat;
import com.jeroscalmera.battleship_project.websocket.Greeting;
import com.jeroscalmera.battleship_project.websocket.Connection;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class MessageController {
    @MessageMapping("/hello")
    @SendTo("/topic/connect")
    public Greeting greeting(Connection message) throws Exception{
        System.out.println("Client connected: " + message);
    return new Greeting("Game server ready....");
    }
    @MessageMapping("/chat")
    @SendTo("/topic/chat")
    public Chat chat(Connection message) throws Exception{
        System.out.println("Chat Connected");
        return new Chat("Connected to chat server");
    }
}
