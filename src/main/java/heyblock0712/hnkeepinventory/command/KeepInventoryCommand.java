package heyblock0712.hnkeepinventory.command;

import heyblock0712.hnkeepinventory.HNKeepInventory;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class KeepInventoryCommand implements CommandExecutor {
    private JavaPlugin plugin;

    public KeepInventoryCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        String messageKey;
        String message;
        String title = HNKeepInventory.getInstance().getLanguageCore().getString("title");
        if (!(commandSender instanceof Player)) {
            messageKey = "commands.keepInventory.notaPlayer";
            commandSender.sendMessage(title + messageKey);
            return true;
        }

        if (strings.length > 0 && strings[0].equalsIgnoreCase("reload")) {
            if (commandSender.hasPermission("hnkeepinventory.reload")) {
                HNKeepInventory.getInstance().getConfigCore().reloadConfig();
                messageKey = "commands.keepInventory.reload";
            } else {
                messageKey = "commands.keepInventory.noPermission";
            }
        }else{
            String mode = HNKeepInventory.getInstance().getConfigCore().getString("global.deathMode");
            if (mode.equalsIgnoreCase("default") || mode.equalsIgnoreCase("vault")) {
                message = setToggle(commandSender);
            } else {
                message = HNKeepInventory.getInstance().getLanguageCore().getString("commands.keepInventory.disable")
                        .replace("{mode}", mode);
            }
            commandSender.sendMessage(title + message);
            return true;
        }

        message = HNKeepInventory.getInstance().getLanguageCore().getString(messageKey);
        commandSender.sendMessage(title + message);
        return true;
    }

    public String setToggle(CommandSender commandSender) {
        Boolean keepInventory = null;
        String messageKey;
        Player player = (Player) commandSender;
        File playerFile = new File(
                plugin.getDataFolder() + File.separator + "playerdata" + File.separator + player.getUniqueId() + ".json"
        );

        try {
            JSONObject data;
            if (playerFile.exists()) {
                JSONParser parser = new JSONParser();
                data = (JSONObject) parser.parse(new FileReader(playerFile));
            } else {
                data = new JSONObject();
                data.put("config", new JSONObject());
            }

            JSONObject config = (JSONObject) data.get("config");
            keepInventory = (Boolean) config.getOrDefault("keepInventory", false);
            keepInventory = !keepInventory; // 反轉keepInventory的值

            config.put("keepInventory", keepInventory);
            data.put("config", config);

            try (FileWriter file = new FileWriter(playerFile)) {
                file.write(data.toJSONString());
                file.flush();
            }

            messageKey = "commands.keepInventory.toggle";
        } catch (Exception e) {
            plugin.getLogger().warning(String.valueOf(e));
            messageKey = "commands.keepInventory.error";
        }

        return HNKeepInventory.getInstance().getLanguageCore().getString(messageKey)
                .replace("{keepInventory}", String.valueOf(keepInventory));
    }
}
