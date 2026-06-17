package org.plugin2.mcplugin2;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.*;

public class Main {
    JavaPlugin plugin;
    int maxSlot;
    ArrayList<ChessGame> freeGames = new ArrayList<>();
    ArrayList<ChessGame> games = new ArrayList<>();
    Map<UUID, UUID> gameRequests = new HashMap<>();
    public Main (JavaPlugin plugin){
        this.plugin = plugin;
        maxSlot = plugin.getConfig().getInt("variables.maxSlots", 0);
        for (int i = 1; i <= maxSlot; i++){
            String worldName = getVariable("gameSlot." + i+ ".world");
            if (worldName == null) {
                plugin.getLogger().warning("gameSlot." + i + " hat keine World gespeichert.");
                continue;
            }
            World world = Bukkit.getWorld(worldName);
            double x = Double.parseDouble(getVariable("gameSlot." + i+ ".x"));
            double y = Double.parseDouble(getVariable("gameSlot." + i+ ".y"));
            double z = Double.parseDouble(getVariable("gameSlot." + i+ ".z"));
            Location gameLoc = new Location(world, x, y, z);
            ChessGame game = new ChessGame(gameLoc, null, null, i);
            GameVisualizer.visualizeField(game);
            freeGames.add(game);
        }
    }
    public ChessGame getNewGame(){
        if (freeGames.isEmpty()) {
            return null;
        }
        return freeGames.getFirst();
    }
    public void setVariable(String name, String value) {
        plugin.getConfig().set("variables." + name, value);
        plugin.saveConfig();
    }
    public String getVariable(String name) {
        return plugin.getConfig().getString("variables." + name);
    }
    public void reloadGames(){
        for (ChessGame game : freeGames) {
            if (game == null) continue;

            if (game.clockDisplay[0] != null) {
                game.clockDisplay[0].remove();
            }

            if (game.clockDisplay[1] != null) {
                game.clockDisplay[1].remove();
            }

            if (game.nameDisplay[0] != null) {
                game.nameDisplay[0].remove();
            }

            if (game.nameDisplay[1] != null) {
                game.nameDisplay[1].remove();
            }

            if (game.horseDisplays[0] != null) {
                game.horseDisplays[0].remove();
            }

            if (game.horseDisplays[1] != null) {
                game.horseDisplays[1].remove();
            }
        }
        freeGames.clear();
        for (int i = 1; i <= maxSlot; i++){
            String worldName = getVariable("gameSlot." + i+ ".world");
            if (worldName == null) {
                plugin.getLogger().warning("gameSlot." + i + " hat keine World gespeichert.");
                continue;
            }
            World world = Bukkit.getWorld(worldName);
            double x = Double.parseDouble(getVariable("gameSlot." + i+ ".x"));
            double y = Double.parseDouble(getVariable("gameSlot." + i+ ".y"));
            double z = Double.parseDouble(getVariable("gameSlot." + i+ ".z"));
            Location gameLoc = new Location(world, x, y, z);
            ChessGame game = new ChessGame(gameLoc, null, null, i);
            //GameVisualizer.visualizeGame(game, plugin);
            freeGames.add(game);
            GameVisualizer.visualizeField(game);
        }
    }
    public void checkGames() {
        Random random = new Random();

        for (int i = games.size() - 1; i >= 0; i--) {
            ChessGame game = games.get(i);

            if (game == null) {
                continue;
            }
            int timelimit = 1200;

            for (int p = 0; p < 2; p++) {
                int remainingTicks = timelimit - game.ticksSpend[p];

                String time = GameVisualizer.formatTicks(remainingTicks);

                if (game.clockDisplay[p] != null) {
                    game.clockDisplay[p].text(Component.text("Time: " + time));
                }
            }

            if (!game.over) {
                if(game.ticksSpend [game.playerToMove-1]++ > timelimit){
                    game.over = true;
                    String timeoutName = game.players[game.playerToMove-1].getName();
                    game.players [0].sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&cGame | &c&l" + timeoutName+ "&c&ltimed out!"));
                    game.players [1].sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&cGame | &c&l" + timeoutName+ "&c&l timed out!"));
                }
                if (random.nextInt(2000) > 1998 && game.players[0] != null) {
                    Location horse1Loc = game.location.clone();
                    int l = game.playerLocs[0];
                    int[] loc1 = BitField.getCoords(l);

                    horse1Loc.add(loc1[0] + 0.5, 1, loc1[1] + 0.5);

                    game.players[0].playSound(
                            horse1Loc,
                            Sound.ENTITY_HORSE_AMBIENT,
                            0.8f,
                            1.0f
                    );
                }

                if (random.nextInt(2000) > 1998 && game.players[1] != null) {
                    Location horse2Loc = game.location.clone();
                    int l = game.playerLocs[1];
                    int[] loc2 = BitField.getCoords(l);

                    horse2Loc.add(loc2[0] + 0.5, 1, loc2[1] + 0.5);

                    game.players[1].playSound(
                            horse2Loc,
                            Sound.ENTITY_HORSE_AMBIENT,
                            0.8f,
                            1.0f
                    );
                }
            }

            if (game.over || !game.players[game.playerToMove-1].isOnline()) {
                if (game.deletionCooldown-- <= 0) {
                    String worldName = getVariable("spawn.world");
                    World world = Bukkit.getWorld(worldName);
                    double x = Double.parseDouble(getVariable("spawn.x"));
                    double y = Double.parseDouble(getVariable("spawn.y"));
                    double z = Double.parseDouble(getVariable("spawn.z"));
                    Location spawnLocation;
                    if (world != null){
                        spawnLocation = new Location (world, x, y, z);
                    }
                    else{
                        spawnLocation = new Location (Bukkit.getWorld("world"), 1, 1, 1);
                    }

                    game.players [0].teleport(spawnLocation);
                    game.players [1].teleport(spawnLocation);
                    GameVisualizer.delete(game);

                    games.remove(i);

                    ChessGame emptyGame = new ChessGame(game.location, null, null, game.id);
                    freeGames.add(emptyGame);

                    GameVisualizer.visualizeField(emptyGame);
                }
            }
        }
    }
    public void setIntVariable(String name, int value) {
        plugin.getConfig().set("variables." + name, value);
        plugin.saveConfig();
    }
    public boolean isInGame(Player player){
        for(ChessGame game : games){
            if (game == null){continue;}
            if (game.players [0] == player || game.players [1] == player){
                return true;
            }
        }
        return false;
    }
}
