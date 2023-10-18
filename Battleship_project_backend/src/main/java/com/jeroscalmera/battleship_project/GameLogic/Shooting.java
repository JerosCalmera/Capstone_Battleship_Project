package com.jeroscalmera.battleship_project.GameLogic;

import com.jeroscalmera.battleship_project.models.Player;
import com.jeroscalmera.battleship_project.models.Ship;
import com.jeroscalmera.battleship_project.repositories.PlayerRepository;
import com.jeroscalmera.battleship_project.repositories.ShipRepository;
import com.jeroscalmera.battleship_project.websocket.Chat;
import com.jeroscalmera.battleship_project.websocket.Hidden;
import com.jeroscalmera.battleship_project.websocket.WebSocketMessageSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class Shooting {
    private PlayerRepository playerRepository;
    private ShipRepository shipRepository;
    private WebSocketMessageSender webSocketMessageSender;

    public Shooting(PlayerRepository playerRepository, ShipRepository shipRepository, WebSocketMessageSender webSocketMessageSender) {
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
        Player selectedPlayer = playerRepository.findByNameContaining(target.substring(2, 6));
        Player selectedPlayer2 = playerRepository.findByNameContaining(target.substring(6, 10));
        List<String> shipList = playerRepository.findAllCoOrdsByPlayerName(selectedPlayer.getName());
        String converted = String.join("", shipList);
        System.out.println(converted);
        System.out.println(selectedPlayer.getId());
        if (converted.contains(aimPoint)) {
            webSocketMessageSender.sendMessage("/topic/gameInfo", new Chat(selectedPlayer2.getName() + " Hit!"));
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
            webSocketMessageSender.sendMessage("/topic/miss", new Hidden(selectedPlayer.getName() + aimPoint));
        }
        webSocketMessageSender.sendMessage("/topic/turn", new Hidden(selectedPlayer.getName()));
    }

    public void enumerateShips(Long id) {
        Player playerToCheck = playerRepository.findPlayerById(id);
        boolean allShipsDestroyed = true;
        Ship ship = new Ship();
        List<Ship> shipToModify = shipRepository.findAllShipsByPlayerId(id);
        for (Ship shipToCheck : shipToModify)
            if (!shipToCheck.getShipDamage().matches("^X+$")) {
                allShipsDestroyed = false;
            } else if (Objects.equals(shipToCheck.getShipDamage(), "XXXX")) {
                webSocketMessageSender.sendMessage("/topic/chat", new Chat(playerToCheck.getName() + ": You destroyed my Destroyer!"));
            } else if (Objects.equals(shipToCheck.getShipDamage(), "XXXXXX")) {
                webSocketMessageSender.sendMessage("/topic/chat", new Chat(playerToCheck.getName() + ": You destroyed my Cruiser!"));
            } else if (Objects.equals(shipToCheck.getShipDamage(), "XXXXXXXX")) {
                webSocketMessageSender.sendMessage("/topic/chat", new Chat(playerToCheck.getName() + ": You destroyed my Battleship!"));
            } else if (Objects.equals(shipToCheck.getShipDamage(), "XXXXXXXXXX")) {
                webSocketMessageSender.sendMessage("/topic/chat", new Chat(playerToCheck.getName() + ": You destroyed my Carrier!"));
            }
        if (allShipsDestroyed) {
            webSocketMessageSender.sendMessage("/topic/chat", new Chat(playerToCheck.getName() + " has had all their starships destroyed! And is defeated!"));
        }
    }
}
