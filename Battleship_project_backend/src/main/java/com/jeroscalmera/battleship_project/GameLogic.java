package com.jeroscalmera.battleship_project;

import com.jeroscalmera.battleship_project.models.Player;
import com.jeroscalmera.battleship_project.models.Room;
import com.jeroscalmera.battleship_project.models.Ship;
import com.jeroscalmera.battleship_project.repositories.PlayerRepository;
import com.jeroscalmera.battleship_project.repositories.RoomRepository;
import com.jeroscalmera.battleship_project.repositories.ShipRepository;
import com.jeroscalmera.battleship_project.websocket.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameLogic {
    private PlayerRepository playerRepository;
    private ShipRepository shipRepository;
    private RoomRepository roomRepository;
    private WebSocketMessageSender webSocketMessageSender;
    private static final List<Player> playersNotInRoom = new ArrayList<>();

    public GameLogic(PlayerRepository playerRepository, ShipRepository shipRepository, RoomRepository roomRepository, WebSocketMessageSender webSocketMessageSender) {
        this.playerRepository = playerRepository;
        this.shipRepository = shipRepository;
        this.roomRepository = roomRepository;
        this.webSocketMessageSender = webSocketMessageSender;
    }

    public void submitStartStats() {
        List<String> allCoOrds = shipRepository.findAllCoOrds();
        String converted = String.join("", allCoOrds);
        webSocketMessageSender.sendMessage("/topic/gameData2", new GameData(converted));
    }
    public void submitStats() {
        List<String> allCoOrds = shipRepository.findAllCoOrds();
        String converted = String.join("", allCoOrds);
        webSocketMessageSender.sendMessage("/topic/gameData", new GameData(converted));
    }
    public void handlePassword(Room roomNumber) {
        if (roomRepository.findRoom().isEmpty()) {
            roomRepository.save(roomNumber);
            webSocketMessageSender.sendMessage("/topic/connect", new Greeting("Server: Room saved!"));
        } else if (!roomRepository.findRoom().contains(roomNumber.getRoomNumber())) {
            webSocketMessageSender.sendMessage("/topic/chat", new Chat("Admin: Psst! Wrong room number!"));
        } else {
            webSocketMessageSender.sendMessage("/topic/connect", new Greeting("Server: Rooms synced"));
            for (Player player : playersNotInRoom) {
                roomNumber.addPlayerToRoom(player);
                playerRepository.save(player);
            }
        }
    }

    public void handleNewPlayer(Player playerName) {
        if (!playerRepository.findName().contains(playerName.getName())) {
            String name = playerName.getName();
            Player player = new Player(name);
            playersNotInRoom.add(playerName);
            webSocketMessageSender.sendMessage("/topic/connect", new Greeting("Server: Player saved!"));}
        else{
            webSocketMessageSender.sendMessage("/topic/connect", new Greeting("Server: Player already exists!"));
        }
    }

    public void shootAtShip(String target) {
        if (shipRepository.findShipIdsByCoOrdsContainingPair(target) != null) {
            webSocketMessageSender.sendMessage("/topic/chat", new Chat("Admin: Hit!"));
            Long shipID = shipRepository.findShipIdsByCoOrdsContainingPair(target);
            Optional<Ship> shipToUpdate = shipRepository.findById(shipID);
            Ship ship = shipToUpdate.get();
            String shipHealth = ship.getCoOrds();
            String newShipHealth = shipHealth.replace(target, "");
            ship.setCoOrds(newShipHealth);
            shipRepository.save(ship);
        } else {
            webSocketMessageSender.sendMessage("/topic/chat", new Chat("Admin: Miss!"));
        }
    }
}