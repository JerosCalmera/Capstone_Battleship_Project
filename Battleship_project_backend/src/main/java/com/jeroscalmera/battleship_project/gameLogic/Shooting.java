package com.jeroscalmera.battleship_project.gameLogic;

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
import java.util.*;

@Service
public class Shooting {
    private PlayerRepository playerRepository;
    private ShipRepository shipRepository;
    private WebSocketMessageSender webSocketMessageSender;
    private RoomRepository roomRepository;
    private List<String> coOrdLetters = new ArrayList<>();
    private List<String> coOrdNumbers = new ArrayList<>();
    private Placing placing;

    public Shooting(PlayerRepository playerRepository, ShipRepository shipRepository, WebSocketMessageSender webSocketMessageSender, RoomRepository roomRepository, Placing placing) {
        this.playerRepository = playerRepository;
        this.shipRepository = shipRepository;
        this.webSocketMessageSender = webSocketMessageSender;
        this.roomRepository = roomRepository;
        this.placing = placing;
    }

    public void computerCheck(String string) throws InterruptedException {
        System.out.println("Checking if: " + string + " is a human or computer player");
        Player playerToCheck;
        playerToCheck = playerRepository.findByNameContaining(string.substring(1, 5));
        System.out.println(playerToCheck.getName());
        if (playerToCheck.isComputer()) {
            computerShoot(playerToCheck.getName());
            System.out.println("Computer: " + playerToCheck.getName() + " is shooting");
        }
    }

    public void shootAtShip(String input) throws InterruptedException {
        String target = input.trim();
        String aimPoint = target.substring(0, 2);
        aimPoint = aimPoint.trim();
        System.out.println("Targeting data : " + target);
        Player selectedPlayer = playerRepository.findByNameContaining(target.substring(2, 6));
        System.out.println("The target is : " + selectedPlayer.getName());
        Player selectedPlayer2 = playerRepository.findByNameContaining(target.substring(6, 10));
        System.out.println("The shooter is : " + selectedPlayer2.getName());
        System.out.println(selectedPlayer2.getName() + " shooting at " + selectedPlayer.getName());
        List<String> shipList = playerRepository.findAllCoOrdsByPlayerName(selectedPlayer.getName());
        String converted = String.join("", shipList);
        if (converted.contains(aimPoint)) {
            if (!selectedPlayer2.isComputer()) {
                webSocketMessageSender.sendMessage("/topic/gameInfo", new Chat(selectedPlayer.getRoom().getRoomNumber() + selectedPlayer2.getName() + " Hit!"));
            } else {
                webSocketMessageSender.sendMessage("/topic/gameInfo", new Chat(selectedPlayer.getRoom().getRoomNumber() + "Computer Hit!"));
                selectedPlayer2.setAiConfirmedHit(aimPoint);
                selectedPlayer2.setAiHitCheck(true);
                playerRepository.save(selectedPlayer2);
            }
            webSocketMessageSender.sendMessage("/topic/turn", new Hidden(selectedPlayer.getRoom().getRoomNumber() + selectedPlayer.getName()));
            webSocketMessageSender.sendMessage("/topic/enemyDamage", new Chat(selectedPlayer.getRoom().getRoomNumber() + aimPoint + selectedPlayer.getName()));
            Long shipID = shipRepository.findShipIdsByPlayerAndCoOrdsContainingPair(selectedPlayer.getId(), aimPoint);
            Optional<Ship> shipToUpdate = shipRepository.findById(shipID);
            Ship ship = shipToUpdate.get();
            String shipHealth = ship.getCoOrds();
            String newShipHealth = shipHealth.replace(aimPoint, "XX");
            ship.setCoOrds(newShipHealth);
            shipRepository.save(ship);
            enumerateShips(selectedPlayer.getId());
            computerCheck(selectedPlayer.getName());
        } else {
            if (!selectedPlayer2.isComputer()) {
                webSocketMessageSender.sendMessage("/topic/gameInfo", new Chat(selectedPlayer.getRoom().getRoomNumber() + selectedPlayer2.getName() + " Miss!"));
            } else {
                webSocketMessageSender.sendMessage("/topic/gameInfo", new Chat(selectedPlayer.getRoom().getRoomNumber() + "Computer Miss!"));
                selectedPlayer2.setAiConfirmedHit("");
                selectedPlayer2.setAiHitCheck(false);
                playerRepository.save(selectedPlayer2);
            }
            webSocketMessageSender.sendMessage("/topic/turn", new Hidden(selectedPlayer.getRoom().getRoomNumber() + selectedPlayer.getName()));
            webSocketMessageSender.sendMessage("/topic/miss", new Hidden(selectedPlayer.getRoom().getRoomNumber() + selectedPlayer.getName() + aimPoint));
            computerCheck(selectedPlayer.getName());
        }
    }

