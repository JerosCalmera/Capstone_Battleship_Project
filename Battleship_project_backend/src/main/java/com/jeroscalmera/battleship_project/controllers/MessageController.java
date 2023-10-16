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
        String chat = message.getContent();
        return new Chat(chat);
    }
    @MessageMapping("/gameUpdate")
    public void gameUpdate(Player name) throws InterruptedException {
        Thread.sleep(25);
        GameLogic.submitStats(name);
    }
    @MessageMapping("/startup")
    public void startup(Player name) throws InterruptedException {
        Thread.sleep(25);
        GameLogic.submitStartStats(name);
    }
    @MessageMapping("/gameData")
    public void handleGameData(GameData gameData) throws InterruptedException {
        Thread.sleep(25);
        String target = gameData.getContent();
        System.out.println(target);
        GameLogic.shootAtShip(target);
        GameLogic.enumerateShips(target);
    }
    @MessageMapping("/restart")
    public void handleRestart(String hidden) {
        GameLogic.restart();
    }
    @MessageMapping("/room")
    public void handlePassword(String newRoom) throws InterruptedException {
        Thread.sleep(25);
        GameLogic.handlePassword(newRoom);
    }

    @MessageMapping("/name")
    @SendTo("/topic/name")
    public void handleName (Player playerName) throws InterruptedException {
        Thread.sleep(25);
        GameLogic.handleNewPlayer(playerName);
    }
    @MessageMapping("/placement")
    public void shipPlacement(String string) throws InterruptedException {
        Thread.sleep(25);
        GameLogic.placeShip(string);
    }
    @MessageMapping("/placement2")
    public void resetPlacement(String string) throws InterruptedException {
        Thread.sleep(25);
        GameLogic.resetPlacement(string);
    }
//    @MessageMapping("/enemyShips")
//    public void getEnemyShips(String string) {
//        GameLogic.getEnemyShips(string);
//    }
}
