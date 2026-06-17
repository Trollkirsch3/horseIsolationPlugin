package org.plugin2.mcplugin2;

import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.*;

import static java.awt.SystemColor.text;

public class ChessGame {
    List<UUID> displays = new ArrayList<>();
    List<UUID> barrierDisplays = new ArrayList<>();
    List<TextDisplay> textDisplays = new ArrayList<>();
    ItemDisplay [] horseDisplays = new ItemDisplay[2];

    int ticksSpend [] = new int [2];

    TextDisplay [] clockDisplay = new TextDisplay[2];
    TextDisplay [] nameDisplay = new TextDisplay[2];
    int deletionCooldown = 100;
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
        ticksSpend [0] = 0;
        ticksSpend [1] = 0;
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
        Location spawnLoc1 = location.clone().add(4, 1,-1);
        Location spawnLoc2 = location.clone().add(4, 1,9);
        clockDisplay [0]= GameVisualizer.spawnStaticText(spawnLoc1, "Time: ");
        clockDisplay [1]= GameVisualizer.spawnStaticText(spawnLoc2, "Time: ");
        clockDisplay [0].addScoreboardTag("chess_entity");
        clockDisplay[0].addScoreboardTag("chess_slot_" + id);
        clockDisplay [1].addScoreboardTag("chess_entity");
        clockDisplay[1].addScoreboardTag("chess_slot_" + id);

        clockDisplay [0].setTransformation(new Transformation(
                new Vector3f(0f, 0f, 0f),        // Verschiebung
                new AxisAngle4f(0f, 0f, 1f, 0f), // linke Rotation
                new Vector3f(3f, 3f, 3f),        // Größe / Scale
                new AxisAngle4f(0f, 0f, 1f, 0f)  // rechte Rotation
        ));
        clockDisplay [1].setTransformation(new Transformation(
                new Vector3f(0f, 0f, 0f),        // Verschiebung
                new AxisAngle4f(0f, 0f, 1f, 0f), // linke Rotation
                new Vector3f(3f, 3f, 3f),        // Größe / Scale
                new AxisAngle4f(0f, 0f, 1f, 0f)  // rechte Rotation
        ));
        clockDisplay[1].setRotation(180f, 0f);

        Location spawnNameLoc1 = spawnLoc1.clone().add(0, 1,0);
        Location spawnNameLoc2 = spawnLoc2.clone().add(0, 1,0);
        nameDisplay [0] = GameVisualizer.spawnStaticText(spawnNameLoc1, "name1");
        nameDisplay [1] = GameVisualizer.spawnStaticText(spawnNameLoc2, "name2");
        nameDisplay [0].addScoreboardTag("chess_entity");
        nameDisplay[0].addScoreboardTag("chess_slot_" + id);
        nameDisplay [1].addScoreboardTag("chess_entity");
        nameDisplay[1].addScoreboardTag("chess_slot_" + id);
        nameDisplay [0].setTransformation(new Transformation(
                new Vector3f(0f, 0f, 0f),        // Verschiebung
                new AxisAngle4f(0f, 0f, 1f, 0f), // linke Rotation
                new Vector3f(2f, 2f, 2f),        // Größe / Scale
                new AxisAngle4f(0f, 0f, 1f, 0f)  // rechte Rotation
        ));
        nameDisplay [1].setTransformation(new Transformation(
                new Vector3f(0f, 0f, 0f),        // Verschiebung
                new AxisAngle4f(0f, 0f, 1f, 0f), // linke Rotation
                new Vector3f(2f, 2f, 2f),        // Größe / Scale
                new AxisAngle4f(0f, 0f, 1f, 0f)  // rechte Rotation
        ));
        nameDisplay[1].setRotation(180f, 0f);
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
