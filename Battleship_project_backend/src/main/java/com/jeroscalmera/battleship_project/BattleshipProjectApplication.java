package com.jeroscalmera.battleship_project;
import com.jeroscalmera.battleship_project.websocket.Chat;
import com.jeroscalmera.battleship_project.websocket.Hidden;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import java.util.Scanner;

@SpringBootApplication
public class BattleshipProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(BattleshipProjectApplication.class, args);
		System.out.println("-------------Server Online-------------");
		Scanner message = new Scanner(System.in);
		while (true) {
			System.out.println("Message Player");
			String userInput = message.nextLine();
			new Hidden(userInput);
			new Chat(userInput);
		}
	}
}