package com.jeroscalmera.battleship_project;

import com.jeroscalmera.battleship_project.models.Player;
import com.jeroscalmera.battleship_project.models.Room;
import com.jeroscalmera.battleship_project.models.Ship;
import com.jeroscalmera.battleship_project.repositories.PlayerRepository;
import com.jeroscalmera.battleship_project.repositories.RoomRepository;
import com.jeroscalmera.battleship_project.repositories.ShipRepository;
import com.jeroscalmera.battleship_project.websocket.*;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

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

//    public void getEnemyShips (String target){
//        List<String> shipList = new ArrayList<>();
//        Player playerToCheck = playerRepository.findByNameContaining((target.substring(1, 3)));
//        long ships = playerToCheck.getId();
//        shipList = shipRepository.findAllCoOrdsByPlayerId(ships);
//        String converted = String.join("", shipList);
//        System.out.println(converted);
//        webSocketMessageSender.sendMessage("/topic/gameData", new GameData(playerToCheck.getName()+converted));
//    }
    public void submitStartStats(Player name) {
        List<String> allCoOrds = playerRepository.findAllCoOrdsByPlayerName(name.getName());
        String converted = String.join("", allCoOrds);
        webSocketMessageSender.sendMessage("/topic/gameData", new GameData(name.getName()+converted));
    }

    public void submitStats(Player name) {
//        List<String> allCoOrds = playerRepository.findAllCoOrdsByPlayerName(name.getName());
//        System.out.println(allCoOrds);
//        String converted = String.join("", allCoOrds);
//        webSocketMessageSender.sendMessage("/topic/gameData2", new GameData(converted));
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
            Thread.sleep(100);
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
            playersNotInRoom.add(player);
            webSocketMessageSender.sendMessage("/topic/connect", new Greeting("Server: Player saved!"));
            webSocketMessageSender.sendMessage("/topic/hidden", new Hidden("player connected"));
        } else {
            webSocketMessageSender.sendMessage("/topic/connect", new Greeting("Server: Welcome back " + playerName.getName() + "!"));
            webSocketMessageSender.sendMessage("/topic/hidden", new Hidden("player connected"));
            String name = playerName.getName();
            Player player = new Player(name);
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
            String newShipHealth = shipHealth.replace(target, "XX");
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
        shipRepository.deleteAll();
    }

    private List<String> coOrds = new ArrayList<>();
    private String damage = "";

    public void enumerateShips (String target){
        List<String> shipList = new ArrayList<>();
        Player playerToCheck = playerRepository.findByNameContaining((target.substring(4, 8)));
        long ships = playerToCheck.getId();
        shipList = shipRepository.findAllCoOrdsByPlayerId(ships);
        System.out.println(shipList);
        for (String ship: shipList){
        if (!ship.matches("^X+$")) {
            webSocketMessageSender.sendMessage("/topic/chat", new Chat(playerToCheck.getName() + " has had all their starships destroyed! And is defeated!"));
            break;
        }
            if (ship.equals("XXXX")) {
                webSocketMessageSender.sendMessage("/topic/chat", new Chat(playerToCheck.getName() + ": You destroyed my Destroyer!"));
            } else if (ship.equals("XXXXXX")) {
                webSocketMessageSender.sendMessage("/topic/chat", new Chat(playerToCheck.getName() + ": You destroyed my Cruiser!"));
            } else if (ship.equals("XXXXXXXX")) {
                webSocketMessageSender.sendMessage("/topic/chat", new Chat(playerToCheck.getName() + ": You destroyed my Battleship!"));
            } else if (ship.equals("XXXXXXXXXX")) {
                webSocketMessageSender.sendMessage("/topic/chat", new Chat(playerToCheck.getName() + ": You destroyed my Carrier!"));
            }
        }
    }
    public void placeShip(String target) {
        if (!coOrds.contains(target.substring(1, 3))) {
            coOrds.add(target.substring(1, 3));
            damage += target.substring(1, 3);}
        Player newPlayer = new Player("");
        boolean validPlacement = false;
        boolean horizontalPlacement = false;
        boolean verticalPlacement = false;
        boolean invalidPlacement = false;
        int max = Integer.parseInt(target.substring(3, 4));
        Ship newShip = new Ship("",0,"");
        if (max == 5) {
            newShip = new Ship("Carrier", 10, "");
        } else if (max == 4) {
            newShip = new Ship("Battleship", 8, "");
        } else if (max == 3) {
            newShip = new Ship("Cruiser", 6, "");
        } else if (max == 2) {
            newShip = new Ship("Destroyer", 4, "");}
            if (coOrds.size() == max) {
                for (int i = 0; i < coOrds.size() - 1; i++) {
                    int inputOne = i;
                    int inputTwo = 1 + i;
                    int letter = 0;
                    int number = 1;

                    if (!(Math.abs(coOrds.get(inputOne).charAt(letter) - coOrds.get(inputTwo).charAt(letter)) <= 1
                            && Math.abs(coOrds.get(inputOne).charAt(number) - coOrds.get(inputTwo).charAt(number)) <= 1)) {
                        invalidPlacement = true;
                        break;
                    }
                    if ((coOrds.get(inputOne).charAt(letter) != coOrds.get(inputTwo).charAt(letter) && coOrds.get(inputOne).charAt(number) != coOrds.get(inputTwo).charAt(number))) {
                        invalidPlacement = true;
                        webSocketMessageSender.sendMessage("/topic/chat", new Chat("Invalid alignment selected!"));
                    } else if (coOrds.get(inputOne).charAt(letter) == coOrds.get(inputTwo).charAt(letter)) {
//                    webSocketMessageSender.sendMessage("/topic/chat", new Chat("Horizontal alignment selected!"));
                        horizontalPlacement = true;
                    } else if ((coOrds.get(inputOne).charAt(letter) != coOrds.get(inputTwo).charAt(letter))) {
//                    webSocketMessageSender.sendMessage("/topic/chat", new Chat("Vertical alignment selected!"));
                        verticalPlacement = true;
                    }
                }
                if (invalidPlacement == true || horizontalPlacement == true && verticalPlacement == true) {
                    damage = "";
                    coOrds.clear();
                    invalidPlacement = false;
                    horizontalPlacement = false;
                    verticalPlacement = false;

                } else {
                    coOrds.clear();
                    Player selectedPlayer = playerRepository.findByNameContaining((target.substring(4, 8)));
                    newShip.setDamage(damage);
                    selectedPlayer.addShip(newShip);
                    newShip.setPlayer(selectedPlayer);
                    shipRepository.save(newShip);
                    playerRepository.save(selectedPlayer);
                    webSocketMessageSender.sendMessage("/topic/chat", new Chat(newShip.getName() + " placed!"));
                    webSocketMessageSender.sendMessage("/topic/placement2", new Chat(selectedPlayer.getName()+newShip.getName()));
                    damage = "";
                    Player playerToCheck = playerRepository.findByNameContaining((target.substring(4, 8)));
                    long ships = playerToCheck.getId();
                    List<String> shipList = new ArrayList<>();
                    shipList = shipRepository.findAllCoOrdsByPlayerId(ships);
                    validPlacement = true;
                    invalidPlacement = false;
                    horizontalPlacement = false;
                    verticalPlacement = false;
                }
            }
        }

    public void resetPlacement(String trigger) {
        if (trigger.length() > 1) {
            coOrds.clear();
            damage="";
            webSocketMessageSender.sendMessage("/topic/chat", new Chat("Placement list cleared."));
        }
    }
}
