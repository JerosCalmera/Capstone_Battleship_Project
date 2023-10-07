package com.jeroscalmera.battleship_project.websocket;

public class Connection {
    private String message;
    public Connection() {

    }
    public Connection(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
