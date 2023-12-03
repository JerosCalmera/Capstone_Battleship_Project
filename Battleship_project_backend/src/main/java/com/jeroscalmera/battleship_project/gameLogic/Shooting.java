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
    private List<String> used = new ArrayList<>();
    private String computerHit;
    public boolean allShipsDestroyedForAutoShoot = false;

    private Boolean computerHitCheck;
    private Placing placing;

    public Shooting(PlayerRepository playerRepository, ShipRepository shipRepository, WebSocketMessageSender webSocketMessageSender, RoomRepository roomRepository, Placing placing) {
        this.playerRepository = playerRepository;
        this.shipRepository = shipRepository;
        this.webSocketMessageSender = webSocketMessageSender;
        this.roomRepository = roomRepository;
        this.placing = placing;
    }

    public void computerCheck(String string) throws InterruptedException {
        System.out.println("Checking: " + string);
        Player playerToCheck;
        playerToCheck = playerRepository.findByNameContaining(string.substring(1,5));
        if (Objects.equals(playerToCheck.getPlayerType(), "Computer")) {
            System.out.println("Computer Player Action");
            computerShoot();
            }
    }
    public void shootAtShip(String input) throws InterruptedException {
        String target = input.trim();
        System.out.println("shooting target: " + target);
        String aimPoint = target.substring(0, 2);
        aimPoint = aimPoint.trim();
        System.out.println("Target coOrd: " + aimPoint);
        Player selectedPlayer = playerRepository.findByNameContaining(target.substring(2, 6));
        System.out.println("Player 1 :" + (target.substring(2, 6)));
        Player selectedPlayer2 = playerRepository.findByNameContaining(target.substring(6, 10));
        System.out.println("Player 2 :" +(target.substring(6, 10)));
        List<String> shipList = playerRepository.findAllCoOrdsByPlayerName(selectedPlayer.getName());
        String converted = String.join("", shipList);
        System.out.println(converted);
        System.out.println(selectedPlayer.getId());
        if (converted.contains(aimPoint)) {
            if (Objects.equals(selectedPlayer2.getPlayerType(), "Human")){
            webSocketMessageSender.sendMessage("/topic/gameInfo", new Chat(selectedPlayer2.getName() + " Hit!"));}
            else {
                webSocketMessageSender.sendMessage("/topic/gameInfo", new Chat("Computer Hit!"));
            }
            webSocketMessageSender.sendMessage("/topic/turn", new Hidden(selectedPlayer.getName()));
            if (Objects.equals(selectedPlayer2.getPlayerType(), "Computer")){
                computerHit = aimPoint;
                computerHitCheck = true;}
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
            computerCheck(selectedPlayer.getName());
        } else {
            if (Objects.equals(selectedPlayer2.getPlayerType(), "Human")){
                webSocketMessageSender.sendMessage("/topic/gameInfo", new Chat(selectedPlayer2.getName() + " Miss!"));}
            else {
                webSocketMessageSender.sendMessage("/topic/gameInfo", new Chat("Computer Miss!"));
            }
            webSocketMessageSender.sendMessage("/topic/turn", new Hidden(selectedPlayer.getName()));
            webSocketMessageSender.sendMessage("/topic/miss", new Hidden(selectedPlayer.getName() + aimPoint));
            computerCheck(selectedPlayer.getName());
        }
    }

    @Transactional
    public void enumerateShips(Long id) throws InterruptedException {
        boolean allShipsDestroyed = true;
        Player playerToCheck = playerRepository.findPlayerById(id);
        String playerName = playerToCheck.getName();
        if (Objects.equals(playerToCheck.getPlayerType(), "Computer")){
            playerName = "Computer";
        }
        Ship ship = new Ship();
        List<Ship> shipToModify = shipRepository.findAllShipsByPlayerId(id);
        for (Ship shipToCheck : shipToModify)
            if (Objects.equals(shipToCheck.getShipDamage(), "XXXXXXXXXX")) {
                shipToCheck.setShipDamage("Destroyed");
                shipRepository.save(shipToCheck);
                webSocketMessageSender.sendMessage("/topic/chat", new Chat(playerName + ": You destroyed my Carrier!"));
            } else if (Objects.equals(shipToCheck.getShipDamage(), "XXXXXXXX")) {
                shipToCheck.setShipDamage("Destroyed");
                shipRepository.save(shipToCheck);
                webSocketMessageSender.sendMessage("/topic/chat", new Chat(playerName + ": You destroyed my Battleship!"));
            } else if (Objects.equals(shipToCheck.getShipDamage(), "XXXXXX")) {
                shipToCheck.setShipDamage("Destroyed");
                shipRepository.save(shipToCheck);
                webSocketMessageSender.sendMessage("/topic/chat", new Chat(playerName + ": You destroyed my Cruiser!"));
            } else if (Objects.equals(shipToCheck.getShipDamage(), "XXXX")) {
                shipToCheck.setShipDamage("Destroyed");
                shipRepository.save(shipToCheck);
                webSocketMessageSender.sendMessage("/topic/chat", new Chat(playerName + ": You destroyed my Destroyer!"));
            }
        for (Ship shipToCheck : shipToModify)
            if (!Objects.equals(shipToCheck.getShipDamage(), "Destroyed")) {
                allShipsDestroyed = false;
            }

        if (allShipsDestroyed) {
            allShipsDestroyedForAutoShoot = true;
            used.clear();
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
                    Thread.sleep(50);
                    webSocketMessageSender.sendMessage("/topic/gameInfo", new Chat(winner.getName() + " Wins!"));
                }
            }
        }
        allShipsDestroyedForAutoShoot = false;
    }

    public String computerRandomCoOrd() {
        computerHitCheck = false;
        Random random = new Random();
        int randomIndex = random.nextInt(10);
        coOrdLetters = Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J");
        coOrdNumbers = Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
        String startLetter = coOrdLetters.get(randomIndex);
        randomIndex = random.nextInt(10);
        String startNumber = coOrdNumbers.get(randomIndex);
        return startLetter + startNumber;
    }
    public void autoShoot() throws InterruptedException {
        List<Room> playersInRoom = roomRepository.findAllWithPlayers();
        List<Player> players = new ArrayList<>();
        for (Room room : playersInRoom) {
            players = room.getPlayers();
        }
        System.out.println("autoshoot1:" + computerRandomCoOrd() + players.get(0).getName());
        System.out.println("autoshoot2:" + computerRandomCoOrd() + players.get(1).getName());
        while (!allShipsDestroyedForAutoShoot) {
            String shoot = computerRandomCoOrd();
            while (used.contains(shoot)) {
                shoot = computerRandomCoOrd();
            }
            used.add(shoot);
            shootAtShip(shoot + players.get(1).getName().substring(0, 4) + players.get(0).getName().substring(0, 4));
            Thread.sleep(50);
            shootAtShip(shoot + players.get(0).getName().substring(0, 4) + players.get(1).getName().substring(0, 4));

        }
        allShipsDestroyedForAutoShoot = false;
    }

    public void computerShoot() throws InterruptedException {
        Thread.sleep(1000);
        List<Room> playersInRoom = roomRepository.findAllWithPlayers();
        List<Player> players = new ArrayList<>();
        for (Room room : playersInRoom) {
            players = room.getPlayers();
        }
        String humanPlayer;
        String computerPlayer;
        if (Objects.equals(players.get(0).getPlayerType(), "Computer")) {
            humanPlayer = players.get(1).getName();
            computerPlayer = players.get(0).getName();}
        else
            humanPlayer = players.get(0).getName();
            computerPlayer = players.get(1).getName();
        System.out.println("human: " + humanPlayer);
        System.out.println("Computer: " + computerPlayer);
        String shoot = computerRandomCoOrd();
            if (computerHitCheck){
            shoot = (placing.generateStartingRandomCoOrds(computerHit, true));}
            System.out.println("computer shoot after hitting: " + shoot );
            while (used.contains(shoot)) {
                if (computerHitCheck){
                    shoot = (placing.generateStartingRandomCoOrds(computerHit, true));
                    System.out.println("computer shoot after hitting existing: " + shoot );}
                else {
            shoot = computerRandomCoOrd();}
                System.out.println("computer shoot after NOT hitting: " + shoot);
            }
            used.add(shoot);
            shootAtShip(shoot + humanPlayer.substring(0, 4) + computerPlayer.substring(0, 4));
        }

        public void computerSelectSecondTarget(String firstTarget) {
            Random random = new Random();
            int randomCoOrd = random.nextInt(100);
            String firstCoOrd = placing.computerAllCoOrds.get(randomCoOrd);
            int rando = random.nextInt(3);
            int secondCoOrdIndexLetter = 0;
            int secondCoOrdIndexNumber = 0;
            do {
                randomCoOrd = random.nextInt(100);
                firstCoOrd = placing.computerAllCoOrds.get(randomCoOrd);
                placing.firstCoOrdIndexLetter = coOrdLetters.indexOf(String.valueOf(firstCoOrd.charAt(0)));
                placing.firstCoOrdIndexNumber = coOrdNumbers.indexOf(String.valueOf(firstCoOrd.charAt(1)));
            } while (firstCoOrdIndexLetter == 0 || firstCoOrdIndexLetter == 9 || firstCoOrdIndexNumber == 0 || firstCoOrdIndexNumber == 9);
            if (rando == 0) {
                secondCoOrdIndexLetter = firstCoOrdIndexLetter;
                secondCoOrdIndexNumber = firstCoOrdIndexNumber + 1;
            } else if (rando == 1) {
                secondCoOrdIndexLetter = firstCoOrdIndexLetter;
                secondCoOrdIndexNumber = firstCoOrdIndexNumber - 1;
            } else if (rando == 2) {
                secondCoOrdIndexLetter = firstCoOrdIndexLetter + 1;
                secondCoOrdIndexNumber = firstCoOrdIndexNumber;
            } else {
                secondCoOrdIndexLetter = firstCoOrdIndexLetter - 1;
                secondCoOrdIndexNumber = firstCoOrdIndexNumber;
            }
            String secondCoOrd = coOrdLetters.get(secondCoOrdIndexLetter) + coOrdNumbers.get(secondCoOrdIndexNumber);
            return firstCoOrd + secondCoOrd;
        }
    }

