package com.jeroscalmera.battleship_project.GameLogic;

import com.jeroscalmera.battleship_project.models.Player;
import com.jeroscalmera.battleship_project.models.Room;
import com.jeroscalmera.battleship_project.repositories.PlayerRepository;
import com.jeroscalmera.battleship_project.repositories.RoomRepository;
import com.jeroscalmera.battleship_project.websocket.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PlayerAndRoom {

    private PlayerRepository playerRepository;
    private RoomRepository roomRepository;
    private WebSocketMessageSender webSocketMessageSender;
    private static final List<Player> playersNotInRoom = new ArrayList<>();

    public PlayerAndRoom(PlayerRepository playerRepository, RoomRepository roomRepository, WebSocketMessageSender webSocketMessageSender) {
        this.playerRepository = playerRepository;
        this.roomRepository = roomRepository;
        this.webSocketMessageSender = webSocketMessageSender;
    }

    public void submitStartStats(Player name) {
        List<String> allCoOrds = playerRepository.findAllCoOrdsByPlayerName(name.getName());
        String converted = String.join("", allCoOrds);
        webSocketMessageSender.sendMessage("/topic/gameData", new GameData(name.getName() + converted));
    }

    boolean roomSaved = false;
    boolean roomValidated = false;

    String player1;

    String player2;
    public String roomNumberString;

    int trigger = 0;

    public void matchStart() {
        trigger = (trigger + 1);
        if (trigger == 2) {
            webSocketMessageSender.sendMessage("/topic/chat", new Chat("All ships placed! Match Start!"));
            trigger = 0;
            coinFlip();
        }
    }

    public void leaderBoard() throws InterruptedException {
        List<Player> leaderboard = playerRepository.findAll();
        Player player = new Player();
        Collections.sort(leaderboard);
        for (Player playerLeader : leaderboard) {
            Thread.sleep(100);
            webSocketMessageSender.sendMessage("/topic/leaderBoard", new Chat("Level (" + playerLeader.getLevel() + ") " + playerLeader.getName()));
        }
    }

    public void coinFlip() {
        Random random = new Random();
        int coin = random.nextInt(2) + 1;
        selectPlayer(coin);
    }

    public void selectPlayer(int coin) {
        if (coin == 1) {
            webSocketMessageSender.sendMessage("/topic/turn", new Chat(player1));
        }
        if (coin == 2) {
            webSocketMessageSender.sendMessage("/topic/turn", new Chat(player2));
        }
    }

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
            webSocketMessageSender.sendMessage("/topic/playerData1", new Hidden(playersNotInRoom.get(1).getName()));
            player1 = playersNotInRoom.get(1).getName();
            player2 = playersNotInRoom.get(0).getName();
            webSocketMessageSender.sendMessage("/topic/playerData2", new Hidden(playersNotInRoom.get(0).getName()));
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

    public void handleNewPlayer(Player playerName) throws InterruptedException {
            Thread.sleep(100);
                if (!playerRepository.findName().contains(playerName.getName())) {
                    if (playerRepository.findName().contains(playerName.getName().substring(0, 4))) {
                        webSocketMessageSender.sendMessage("/topic/hidden", new Chat(playerName.getName() + "Admin: Sorry, this username is too similar to an existing username"));
                    } else {
                        String name = playerName.getName();
                        Player player = new Player(name);
                        playersNotInRoom.add(player);
                        webSocketMessageSender.sendMessage("/topic/chat", new Chat("Admin: Hello to our new player " + playerName.getName() + " your profile has been saved!"));
                    }
                } else {
                    webSocketMessageSender.sendMessage("/topic/chat", new Chat("Admin: Welcome back " + playerName.getName() + "!"));
                    String name = playerName.getName();
                    Player player = new Player(name);
                    playersNotInRoom.add(player);
                }
            }
        }


