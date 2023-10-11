package com.jeroscalmera.battleship_project.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("gameboard")
public class GameBoardController {
    @GetMapping
    public ResponseEntity<String> gameboard() {
        String htmlContent = "<html><body><h1>Gameroom</h1></body></html>";
        return ResponseEntity.ok(htmlContent);
    }
}
