package com.jeroscalmera.battleship_project;

import com.jeroscalmera.battleship_project.models.Ship;
import com.jeroscalmera.battleship_project.repositories.PlayerRepository;
import com.jeroscalmera.battleship_project.repositories.ShipRepository;
import com.jeroscalmera.battleship_project.websocket.GameData;
import com.jeroscalmera.battleship_project.websocket.Greeting;
import com.jeroscalmera.battleship_project.websocket.WebSocketMessageSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GameLogic {
    private PlayerRepository playerRepository;
    private ShipRepository shipRepository;
    private WebSocketMessageSender webSocketMessageSender;

    public GameLogic(PlayerRepository playerRepository, ShipRepository shipRepository, WebSocketMessageSender webSocketMessageSender) {
        this.playerRepository = playerRepository;
        this.shipRepository = shipRepository;
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
    public void shootAtShip(String target) {
        if (shipRepository.findShipIdsByCoOrdsContainingPair(target) != null) {
            webSocketMessageSender.sendMessage("/topic/connect", new Greeting("Hit!"));
            Long shipID = shipRepository.findShipIdsByCoOrdsContainingPair(target);
            Optional<Ship> shipToUpdate = shipRepository.findById(shipID);
            Ship ship = shipToUpdate.get();
            String shipHealth = ship.getCoOrds();
            String newShipHealth = shipHealth.replace(target, "");
            ship.setCoOrds(newShipHealth);
            shipRepository.save(ship);
        } else {
            webSocketMessageSender.sendMessage("/topic/connect", new Greeting("Miss!"));
        }
    }
}