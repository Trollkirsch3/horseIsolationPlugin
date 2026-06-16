package org.plugin2.mcplugin2;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ChessCommands implements CommandExecutor {
    Main main;
    JavaPlugin plugin;

    public ChessCommands (Main main, JavaPlugin plugin){
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
        Player player;
        switch (command.getName().toLowerCase()) {

            case "newgame":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("Nur Spieler können diesen Command benutzen.");
                    return true;
                }
                player = (Player) sender;
                Player player2 = Bukkit.getPlayer(args[0]);

                if (player2 == null) {
                    player.sendMessage("This play is not online.");
                    return true;
                }
                ChessGame game = main.getNewGame();

                if (game == null) {
                    player.sendMessage("Kein freier Spielslot verfügbar.");
                    return true;
                }
                game.players [0] = player;
                game.players [1] = player2;

                Location player1Loc = game.location.clone();
                player1Loc.add(4, 1, 4);

                Location player2Loc = game.location.clone();
                player2Loc.add(4, 1, 4);


                player1Loc.setYaw(player.getLocation().getYaw());
                player1Loc.setPitch(player.getLocation().getPitch());

                player2Loc.setYaw(player2.getLocation().getYaw());
                player2Loc.setPitch(player2.getLocation().getPitch());

                player.teleport(player1Loc);
                player2.teleport(player2Loc);

                main.games.add(game);
                GameVisualizer.visualizeGame(game, plugin);
                GameVisualizer.visualizePossibleMoves(game, plugin);
                main.freeGames.remove(game);
                break;

            case "setgameloc":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("Nur Spieler können diesen Command benutzen.");
                    return true;
                }
                player = (Player) sender;
                main.setVariable("gameSlot." + args [0]+ ".world", player.getLocation().getWorld().getName());
                main.setVariable("gameSlot." + args [0]+ ".x", Double.toString(player.getLocation().getX()));
                main.setVariable("gameSlot." + args [0]+ ".z", Double.toString(player.getLocation().getZ()));
                main.setVariable("gameSlot." + args [0]+ ".y", Double.toString(player.getLocation().getY()));
                main.reloadGames();
                break;

            case "setmaxgames":
                int amount = Integer.parseInt(args [0]);
                main.setIntVariable("maxSlots", amount);
                main.maxSlot = Integer.parseInt(args [0]);
                main.reloadGames();
                break;
            case "duel":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("Nur Spieler können diesen Command benutzen.");
                    return true;
                }
                player = (Player) sender;
                Player challenged = Bukkit.getPlayer(args [0]);
                if (challenged == null || !challenged.isOnline()) {
                    sender.sendMessage("This player is not online!");
                    return true;
                }
                if(main.isInGame(player)|| main.isInGame(challenged)){
                    sender.sendMessage("Duel request canceled: a player is not available");
                    return true;
                }
                main.gameRequests.put(challenged.getUniqueId(), player.getUniqueId());
                challenged.sendMessage(player.getName() + " challenged you to a duel: /duelaccept " + player.getName());
                break;
            case "acceptduel":

                if (!(sender instanceof Player)) {
                    sender.sendMessage("Nur Spieler können diesen Command benutzen.");
                    return true;
                }
                player = (Player) sender;
                UUID requesterUUID = main.gameRequests.get(player.getUniqueId());
                if (requesterUUID == null) {
                    player.sendMessage("You do not have any duel requests");
                    return true;
                }
                player2 = Bukkit.getPlayer(requesterUUID);
                if (player2 == null || !player2.isOnline()) {
                    sender.sendMessage("This player is not online!");
                    return true;
                }
                main.gameRequests.remove(player.getUniqueId());
                if(main.isInGame(player)|| main.isInGame(player2)){
                    sender.sendMessage("Duel request canceled: a player is not available");
                    return true;
                }
                if (player2 == null || !player2.isOnline()) {
                    player.sendMessage("This player is not online.");
                    return true;
                }
                game = main.getNewGame();

                if (game == null) {
                    player.sendMessage("Kein freier Spielslot verfügbar.");
                    return true;
                }
                game.players [0] = player;
                game.players [1] = player2;

                player1Loc = game.location.clone();
                player1Loc.add(4, 1, 4);

                player2Loc = game.location.clone();
                player2Loc.add(4, 1, 4);


                player1Loc.setYaw(player.getLocation().getYaw());
                player1Loc.setPitch(player.getLocation().getPitch());

                player2Loc.setYaw(player2.getLocation().getYaw());
                player2Loc.setPitch(player2.getLocation().getPitch());

                player.teleport(player1Loc);
                player2.teleport(player2Loc);

                main.games.add(game);
                GameVisualizer.visualizeGame(game, plugin);
                GameVisualizer.visualizePossibleMoves(game, plugin);
                main.freeGames.remove(game);
                break;
            case "setchessspawn":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("Nur Spieler können diesen Command benutzen.");
                    return true;
                }
                player = (Player) sender;
                main.setVariable("spawn.world", player.getLocation().getWorld().getName());
                main.setVariable("spawn.x", Double.toString(player.getLocation().getX()));
                main.setVariable("spawn.z", Double.toString(player.getLocation().getZ()));
                main.setVariable("spawn.y", Double.toString(player.getLocation().getY()));
                break;
        }

        return true;
    }

}
