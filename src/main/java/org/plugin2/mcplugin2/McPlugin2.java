package org.plugin2.mcplugin2;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class McPlugin2 extends JavaPlugin {
    Main main;
    @Override
    public void onEnable() {
        // Plugin startup logic
        main = new Main(this);
        getServer().getPluginManager().registerEvents(new InteractListener(main), this);
        getCommand("setchessspawn").setExecutor(new ChessCommands(main, this));
        getCommand("setgameloc").setExecutor(new ChessCommands(main, this));
        getCommand("setmaxgames").setExecutor(new ChessCommands(main, this));
        getCommand("duel").setExecutor(new ChessCommands(main, this));
        getCommand("acceptDuel").setExecutor(new ChessCommands(main, this));
        getServer().getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                main.checkGames();
            }
        }, 1L, 1L);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        for (ChessGame game : main.games){
            if(game ==null){continue;}
            String worldName = main.getVariable("spawn.world");
            World world = Bukkit.getWorld(worldName);
            double x = Double.parseDouble(main.getVariable("spawn.x"));
            double y = Double.parseDouble(main.getVariable("spawn.y"));
            double z = Double.parseDouble(main.getVariable("spawn.z"));
            Location spawnLocation;
            if (world != null){
                spawnLocation = new Location (world, x, y, z);
            }
            else{
                spawnLocation = new Location (Bukkit.getWorld("world"), 1, 1, 1);
            }
            if (game.players [0] !=null){game.players [0].teleport(spawnLocation);}

            if (game.players [1] !=null){game.players [1].teleport(spawnLocation);}
            GameVisualizer.delete(game);
        }
    }

}
