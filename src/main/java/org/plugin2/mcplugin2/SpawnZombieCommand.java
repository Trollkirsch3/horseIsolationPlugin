package org.plugin2.mcplugin2;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.jetbrains.annotations.NotNull;

public class SpawnZombieCommand implements CommandExecutor {


    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Dieser Command kann nur von Spielern benutzt werden.");
            return true;
        }

        Location spawnLocation = player.getLocation().add(0, 0, 2);

        Zombie zombie = (Zombie) player.getWorld().spawnEntity(spawnLocation, EntityType.ZOMBIE);
        zombie.customName(Component.text("Command Zombie", NamedTextColor.RED));
        zombie.setCustomNameVisible(true);
        zombie.setAI(false);

        player.sendMessage(Component.text("Zombie gespawnt!", NamedTextColor.GREEN));

        return true;
    }
}
