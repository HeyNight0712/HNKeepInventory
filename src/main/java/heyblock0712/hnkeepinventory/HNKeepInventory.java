package heyblock0712.hnkeepinventory;

import heyblock0712.hnkeepinventory.command.KeepInventoryCommand;
import heyblock0712.hnkeepinventory.core.ConfigCore;
import heyblock0712.hnkeepinventory.core.Folder;
import heyblock0712.hnkeepinventory.core.LanguageCore;
import heyblock0712.hnkeepinventory.hook.VaultHook;
import heyblock0712.hnkeepinventory.listener.PlayerDeathListener;
import heyblock0712.hnkeepinventory.listener.PlayerJoinListener;
import heyblock0712.hnkeepinventory.listener.PlayerRespawnListener;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public final class HNKeepInventory extends JavaPlugin {
    private static JavaPlugin instance;
    private ConfigCore configCore;
    private LanguageCore languageCore;

    @Override
    public void onEnable() {
        // init
        instance = this;
        saveDefaultConfig();
        this.configCore = new ConfigCore(this);
        this.languageCore = new LanguageCore(this);

        File pluginFolder = Folder.ensureFolderExists(getDataFolder());
        File playerDataFolder = Folder.ensureFolderExists(new File(pluginFolder, "playerdata"));
        File deathDataFolder = Folder.ensureFolderExists(new File(pluginFolder, "deathdata"));
        registerCommands();

        // Listener
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerRespawnListener(this), this);
        // plugin

        this.getLogger().info("Enabled HNKeepInventory!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        instance = null;
        this.getLogger().info("Disabled HNKeepInventory!");
    }

    public static HNKeepInventory getInstance() {return (HNKeepInventory) instance;}

    private void registerCommands() {

        PluginCommand keepinventory =this.getCommand("keepinventory");
        if (keepinventory != null) {
            keepinventory.setExecutor(new KeepInventoryCommand(this));
        }
    }
    public ConfigCore getConfigCore() {
        return configCore;
    }

    public LanguageCore getLanguageCore() {
        return languageCore;
    }
}
