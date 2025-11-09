## Battleship Backend
Backend server for <a href="https://github.com/Mano-08/battleship">battleship</a>. Play strategic naval combat game online with friends online!

---

## Description
A real-time multiplayer backend for the Battleship game, built with **Spring Boot** and **Netty Socket.IO**.  
It manages player connections, rooms, and all real-time gameplay events like torpedo drops and rematches.

---

## Tech Stack
- **Spring Boot** for REST and scheduling
- **Socket.IO (Java server)** for real-time gameplay

---

## Connection State

`dropTorpedo` transmit the coordinates of bomb drop location <br>
`ready` when player place their ships and are ready to begin the battle <br>
`gameOver` when one player wins the game, this event is triggered to other player <br>
`requestPlayAgain` transmit a request to replay with your opponent <br>
`acceptPlayAgain` transmit acknowledgment when opponent accepts the play again request <br>
`join` transmit request to join a room <br>
`full` transmit error when player tries to join a room already full (2 players play in one room) <br>

---

## Play Battleship
🚢 Try Battleship at <a href="https://playbattleship.vercel.app/">https://playbattleship.vercel.app/</a>
