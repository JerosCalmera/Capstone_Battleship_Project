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
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @JsonIgnoreProperties({"room"})
    private List<Player> players;

    public Room(String roomNumber) {
        this.roomNumber = roomNumber;
        this.players = new ArrayList<>();
    }
    public Room(){
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getPlayers() {
        return players.size();
    }
    public void addPlayerToRoom(Player player) {
        this.players.add(player);
    }
}
