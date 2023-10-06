Capstone Project - Multiplayer Battleship

Brief:

To create a multiplayer Battleship board game, playable in the browser between two people.
Players can play a game of battleship via websocket, each player can see their board and the enemies, game plays in the same fashion as the board game, two boards will be visible to each player, their own with the ships they placed
and the opponents.

Tech Stack:
Frontend - JavaScript using TypeScript and React
Backend - Java using an SQL database to store game data (game board info, player names, games won etc)

MVP:
- Players can place ships on a board 10x10 board
- Players can click on the other players board, which appears "empty" to start shooting at cells to attempt to hit their ships
- Players game state is constantly updated due to the connection being handled via Websocket
- Players have a name they can set
- Players can see how many turns have passed and how many games they have won
- Players can restart the game once complete
- Players connect to a "room" designated by a randomly generated token that both players must enter
- Styled in CSS

Possible extensions:
- Special abilities (scan for ships, shoot a large area etc.)
- Leaderboard
- A player leveling system  (perhaps to unlock new special abilities as mentioned above)
- Enhanced styling
- Option/s for larger board/s
- A chat function 
- Anything else that comes up during development as a interesting idea
