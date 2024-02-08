package heyblock0712.hnkeepinventory.listener;

import heyblock0712.hnkeepinventory.HNKeepInventory;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;

public class PlayerRespawnListener implements Listener {
    private JavaPlugin plugin;

    public PlayerRespawnListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        File deathDataFolder = new File(plugin.getDataFolder(), "deathdata");
        File playerFile = new File(deathDataFolder, player.getUniqueId() + ".yml");
        HNKeepInventory instance = HNKeepInventory.getInstance();

        if (playerFile.exists()) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
            List<ItemStack> items = (List<ItemStack>) config.getList("items");
            if (items != null && !items.isEmpty()) {
                String title = instance.getLanguageCore().getString("title");
                int itemCount = items.size();
                for (ItemStack item : items) {
                    player.getInventory().addItem(item);
                }
                String respawn = instance.getLanguageCore().getString("mode.random.respawn")
                        .replace("{itemCount}", String.valueOf(itemCount));
                player.sendMessage(title + respawn);
            }
            String system = instance.getLanguageCore().getString("mode.random.system")
                    .replace("{playerName}", player.getName());
            plugin.getLogger().info(system);
            playerFile.delete();
        }
    }
}
