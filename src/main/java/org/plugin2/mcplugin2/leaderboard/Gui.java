package org.plugin2.mcplugin2.leaderboard;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.plugin2.mcplugin2.McPlugin2;
import org.plugin2.mcplugin2.PlayerData;

import java.util.List;
import java.util.UUID;

public class Gui {
    public final static String GUI_COLOR1 = "<#e8e7d9>";
    public final static String GUI_COLOR2 = "<#b1b1cf>";
    public static void openMainMenu(Player player, McPlugin2 plugin) {

        ActionButton leaderboardButton = ActionButton.builder(
                        Component.text("Leaderboard")
                )
                .tooltip(Component.text("Open the leaderboard"))
                .action(
                        DialogAction.customClick(
                                (view, audience) -> {

                                    if (!(audience instanceof Player clickedPlayer)) {
                                        return;
                                    }
                                    openLeaderboard(clickedPlayer, 1, plugin);
                                },
                                ClickCallback.Options.builder()
                                        .uses(1)
                                        .build()
                        )
                )
                .build();


        ActionButton statsButton = ActionButton.builder(
                        Component.text("My Stats")
                )
                .tooltip(Component.text("View your statistics"))
                .action(
                        DialogAction.customClick(
                                (view, audience) -> {

                                    if (!(audience instanceof Player clickedPlayer)) {
                                        return;
                                    }

                                    openStats(clickedPlayer);
                                },
                                ClickCallback.Options.builder()
                                        .uses(1)
                                        .build()
                        )
                )
                .build();


        ActionButton historyButton = ActionButton.builder(
                        Component.text("Game History")
                )
                .tooltip(Component.text("View your recent games"))
                .action(
                        DialogAction.customClick(
                                (view, audience) -> {

                                    if (!(audience instanceof Player clickedPlayer)) {
                                        return;
                                    }

                                    openHistory(clickedPlayer, clickedPlayer.getUniqueId(), 1, plugin);
                                },
                                ClickCallback.Options.builder()
                                        .uses(1)
                                        .build()
                        )
                )
                .build();


        Dialog dialog = Dialog.create(builder -> builder.empty()

                .base(
                        DialogBase.builder(
                                        Component.text("Main menu")
                                )
                                .body(List.of(
                                        DialogBody.plainMessage(
                                                Component.text(
                                                        "Choose what you want to view."
                                                )
                                        )
                                ))
                                .build()
                )

                .type(
                        DialogType.multiAction(
                                List.of(
                                        leaderboardButton,
                                        statsButton,
                                        historyButton
                                )
                        ).build()
                )
        );

        player.showDialog(dialog);
    }


