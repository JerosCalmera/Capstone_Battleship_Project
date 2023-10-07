package com.jeroscalmera.battleship_project;

import com.jeroscalmera.battleship_project.websocket.Greeting;
import com.jeroscalmera.battleship_project.websocket.WebSocketMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.util.Scanner;

@SpringBootApplication
public class BattleshipProjectApplication {

	private static WebSocketMessageSender webSocketMessageSender;

	@Autowired
	public BattleshipProjectApplication(WebSocketMessageSender webSocketMessageSender) {
		this.webSocketMessageSender = webSocketMessageSender;
	}
	public static void main(String[] args) {
		SpringApplication.run(BattleshipProjectApplication.class, args);
		System.out.println("-------------Server Online-------------");
		Scanner message = new Scanner(System.in);
		while (true) {
			System.out.println("Message Player");
			String userInput = message.nextLine();
			webSocketMessageSender.sendMessage("/topic/connect", new Greeting("Server Message: " + userInput));
		}
	}
}
