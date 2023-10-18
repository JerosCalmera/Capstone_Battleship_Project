package com.jeroscalmera.battleship_project.GameLogic;

import com.jeroscalmera.battleship_project.models.Player;
import com.jeroscalmera.battleship_project.models.Ship;
import com.jeroscalmera.battleship_project.repositories.PlayerRepository;
import com.jeroscalmera.battleship_project.repositories.RoomRepository;
import com.jeroscalmera.battleship_project.repositories.ShipRepository;
import com.jeroscalmera.battleship_project.websocket.Chat;
import com.jeroscalmera.battleship_project.websocket.WebSocketMessageSender;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class Placing {
    private PlayerRepository playerRepository;
    private ShipRepository shipRepository;
    private RoomRepository roomRepository;
    private WebSocketMessageSender webSocketMessageSender;

    public Placing(PlayerRepository playerRepository, ShipRepository shipRepository, RoomRepository roomRepository, WebSocketMessageSender webSocketMessageSender) {
        this.playerRepository = playerRepository;
        this.shipRepository = shipRepository;
        this.roomRepository = roomRepository;
        this.webSocketMessageSender = webSocketMessageSender;
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

    public void placeShip(String target) throws InterruptedException {
        System.out.println(target);

        if (!coOrds.contains(target.substring(1, 3))) {
            coOrds.add(target.substring(1, 3));
            damage += target.substring(1, 3);}
        Player newPlayer = new Player("");
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
                webSocketMessageSender.sendMessage("/topic/chat", new Chat("Invalid Placement!"));
                webSocketMessageSender.sendMessage("/topic/placement2", new Chat("Invalid"));
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
                webSocketMessageSender.sendMessage("/topic/placement2", new Chat(selectedPlayer.getName()+newShip.getName()));
                damage = "";
                invalidPlacement = false;
                horizontalPlacement = false;
                verticalPlacement = false;
            }
        }
    }

    public void resetPlacement(String trigger) {
        if (trigger.length() > 1) {
            coOrds.clear();
            damage="";}
    }
}