package org.plugin2.mcplugin2;

import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerTextures;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;

public class SkullUtil {

    public static void placeCustomSkull(Location location, String textureHash) {
        try {
            Block block = location.getBlock();

            // Block zu Player Head machen
            block.setType(Material.PLAYER_HEAD);

            // Skull-State holen
            Skull skull = (Skull) block.getState();

            // Neues Profil erstellen
            PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());

            // Texture setzen
            PlayerTextures textures = profile.getTextures();

            URL skinUrl = URI.create("http://textures.minecraft.net/texture/" + textureHash).toURL();
            textures.setSkin(skinUrl);

            profile.setTextures(textures);

            // Profil auf den Skull setzen
            skull.setPlayerProfile(profile);

            // Wichtig: Änderung in die Welt übernehmen
            skull.update();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static ItemStack getHead(Player player) {
        int lifePlayer = (int) player.getHealth();
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1, (short) 3);
        SkullMeta skull = (SkullMeta) item.getItemMeta();
        skull.setDisplayName(player.getName());
        ArrayList<String> lore = new ArrayList<String>();
        lore.add("Custom head");
        skull.setLore(lore);
        skull.setOwner(player.getName());
        item.setItemMeta(skull);
        return item;
    }
    public static ItemDisplay spawnCustomSkullDisplay(Location loc, String textureHash) {
        try {
            ItemStack skullItem = new ItemStack(Material.PLAYER_HEAD);

            SkullMeta meta = (SkullMeta) skullItem.getItemMeta();

            PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
            PlayerTextures textures = profile.getTextures();

            URL skinUrl = URI.create("http://textures.minecraft.net/texture/" + textureHash).toURL();

            textures.setSkin(skinUrl);
            profile.setTextures(textures);

            meta.setPlayerProfile(profile);
            skullItem.setItemMeta(meta);

            ItemDisplay display = loc.getWorld().spawn(loc, ItemDisplay.class);
            display.setItemStack(skullItem);

            display.setTransformation(new Transformation(
                    new Vector3f(0f, 0f, 0f),
                    new AxisAngle4f(0f, 0f, 1f, 0f),
                    new Vector3f(1f, 1f, 1f),
                    new AxisAngle4f(0f, 0f, 1f, 0f)
            ));

            return display;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
