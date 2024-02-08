package heyblock0712.hnkeepinventory.core;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class LanguageCore {
    private final JavaPlugin plugin;
    private FileConfiguration languageConfig;

    public LanguageCore(JavaPlugin plugin) {
        this.plugin = plugin;
        loadLanguageFile();
    }

    private void loadLanguageFile() {
        File languageFile = new File(plugin.getDataFolder(), "language.yml");
        if (!languageFile.exists()) {
            plugin.saveResource("language.yml", false);
        }
        languageConfig = YamlConfiguration.loadConfiguration(languageFile);

        // UTF-8
        try (Reader reader = new InputStreamReader(plugin.getResource("language.yml"), StandardCharsets.UTF_8)) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(reader);
            languageConfig.setDefaults(defaultConfig);
            languageConfig.options().copyDefaults(true);
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load language file: " + e.getMessage());
        }
    }

    public String getString(String path) {
        if (languageConfig == null) {
            loadLanguageFile();
        }
        String message = languageConfig.getString(path, path);
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
