package com.server.battleship.events;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReadyData {
    private String room;
    private String playerId;
    private Object placement;
}
