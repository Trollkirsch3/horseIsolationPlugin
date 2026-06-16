package org.plugin2.mcplugin2;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

public class BlockDataUtil {

    public static void setOwnerMetadata(JavaPlugin plugin, Block block, Player player) {
        block.setMetadata("owner", new FixedMetadataValue(plugin, player.getUniqueId().toString()));
    }

    public static String getOwnerMetadata(JavaPlugin plugin, Block block) {
        if (!block.hasMetadata("owner")) {
            return null;
        }

        return block.getMetadata("owner").stream()
                .filter(value -> value.getOwningPlugin() == plugin)
                .findFirst()
                .map(value -> value.asString())
                .orElse(null);
    }
}
