package org.plugin2.mcplugin2;

import org.bukkit.Location;
import org.bukkit.entity.*;

import java.util.*;

public class ChessGame {
    List<BlockDisplay> displays = new ArrayList<>();
    List<ItemDisplay> barrierDisplays = new ArrayList<>();
    List<TextDisplay> textDisplays = new ArrayList<>();
    ItemDisplay [] horseDisplays = new ItemDisplay[2];

    int deletionCooldown = 5;
    Location location;
    long field;
    int [] playerLocs = new int [2];
    Player [] players = new Player [2];
    int playerToMove = 1;
    public Map<UUID, Integer> displayMoves = new HashMap<>();
    public List<Entity> moveClickEntities = new ArrayList<>();
    boolean over = false;
    int id;
    public ChessGame(Location location, Player player1, Player player2, int gameId){
        field = 0L;
        this.location = location.getBlock().getLocation();
        players [0] = player1;
        players [1] = player2;
        Random random = new Random();
        int x1 = random.nextInt(8);
        int z1 = random.nextInt(8);
        int z2 = z1;
        int x2 = x1;
        while (x2 == x1 && z2 == z1){
            x2 = random.nextInt(8);
            z2 = random.nextInt(8);
        }
        playerLocs [0] = BitField.bitLoc(x1, z1);
        playerLocs [1] = BitField.bitLoc(x2, z2);
        field = field | BitField.bit(x1, z1);
        field = field | BitField.bit(x2, z2);
        id = gameId;
    }
    public void move(int moveX, int moveZ, int player){
        if (player != playerToMove){
            return;
        }
        playerLocs [player-1] = BitField.bitLoc(moveX, moveZ);
        field = field | BitField.bit(moveX, moveZ);
        playerToMove = nextPlayer(playerToMove);
        int [] possibleMoves = new int [8];
        long possible = BitField.getPossibleMoves(field, playerLocs [playerToMove-1]);

        if (BitField.bitMovesToArray(possible, possibleMoves) <=0){
            over = true;
        }
    }
    public int nextPlayer(int player){
        if (player == 1){
            return 2;
        }
        return 1;
    }
    public Player getCurrentPlayer() {
        if (playerToMove == 1) {
            return players [0];
        }
        return players [1];
    }

}
