package com.server.battleship.events;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinData {
    private String room;
    private String nickname;
    private String playerId;
}
