package org.plugin2.mcplugin2.leaderboard;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.plugin2.mcplugin2.Main;
import org.plugin2.mcplugin2.McPlugin2;

public class DialogCommands implements CommandExecutor {
    Main main;
    McPlugin2 plugin;

    public DialogCommands (Main main, McPlugin2 plugin){
        this.main = main;
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        Player player = (Player) sender;
        switch (command.getName().toLowerCase()) {

            case "menu":
                Gui.openMainMenu(player, plugin);
                return true;
            case "leaderboard":
                Gui.openLeaderboard(player, 1, plugin);
                return true;
        }
        return false;
    }
}
