package com.server.battleship.socket;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.SocketIOClient;

import com.server.battleship.events.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

import java.util.*;

@org.springframework.context.annotation.Configuration
public class SocketConfig {

    @Value("${socket.port}")
    private int socketPort;

    @Value("${client.url}")
    private String clientURL;

    @Value("${client.hostname}")
    private String clientHostname;

    @Bean
    public SocketIOServer socketIOServer() {
        Configuration config = new Configuration();
        config.setHostname(clientHostname);
        config.setPort(socketPort);
        config.setOrigin(clientURL);

        return new SocketIOServer(config);
    }

    @Bean
    public CommandLineRunner runner(SocketIOServer server) {
        return args -> {
            server.addConnectListener(client -> {
                System.out.println("[SUCCESS] A user just connected: " + client.getSessionId());
            });

            server.addDisconnectListener(client -> {
                String room = client.get("room");
                String playerId = client.get("playerId");
                String nickname = client.get("nickname");

                if (room != null) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("playerId", playerId);
                    response.put("nickname", nickname);
                    response.put("message", "Player left the room. Game over!");

                    server.getRoomOperations(room).sendEvent("playerLeft", response);
                    client.leaveRoom(room);
                }
            });

            server.addEventListener("join", JoinData.class, (client, data, ack) -> {
                String clientId = client.getSessionId().toString();
                Collection<SocketIOClient> clients = server.getRoomOperations(data.getRoom()).getClients();
                // Avoid duplicate join
                boolean alreadyInRoom = clients.stream()
                        .anyMatch(c -> c.getSessionId().toString().equals(clientId));

                if (alreadyInRoom) {
                    System.out.println("[ERROR] Client " + clientId + " already in room " + data.getRoom());
                    return;
                }

                // Two players max
                if (clients.size() < 2) {
                    String room = data.getRoom();

                    client.set("room", room);
                    client.set("playerId", data.getPlayerId());
                    client.set("nickname", data.getNickname());

                    client.joinRoom(room);
                    Collection<SocketIOClient> updatedClients = server.getRoomOperations(room).getClients();
                    Map<String, Object> response = new HashMap<>();
                    response.put("playerId", data.getPlayerId());
                    response.put("nickname", data.getNickname());
                    response.put("timeline", updatedClients.size() == 1 ? "first" : "second");
                    server.getRoomOperations(room).sendEvent("playerJoined", response);
                    System.out.println("[SUCCESS] Client " + clientId + " joined room " + room);
                } else {
                    client.sendEvent("full");
                }
            });

            server.addEventListener("requestPlayAgain", RoomPlayerData.class,
                    (client, data, ack) -> {
                        server.getRoomOperations(data.getRoom())
                                .sendEvent("requestPlayAgain", data.getPlayerId());
                    });

            server.addEventListener("acceptPlayAgain", RoomPlayerData.class,
                    (client, data, ack) -> {
                        server.getRoomOperations(data.getRoom())
                                .sendEvent("acceptPlayAgain", data.getPlayerId());
                    });

            server.addEventListener("gameOver", GameOverData.class,
                    (client, data, ack) -> {
                        String room = client.get("room");
                        if (room == null) {
                            System.out.println("[ERROR]️ client has no room associated for gameOver!");
                            return;
                        }
                        server.getRoomOperations(room)
                                .sendEvent("gameOver", data);
                    });

            server.addEventListener("ready", ReadyData.class,
                    (client, data, ack) -> {
                        Map<String, Object> response = new HashMap<>();
                        response.put("placement", data.getPlacement());
                        response.put("playerId", data.getPlayerId());

                        server.getRoomOperations(data.getRoom())
                                .sendEvent("ready", response);
                    });

            server.addEventListener("dropTorpedo", TorpedoData.class,
                    (client, data, ack) -> {
                        server.getRoomOperations(data.getRoom()).sendEvent("dropTorpedo", data);
                    });

            server.start();
            System.out.println("[SUCCESS] Socket server started on port " + socketPort);
        };
    }
}
