package com.jeroscalmera.battleship_project.GameLogic;

import com.jeroscalmera.battleship_project.models.Player;
import com.jeroscalmera.battleship_project.models.Room;
import com.jeroscalmera.battleship_project.models.Ship;
import com.jeroscalmera.battleship_project.repositories.PlayerRepository;
import com.jeroscalmera.battleship_project.repositories.RoomRepository;
import com.jeroscalmera.battleship_project.repositories.ShipRepository;
import com.jeroscalmera.battleship_project.websocket.Chat;
import com.jeroscalmera.battleship_project.websocket.Hidden;
import com.jeroscalmera.battleship_project.websocket.WebSocketMessageSender;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class Shooting {
    private PlayerRepository playerRepository;
    private ShipRepository shipRepository;
    private WebSocketMessageSender webSocketMessageSender;
    private RoomRepository roomRepository;

    public Shooting(RoomRepository repository, PlayerRepository playerRepository, ShipRepository shipRepository, WebSocketMessageSender webSocketMessageSender) {
        this.roomRepository = repository;
        this.playerRepository = playerRepository;
        this.shipRepository = shipRepository;
        this.webSocketMessageSender = webSocketMessageSender;
    }


    public void shootAtShip(String input) {
        String target = input.trim();
        System.out.println(target);
        System.out.println((target.substring(2, 6)));
        String aimPoint = target.substring(0, 2);
        aimPoint = aimPoint.trim();
        System.out.println(aimPoint);
        Player selectedPlayer = playerRepository.findByPlayerNumber(target.substring(2, 7));
        Player selectedPlayer2 = playerRepository.findByPlayerNumber(target.substring(7, 12));
        List<String> shipList = playerRepository.findAllCoOrdsByPlayerName(selectedPlayer.getName());
        String converted = String.join("", shipList);
        System.out.println(converted);
        System.out.println(selectedPlayer.getId());
        if (converted.contains(aimPoint)) {
            webSocketMessageSender.sendMessage("/topic/gameInfo", new Chat(selectedPlayer2.getName() + " Hit!"));
            webSocketMessageSender.sendMessage("/topic/turn", new Hidden(selectedPlayer.getName()));
            webSocketMessageSender.sendMessage("/topic/enemyDamage", new Chat(aimPoint + selectedPlayer.getName()));
            Long shipID = shipRepository.findShipIdsByPlayerAndCoOrdsContainingPair(selectedPlayer.getId(), aimPoint);
            System.out.println();
            Optional<Ship> shipToUpdate = shipRepository.findById(shipID);
            Ship ship = shipToUpdate.get();
            String shipHealth = ship.getCoOrds();
            String newShipHealth = shipHealth.replace(aimPoint, "XX");
            ship.setCoOrds(newShipHealth);
            shipRepository.save(ship);
            enumerateShips(selectedPlayer.getId());
        } else {
            webSocketMessageSender.sendMessage("/topic/gameInfo", new Chat(selectedPlayer2.getName() + " Missed!"));
            webSocketMessageSender.sendMessage("/topic/turn", new Hidden(selectedPlayer.getName()));
            webSocketMessageSender.sendMessage("/topic/miss", new Hidden(selectedPlayer.getName() + aimPoint));
        }
    }
    @Transactional
    public void enumerateShips(Long id) {
        Player playerToCheck = playerRepository.findPlayerById(id);
        boolean allShipsDestroyed = true;
        Ship ship = new Ship();
        List<Ship> shipToModify = shipRepository.findAllShipsByPlayerId(id);
        for (Ship shipToCheck : shipToModify)
            if (Objects.equals(shipToCheck.getShipDamage(), "XXXXXXXXXX")) {
                shipToCheck.setShipDamage("Destroyed");
                shipRepository.save(shipToCheck);
                webSocketMessageSender.sendMessage("/topic/chat", new Chat(playerToCheck.getName() + ": You destroyed my Carrier!"));
            } else if (Objects.equals(shipToCheck.getShipDamage(), "XXXXXXXX")) {
                shipToCheck.setShipDamage("Destroyed");
                shipRepository.save(shipToCheck);
                webSocketMessageSender.sendMessage("/topic/chat", new Chat(playerToCheck.getName() + ": You destroyed my Battleship!"));
            } else if (Objects.equals(shipToCheck.getShipDamage(), "XXXXXX")) {
                shipToCheck.setShipDamage("Destroyed");
                shipRepository.save(shipToCheck);
                webSocketMessageSender.sendMessage("/topic/chat", new Chat(playerToCheck.getName() + ": You destroyed my Cruiser!"));
            } else if (Objects.equals(shipToCheck.getShipDamage(), "XXXX")) {
                shipToCheck.setShipDamage("Destroyed");
                shipRepository.save(shipToCheck);
                webSocketMessageSender.sendMessage("/topic/chat", new Chat(playerToCheck.getName() + ": You destroyed my Destroyer!"));
            }
        for (Ship shipToCheck : shipToModify)
            if (!Objects.equals(shipToCheck.getShipDamage(), "Destroyed")) {
                allShipsDestroyed = false;}

        if (allShipsDestroyed) {
            webSocketMessageSender.sendMessage("/topic/chat", new Chat(playerToCheck.getName() + " has had all their starships destroyed! And is defeated!"));
                    Room roomToCheck = new Room();
                    Room roomId = roomRepository.findRoomIdByPlayersId(playerToCheck.getId());
                    System.out.println(roomId);
                    List<Player> players = playerRepository.findPlayersByRoomId(roomId.getId());
                    System.out.println(players);
                    for (Player winner : players) {
                        if (!Objects.equals(winner.getName(), playerToCheck.getName())) {
                            winner.setLevel(winner.levelUp(1));
                            playerRepository.save(winner);
                            webSocketMessageSender.sendMessage("/topic/chat", new Chat(winner.getName() + " is the Winner!"));
                        }
                    }
                }
            }
    }

