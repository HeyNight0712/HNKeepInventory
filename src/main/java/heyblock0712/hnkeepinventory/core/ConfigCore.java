package heyblock0712.hnkeepinventory.core;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigCore {
    private final JavaPlugin plugin;
    private FileConfiguration config;

    public ConfigCore(JavaPlugin plugin) {
        this.plugin = plugin;
        initialize();
    }

    private void initialize() {
        plugin.saveDefaultConfig();
        this.config = plugin.getConfig();
    }

    public void reloadConfig() {
        plugin.reloadConfig(); // 从插件重新加载配置文件
        this.config = plugin.getConfig(); // 更新引用以反映新的配置状态
    }
    public int getInt(String path) {
        return config.getInt(path, 0);
    }

    public String getString(String path) {
        String message = config.getString(path, "&cNull");
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public boolean getBoolean(String path) {
        return config.getBoolean(path, false);
    }
}
