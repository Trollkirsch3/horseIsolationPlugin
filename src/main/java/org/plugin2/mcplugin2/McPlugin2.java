package org.plugin2.mcplugin2;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.plugin2.mcplugin2.databases.DatabaseManager;
import org.plugin2.mcplugin2.databases.GameDatabase;
import org.plugin2.mcplugin2.databases.PlayerDatabase;
import org.plugin2.mcplugin2.leaderboard.DialogCommands;

import java.awt.desktop.QuitEvent;
import java.util.*;

public final class McPlugin2 extends JavaPlugin implements Listener {
    public Main main;
    DatabaseManager databaseManager;
    public GameDatabase gameDatabase;
    public PlayerDatabase playerDatabase;
    Map<UUID, PlayerData> players = new HashMap();
    List<GameData> unsavedGameData = new ArrayList<>();
    @Override
    public void onEnable() {

        databaseManager = new DatabaseManager(this);

        try {
            databaseManager.connect();
            databaseManager.createTables();
        } catch (Exception e) {
            getLogger().severe("Could not initialize database!");
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        gameDatabase = new GameDatabase(databaseManager);
        playerDatabase = new PlayerDatabase(databaseManager);

        main = new Main(this);
        getServer().getPluginManager().registerEvents(new InteractListener(main), this);
        getServer().getPluginManager().registerEvents(this, this);

        getCommand("menu").setExecutor(new DialogCommands(main, this));
        getCommand("leaderboard").setExecutor(new DialogCommands(main, this));

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
        getServer().getScheduler().runTaskTimer(
                this,
                this::saveAllData,
                20L * 30,
                20L * 30
        );
    }
    public void saveAllData() {

        for (PlayerData data : players.values()) {
            playerDatabase.savePlayer(data);
        }

        Iterator<GameData> iterator =
                unsavedGameData.iterator();

        while (iterator.hasNext()) {

            GameData data = iterator.next();

            int id = gameDatabase.saveGame(data);

            if (id != -1) {
                data.id = id;
                iterator.remove();
            }
        }
    }

    @Override
    public void onDisable() {
        saveAllData();

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
        if (databaseManager != null) {
            databaseManager.disconnect();
        }
    }
    @EventHandler
    public void playerJoin( PlayerJoinEvent event){
        UUID uuid = event.getPlayer().getUniqueId();
        PlayerData data = playerDatabase.loadPlayer(uuid);

        if (data == null) {

            playerDatabase.createPlayer(uuid);

            data = playerDatabase.loadPlayer(uuid);
        }

        players.put(
                uuid,
                data
        );
        event.getPlayer().teleport(main.getSpawnLoc());
    }
    @EventHandler
    public void playerLeave(PlayerQuitEvent event) {

        UUID uuid = event.getPlayer().getUniqueId();
        PlayerData data = players.get(uuid);

        if (data == null) {
            return;
        }

        for (ChessGame game : main.games) {

            if (game == null) {
                continue;
            }

            if (game.players[0] == event.getPlayer()
                    || game.players[1] == event.getPlayer()) {

                data.addLoss();
                break;
            }
        }

        playerDatabase.savePlayer(data);
        players.remove(uuid);
    }

}
