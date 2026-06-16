package org.plugin2.mcplugin2;

import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class InteractListener implements Listener {

    private final Main main;

    public InteractListener(Main main) {
        this.main = main;
    }
    @EventHandler
    public void onMoveClick(PlayerInteractEntityEvent event) {
        Entity clicked = event.getRightClicked();

        if (!(clicked instanceof Interaction)) {
            return;
        }
        for (ChessGame game : main.games) {
            Integer move = game.displayMoves.get(clicked.getUniqueId());

            if (move == null) {
                continue;
            }

            int[] coords = BitField.getCoords(move);

            event.getPlayer().sendMessage(
                    "Du hast Move " + move + " angeklickt: x=" + coords[0] + " z=" + coords[1]
            );
            event.getPlayer().getWorld().playSound(
                    game.location.clone().add(coords [0], 0, coords [1]),
                    Sound.BLOCK_WOOD_STEP,
                    1.0f,
                    1.0f
            );
            game.move(coords[0], coords[1], game.playerToMove);

            GameVisualizer.visualizeGame(game, main.plugin);
            GameVisualizer.visualizePossibleMoves(game, main.plugin);

            return;
        }
    }
}
