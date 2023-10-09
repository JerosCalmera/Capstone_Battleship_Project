package com.jeroscalmera.battleship_project;

import com.jeroscalmera.battleship_project.models.Ship;
import com.jeroscalmera.battleship_project.repositories.PlayerRepository;
import com.jeroscalmera.battleship_project.repositories.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GameLogic {
    @Autowired
    PlayerRepository playerRepository;
    @Autowired
    ShipRepository shipRepository;

    public void shootAtShip(String target) {
        if (shipRepository.findShipIdsByCoOrdsContainingPair(target) != null) {
            System.out.println("Hit!");
            Long shipID = shipRepository.findShipIdsByCoOrdsContainingPair(target);
            Optional<Ship> shipToUpdate = shipRepository.findById(shipID);
            Ship ship = shipToUpdate.get();
            String shipHealth = ship.getCoOrds();
            String newShipHealth = shipHealth.replace(target, "");
            ship.setCoOrds(newShipHealth);
            shipRepository.save(ship);
        } else {
            System.out.println("Miss!");
        }
    }
}