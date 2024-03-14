package com.jeroscalmera.battleship_project.gameLogic;

import com.jeroscalmera.battleship_project.models.BugReport;
import com.jeroscalmera.battleship_project.models.Lobby;
import com.jeroscalmera.battleship_project.models.Player;
import com.jeroscalmera.battleship_project.models.Room;
import com.jeroscalmera.battleship_project.repositories.*;
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

    private LobbyRepository lobbyRepository;
    private WebSocketMessageSender webSocketMessageSender;
    private static final List<Player> playersNotInRoom = new ArrayList<>();
    private Placing placing;
    private Shooting shooting;

    public PlayerAndRoom(LobbyRepository lobbyRepository, PlayerRepository playerRepository, RoomRepository roomRepository, ShipRepository shipRepository, BugreportRepository bugreportRepository, WebSocketMessageSender webSocketMessageSender, Placing placing, Shooting shooting) {
        this.playerRepository = playerRepository;
        this.roomRepository = roomRepository;
        this.shipRepository = shipRepository;
        this.bugreportRepository = bugreportRepository;
        this.lobbyRepository = lobbyRepository;
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

    public void leaderBoard(String trigger) throws InterruptedException {
        List<Player> leaderboard = playerRepository.findAll();
        Collections.sort(leaderboard);
        int total = 0;
        for (Player playerLeader : leaderboard) {
            if (total < 10) {
                if (!playerLeader.isComputer()){
                webSocketMessageSender.sendMessage("/topic/leaderBoard", new Chat("Level (" + playerLeader.getLevel() + ") " + playerLeader.getName()));
                }
                total++;
            } else {
                break;
            }
        }
    }

    public void matchStart(String playerName) throws InterruptedException {
        if (!playerName.contains("Computer")) {
            playerName = playerName.substring(1, playerName.length() -1);
        }
        System.out.println("input: " + playerName);
        Player activePlayer = playerRepository.findByNameContaining(playerName);
        System.out.println("found player:  " + activePlayer.getName());
        activePlayer.setReady();
        playerRepository.save(activePlayer);
        Room activeRoom = roomRepository.findRoomByPlayersName(playerName);
        if (activeRoom.getPlayersReady() == 0) {
            activeRoom.setPlayersReady(1);
            roomRepository.save(activeRoom);
        }
        else if (activeRoom.getPlayersReady() == 1) {
            activeRoom.setPlayersReady(2);
            roomRepository.save(activeRoom);
            Lobby lobbyRoomToDelete = lobbyRepository.findLobbySingleRoom(activeRoom.getRoomNumber());
            coinFlip(playerName);
        }
    }


    public void coinFlip(String playerName) throws InterruptedException {
        Random random = new Random();
        Room room = roomRepository.findRoomByPlayersName(playerName);
        Lobby lobby = lobbyRepository.findLobbySingleRoom(room.getRoomNumber());
        System.out.println("Room number is: " + room.getRoomNumber());
        List<Player> playerList = room.getPlayers();
        Player playerToSelect = new Player(playerName);
        int coin = random.nextInt(2) + 1;
        if (coin == 1) {
            Player player = playerRepository.findByNameContaining(playerName);
            webSocketMessageSender.sendMessage("/topic/turn", new Chat(room.getRoomNumber() + player.getName()));
            if (player.isComputer()) {
                shooting.computerShoot(player.getName());
            }
            webSocketMessageSender.sendMessage("/topic/chat", new Chat(room.getRoomNumber() + "All ships placed! Match Start!"));
            lobbyRepository.delete(lobby);
        }
        if (coin == 2) {
            if (Objects.equals(playerList.get(0).getName(), playerToSelect.getName())) {
                playerToSelect = playerList.get(0);}
            System.out.println("Coin flip 2, setting player to index 0");
        } else {
            playerToSelect = playerList.get(1);
            System.out.println("Coin flip 2, setting player to index 1"); }
        webSocketMessageSender.sendMessage("/topic/turn", new Chat(room.getRoomNumber() + playerToSelect.getName()));
        if (playerToSelect.isComputer()) {
            shooting.computerShoot(playerToSelect.getName());}
        webSocketMessageSender.sendMessage("/topic/chat", new Chat(room.getRoomNumber() + "All ships placed! Match Start!"));
        lobbyRepository.delete(lobby);
    }


    public void handlePassword(String roomNumber) throws InterruptedException {
        System.out.println("Starting handle password");
        roomNumber = roomNumber.substring(1, roomNumber.length() - 1);
            if
            (!lobbyRepository.findLobbyRoomExists(roomNumber)) {
                Lobby roomToSave = new Lobby(roomNumber);
                roomToSave.setSaved(true);
                lobbyRepository.save(roomToSave);
                System.out.println("Lobby saved for first time");
                webSocketMessageSender.sendMessage("/topic/connect", new Greeting("Server: Room saved!"));
            } else {
                Lobby roomToValidate = lobbyRepository.findLobbySingleRoom(roomNumber);
                roomToValidate.setValidated(true);
                lobbyRepository.save(roomToValidate);
                System.out.println("Lobby validated");
            }
            Lobby roomToCheck = lobbyRepository.findLobbySingleRoom(roomNumber);
            if (roomToCheck.isSaved() && roomToCheck.isValidated()) {
                webSocketMessageSender.sendMessage("/topic/connect", new Greeting("Server: Rooms synced"));
                Room addRoom = new Room(roomNumber);
                addRoom.setRoomNumber(roomNumber.substring(0, 4));
                roomRepository.save(addRoom);
                Thread.sleep(50);
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
                webSocketMessageSender.sendMessage("/topic/playerData2", new Hidden(addRoom.getRoomNumber() + playerDetails2.getDetails()));
                roomRepository.save(addRoom);
                playerRepository.save(playerDetails1);
                playerRepository.save(playerDetails2);
                playersNotInRoom.clear();
            }
        }
    public void handleNewPlayer(Player playerName) throws InterruptedException {
        Thread.sleep(100);
        List<String> players = playerRepository.findName();
        if (!players.contains(playerName.getName())) {
            if (players.stream().anyMatch(name -> name.startsWith(playerName.getName().substring(0, 4)))) {
                webSocketMessageSender.sendMessage("/topic/chat", new Chat("Admin: Sorry, " + playerName.getName() + " is too similar to an existing username!"));
                webSocketMessageSender.sendMessage("/topic/hidden", new Chat(playerName.getName()));
            } else {
                String name = playerName.getName();
                Player player = new Player(name);
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
        computerPlayerCreated.setComputer(true);
        playerRepository.save(computerPlayerCreated);
        handleNewPlayer(computerPlayerCreated);
        Thread.sleep(50);
        handlePassword(roomNumber);
        Thread.sleep(50);
        handlePassword(roomNumber);
        Thread.sleep(50);;
        Thread placeShipsThread = new Thread(() -> {
            try {
                placing.computerPlaceShips(computerPlayerCreated);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        placeShipsThread.start();
        placeShipsThread.join();
        matchStart(computerPlayerCreated.getName());
        webSocketMessageSender.sendMessage("/topic/chat", new Chat("Admin: Computer player ready"));
    }
}

