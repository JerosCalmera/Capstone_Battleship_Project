package com.jeroscalmera.battleship_project.gameLogic;

import com.jeroscalmera.battleship_project.models.Player;
import com.jeroscalmera.battleship_project.models.Ship;
import com.jeroscalmera.battleship_project.repositories.PlayerRepository;
import com.jeroscalmera.battleship_project.repositories.RoomRepository;
import com.jeroscalmera.battleship_project.repositories.ShipRepository;
import com.jeroscalmera.battleship_project.websocket.Chat;
import com.jeroscalmera.battleship_project.websocket.WebSocketMessageSender;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class Placing {
    private PlayerRepository playerRepository;
    private ShipRepository shipRepository;
    private RoomRepository roomRepository;
    private WebSocketMessageSender webSocketMessageSender;

    public List<String> computerAllCoOrds = new ArrayList<>();
    public List<String> coOrdLetters = new ArrayList<>();
    public List<String> coOrdNumbers = new ArrayList<>();

    public Placing(PlayerRepository playerRepository, ShipRepository shipRepository, RoomRepository roomRepository, WebSocketMessageSender webSocketMessageSender) {
        this.playerRepository = playerRepository;
        this.shipRepository = shipRepository;
        this.roomRepository = roomRepository;
        this.webSocketMessageSender = webSocketMessageSender;
    }

    private List<String> coOrds = new ArrayList<>();
    private String existingCoOrds;
    private String damage = "";
    boolean horizontalPlacement = false;
    boolean verticalPlacement = false;
    boolean invalidPlacement = false;

    public void restart() {
        List<Player> playerList = playerRepository.findAll();
        for (Player player : playerList) {
            if (Objects.equals(player.getPlayerType(), "Human")) {
                player.setRoom(null);
                playerRepository.save(player);
            } else if (Objects.equals(player.getPlayerType(), "Computer")) {
                playerRepository.delete(player);
            }
        }
    }

    public synchronized void placeShip(String target) throws InterruptedException {
        Thread.sleep(50);
        System.out.println("Target =" + target);
        Player selectedPlayer = playerRepository.findByNameContaining((target.substring(4, 9)));
        System.out.println("Name =" + (target.substring(4, 9)));
        List<String> shipsList = playerRepository.findAllCoOrdsByPlayerName(selectedPlayer.getName());
        if (shipsList.contains(target.substring(1, 3))) {
            invalidPlacement = true;
        }
        String shipList = String.join("", shipsList);
        System.out.println("ships:" + shipList);
        if (!coOrds.contains(target.substring(1, 3))) {
            coOrds.add(target.substring(1, 3));
            damage += target.substring(1, 3);
        }
        if (shipList.contains(target.substring(1, 3))) {
            invalidPlacement = true;
        }
        System.out.println("CoOrds list =" + coOrds);
        System.out.println((target.substring(4)));
        System.out.println("Selected player =" + selectedPlayer.getName());
        int max = Integer.parseInt(target.substring(3, 4));
        System.out.println("Ship max size =" + max);
        Ship newShip = new Ship("", 0, "");
        if (max == 5) {
            newShip = new Ship("Carrier", 10, "");
        } else if (max == 4) {
            newShip = new Ship("Battleship", 8, "");
        } else if (max == 3) {
            newShip = new Ship("Cruiser", 6, "");
        } else if (max == 2) {
            newShip = new Ship("Destroyer", 4, "");
        }
        if (coOrds.size() > 2) {
            coOrds.clear();
            damage = "";
            System.out.println("CordsCleared");
        }
        if (coOrds.size() == 2) {
            for (int i = 0; i < coOrds.size() - 1; i++) {
                int inputOne = i;
                int inputTwo = 1 + i;
                int letter = 0;
                int number = 1;
                System.out.println("inputOne letter: " + coOrds.get(inputOne).charAt(letter));
                System.out.println("inputTwo letter: " + coOrds.get(inputTwo).charAt(letter));

                if (!(Math.abs(coOrds.get(inputOne).charAt(letter) - coOrds.get(inputTwo).charAt(letter)) <= 1
                        && Math.abs(coOrds.get(inputOne).charAt(number) - coOrds.get(inputTwo).charAt(number)) <= 1)) {
                    invalidPlacement = true;
                    webSocketMessageSender.sendMessage("/topic/gameInfo", new Chat(selectedPlayer.getRoom().getRoomNumber() + selectedPlayer.getName() + " Invalid alignment selected!"));
                    coOrds.clear();
                    damage = "";
                    break;
                }
                if ((coOrds.get(inputOne).charAt(letter) != coOrds.get(inputTwo).charAt(letter) && coOrds.get(inputOne).charAt(number) != coOrds.get(inputTwo).charAt(number))) {
                    invalidPlacement = true;
                    webSocketMessageSender.sendMessage("/topic/gameInfo", new Chat(selectedPlayer.getRoom().getRoomNumber() + selectedPlayer.getName() + " Invalid alignment selected!"));
                    coOrds.clear();
                    damage = "";
                    break;
                } else if (coOrds.get(inputOne).charAt(letter) == coOrds.get(inputTwo).charAt(letter)) {
                    int numberOfLoops = max - 2;
                    int numberToAdd = Character.getNumericValue(coOrds.get(inputTwo).charAt(number));
                    char addLetter = coOrds.get(inputTwo).charAt(letter);
                    for (int j = 0; j < numberOfLoops; j++) {
                        if (coOrds.get(inputOne).charAt(number) > coOrds.get(inputTwo).charAt(number)) {
                            numberToAdd = numberToAdd - 1;
                            if (numberToAdd < 0) {
                                invalidPlacement = true;
                                break;
                            }
                            String addCoOrd = addLetter + String.valueOf(numberToAdd);
                            System.out.println(addCoOrd);
                            if (shipList.contains(addCoOrd)) {
                                invalidPlacement = true;
                            } else {
                                damage += addCoOrd;
                                System.out.println(damage);
                            }
                        } else {
                            numberToAdd = numberToAdd + 1;
                            if (numberToAdd > 9) {
                                invalidPlacement = true;
                                break;
                            }
                            String addCoOrd = addLetter + String.valueOf(numberToAdd);
                            System.out.println(addCoOrd);
                            if (shipList.contains(addCoOrd)) {
                                invalidPlacement = true;
                            } else {
                                damage += addCoOrd;
                                System.out.println(damage);
                            }
                        }
                        horizontalPlacement = true;
                    }


                } else if ((coOrds.get(inputOne).charAt(letter) != coOrds.get(inputTwo).charAt(letter))) {
                    int numberOfLoops = max - 2;
                    String letterToAdd = String.valueOf(coOrds.get(inputTwo).charAt(letter));
                    int addNumber = Character.getNumericValue(coOrds.get(inputTwo).charAt(number));
                    System.out.println("Number: " + addNumber);
                    coOrdLetters = Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J");
                    int indexFirst = coOrdLetters.indexOf(String.valueOf(coOrds.get(inputOne).charAt(letter)));
                    int indexSecond = coOrdLetters.indexOf(String.valueOf(coOrds.get(inputTwo).charAt(letter)));
                    for (int j = 0; j < numberOfLoops; j++) {
                        System.out.println("indexFirst: " + indexFirst);
                        System.out.println("indexSecond: " + indexSecond);
                        if (indexFirst > indexSecond) {
                            indexSecond = indexSecond - 1;
                            if (indexSecond > 9 || indexSecond < 0) {
                                invalidPlacement = true;
                                break;
                            }
                            letterToAdd = coOrdLetters.get(indexSecond);
                            String addCoOrd = letterToAdd + addNumber;
                            System.out.println("Letter to add: " + letterToAdd);
                            System.out.println(addCoOrd);
                            if (shipList.contains(addCoOrd)) {
                                invalidPlacement = true;
                            } else {
                                damage += addCoOrd;
                                System.out.println(damage);
                            }
                        } else {
                            indexSecond = indexSecond + 1;
                            if (indexSecond > 9 || indexSecond < 0) {
                                invalidPlacement = true;
                                break;
                            }
                            letterToAdd = coOrdLetters.get(indexSecond);
                            String addCoOrd = letterToAdd + addNumber;
                            System.out.println("Letter to add: " + letterToAdd);
                            System.out.println(addCoOrd);
                            if (shipList.contains(addCoOrd)) {
                                invalidPlacement = true;
                            } else {
                                damage += addCoOrd;
                                System.out.println(damage);
                            }
                        }
                    }
                    verticalPlacement = true;
                }
            }
            if (invalidPlacement == true || horizontalPlacement == true && verticalPlacement == true) {
                damage = "";
                coOrds.clear();
                if (target.charAt(0) != 'P') {
                    webSocketMessageSender.sendMessage("/topic/gameInfo", new Chat(selectedPlayer.getRoom().getRoomNumber() + selectedPlayer.getName() + " Invalid Placement!"));
                }
                webSocketMessageSender.sendMessage("/topic/placement2", new Chat(selectedPlayer.getRoom().getRoomNumber() + "Invalid"));
                invalidPlacement = false;
                horizontalPlacement = false;
                verticalPlacement = false;

            } else {
                coOrds.clear();
                newShip.setDamage(damage);
                existingCoOrds += damage;
                selectedPlayer.addShip(newShip);
                newShip.setPlayer(selectedPlayer);
                shipRepository.save(newShip);
                playerRepository.save(selectedPlayer);
                webSocketMessageSender.sendMessage("/topic/placement2", new Chat(selectedPlayer.getRoom().getRoomNumber() + selectedPlayer.getName() + newShip.getName()));
                damage = "";
                invalidPlacement = false;
                horizontalPlacement = false;
                verticalPlacement = false;
            }
        }
    }

    public void resetPlacement(String trigger) {
        if (trigger.length() > 1) {
            coOrds.clear();
            damage = "";
        }
    }

    public void fillCoOrds() {
        coOrdLetters = Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J");
        coOrdNumbers = Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
        for (int i = 0; i < 10; i++) {
            String coOrdLetter = "";
            String coOrdNumber = "";
            String coOrdSelected = "";
            for (int j = 0; j < 10; j++) {
                coOrdLetter = coOrdLetters.get(i);
                coOrdNumber = coOrdNumbers.get(j);
                coOrdSelected = coOrdLetter + coOrdNumber;
                computerAllCoOrds.add(coOrdSelected);
            }
        }
    }

    public String generateStartingRandomCoOrds(String firstCoOrds, Boolean computerGame) {
        coOrdLetters = Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J");
        coOrdNumbers = Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
        if (computerAllCoOrds.size() < 100) {
            fillCoOrds();
        }
        Random random = new Random();
        int randomCoOrd = random.nextInt(100);
        // Of random CoOrd letter = index 0, number index 1,
        String firstCoOrd = computerAllCoOrds.get(randomCoOrd);
        int rando = random.nextInt(3);
        int firstCoOrdIndexLetter = coOrdLetters.indexOf(String.valueOf(firstCoOrd.charAt(0)));
        int firstCoOrdIndexNumber = coOrdLetters.indexOf(String.valueOf(firstCoOrd.charAt(1)));
        int secondCoOrdIndexLetter = 0;
        int secondCoOrdIndexNumber = 0;
        do {
            randomCoOrd = random.nextInt(100);
            if (computerGame) {
                firstCoOrd = firstCoOrds;
            } else {
                firstCoOrd = computerAllCoOrds.get(randomCoOrd);
            }
            firstCoOrdIndexLetter = coOrdLetters.indexOf(String.valueOf(firstCoOrd.charAt(0)));
            firstCoOrdIndexNumber = coOrdNumbers.indexOf(String.valueOf(firstCoOrd.charAt(1)));
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
        if (computerGame) {
            return secondCoOrd;
        } else {
            return firstCoOrd + secondCoOrd;
        }
    }


    public void computerPlaceShips(Player player) throws InterruptedException {
        List<String> shipsList = playerRepository.findAllCoOrdsByPlayerName(player.getName());
        String shipList = String.join("", shipsList);
        String placedShips = "";
        while ((shipList = String.join("", playerRepository.findAllCoOrdsByPlayerName(player.getName()))).length() < 10) {
            String placementCoOrds = generateStartingRandomCoOrds("N/A", false);
            String firstCoOrd = placementCoOrds.substring(0, 2);
            String secondCoOrd = placementCoOrds.substring(2, 4);
            placeShip("P" + firstCoOrd + 5 + player.getName());
            placeShip("P" + secondCoOrd + 5 + player.getName());
        }
        while ((shipList = String.join("", playerRepository.findAllCoOrdsByPlayerName(player.getName()))).length() < 26) {
            String placementCoOrds = generateStartingRandomCoOrds("N/A", false);
            String firstCoOrd = placementCoOrds.substring(0, 2);
            String secondCoOrd = placementCoOrds.substring(2, 4);
            placeShip("P" + firstCoOrd + 4 + player.getName());
            placeShip("P" + secondCoOrd + 4 + player.getName());
        }
        while ((shipList = String.join("", playerRepository.findAllCoOrdsByPlayerName(player.getName()))).length() < 44) {
            String placementCoOrds = generateStartingRandomCoOrds("N/A", false);
            String firstCoOrd = placementCoOrds.substring(0, 2);
            String secondCoOrd = placementCoOrds.substring(2, 4);
            placeShip("P" + firstCoOrd + 3 + player.getName());
            placeShip("P" + secondCoOrd + 3 + player.getName());
        }
        while ((shipList = String.join("", playerRepository.findAllCoOrdsByPlayerName(player.getName()))).length() < 60) {
            String placementCoOrds = generateStartingRandomCoOrds("N/A", false);
            String firstCoOrd = placementCoOrds.substring(0, 2);
            String secondCoOrd = placementCoOrds.substring(2, 4);
            placeShip("P" + firstCoOrd + 2 + player.getName());
            placeShip("P" + secondCoOrd + 2 + player.getName());
        }
    }
    }