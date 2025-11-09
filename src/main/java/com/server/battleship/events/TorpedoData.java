package com.server.battleship.events;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TorpedoData {
    private String playerId;
    private Integer rindex;
    private Integer cindex;
    private String room;
}