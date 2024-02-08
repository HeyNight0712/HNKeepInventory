package heyblock0712.hnkeepinventory.utils;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class KeepInventoryUtils {
    public static boolean setBounty() {
        // 賞金 依賴 <bounty>
        return false;
    }
    public static boolean setProtect(PlayerDeathEvent event) {
        // 保護
        event.setKeepInventory(true);
        event.setKeepLevel(true);
        event.getDrops().clear();
        event.setDroppedExp(0);
        return true;
    }
    public static boolean setNotProtect() {
        // 無保護 全噴
        return false;
    }

    public static boolean setDefault() {
        // 預設 玩家設定
        return true;
    }

    /**
     * 隨機掉落 經驗 物品 數量
     */
    public static Result setRandom(PlayerDeathEvent event, File pluginFolder, int chance) {
        Player player = event.getEntity();
        Random random = new Random();
        List<ItemStack> itemsToKeep = new ArrayList<>();
        int originalDroppedExp = event.getDroppedExp();
        int droppedExpCount = 0;

        chance = Math.max(0, Math.min(chance, 100));

        // 隨機掉落物品
        Iterator<ItemStack> iterator = event.getDrops().iterator();
        while (iterator.hasNext()) {
            ItemStack item = iterator.next();
            if (random.nextInt(100) < chance) { // 機率保留
                itemsToKeep.add(item.clone()); // 複製保留
                iterator.remove(); // 阻止保留掉落地面
            }
        }

        int droppedItemsCount = event.getDrops().size();

        // 隨機掉落經驗量
        if (originalDroppedExp > 0) {
            double expDropRate = random.nextDouble();
            droppedExpCount = (int) (originalDroppedExp * expDropRate);
            event.setDroppedExp(droppedExpCount);
        }

        // 保存保留物品到文件
        if (!itemsToKeep.isEmpty()) {
            saveItems(player, itemsToKeep, new File(pluginFolder, "deathdata"));
        }

        return new Result(droppedItemsCount, originalDroppedExp - droppedExpCount);
    }

    private static void saveItems(Player player, List<ItemStack> items, File deathDataFolder) {
        if (!deathDataFolder.exists()) {deathDataFolder.mkdirs();}
        File playerFile = new File(deathDataFolder, player.getUniqueId() + ".yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
        config.set("items", items);
        try {
            config.save(playerFile);
        } catch (Exception e) {}
    }

    public static class Result {

        public final int droppedItemsCount;
        public final int droppedExpCount;

        public Result(int droppedItemsCount, int droppedExpCount) {
            this.droppedItemsCount = droppedItemsCount;
            this.droppedExpCount = droppedExpCount;
        }
    }
}
