package com.jeroscalmera.battleship_project.models;


import javax.persistence.*;

@Entity
@Table(name = "lobby")
public class Lobby {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "lobby_room")
    private String lobbyRoom;

    @Column(name = "IsSaved")
    private boolean isSaved;

    @Column(name = "IsValidated")
    private boolean isValidated;

    public Lobby(String lobbyRoom) {
        this.lobbyRoom = lobbyRoom;
        this.isSaved = isSaved;
        this.isValidated = isValidated;
    }

    public Lobby() {

    }

    public Long getId() {
        return id;
    }

    public String getLobbyRoom() {
        return lobbyRoom;
    }

    public void setLobbyRoom(String lobbyroom) {
        this.lobbyRoom = lobbyroom;
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void setSaved(boolean saved) {
        isSaved = saved;
    }

    public boolean isValidated() {
        return isValidated;
    }

    public void setValidated(boolean validated) {
        isValidated = validated;
    }
}