package org.plugin2.mcplugin2.leaderboard;

import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.plugin2.mcplugin2.McPlugin2;
import org.plugin2.mcplugin2.PlayerData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.plugin2.mcplugin2.leaderboard.Gui.*;

public class Leaderboard {
    public final static int playersPerPage = 10;

    public static List<ActionButton> getLeaderBoardButtons(Player viewing, int page, McPlugin2 plugin){
        List<ActionButton> buttons = new ArrayList<>();
        List<PlayerData> players = plugin.playerDatabase.getLeaderboardPlayers(page);
        for (PlayerData player : players){
           buttons.add(getPlayerButton(viewing, player, plugin));
        }
        return buttons;
    }
    public static ActionButton getPlayerButton(Player viewing, PlayerData data, McPlugin2 plugin) {

        OfflinePlayer offlinePlayer =
                Bukkit.getOfflinePlayer(data.getUUID());

        String name = offlinePlayer.getName();

        if (name == null) {
            name = "Unknown";
        }

        int wins = data.getWins();
        int losses = data.getLosses();
        int rank = data.getRank();

        MiniMessage mm = MiniMessage.miniMessage();

        Component text = mm.deserialize(
                "<white>" + "#" + rank + " " +
                "<white><head:" + data.getUUID() + "> " +
                        GUI_COLOR2 + name + "  " +
                        GUI_COLOR1 + wins + " wins"
        );

        return ActionButton.builder(text)
                .width(500)
                .tooltip(
                        mm.deserialize(
                                GUI_COLOR1 + "Click to view " +
                                       GUI_COLOR2 + name + GUI_COLOR1 + "'s profile"
                        )
                )
                .action(
                        DialogAction.customClick(
                                (view, audience) -> {

                                    if (!(audience instanceof Player clickedPlayer)) {
                                        return;
                                    }

                                    openProfile(viewing, data.getUUID(), plugin);
                                },
                                ClickCallback.Options.builder()
                                        .uses(1)
                                        .build()
                        )
                )
                .build();
    }
}
