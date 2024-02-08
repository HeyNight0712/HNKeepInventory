package heyblock0712.hnkeepinventory.listener;

import heyblock0712.hnkeepinventory.core.Folder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PlayerJoinListener implements Listener {
    private JavaPlugin plugin;

    public PlayerJoinListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        File playerDateFolder = Folder.ensureFolderExists(new File(plugin.getDataFolder(), "playerdata"));
        File playerFile = new File(playerDateFolder, event.getPlayer().getUniqueId() + ".json");
        try {
            if (playerFile.createNewFile()) {
                // 確認數據是否存在
                JSONObject playerData = new JSONObject();
                JSONObject config = new JSONObject();

                config.put("keepInventory", false);
                playerData.put("config", config);

                try (FileWriter file = new FileWriter(playerFile)){
                    file.write(playerData.toJSONString());
                    file.flush();
                }
            }
        } catch (IOException e) {
            plugin.getLogger().warning("創建資料出現問題: " + playerFile);
        }
    }
}
