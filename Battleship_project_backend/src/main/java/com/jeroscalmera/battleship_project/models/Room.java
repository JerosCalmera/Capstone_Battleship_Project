package com.jeroscalmera.battleship_project.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "room")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "room")
    private String roomNumber;
    @OneToMany(mappedBy = "room")
    @Cascade(org.hibernate.annotations.CascadeType.REMOVE)
    @JsonIgnoreProperties({"room"})
    private List<Player> players;

    @Column(name = "players_ready")
    private int playersReady;


    public Room(String roomNumber) {
        this.roomNumber = roomNumber;
        this.players = new ArrayList<>();
        this.playersReady = 0;
    }

    public Room() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void addPlayerToRoom(Player player) {
        this.players.add(player);
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public void setPlayersReady(int playersReady) {
        this.playersReady = playersReady;
    }

    public int getPlayersReady() {
        return playersReady;
    }
}