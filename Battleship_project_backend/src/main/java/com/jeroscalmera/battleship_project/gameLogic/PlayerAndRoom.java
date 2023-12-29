package com.jeroscalmera.battleship_project.gameLogic;

import com.jeroscalmera.battleship_project.models.BugReport;
import com.jeroscalmera.battleship_project.models.Player;
import com.jeroscalmera.battleship_project.models.Room;
import com.jeroscalmera.battleship_project.repositories.BugreportRepository;
import com.jeroscalmera.battleship_project.repositories.PlayerRepository;
import com.jeroscalmera.battleship_project.repositories.RoomRepository;
import com.jeroscalmera.battleship_project.repositories.ShipRepository;
import com.jeroscalmera.battleship_project.websocket.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.Random;

@Service
public class PlayerAndRoom {
    private PlayerRepository playerRepository;
    private RoomRepository roomRepository;
    private ShipRepository shipRepository;
    private BugreportRepository bugreportRepository;
    private WebSocketMessageSender webSocketMessageSender;
    private static final List<Player> playersNotInRoom = new ArrayList<>();

    private Placing placing;
    private Shooting shooting;

    public PlayerAndRoom(PlayerRepository playerRepository, RoomRepository roomRepository, ShipRepository shipRepository, BugreportRepository bugreportRepository, WebSocketMessageSender webSocketMessageSender, Placing placing, Shooting shooting) {
        this.playerRepository = playerRepository;
        this.roomRepository = roomRepository;
        this.shipRepository = shipRepository;
        this.bugreportRepository = bugreportRepository;
        this.webSocketMessageSender = webSocketMessageSender;
        this.placing = placing;
        this.shooting = shooting;
    }

    public void bugReport(BugReport bugReport) {
        bugreportRepository.save(bugReport);
    }

    public void submitStartStats(Player name) {
        Player player = playerRepository.findByNameContaining(name.getName());
        List<String> allCoOrds = playerRepository.findAllCoOrdsByPlayerName(name.getName());
        String converted = String.join("", allCoOrds);
        webSocketMessageSender.sendMessage("/topic/gameData", new GameData(player.getRoom().getRoomNumber() + name.getName() + converted));
    }

    boolean roomSaved = false;
    boolean roomValidated = false;

    String player1;

    String player2;
    public String roomNumberString;

    int trigger = 0;


    public void matchStart() throws InterruptedException {
        trigger = (trigger + 1);
        if (trigger == 2) {
            trigger = 0;
            coinFlip();
        }
    }

    public void leaderBoard(String trigger) throws InterruptedException {
        List<Player> leaderboard = playerRepository.findAll();
        Player player = new Player();
        Collections.sort(leaderboard);
        int total = 0;
        for (Player playerLeader : leaderboard) {
            if (total < 10) {
                webSocketMessageSender.sendMessage("/topic/leaderBoard", new Chat("Level (" + playerLeader.getLevel() + ") " + playerLeader.getName()));
                total++;
            } else {
                break;
            }
        }
    }
    public void coinFlip() throws InterruptedException {
        Random random = new Random();
        int coin = random.nextInt(2) + 1;
        selectPlayer(coin);
    }

    public void selectPlayer(int coin) throws InterruptedException {
        if (coin == 1) {
            webSocketMessageSender.sendMessage("/topic/turn", new Chat(player1));
            Player player = playerRepository.findByNameContaining(player1);
            if (Objects.equals(player.getPlayerType(), "Computer")) {
                shooting.computerShoot(player1);
            }
            webSocketMessageSender.sendMessage("/topic/chat", new Chat(player.getRoom().getRoomNumber() + "All ships placed! Match Start!"));
        }
        if (coin == 2) {
            webSocketMessageSender.sendMessage("/topic/turn", new Chat(player2));
        Player player = playerRepository.findByNameContaining(player2);
            if (Objects.equals(player.getPlayerNumber(), "Computer")) {;
                shooting.computerShoot(player1);
            }
            webSocketMessageSender.sendMessage("/topic/chat", new Chat(player.getRoom().getRoomNumber() + "All ships placed! Match Start!"));
        }
    }

