package heyblock0712.hnkeepinventory.hook;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

import static org.bukkit.Bukkit.getServer;

public class VaultHook {
    private static Economy econ = null;
    private static Permission perms = null;
    private static Chat chat = null;
    private VaultHook() {
    }

    private static void setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);

        if (rsp != null) econ = rsp.getProvider();
    }

    private static void setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);

        if (rsp != null) chat = rsp.getProvider();
    }

    private static void setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);

        if (rsp != null) perms = rsp.getProvider();
    }

    public static boolean hasEconomy() {
        return econ != null;
    }

    private static double getEconomy(OfflinePlayer target) {
        if (!hasEconomy())
            throw new UnsupportedOperationException("Vault Economy not found, call hasEconomy() to check it first.");

        return econ.getBalance(target);
    }

    public static double getBalance(OfflinePlayer target) {
        if (!hasEconomy())
            throw new UnsupportedOperationException("Vault Economy not found, call hasEconomy() to check it first.");

        return econ.getBalance(target);
    }
    public static boolean withdraw(OfflinePlayer target, double amount) {
        if (!hasEconomy())
            throw new UnsupportedOperationException("Vault Economy not found, call hasEconomy() to check it first.");

        EconomyResponse r = econ.withdrawPlayer(target, amount);
        return r.transactionSuccess();
    }

    public static String deposit(OfflinePlayer target, double amount) {
        if (!hasEconomy())
            throw new UnsupportedOperationException("Vault Economy not found, call hasEconomy() to check it first.");

        return econ.depositPlayer(target, amount).errorMessage;
    }

    public static String formatCurrencySymbol(double amount) {
        if (!hasEconomy())
            throw new UnsupportedOperationException("Vault Economy not found, call hasEconomy() to check it first.");

        return econ.format(amount);
    }

    static {
        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            setupEconomy();
            setupChat();
            setupPermissions();
        }
    }
}
