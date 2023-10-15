package com.jeroscalmera.battleship_project.controllers;

import com.fasterxml.jackson.core.JsonToken;
import com.jeroscalmera.battleship_project.GameLogic;
import com.jeroscalmera.battleship_project.models.Player;
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
    public Greeting greeting(Connection message, String name) throws Exception {
        System.out.println("Client connected: " + message);
        return new Greeting("Game server ready....");
    }

    @MessageMapping("/chat")
    @SendTo("/topic/chat")
    public Chat chat(Chat message) throws Exception {
        System.out.println(message);
        String chat = message.getContent();
        System.out.println(chat);
        return new Chat(chat);
    }
    @MessageMapping("/gameUpdate")
    public void gameUpdate(String name) {
        GameLogic.submitStats(name);
    }
    @MessageMapping("/startup")
    public void startup(String name) {
        GameLogic.submitStartStats(name);
    }
    @MessageMapping("/gameData")
    public void handleGameData(GameData gameData, String name) {
        String target = gameData.getContent();
        System.out.println(target);
        GameLogic.shootAtShip(target);
    }
    @MessageMapping("/restart")
    public void handleRestart(String hidden) {
        GameLogic.restart();
    }
    @MessageMapping("/room")
    public void handlePassword(String newRoom) throws InterruptedException {
        GameLogic.handlePassword(newRoom);
    }

    @MessageMapping("/name")
    @SendTo("/topic/name")
    public void handleName (Player playerName) throws Exception {
        GameLogic.handleNewPlayer(playerName);
    }
    @MessageMapping("/placement")
    public void shipPlacement(String string) {
        GameLogic.placeShip(string);
    }
    @MessageMapping("/placement2")
    public void resetPlacement(String string) {
        GameLogic.resetPlacement(string);
    }
}
