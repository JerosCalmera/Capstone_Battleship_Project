Capstone Project - Multiplayer Battleship

Brief:  
To solo develop a multiplayer Battleship board game, playable in the browser between two people.
Players can play a game of battleship via websocket, each player can see their board and the enemies, game plays in the same fashion as the board game, two boards will be visible to each player, their own with the ships they placed and the opponents.

Tech Stack:  
Frontend - TypeScript with React  
Backend - Java with Spring Boot  
Database: PostgreSQL database used to store game data (game board info, player names, games won etc  
WebSocket is used for frontend and backend communication  

Features:
- The game can be played multiplayer between two players or single player against a computer controlled opponent
- A chat feature allows players to communicate with each other, and also allows the "Admin" to send messages via the backend if needed
- Players connect to a "room" designated by a manually typed or randomly generated token that both players must enter
- Players can randomly place ships on the game board if they do not wish to manually place them
- Players have a name they can set, if a player is returning, they can access their saved user data within the database that has saved name and level
- Players can see how many games they have won in total (players gain a level for every game won)
- A Leaderboard shows top players and their levels
- Players can submit bug reports that are saved into the database (This is a feature intended for troubleshooting when the game is hosted online)

To do:
- Ensure that more than one game can be played at once without bugs, currently only one game at a time can be played
- Replace coloured squares representing ships with images
- Improve computer player AI
- General bugfixes
- Host online
  
![Chat](https://github.com/JerosCalmera/Capstone_Battleship_Project/assets/136751073/ff71de8d-2bfd-4e93-8560-d649054c3b52)
![AIBattle](https://github.com/JerosCalmera/Capstone_Battleship_Project/assets/136751073/250d41c4-18ed-430b-814f-0d89787ba2a4)
![multplayer](https://github.com/JerosCalmera/Capstone_Battleship_Project/assets/136751073/7dfb383a-846d-4d6e-9baf-5c3ba51fb943)
