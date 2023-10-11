package com.jeroscalmera.battleship_project.models;

import javax.persistence.*;
@Entity
@Table(name = "room")
public class Room {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        @Column(name = "room")
        private String roomNumber;

    public Room(String roomNumber) {
        this.roomNumber = roomNumber;
    }
    public Room(){
    }
    public Long getId() {
        return id;
    }
    public String getRoom() {
        return roomNumber;
    }
    public void setRoom(String roomNumber) {
        this.roomNumber = roomNumber;
    }
}
