package com.jeroscalmera.battleship_project.controllers;

import com.jeroscalmera.battleship_project.models.Player;
import com.jeroscalmera.battleship_project.repositories.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "*")
@RestController
public class PlayerController {
    @Autowired
    PlayerRepository playerRepository;

    @GetMapping(value = "/players")
    public ResponseEntity<List<Player>> getAllPlayers() {
        return new ResponseEntity<>(playerRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping(value = "/players/{id}")
    public ResponseEntity getPlayer(@PathVariable Long id) {
        return new ResponseEntity<>(playerRepository.findById(id), HttpStatus.OK);
    }
    @PostMapping(value = "/players")
    public ResponseEntity<Player> createPirate(@RequestBody Player player) {
        playerRepository.save(player);
        return new ResponseEntity<>(player, HttpStatus.CREATED);
    }
    @DeleteMapping(value = "/players/{id}")
    public void deletePirate(@PathVariable Long id){
        playerRepository.deleteById(id);
    }
    }
