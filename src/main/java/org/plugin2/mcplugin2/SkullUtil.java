package org.plugin2.mcplugin2;

import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.profile.PlayerTextures;

import java.net.URI;
import java.net.URL;
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
}
