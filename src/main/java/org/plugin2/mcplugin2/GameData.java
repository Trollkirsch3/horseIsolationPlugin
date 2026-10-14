package org.plugin2.mcplugin2;

import java.util.UUID;

public class GameData {
    public UUID winner;
    public UUID player1;
    public UUID player2;
    public int timeSpendP1;
    public int timeSpendP2;
    public int duration;
    public int id;
    public GameData(int id, UUID player1, UUID player2, UUID winner, int timeSpendP1, int timeSpendP2, int duration) {
        this.player1 = player1;
        this.player2 = player2;
        this.winner = winner;
        this.timeSpendP1 = timeSpendP1;
        this.timeSpendP2 = timeSpendP2;
        this.duration = duration;
        this.id = id;
    }
}