    @Transactional
    public void enumerateShips(Long id) throws InterruptedException {
        boolean allShipsDestroyed = true;
        Player playerToCheck = playerRepository.findPlayerById(id);
        String playerName = playerToCheck.getName();
        if (playerToCheck.isComputer()) {
            playerName = "Computer";
        }
        Ship ship = new Ship();
        List<Ship> shipToModify = shipRepository.findAllShipsByPlayerId(id);
        for (Ship shipToCheck : shipToModify)
            if (Objects.equals(shipToCheck.getShipDamage(), "XXXXXXXXXX")) {
                shipToCheck.setShipDamage("Destroyed");
                shipRepository.save(shipToCheck);
                webSocketMessageSender.sendMessage("/topic/chat", new Chat(playerToCheck.getRoom().getRoomNumber() + playerName + ": You destroyed my Carrier!"));
                if (!playerName.contains("Computer")) {
                    playerToCheck.setAiHitCheck(false);
                    playerRepository.save(playerToCheck);
                }
            } else if (Objects.equals(shipToCheck.getShipDamage(), "XXXXXXXX")) {
                shipToCheck.setShipDamage("Destroyed");
                shipRepository.save(shipToCheck);
                webSocketMessageSender.sendMessage("/topic/chat", new Chat(playerToCheck.getRoom().getRoomNumber() + playerName + ": You destroyed my Battleship!"));
                if (!playerName.contains("Computer")) {
                    playerToCheck.setAiHitCheck(false);
                    playerRepository.save(playerToCheck);
                }
            } else if (Objects.equals(shipToCheck.getShipDamage(), "XXXXXX")) {
                shipToCheck.setShipDamage("Destroyed");
                shipRepository.save(shipToCheck);
                webSocketMessageSender.sendMessage("/topic/chat", new Chat(playerToCheck.getRoom().getRoomNumber() + playerName + ": You destroyed my Cruiser!"));
                if (!playerName.contains("Computer")) {
                    playerToCheck.setAiHitCheck(false);
                    playerRepository.save(playerToCheck);
                }
            } else if (Objects.equals(shipToCheck.getShipDamage(), "XXXX")) {
                shipToCheck.setShipDamage("Destroyed");
                shipRepository.save(shipToCheck);
                webSocketMessageSender.sendMessage("/topic/chat", new Chat(playerToCheck.getRoom().getRoomNumber() + playerName + ": You destroyed my Destroyer!"));
                if (!playerName.contains("Computer")) {
                    playerToCheck.setAiHitCheck(false);
                    playerRepository.save(playerToCheck);
                }
            }
        for (Ship shipToCheck : shipToModify)
            if (!Objects.equals(shipToCheck.getShipDamage(), "Destroyed")) {
                allShipsDestroyed = false;
            }

        if (allShipsDestroyed) {
            webSocketMessageSender.sendMessage("/topic/chat", new Chat(playerToCheck.getRoom().getRoomNumber() + playerToCheck.getName() + " has had all their starships destroyed! And is defeated!"));
            Room roomToCheck = new Room();
            Room roomId = roomRepository.findRoomIdByPlayersId(playerToCheck.getId());
            List<Player> players = playerRepository.findPlayersByRoomId(roomId.getId());
            for (Player winner : players) {
                if (!Objects.equals(winner.getName(), playerToCheck.getName())) {
                    winner.setLevel(winner.levelUp(1));
                    winner.setAiConfirmedHit(null);
                    winner.setAiShot(null);
                    winner.setAiHitCheck(false);
                    playerRepository.save(winner);
                    webSocketMessageSender.sendMessage("/topic/chat", new Chat(playerToCheck.getRoom().getRoomNumber() + winner.getName() + " is the Winner!"));
                    Thread.sleep(50);
                    webSocketMessageSender.sendMessage("/topic/gameInfo", new Chat(playerToCheck.getRoom().getRoomNumber() + winner.getName() + " Wins!"));
                }
            }
        }
    }

