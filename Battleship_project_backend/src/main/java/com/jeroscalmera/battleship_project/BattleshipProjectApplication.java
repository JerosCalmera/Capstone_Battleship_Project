package com.jeroscalmera.battleship_project;
import com.jeroscalmera.battleship_project.websocket.Chat;
import com.jeroscalmera.battleship_project.websocket.WebSocketMessageSender;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import java.util.Scanner;

@SpringBootApplication
public class BattleshipProjectApplication {

	private static WebSocketMessageSender webSocketMessageSender;

	public BattleshipProjectApplication(WebSocketMessageSender webSocketMessageSender) {
		this.webSocketMessageSender = webSocketMessageSender;
	}

	public static void main(String[] args) {
		SpringApplication.run(BattleshipProjectApplication.class, args);
		System.out.println("-------------Server Online-------------");
		Scanner message = new Scanner(System.in);
		while (true) {
			System.out.println("Send admin message players:");
			String userInput = message.nextLine();
			webSocketMessageSender.sendMessage("/topic/chat", new Chat("Admin: " + userInput));
		}
	}
}