package com.server.battleship;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import com.corundumstudio.socketio.SocketIOServer;

@SpringBootApplication
@EnableScheduling
@RestController
public class BattleshipApplication {

	@Value("${client.url}")
	private String clientUrl;

	@Value("${server.url}")
	private String serverUrl;

	@Value("${socket.port}")
	private int socketPort;

	private SocketIOServer socketServer;
	private final RestTemplate restTemplate = new RestTemplate();

	public static void main(String[] args) {
		SpringApplication.run(BattleshipApplication.class, args);
	}

	@GetMapping("/ping")
	public String ping() {
		return "pong";
	}

	@Scheduled(cron = "0 */1 * * * *")
	public void selfPing() {
		try {
			this.restTemplate.getForObject(serverUrl + "/ping", String.class);
		} catch (Exception e) {
			System.err.println("Self-ping failed:");
		}
	}

	@PreDestroy
	public void stopSocketServer() {
		if (socketServer != null) {
			socketServer.stop();
		}
	}
}
