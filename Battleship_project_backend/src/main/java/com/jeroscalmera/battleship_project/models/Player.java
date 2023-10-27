package com.jeroscalmera.battleship_project.models;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


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
    @Column(name = "level")
    private int level;
    @OneToMany(mappedBy = "player")
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @JsonIgnoreProperties({"player"})
    private List<Ship> ships;

    @ManyToOne
    @JoinColumn(name="room_id", nullable = true)
    @JsonIgnoreProperties({"player"})
    private Room room;

    public Player(String name) {
        this.name = name;
        this.playerNumber = playerNumber;
        this.level = level;
        this.ships = new ArrayList<>();
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

    public int levelUp(int value) {
        this.level += value;
        return this.level;
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
}
