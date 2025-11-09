package com.server.battleship.events;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameOverData {
    private String room;
    private String playerId;
    private String nickname;
}
