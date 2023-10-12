package com.jeroscalmera.battleship_project.controllers;

import com.jeroscalmera.battleship_project.models.Player;
import com.jeroscalmera.battleship_project.models.Ship;
import com.jeroscalmera.battleship_project.repositories.PlayerRepository;
import com.jeroscalmera.battleship_project.repositories.RoomRepository;
import com.jeroscalmera.battleship_project.repositories.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("!test")
@Component
public class DataLoader implements ApplicationRunner {
    @Autowired
    PlayerRepository playerRepository;
    @Autowired
    ShipRepository shipRepository;
    @Autowired
    RoomRepository roomRepository;
    public DataLoader(){

    }
    public void run(ApplicationArguments args){
//        shipRepository.deleteAll();
//        playerRepository.deleteAll();
//        roomRepository.deleteAll();
//        Player player1 = new Player("Jack");
//        playerRepository.save(player1);
//        Ship carrier = new Ship(player1, "carrier", 12, "");
//        carrier.setDamage("B2");
//        carrier.setDamage("C2");
//        carrier.setDamage("D2");
//        carrier.setDamage("E2");
//        carrier.setDamage("F2");
//        carrier.setDamage("G2");
//        shipRepository.save(carrier);
//        Ship battleship = new Ship(player1, "battleship", 10, "");
//        battleship.setDamage("J3");
//        battleship.setDamage("J4");
//        battleship.setDamage("J5");
//        battleship.setDamage("J6");
//        battleship.setDamage("J7");
//        shipRepository.save(battleship);
//        Ship cruiser = new Ship(player1, "cruiser", 6, "");
//        cruiser.setDamage("F6");
//        cruiser.setDamage("F7");
//        cruiser.setDamage("F8");
//        shipRepository.save(cruiser);
//        Ship cruiser2 = new Ship(player1, "cruiser", 6, "");
//        cruiser2.setDamage("B5");
//        cruiser2.setDamage("C5");
//        cruiser2.setDamage("D5");
//        shipRepository.save(cruiser2);
//        Ship destroyer = new Ship(player1, "destroyer", 4, "");
//        destroyer.setDamage("A8");
//        destroyer.setDamage("B8");
//        shipRepository.save(destroyer);
//        Ship destroyer2 = new Ship(player1, "destroyer", 4, "");
//        destroyer2.setDamage("I9");
//        destroyer2.setDamage("J9");
//        shipRepository.save(destroyer2);

    }
}
