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

    boolean roomSaved = false;
    boolean roomValidated = false;

    public String roomNumberString;

    @Transactional
    public void handlePassword(String roomNumber) throws InterruptedException {
        if
        (!Objects.equals(roomNumberString, roomNumber)) {
            if (!roomSaved) {
                roomSaved = true;
                roomNumberString = roomNumber;
                webSocketMessageSender.sendMessage("/topic/connect", new Greeting("Server: Room saved!"));
            } else {
                webSocketMessageSender.sendMessage("/topic/chat", new Chat("Admin: Psst! Wrong room number!"));
            }
        } else {
            roomValidated = true;
        }
        if (roomSaved && roomValidated) {
            webSocketMessageSender.sendMessage("/topic/connect", new Greeting("Server: Rooms synced"));
            Room addRoom = new Room(roomNumber);
            roomRepository.save(addRoom);
            webSocketMessageSender.sendMessage("/topic/playerData1", new Hidden(playersNotInRoom.get(1).getName() + " LvL: " + playersNotInRoom.get(0).getLevel()));
            Thread.sleep(250);
            webSocketMessageSender.sendMessage("/topic/playerData2", new Hidden(playersNotInRoom.get(0).getName() + " LvL: " + playersNotInRoom.get(0).getLevel()));
            for (Player newPlayer : playersNotInRoom) {
                Player playerToFind = playerRepository.findByName(newPlayer.getName());
                if (playerToFind != null) {
                    playerToFind.setRoom(addRoom);
                    playerRepository.save(playerToFind);
                } else {
                    newPlayer.setRoom(addRoom);
                    playerRepository.save(newPlayer);
                }
                addRoom.addPlayerToRoom(newPlayer);
            }
            roomRepository.save(addRoom);
            roomSaved = false;
            roomValidated = false;
            playersNotInRoom.clear();
        }
    }

    public void handleNewPlayer(Player playerName) {
        if (!playerRepository.findName().contains(playerName.getName())) {
            String name = playerName.getName();
            Player player = new Player(name);
            System.out.println(player);
            playersNotInRoom.add(player);
            webSocketMessageSender.sendMessage("/topic/connect", new Greeting("Server: Player saved!"));
            webSocketMessageSender.sendMessage("/topic/hidden", new Hidden("player connected"));
        } else {
            webSocketMessageSender.sendMessage("/topic/connect", new Greeting("Server: Welcome back " + playerName.getName() + "!"));
            webSocketMessageSender.sendMessage("/topic/hidden", new Hidden("player connected"));
            String name = playerName.getName();
            Player player = new Player(name);
            System.out.println(player);
            playersNotInRoom.add(player);
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

    public void restart() {
        List<Player> playerList = playerRepository.findAll();
        for (Player player : playerList) {
            player.setRoom(null);
            playerRepository.save(player);
        }
        roomRepository.deleteAll();
    }

    private List<String> coOrds = new ArrayList<>();

    public void placeShip(String target) {
        System.out.println((target));
        if (!coOrds.contains(target)) {
            coOrds.add(target);
            if (coOrds.size() == 2) {
                if (coOrds.get(0).charAt(0) != coOrds.get(1).charAt(0)) {
                    webSocketMessageSender.sendMessage("/topic/chat", new Chat("Vertical alignment selected!"));
                    coOrds.clear();
                } else if (coOrds.get(0).charAt(1) == (coOrds.get(1).charAt(1) + 1)) {
                    webSocketMessageSender.sendMessage("/topic/chat", new Chat("Horizontal alignment selected!"));
                    coOrds.clear();
                } else if (coOrds.get(0).charAt(1) == (coOrds.get(1).charAt(1) - 1)) {
                    webSocketMessageSender.sendMessage("/topic/chat", new Chat("Horizontal alignment selected!"));
                    coOrds.clear();
                } else {
                    webSocketMessageSender.sendMessage("/topic/chat", new Chat("Invalid alignment selected!"));
                    coOrds.clear();
                }
            }
        }
    }
    public void resetPlacement(String trigger) {
        if (Objects.equals(trigger, "Clear")) {
            coOrds.clear();
            webSocketMessageSender.sendMessage("/topic/chat", new Chat("Placement list cleared."));
        }
    }
}


