package heyblock0712.hnkeepinventory.listener;

import heyblock0712.hnkeepinventory.HNKeepInventory;
import heyblock0712.hnkeepinventory.hook.VaultHook;
import heyblock0712.hnkeepinventory.utils.KeepInventoryUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;

public class PlayerDeathListener implements Listener {
    private JavaPlugin plugin;

    public PlayerDeathListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        try {
            File playerFile = new File(
                    plugin.getDataFolder() + File.separator + "playerdata" + File.separator + event.getEntity().getUniqueId() + ".json");

            // init
            JSONParser parser = new JSONParser();
            JSONObject data = (JSONObject) parser.parse(new FileReader(playerFile));
            JSONObject config = (JSONObject) data.get("config");
            Boolean keepInventory = (Boolean) config.get("keepInventory"); // 獲取玩家

            HNKeepInventory instance = HNKeepInventory.getInstance();
            Player player = event.getEntity();
            Player killer = player.getKiller();

            // config 設置
            String title = instance.getLanguageCore().getString("title");
            String pvpDeathMode = instance.getConfigCore().getString("global.pvpDeathMode");
            String deathMode = instance.getConfigCore().getString("global.deathMode");

            int modeVaultAmount = instance.getConfigCore().getInt("global.mode.vault.amount");
            int modeRandomChance = instance.getConfigCore().getInt("global.mode.random.chance");

            String mode;

            if (killer != null) {
                mode = pvpDeathMode;
            } else {
                mode = deathMode;
            }
            String messageKey;
            String message;

            event.getEntity().sendMessage(mode);

            switch (mode) {
                case "random":
                    KeepInventoryUtils.Result result = KeepInventoryUtils.setRandom(event, plugin.getDataFolder(), modeRandomChance);
                    messageKey = "mode.random.death";
                    message = instance.getLanguageCore().getString(messageKey)
                            .replace("{droppedItemsCount}", String.valueOf(result.droppedItemsCount))
                            .replace("{droppedExpCount}", String.valueOf(result.droppedExpCount));
                    event.getEntity().sendMessage(title + message);
                    return;
                case "protect":
                    KeepInventoryUtils.setProtect(event);
                    messageKey = "mode.default.enable";
                    break;
                case "disable":

                    return;
                case "bounty":
                    if (killer != null) {
                        messageKey = "mode.bounty.designing";
                    } else {
                        general(event, instance, keepInventory);
                        return;
                    }
                    break;
                case "vault":
                    if (!vault(event, instance, keepInventory, player, modeVaultAmount)) {
                        general(event, instance, keepInventory);
                    }
                    return;
                default:
                    general(event, instance, keepInventory);
                    return;
            }

            // 發送 訊息
            message = instance.getLanguageCore().getString(messageKey)
                    .replace("{amount}", VaultHook.formatCurrencySymbol(modeVaultAmount))
                    .replace("{balance}", String.valueOf(VaultHook.getBalance(player)));
            event.getEntity().sendMessage(title + message);

        } catch (Exception e) {
            plugin.getLogger().warning(String.valueOf(e));
        }
    }

    public void general(PlayerDeathEvent event, HNKeepInventory instance, Boolean keepInventory){
        String title = instance.getLanguageCore().getString("title");
        String messageKey;
        if (Boolean.TRUE.equals(keepInventory)) {
            messageKey = "mode.default.enable";

            event.setKeepInventory(true);
            event.setKeepLevel(true);
            event.getDrops().clear();
            event.setDroppedExp(0);
        } else {
            messageKey = "mode.default.disable";
        }
        String message = instance.getLanguageCore().getString(messageKey);
        event.getEntity().sendMessage(title + message);
    }

    public boolean vault(PlayerDeathEvent event, HNKeepInventory instance, Boolean keepInventory, Player player, int amount){
        if(VaultHook.hasEconomy()){
            double balance = VaultHook.getBalance(player);
            String title = instance.getLanguageCore().getString("title");
            String messageKey;
            if (Boolean.TRUE.equals(keepInventory)) {
                if (amount > 0) { // 當小於 0 將視同無須經濟
                    if (balance >= amount) {
                        boolean success = VaultHook.withdraw(player, amount);
                        if (success) {
                            messageKey = "mode.vault.enable";
                            KeepInventoryUtils.setProtect(event);
                        } else {
                            messageKey = "mode.vault.error";
                        }
                    } else {
                        messageKey = "mode.vault.disable";
                    }
                } else {
                    return false;
                }
            } else {
                messageKey= "mode.default.disable";
            }
            String message = instance.getLanguageCore().getString(messageKey)
                    .replace("{amount}", VaultHook.formatCurrencySymbol(amount))
                    .replace("{balance}", String.valueOf(VaultHook.getBalance(player)));
            event.getEntity().sendMessage(title + message);
            return true;
        }
        return false;
    }
}