    public static void openLeaderboard(Player player, int page, McPlugin2 plugin) {
        MiniMessage mm = MiniMessage.miniMessage();
        ActionButton leaderboardButton = ActionButton.builder(
                        Component.text("Leaderboard")
                )
                .tooltip(Component.text("Open the leaderboard"))
                .action(
                        DialogAction.customClick(
                                (view, audience) -> {

                                    if (!(audience instanceof Player clickedPlayer)) {
                                        return;
                                    }
                                    openLeaderboard(clickedPlayer, page, plugin);
                                },
                                ClickCallback.Options.builder()
                                        .uses(1)
                                        .build()
                        )
                )
                .build();


        Dialog dialog = Dialog.create(builder -> builder.empty()

                .base(
                        DialogBase.builder(
                                        mm.deserialize(GUI_COLOR1 + "<bold>Leaderboard")
                                )
                                .body(List.of(
                                        DialogBody.plainMessage(
                                                Component.text(
                                                        " "
                                                )
                                        )
                                ))
                                .build()
                )

                .type(
                        DialogType.multiAction(
                                        Leaderboard.getLeaderBoardButtons(player, page, plugin)
                                )
                                .columns(1)
                                .build()
                )
        );

        player.showDialog(dialog);
    }
    public static void openProfile(Player player, UUID profile, McPlugin2 plugin) {
        MiniMessage mm = MiniMessage.miniMessage();
        ActionButton historyButton = ActionButton.builder(
                        Component.text("History")
                )
                .action(
                        DialogAction.customClick(
                                (view, audience) -> {
                                    if (!(audience instanceof Player clickedPlayer)) {
                                        return;
                                    }

                                    openHistory(clickedPlayer, profile, 1, plugin);
                                },
                                ClickCallback.Options.builder()
                                        .uses(1)
                                        .build()
                        )
                )
                .build();

        ActionButton backButton = ActionButton.builder(
                        Component.text("Back")
                )
                .action(
                        DialogAction.customClick(
                                (view, audience) -> {
                                    if (!(audience instanceof Player clickedPlayer)) {
                                        return;
                                    }

                                    openLeaderboard(clickedPlayer, 1, plugin);
                                },
                                ClickCallback.Options.builder()
                                        .uses(1)
                                        .build()
                        )
                )
                .build();

        PlayerData data = plugin.playerDatabase.loadPlayer(profile);
        OfflinePlayer offlinePlayer =
                Bukkit.getOfflinePlayer(data.getUUID());

        String name = offlinePlayer.getName();

        if (name == null) {
            name = "Unknown";
        }

        int wins = data.getWins();
        int losses = data.getLosses();
        int rank = data.getRank();
        int winStreak = data.getWinStreak();
        int maxWinStreak = data.getAllTimeWinStreak();

        String finalName = name;

        ItemStack head = new ItemStack(Material.PLAYER_HEAD);

        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(profile));
        head.setItemMeta(meta);
        Component stats = MiniMessage.miniMessage().deserialize(
                GUI_COLOR2+ "rank: " + "<white>#" +rank + "\n" +
                        "\n"+
                        GUI_COLOR2+ "wins: " + "<white>" +wins + "\n"+
                        "\n"+
                        GUI_COLOR2+  "losses: " + "<white>" +losses + "\n"+
                        "\n"+
                        GUI_COLOR2+ "winstreak: " + "<white>" + winStreak+ "<sprite:blocks:block/campfire_fire>\n"+
                        "<dark_gray>(best streak: "+maxWinStreak + ")"
        );
        Dialog dialog = Dialog.create(builder -> builder.empty()

                .base(
                        DialogBase.builder(
                                        mm.deserialize( "<white> <bold>" + finalName + GUI_COLOR1 + "</bold>'s profile")
                                )
                                .body(List.of(DialogBody.item(head)
                                                .width(16)
                                                .height(16)
                                                .showDecorations(false)
                                                .showTooltip(false)
                                                .build(),
                                        DialogBody.plainMessage(stats)
                                ))
                                .build()
                )

                .type(
                        DialogType.multiAction(
                                        List.of(backButton, historyButton)
                                )
                                .columns(1)
                                .build()
                )
        );

        player.showDialog(dialog);
    }


    private static void openStats(Player player) {
        player.sendMessage("Stats clicked");
    }


    private static void openHistory(Player player, UUID profile, int page, McPlugin2 plugin) {
        MiniMessage mm = MiniMessage.miniMessage();
        OfflinePlayer offlinePlayer =
                Bukkit.getOfflinePlayer(profile);
        String name = offlinePlayer.getName();
        Dialog dialog = Dialog.create(builder -> builder.empty()

                .base(
                        DialogBase.builder(
                                        mm.deserialize("<bold>" + GUI_COLOR2 + name + GUI_COLOR1 + "'s History")
                                )
                                .body(List.of(
                                        DialogBody.plainMessage(
                                                Component.text(
                                                        " "
                                                )
                                        )
                                ))
                                .build()
                )

                .type(
                        DialogType.multiAction(
                                        GameHistory.getGameButtons(profile, page, plugin)
                                )
                                .columns(1)
                                .build()
                )
        );

        player.showDialog(dialog);
    }
}
