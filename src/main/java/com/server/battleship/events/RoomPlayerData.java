package com.server.battleship.events;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomPlayerData {
    private String room;
    private String playerId;
}
