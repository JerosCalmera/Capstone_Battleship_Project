package com.jeroscalmera.battleship_project.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

@Entity
@Table(name = "ships")
public class Ship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name="player_id", nullable = false)
    @JsonIgnoreProperties({"ship"})
    private Player player;
    @Column(name = "name")
    private String name;
    @Column(name = "max_size")
    private int maxSize;
    @Column(name = "coOrds")
    private String coOrds;
    public Ship(Player player, String name, int maxSize, String coOrds) {
        this.player = player;
        this.name = name;
        this.maxSize = maxSize;
        this.coOrds = coOrds;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Player getPlayer() {
        return player;
    }
    public Ship(){
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }
    public String getCoOrds() {
        return coOrds;
    }
    public int getMaxSize() {
        return maxSize;
    }
    public int getDamage() {
        return coOrds.length();
    }
    public void setCoOrds(String coOrds) {
        this.coOrds = coOrds;
    }
    public void setDamage(String newCoOrd) {
        if (this.coOrds.length() < this.maxSize)
        {setCoOrds(getCoOrds() + newCoOrd);}
        else {
            System.out.println(this.getName() + " cannot have any more co-ordinates added");
        }
    }
}
