package com.jeroscalmera.battleship_project.models;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


@Entity
@Table(name = "players")
public class Player implements Comparable<Player>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;

    @Column(name = "playerNumber")
    private String playerNumber;

    @Column(name = "player_is_computer")
    private boolean isComputer;

    @Column(name = "level")
    private int level;

    @Column(name = "room")
    private String roomNumber;
    @OneToMany(mappedBy = "player")
    @Cascade(org.hibernate.annotations.CascadeType.REMOVE)
    @JsonIgnoreProperties({"player"})
    private List<Ship> ships;

    @ManyToOne
    @JoinColumn(name="room_id", nullable = true)
    @Cascade(org.hibernate.annotations.CascadeType.REMOVE)
    @JsonIgnoreProperties({"player"})
    private Room room;

    @Column(name = "ready")
    private boolean ready;

    @Column(name = "aiShot", length = 1000)
    private String aiShot;

    @Column(name = "aiConfirmedHit")
    private String aiConfirmedHit;

    @Column(name = "aiHitCheck")
    private boolean aiHitCheck;


    public Player(String name) {
        this.name = name;
        this.isComputer = false;
        this.ready = false;
        this.aiHitCheck = false;
        this.aiShot = "*";
        this.aiConfirmedHit = "*";
    }

    public Player() {
    }

    @Override
    public int compareTo(Player player) {
        return Integer.compare(player.level, this.level);
    }
    public void setShips(List<Ship> ships) {
        this.ships = ships;
    }

    public boolean isComputer() {
        return isComputer;
    }

    public void setComputer(boolean computer) {
        isComputer = computer;
    }

    public String getPlayerNumber() {
        return playerNumber;
    }

    public void setPlayerNumber(String playerNumber) {
        this.playerNumber = playerNumber;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public String getDetails() {
        return name + " Lvl:(" + this.getLevel() + ")";
    }

    public int levelUp(int value) {
        this.level += value;
        return this.level;
    }

    public String generatePlayerNumber() {
            Random random = new Random();
            int randomNumber = random.nextInt(100000);
            String formattedRandom = String.format("%05d", randomNumber);
            return formattedRandom;
        }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getShips() {
        return ships.size();
    }

    public void addShip(Ship ship) {
        this.ships.add(ship);
    }

    public void setReady() {this.ready = true;}

    public void setUnReady() {this.ready = false;}

    public String checkReady() {
        String output = "No info";
        if (!this.ready) {
            output = this.name + " is not ready!";
        }
        else if (this.ready){
            output = this.name + " is ready!";
        }
        return output;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public String getAiShot() {
        return aiShot;
    }

    public void setAiShot(String aiShot) {
        this.aiShot = aiShot;
    }

    public boolean getAiHitCheck() {
        return aiHitCheck;
    }

    public void setAiHitCheck(boolean aiHitCheck) {
        this.aiHitCheck = aiHitCheck;
    }

    public String getAiConfirmedHit() {
        return aiConfirmedHit;
    }

    public void setAiConfirmedHit(String aiConfirmedHit) {
        this.aiConfirmedHit = aiConfirmedHit;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }
}
