package com.jeroscalmera.battleship_project.controllers;

import com.fasterxml.jackson.core.JsonToken;
import com.jeroscalmera.battleship_project.GameLogic;
import com.jeroscalmera.battleship_project.models.Room;
import com.jeroscalmera.battleship_project.websocket.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;

import org.springframework.stereotype.Controller;

@Controller
public class MessageController {
    @Autowired
    private GameLogic GameLogic;

    @MessageMapping("/hello")
    @SendTo("/topic/connect")
    public Greeting greeting(Connection message) throws Exception {
        System.out.println("Client connected: " + message);
        GameLogic.submitStartStats();
        GameLogic.submitStats();
        return new Greeting("Game server ready....");
    }

    @MessageMapping("/chat")
    @SendTo("/topic/chat")
    public Chat chat(Connection message) throws Exception {
        System.out.println("Chat Connected");
        return new Chat("Connected to chat server");
    }

    @MessageMapping("/gameData")
    public void handleGameData(GameData gameData) {
        String target = gameData.getContent();
        System.out.println(target);
        GameLogic.shootAtShip(target);
        GameLogic.submitStats();
    }

    @MessageMapping("/room")
    public void handlePassword(Room room) {
        GameLogic.handlePassword(room);
    }

    @MessageMapping("/hidden")
    @SendTo("/topic/hidden")
    public Hidden hidden(Hidden message) throws Exception {
        System.out.println(message);
        String hidden = message.getContent();
        System.out.println(hidden);
        return new Hidden(hidden);
    }
}
