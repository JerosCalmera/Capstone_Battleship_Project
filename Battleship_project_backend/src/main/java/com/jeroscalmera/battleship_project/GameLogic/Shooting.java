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
import java.util.*;

@Service
public class Shooting {
    private PlayerRepository playerRepository;
    private ShipRepository shipRepository;
    private WebSocketMessageSender webSocketMessageSender;
    private RoomRepository roomRepository;
    private List<String> coOrdLetters = new ArrayList<>();
    private List<String> coOrdNumbers = new ArrayList<>();

    public boolean allShipsDestroyedForAutoShoot = false;
    Player selectedPlayer1AutoShoot = new Player();
    Player selectedPlayer2AutoShoot = new Player();

    public Shooting(RoomRepository repository, PlayerRepository playerRepository, ShipRepository shipRepository, WebSocketMessageSender webSocketMessageSender) {
        this.roomRepository = repository;
        this.playerRepository = playerRepository;
        this.shipRepository = shipRepository;
        this.webSocketMessageSender = webSocketMessageSender;
    }

    public void shootAtShip(String input) throws InterruptedException {
        String target = input.trim();
        System.out.println("shooting target: " + target);
        System.out.println((target.substring(2, 6)));
        String aimPoint = target.substring(0, 2);
        aimPoint = aimPoint.trim();
        System.out.println(aimPoint);
        Player selectedPlayer = playerRepository.findByNameContaining(target.substring(2, 6));
        System.out.println((target.substring(2, 6)));
        Player selectedPlayer2 = playerRepository.findByNameContaining(target.substring(6, 10));
        System.out.println((target.substring(6, 10)));
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
    public void enumerateShips(Long id) throws InterruptedException {
        boolean allShipsDestroyed = true;
        Player playerToCheck = playerRepository.findPlayerById(id);
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
            allShipsDestroyedForAutoShoot = true;
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
    public synchronized void autoShoot() throws InterruptedException {
        List<String> used = new ArrayList<>();
        List<Room> playersInRoom = roomRepository.findAllWithPlayers();
        List<Player> players = new ArrayList<>();
        for (Room room : playersInRoom) {
            players = room.getPlayers();
        }
        System.out.println("autoshoot1:" + computerRandomCoOrd() + players.get(0).getName());
        System.out.println("autoshoot2:" + computerRandomCoOrd() + players.get(1).getName());
        while (!allShipsDestroyedForAutoShoot){
            String shoot = computerRandomCoOrd();
            while (used.contains(shoot)){
                shoot = computerRandomCoOrd();
            }
            used.add(shoot);
            shootAtShip(shoot + players.get(1).getName().substring(0,4) + players.get(0).getName().substring(0,4));
        Thread.sleep(50);
        shootAtShip(shoot + players.get(0).getName().substring(0,4) + players.get(1).getName().substring(0,4));

        }
        used.clear();
        allShipsDestroyedForAutoShoot = false;
    }
    }