    public void handlePassword(String roomNumber) throws InterruptedException {
        String roomNumberSave = roomNumber;
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
            Room addRoom = new Room(roomNumberSave);
            addRoom.setRoomNumber(roomNumber.substring(1, 5));
            roomRepository.save(addRoom);
            Thread.sleep(50);
            System.out.println(("Player" + playersNotInRoom.get(1).getName()));
            for (Player newPlayer : playersNotInRoom) {
                Player playerToFind = playerRepository.findByName(newPlayer.getName());
                if (playerToFind != null) {
                    playerToFind.setRoom(addRoom);
                    shipRepository.deleteAllCoOrdsByPlayerId((playerToFind.getId()));
                    playerRepository.save(playerToFind);

                } else {
                    newPlayer.setRoom(addRoom);
                    shipRepository.deleteAllCoOrdsByPlayerId((newPlayer.getId()));
                    playerRepository.save(newPlayer);
                }
                addRoom.addPlayerToRoom(newPlayer);
            }
            Player playerDetails1 = playerRepository.findByNameContaining(playersNotInRoom.get(1).getName());
            Player playerDetails2 = playerRepository.findByNameContaining(playersNotInRoom.get(0).getName());
            webSocketMessageSender.sendMessage("/topic/playerData1", new Hidden(addRoom.getRoomNumber() + playerDetails1.getDetails()));
            player1 = playersNotInRoom.get(1).getName();
            player2 = playersNotInRoom.get(0).getName();
            webSocketMessageSender.sendMessage("/topic/playerData2", new Hidden(addRoom.getRoomNumber() + playerDetails2.getDetails()));
            roomRepository.save(addRoom);
            roomSaved = false;
            roomValidated = false;
            playerRepository.save(playerDetails1);
            playerRepository.save(playerDetails2);
            playersNotInRoom.clear();
            webSocketMessageSender.sendMessage("/topic/chat", new Chat(addRoom.getRoomNumber() + "Admin: Welcome to the private chatroom for game: " + addRoom.getRoomNumber() + ", type /global to talk to all players online now"));
        }
    }

    public void handleNewPlayer(Player playerName) throws InterruptedException {
        Thread.sleep(100);
        System.out.println(playerName.getName().substring(0, 4));
        List<String> players = playerRepository.findName();
        if (!players.contains(playerName.getName())) {
            if (players.stream().anyMatch(name -> name.startsWith(playerName.getName().substring(0, 4)))) {
                webSocketMessageSender.sendMessage("/topic/chat", new Chat("Admin: Sorry, " + playerName.getName() + " is too similar to an existing username!"));
                webSocketMessageSender.sendMessage("/topic/hidden", new Chat(playerName.getName()));
            } else {
                String name = playerName.getName();
                Player player = new Player(name);
                player.setPlayerType("Human");
                playersNotInRoom.add(player);
                webSocketMessageSender.sendMessage("/topic/chat", new Chat("Admin: Hello to our new player " + playerName.getName() + " your profile has been saved!"));
            }
        } else {
            if (!playerName.getName().contains("Computer")) {
                webSocketMessageSender.sendMessage("/topic/chat", new Chat("Admin: Welcome back " + playerName.getName() + "!"));
            }else
            {webSocketMessageSender.sendMessage("/topic/chat", new Chat("Admin: A Game against the Computer has been selected"));}
            String name = playerName.getName();
            Player player = new Player(name);
            player.setPlayerType("Human");
            playersNotInRoom.add(player);
        }
    }
    public void computerMatchStart(String roomNumber) throws InterruptedException {
        Random random = new Random();
        int rando = random.nextInt(10000);
        String randomNumber = String.format("%05d", rando);
        String ident = randomNumber;
        Player computerPlayerCreated = new Player();
        computerPlayerCreated.setName(ident + "Computer");
        computerPlayerCreated.setPlayerType("Computer");
        playerRepository.save(computerPlayerCreated);
        handleNewPlayer(computerPlayerCreated);
        Thread.sleep(50);
        handlePassword(roomNumber);
        Thread.sleep(50);
        handlePassword(roomNumber);
        Thread.sleep(50);;
        placing.computerPlaceShips(computerPlayerCreated);
        matchStart();
    }

}

