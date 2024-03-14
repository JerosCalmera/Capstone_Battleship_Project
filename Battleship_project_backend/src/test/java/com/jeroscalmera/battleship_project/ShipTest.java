package com.jeroscalmera.battleship_project;

import com.jeroscalmera.battleship_project.gameLogic.Placing;
import com.jeroscalmera.battleship_project.repositories.PlayerRepository;
import com.jeroscalmera.battleship_project.repositories.RoomRepository;
import com.jeroscalmera.battleship_project.repositories.ShipRepository;
import com.jeroscalmera.battleship_project.websocket.WebSocketMessageSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ShipTest {
    @Autowired
    PlayerRepository playerRepository;
    @Autowired
    ShipRepository shipRepository;

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    WebSocketMessageSender webSocketMessageSender;

    @Test
    public void contextLoads() {
    }
//
//    @Test
//    public void playerHasStats() {
//        Player player1 = new Player("Jack");
//        Player player2 = new Player("James");
//        assertEquals("Jack", player1.getName());
//        assertEquals("James", player2.getName());
//        assertEquals(1, player1.getLevel());
//        assertEquals(10, player2.getLevel());
//    }

    @Test
    public void playerTests() {
//        shipRepository.deleteAll();
//        playerRepository.deleteAll();
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
//        cruiser.setDamage("F7");
//        cruiser.setDamage("F8");
//        cruiser.setDamage("F9");
//        shipRepository.save(cruiser);
//        Ship destroyer = new Ship(player1, "destroyer", 4, "");
//        destroyer.setDamage("A8");
//        destroyer.setDamage("B8");
//        shipRepository.save(destroyer);
    }

//    @Test
//    public void randomCoOrd() {
//        Placing placing = new Placing(playerRepository, shipRepository, roomRepository, webSocketMessageSender);
//    placing.computerPlaceShips();
//    }
}
