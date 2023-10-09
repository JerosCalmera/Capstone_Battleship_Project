package com.jeroscalmera.battleship_project;

import com.jeroscalmera.battleship_project.models.Player;
import com.jeroscalmera.battleship_project.models.Ship;
import com.jeroscalmera.battleship_project.repositories.PlayerRepository;
import com.jeroscalmera.battleship_project.repositories.ShipRepository;
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
    GameLogic gameLogic;
    @Autowired
    PlayerRepository playerRepository;
    @Autowired
    ShipRepository shipRepository;

    @Test
    public void contextLoads() {
    }
    @Test
    public void playerHasStats() {
        Player player1 = new Player("Jack", 1);
        Player player2 = new Player("James", 10);
        assertEquals("Jack", player1.getName());
        assertEquals("James", player2.getName());
        assertEquals( 1, player1.getLevel());
        assertEquals( 10, player2.getLevel());
    }
    @Test
    public void playerTests() {
        shipRepository.deleteAll();
        playerRepository.deleteAll();
        Player player1 = new Player("Jack", 1);
        playerRepository.save(player1);
        Ship carrier = new Ship(player1, "carrier", 12, "");
        carrier.setDamage("A1");
        carrier.setDamage("A5");
        carrier.setDamage("F1");
        carrier.setDamage("S1");
        carrier.setDamage("H6");
        carrier.setDamage("JI");
        shipRepository.save(carrier);
        gameLogic.shootAtShip("F1");
    }
}