    public String computerRandomCoOrd() {
        Random random = new Random();
        int randomIndex = random.nextInt(10);
        coOrdLetters = Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J");
        coOrdNumbers = Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
        String startLetter = coOrdLetters.get(randomIndex);
        randomIndex = random.nextInt(10);
        String startNumber = coOrdNumbers.get(randomIndex);
        return startLetter + startNumber;
    }

//    public void autoShoot() throws InterruptedException { // for presentations
//        List<Room> playersInRoom = roomRepository.findAllWithPlayers();
//        List<Player> players = new ArrayList<>();
//        for (Room room : playersInRoom) {
//            players = room.getPlayers();
//        }
//        while (!allShipsDestroyedForAutoShoot) {
//            String shoot = computerRandomCoOrd();
//            while (used.contains(shoot)) {
//                shoot = computerRandomCoOrd();
//            }
//            used.add(shoot);
//            shootAtShip(shoot + players.get(1).getName().substring(0, 4) + players.get(0).getName().substring(0, 4));
//            Thread.sleep(50);
//            shootAtShip(shoot + players.get(0).getName().substring(0, 4) + players.get(1).getName().substring(0, 4));
//
//        }
//        allShipsDestroyedForAutoShoot = false;
//    }

    @Transactional
    public void computerShoot(String playerName) throws InterruptedException {
        Thread.sleep(1000);
        List<Room> playersInRoom = roomRepository.findByPlayersName(playerName);
        List<Player> players = new ArrayList<>();
        for (Room room : playersInRoom) {
            players = room.getPlayers();
        }
        Player humanPlayer;
        Player computerPlayer;
        if (players.get(0).isComputer()) {
            humanPlayer = players.get(1);
            computerPlayer = players.get(0);
            System.out.println("Human is : " + players.get(1).getName());
            System.out.println("Computer is : " + players.get(0).getName());
        } else {
            humanPlayer = players.get(0);
            computerPlayer = players.get(1);
        System.out.println("Human is : " + players.get(0).getName());
        System.out.println("Computer is : " + players.get(1).getName());}

        if (computerPlayer.getAiShot() == null) {
            computerPlayer.setAiShot("");
        }

        String shoot = computerRandomCoOrd();
        if (computerPlayer.getAiHitCheck()) {
            shoot = (placing.generateStartingRandomCoOrds(computerPlayer.getAiConfirmedHit(), true));
            System.out.println("hit ship at: " + computerPlayer.getAiConfirmedHit() + " , now shooting: " + shoot);}

        while (computerPlayer.getAiShot().contains(shoot)) {
            if (computerPlayer.getAiHitCheck()) {
                shoot = (placing.generateStartingRandomCoOrds(computerPlayer.getAiConfirmedHit(), true));
                System.out.println("hit ship at: " + computerPlayer.getAiConfirmedHit() + " , now shooting: " + shoot);
            } else {
                shoot = computerRandomCoOrd();
            }

            if (!computerPlayer.getAiShot().contains(shoot)) {;
                break;
            }
        }
        computerPlayer.setAiShot(computerPlayer.getAiShot() + shoot);
        System.out.println("Shot: = " + computerPlayer.getAiShot());
        playerRepository.save(computerPlayer);
        shootAtShip(shoot + humanPlayer.getName().substring(0, 4) + computerPlayer.getName().substring(0, 4));
    }
}