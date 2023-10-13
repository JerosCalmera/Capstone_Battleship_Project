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

import javax.transaction.Transactional;
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
    @Transactional
    public void handlePassword(Room roomNumber) {
        boolean roomSaved = false;
        boolean roomValidated = false;
        if (!roomRepository.findRoom().contains(roomNumber.getRoomNumber()) && roomSaved) {
            webSocketMessageSender.sendMessage("/topic/chat", new Chat("Admin: Psst! Wrong room number!"));
        } else if
                (!roomRepository.findRoom().contains(roomNumber.getRoomNumber())){
                roomRepository.save(roomNumber);
                roomSaved = true;
                webSocketMessageSender.sendMessage("/topic/connect", new Greeting("Server: Room saved!"));
            }
        else {roomValidated = true;}

            if (roomSaved && roomValidated) {
            webSocketMessageSender.sendMessage("/topic/connect", new Greeting("Server: Rooms synced"));
            for (Player newPlayer : playersNotInRoom) {
                roomNumber.addPlayerToRoom(newPlayer);
                newPlayer.setRoom(roomNumber);
                playerRepository.save(newPlayer);
            }}
        }


    public void handleNewPlayer(Player playerName) {
        if (!playerRepository.findName().contains(playerName.getName())) {
            String name = playerName.getName();
            Player player = new Player(name);
            System.out.println(player);
            playersNotInRoom.add(player);
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