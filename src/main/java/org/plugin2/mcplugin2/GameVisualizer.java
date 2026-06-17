package org.plugin2.mcplugin2;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.awt.*;
import java.util.Random;

public class GameVisualizer {
    static void visualizeGame(ChessGame game, JavaPlugin plugin){
        if (game.players[0] != null) {
            game.nameDisplay[0].text(Component.text(game.players[0].getName(), NamedTextColor.GOLD));
        } else {
            game.nameDisplay[0].text(Component.text("Player 1", NamedTextColor.GRAY));
        }

        if (game.players[1] != null) {
            game.nameDisplay[1].text(Component.text(game.players[1].getName(), NamedTextColor.GOLD));
        } else {
            game.nameDisplay[1].text(Component.text("Player 2", NamedTextColor.GRAY));
        }
        Location loc = game.location.clone();
        boolean white = true;
        Location airLoc = loc.clone();
        airLoc.add(0,1,0);
        for (ItemDisplay barrier : game.barrierDisplays){
            barrier.remove();
        }
        for (int x = 0; x<8; x++){
            white = !white;
            boolean tempWhite = white;
            for (int z = 0; z<8; z++){
                Location tempLoc = loc.clone();
                Location tempAirLoc = airLoc.clone();
                tempLoc.add(x,0,z);
                tempAirLoc.add(x,0,z);
                tempAirLoc.getBlock().setType(Material.AIR);
                int bitLoc = BitField.bitLoc(x,z);
                removeDisplaysAboveBlock(tempLoc.getBlock());
                if(tempWhite){
                    tempLoc.getBlock().setType(Material.POLISHED_DIORITE);
                }
                else{
                    tempLoc.getBlock().setType(Material.POLISHED_BLACKSTONE);
                }
                tempWhite = !tempWhite;

                if (!BitField.isFree(game.field, bitLoc) && bitLoc != game.playerLocs [0] && bitLoc != game.playerLocs [1]){
                    tempLoc.getBlock().setType(Material.RED_CONCRETE);
                    Location displayLoc = tempLoc.clone().add(0.5, 1.2, 0.5);

                    ItemDisplay display = displayLoc.getWorld().spawn(displayLoc, ItemDisplay.class);
                    display.setItemStack(new ItemStack(Material.BARRIER));
                    display.setTransformation(new Transformation(
                            new Vector3f(0f, 0.01f, 0f), // Position innerhalb des Blocks
                            new AxisAngle4f((float) Math.toRadians(90), 1f, 0f, 0f), // Rotation
                            new Vector3f(1f, 1f, 1f), // Größe
                            new AxisAngle4f(0f, 0f, 1f, 0f) // keine zweite Rotation
                    ));
                    game.barrierDisplays.add(display);
                    //display.setGlowing(true);
                    //display.setVisibleByDefault(false);
                    //game.getCurrentPlayer().showEntity(plugin, display);
                }

            }
        }
        Location tempLoc1 = game.location.clone();
        Location tempLoc2 = game.location.clone();
        int [] pLoc1 = BitField.getCoords(game.playerLocs [0]);
        int [] pLoc2 = BitField.getCoords(game.playerLocs [1]);
        tempLoc1.add(pLoc1 [0]+0.5,1.5,pLoc1[1]+0.5);
        tempLoc2.add(pLoc2[0]+0.5,1.5,pLoc2[1]+0.5);
        if (game.horseDisplays[0] != null){
            game.horseDisplays[0].remove();
        }
        if (game.horseDisplays[1] != null){
            game.horseDisplays[1].remove();
        }
        game.horseDisplays[0] = (SkullUtil.spawnCustomSkullDisplay(tempLoc1, "d9cb9c8b36273b5b4947f6002907dc6d4f75d429a696b8f7996cbbcb6b56f85f"));
        game.horseDisplays[1] = (SkullUtil.spawnCustomSkullDisplay(tempLoc2, "246104eddf634070a2be2cfc6b7adffdf3e851f74e0b0781a217edd7aba45b85"));
        game.horseDisplays [game.playerToMove-1].setGlowing(true);
        if (game.over){
            tempLoc1 = game.location.clone();
            tempLoc2 = game.location.clone();
            pLoc1 = BitField.getCoords(game.playerLocs [0]);
            pLoc2 = BitField.getCoords(game.playerLocs [1]);
            tempLoc1.add(pLoc1 [0]+0.5,1.5,pLoc1[1]+0.5);
            tempLoc2.add(pLoc2[0]+0.5,1.5,pLoc2[1]+0.5);
            if (game.horseDisplays[0] != null){
                game.horseDisplays[0].remove();
            }
            if (game.horseDisplays[1] != null){
                game.horseDisplays[1].remove();
            }
            game.horseDisplays[0] = (SkullUtil.spawnCustomSkullDisplay(tempLoc1, "d9cb9c8b36273b5b4947f6002907dc6d4f75d429a696b8f7996cbbcb6b56f85f"));
            game.horseDisplays[1] = (SkullUtil.spawnCustomSkullDisplay(tempLoc2, "246104eddf634070a2be2cfc6b7adffdf3e851f74e0b0781a217edd7aba45b85"));
            ItemDisplay winningHorse = game.horseDisplays [game.nextPlayer(game.playerToMove)-1];
            winningHorse.setGlowing(true);
            winningHorse.setGlowColorOverride(Color.LIME);
            loc = game.location.clone();
            game.players [game.nextPlayer(game.playerToMove)-1].getWorld().playSound(
                    game.players [game.nextPlayer(game.playerToMove)-1],
                    Sound.BLOCK_NOTE_BLOCK_BELL,
                    1.0f,
                    1.0f
            );
            spawnWinFirework(loc.clone().add(8,1,0));
            spawnWinFirework(loc.clone().add(0,1,0));
            spawnWinFirework(loc.clone().add(0,1,8));
            spawnWinFirework(loc.clone().add(8,1,8));

            TextDisplay textDisplay = loc.getWorld().spawn(loc.clone().add(4, 3, 4), TextDisplay.class);
            Player player = game.players[game.nextPlayer(game.playerToMove)-1];
            Component text = Component.text(player.getName() + " won!", NamedTextColor.GREEN);
            text.decorate(TextDecoration.BOLD);
            textDisplay.text(text);
            game.textDisplays.add(textDisplay);
            textDisplay.setBillboard(TextDisplay.Billboard.CENTER); // dreht sich zum Spieler
            textDisplay.setSeeThrough(false);
            textDisplay.setShadowed(true);
            textDisplay.setTransformation(new Transformation(
                    new Vector3f(0f, 0f, 0f),          // Verschiebung
                    new AxisAngle4f(0f, 0f, 1f, 0f),   // Rotation
                    new Vector3f(3.5f, 3.5f, 3.5f),          // Größe
                    new AxisAngle4f(0f, 0f, 1f, 0f)    // zweite Rotation
            ));
        }
    }
    static void visualizePossibleMoves(ChessGame game, JavaPlugin plugin){
        clearDisplays(game);
        int [] possible = new int [8];
        long possibleMoves = BitField.getPossibleMoves(game.field, game.playerLocs[game.playerToMove - 1]);
        int count = BitField.bitMovesToArray(possibleMoves, possible);
        for (int i = 0; i < count; i++){
            int move = possible [i];
            Location moveLoc = game.location.clone();
            moveLoc.setYaw(0f);
            moveLoc.setPitch(0f);
            int [] coords = BitField.getCoords(move);
            moveLoc.add(coords [0], 1, coords [1]);
            BlockDisplay display = moveLoc.getWorld().spawn(moveLoc, BlockDisplay.class);
            display.setBlock(Bukkit.createBlockData(Material.LIME_STAINED_GLASS));
            display.setTransformation(new Transformation(
                        new Vector3f(0f, -0.05f, 0f),              // Verschiebung
                        new AxisAngle4f(0f, 0f, 1f, 0f),        // linke Rotation
                        new Vector3f(1f, 0.1f, 1f),              // Größe
                        new AxisAngle4f(0f, 0f, 1f, 0f)         // rechte Rotation
                ));
            display.setGlowing(true);
            display.setVisibleByDefault(false);
            game.getCurrentPlayer().showEntity(plugin, display);
            game.displays.add(display);
            game.displayMoves.put(display.getUniqueId(), move);

            Location interactionLoc = game.location.clone();
            interactionLoc.add(coords[0] + 0.5, 1.05, coords[1] + 0.5);
            Interaction interaction = interactionLoc.getWorld().spawn(interactionLoc, Interaction.class);

            interaction.setInteractionWidth(1.0f);
            interaction.setInteractionHeight(0.3f);

            // falls nur aktueller Spieler es sehen/clicken soll
            interaction.setVisibleByDefault(false);
            game.getCurrentPlayer().showEntity(plugin, interaction);

            // Move speichern
            game.moveClickEntities.add(interaction);
            game.displayMoves.put(interaction.getUniqueId(), move);
        }
    }
    static void clearDisplays(ChessGame game) {
        for (BlockDisplay display : game.displays) {
            display.remove();
        }


        for (Entity entity : game.moveClickEntities) {
            entity.remove();
        }
        game.moveClickEntities.clear();

        game.displayMoves.clear();
    }
    public static void removeDisplaysAboveBlock(Block block) {
        Location center = block.getLocation().clone().add(0.5, 1.0, 0.5);

        for (Entity entity : block.getWorld().getNearbyEntities(center, 0.6, 1.0, 0.6)) {
            if (entity instanceof Display) {
                entity.remove();
            }
        }
    }
    static void delete(ChessGame game){
        clearDisplays(game);
        game.clockDisplay[0].remove();
        game.clockDisplay[1].remove();
        game.nameDisplay[0].remove();
        game.nameDisplay[1].remove();
        if (game.horseDisplays[0] != null){
            game.horseDisplays[0].remove();
        }
        if (game.horseDisplays[1] != null){
            game.horseDisplays[1].remove();
        }
        for (TextDisplay textDisplay : game.textDisplays) {
            textDisplay.remove();
        }
        game.textDisplays.clear();
        for (ItemDisplay barrier : game.barrierDisplays){
            barrier.remove();
        }
        game.barrierDisplays.clear();
        Location loc = game.location.clone();
        Location airLoc = game.location.clone().add(0,1,0);
        for (int x = 0; x<8; x++){
            for (int z = 0; z<8; z++){
                Location tempLoc = loc.clone();
                Location tempAirLoc = airLoc.clone();
                tempLoc.add(x,0,z);
                tempAirLoc.add(x,0,z);
                tempAirLoc.getBlock().setType(Material.AIR);
                Block block = tempLoc.getBlock();
                block.setType(Material.AIR);
                removeDisplaysAboveBlock(block);
            }
        }
    }
    public static void spawnWinFirework(Location loc) {
        Firework firework = loc.getWorld().spawn(loc, Firework.class);
        firework.setSilent(true);
        FireworkMeta meta = firework.getFireworkMeta();

        meta.addEffect(FireworkEffect.builder()
                .withColor(Color.GREEN)
                .withFade(Color.YELLOW)
                .with(FireworkEffect.Type.BALL_LARGE)
                .trail(true)
                .flicker(true)
                .build());

        meta.setPower(1); // Flughöhe

        firework.setFireworkMeta(meta);
    }
    static void visualizeField(ChessGame game){
        Location loc = game.location.clone();
        boolean white = true;
        Location airLoc = loc.clone();
        airLoc.add(0,1,0);
        for (int x = 0; x<8; x++){
            white = !white;
            boolean tempWhite = white;
            for (int z = 0; z<8; z++){
                Location tempLoc = loc.clone();
                Location tempAirLoc = airLoc.clone();
                tempLoc.add(x,0,z);
                tempAirLoc.add(x,0,z);
                tempAirLoc.getBlock().setType(Material.AIR);
                int bitLoc = BitField.bitLoc(x,z);
                removeDisplaysAboveBlock(tempLoc.getBlock());
                if(tempWhite){
                    tempLoc.getBlock().setType(Material.POLISHED_DIORITE);
                }
                else{
                    tempLoc.getBlock().setType(Material.POLISHED_BLACKSTONE);
                }
                tempWhite = !tempWhite;

            }
        }
    }
    public static TextDisplay spawnStaticText(Location loc, String text) {
        TextDisplay display = loc.getWorld().spawn(loc, TextDisplay.class);

        display.text(Component.text(text, NamedTextColor.GREEN));

        // Wichtig: Text dreht sich NICHT automatisch zum Spieler
        display.setBillboard(TextDisplay.Billboard.FIXED);

        // Optional
        display.setShadowed(true);
        display.setSeeThrough(false);
        display.setGravity(false);

        return display;
    }
    public static String formatTicks(int ticks){
        double s = Math.floor(ticks/20);
        int minutes = (int) Math.floor(s /60);
        int seconds = (int) Math.floor(s - minutes * 60);
        String text = minutes + " : " + seconds;
        return text;
    }
}
