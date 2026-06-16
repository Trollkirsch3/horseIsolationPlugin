package org.plugin2.mcplugin2;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class McPlugin2 extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Main main = new Main(this);
        getServer().getPluginManager().registerEvents(new InteractListener(main), this);
        getCommand("setchessspawn").setExecutor(new ChessCommands(main, this));
        getCommand("newgame").setExecutor(new ChessCommands(main, this));
        getCommand("setgameloc").setExecutor(new ChessCommands(main, this));
        getCommand("setmaxgames").setExecutor(new ChessCommands(main, this));
        getCommand("duel").setExecutor(new ChessCommands(main, this));
        getCommand("acceptDuel").setExecutor(new ChessCommands(main, this));
        getServer().getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                main.checkGames();
            }
        }, 20L, 20L);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

}
