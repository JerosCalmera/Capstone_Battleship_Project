package com.jeroscalmera.battleship_project.controllers;

import com.jeroscalmera.battleship_project.gameLogic.Placing;
import com.jeroscalmera.battleship_project.gameLogic.PlayerAndRoom;
import com.jeroscalmera.battleship_project.gameLogic.Shooting;
import com.jeroscalmera.battleship_project.models.BugReport;
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
    private PlayerAndRoom playerAndRoom;
    @Autowired
    private Placing placing;
    @Autowired
    private Shooting shooting;
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
    }
    @MessageMapping("/startup")
    public void startup(Player name) throws InterruptedException {
        playerAndRoom.submitStartStats(name);
    }
    @MessageMapping("/gameData")
    public void handleGameData(GameData gameData) throws InterruptedException {
        String target = gameData.getContent();
        System.out.println(target);
        shooting.shootAtShip(target);
    }

    @MessageMapping("/randomPlacement")
    public void handleRandomPlacement(Player name) throws InterruptedException {
        placing.computerPlaceShips(name);
    }
    @MessageMapping("/restart")
    public void handleRestart(String roomNumber) {placing.restart(roomNumber);
    }
    @MessageMapping("/room")
    public void handlePassword(String newRoom) throws InterruptedException {
        playerAndRoom.handlePassword(newRoom);
    }
    @MessageMapping("/name")
    @SendTo("/topic/name")
    public void handleName (Player player) throws InterruptedException {
        playerAndRoom.handleNewPlayer(player);
    }
    @MessageMapping("/placement")
    public void shipPlacement(String string) throws InterruptedException {
        placing.placeShip(string);
    }
    @MessageMapping("/placement2")
    public void resetPlacement(String string) throws InterruptedException {
        placing.resetPlacement(string);
    }
    @MessageMapping("/enemyDamage")
    public void enemyDamage(String string) {
    }
    @MessageMapping("/hidden")
    public Hidden hidden(String string) {
        return null;
    }

    @MessageMapping("/computer")
    public void computer(String roomNumber) throws InterruptedException {
        playerAndRoom.computerMatchStart(roomNumber);
    }

    @MessageMapping("/miss")
    public void miss(String string) {
    }
    @MessageMapping("/gameInfo")
    public void gameInfo(String string) {
    }

    @MessageMapping("/leaderBoard")
    public void leaderboard(String trigger) throws InterruptedException {
        playerAndRoom.leaderBoard(trigger);
    }

    @MessageMapping("/autoShoot")
    public void autoShoot() throws InterruptedException {
        shooting.autoShoot();
    }
    @MessageMapping("/turn")
    public void turn(String playerName) throws InterruptedException {
        playerAndRoom.coinFlip();
    }

    @MessageMapping("/turnCheck")
    public void turnCheck(String playerName) throws InterruptedException {
    }

    @MessageMapping("/matchStart")
    public void matchStart(String playerName) throws InterruptedException {
        playerAndRoom.matchStart(playerName);
    }
    @MessageMapping("/bugReport")
    public void bugReport(BugReport bugReport) throws InterruptedException {
        playerAndRoom.bugReport(bugReport);
    }
}
