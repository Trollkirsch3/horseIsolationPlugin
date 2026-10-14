package org.plugin2.mcplugin2.leaderboard;

import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.plugin2.mcplugin2.GameData;
import org.plugin2.mcplugin2.McPlugin2;
import org.plugin2.mcplugin2.PlayerData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.plugin2.mcplugin2.GameVisualizer.formatTicks;
import static org.plugin2.mcplugin2.leaderboard.Gui.*;

public class GameHistory {
    public final static int gamesPerPage = 10;

    public static List<ActionButton> getGameButtons(UUID player, int page, McPlugin2 plugin){
        List<ActionButton> buttons = new ArrayList<>();
        List<GameData> games = plugin.gameDatabase.getHistory(player, page);
        for (GameData game : games){
            buttons.add(getGameButton(game, plugin));
        }
        return buttons;
    }
    public static ActionButton getGameButton(GameData data, McPlugin2 plugin) {
        int id = data.id;
        UUID player1 = data.player1;
        UUID player2 = data.player2;
        OfflinePlayer offlinePlayer1 =
                Bukkit.getOfflinePlayer(player1);
        OfflinePlayer offlinePlayer2 =
                Bukkit.getOfflinePlayer(player2);
        String name1 = offlinePlayer1.getName();
        String name2 = offlinePlayer2.getName();

        UUID winner = data.winner;
        name1 = String.format("%-16s", name1);
        name2 = String.format("%-16s", name2);
        int width = 16;

        String rawName1 = offlinePlayer1.getName();
        String spaces1 = " ".repeat(Math.max(0, width - rawName1.length()));

        String coloredName1;

        if (player1.equals(winner)) {
            coloredName1 = "<green>" + rawName1 + "</green>";
        } else {
            coloredName1 = "<red>" + rawName1 + "</red>";
        }

        String player1Text =
                spaces1 +
                        "<white><bold><!shadow>■ </!shadow></bold>" +
                        "<white><head:" + player1 + "> " +
                        coloredName1;
        if (player1.equals(winner)){
            //name1 = "<green>" + name1 + "</green>";
            name2 = "<red>" + name2 + "</red>";
        }
        else {
            name2 = "<green>" + name2 + "</green>";
            //name1 = "<red>" + name1 + "</red>";
        }

        int timeSpendP1 = data.timeSpendP1;
        int timeSpendP2 = data.timeSpendP2;
        int duration = data.duration;
        MiniMessage mm = MiniMessage.miniMessage();

        Component text = mm.deserialize(
                "" +
                        player1Text + "<white>  vs  " +
                        "<black><bold><!shadow>■ </!shadow></bold><white><head:" + player2 + "> " +  GUI_COLOR2 + name2 +
                        "  <white> <sprite:items:item/clock_00> " + GUI_COLOR1 + formatTicks(duration)
        );

        return ActionButton.builder(text)
                .width(500)
                .tooltip(
                        mm.deserialize(
                                GUI_COLOR1 + "Click to view details"
                        )
                )
                .action(
                        DialogAction.customClick(
                                (view, audience) -> {

                                    if (!(audience instanceof Player clickedPlayer)) {
                                        return;
                                    }

                                    //TODO open game info
                                },
                                ClickCallback.Options.builder()
                                        .uses(1)
                                        .build()
                        )
                )
                .build();
    }
}