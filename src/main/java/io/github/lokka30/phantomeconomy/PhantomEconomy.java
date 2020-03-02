package io.github.lokka30.phantomeconomy;

import de.leonhard.storage.LightningBuilder;
import de.leonhard.storage.internal.FlatFile;
import io.github.lokka30.phantomeconomy.commands.CBalance;
import io.github.lokka30.phantomeconomy.commands.CEconomy;
import io.github.lokka30.phantomeconomy.utils.*;
import net.milkbowl.vault.economy.Economy;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;
import java.util.logging.Logger;

public class PhantomEconomy extends JavaPlugin {

    private static PhantomEconomy instance;
    public EconomyManager economyManager;
    public FlatFile settings;
    public FlatFile messages;
    public FlatFile data;

    public static PhantomEconomy getInstance() {
        return instance;
    }

    public EconomyManager getEconomyManager() {
        return economyManager;
    }

    public boolean hookVault = false;
    private Economy provider;

    @Override
    public void onLoad() {
        instance = this;
        economyManager = new EconomyManager(this);
    }

    @Override
    public void onEnable() {
        log(LogLevel.INFO, "&8[&71&8/&76&8] &7Checking compatibility...");
        checkCompatibility();

        log(LogLevel.INFO, "&8[&72&8/&76&8] &7Loading files...");
        loadFiles();

        log(LogLevel.INFO, "&8[&73&8/&76&8] &7Registering events...");
        registerEvents();

        log(LogLevel.INFO, "&8[&74&8/&76&8] &7Registering commands...");
        registerCommands();

        log(LogLevel.INFO, "&8[&75&8/&76&8] &7Hooking to Vault...");
        hookVault();

        log(LogLevel.INFO, "&8[&76&8/&76&8] &7Hooking to bStats metrics...");
        new Metrics(this);
        log(LogLevel.INFO, "Loaded successfuly, enjoy!");
        checkUpdates();
    }

    @Override
    public void onDisable() {
        unhookVault();

        instance = null;
        economyManager = null;
    }

    private void checkCompatibility() {
        final String currentVersion = getServer().getVersion();
        final String recommendedVersion = Utils.getRecommendedServerVersion();
        if (currentVersion.contains(recommendedVersion)) {
            log(LogLevel.INFO, "Server is running supported version &a" + currentVersion + "&7.");
        } else {
            log(LogLevel.WARNING, " ");
            log(LogLevel.WARNING, "Server is running &cunsupported&7 version &a" + currentVersion + "&7.");
            log(LogLevel.WARNING, "The recommended version is &a" + recommendedVersion + "&7.");
            log(LogLevel.WARNING, "You will not get support with the plugin whilst using an unsupported version!");
            log(LogLevel.WARNING, " ");
        }

        final PluginManager pm = getServer().getPluginManager();
        if (pm.getPlugin("Vault") != null) {
            hookVault = true;
        }
    }

    private void loadFiles() {
        //Load the files
        final String path = "plugins/PhantomEconomy/";
        settings = LightningBuilder
                .fromFile(new File(path + "settings"))
                .addInputStreamFromResource("settings.yml")
                .createYaml();
        messages = LightningBuilder
                .fromFile(new File(path + "messages"))
                .addInputStreamFromResource("messages.yml")
                .createYaml();
        data = LightningBuilder
                .fromFile(new File(path + "data"))
                .addInputStreamFromResource("data.json")
                .createJson();

        //Check if they exist
        final File settingsFile = new File(path + "settings.yml");
        final File messagesFile = new File(path + "messages.yml");
        final File dataFile = new File(path + "data.json");

        if (!(settingsFile.exists() && !settingsFile.isDirectory())) {
            log(LogLevel.INFO, "File &asettings.yml&7 doesn't exist. Creating it now.");
            saveResource("settings.yml", false);
        }

        if (!(messagesFile.exists() && !messagesFile.isDirectory())) {
            log(LogLevel.INFO, "File &amessages.yml&7 doesn't exist. Creating it now.");
            saveResource("messages.yml", false);
        }

        if (!(dataFile.exists() && !dataFile.isDirectory())) {
            log(LogLevel.INFO, "File &adata.json&7 doesn't exist. Creating it now.");
            saveResource("data.json", false);
        }

        //Check their versions
        if (settings.get("file-version", 0) != Utils.getRecommendedSettingsVersion()) {
            log(LogLevel.SEVERE, "File &asettings.yml&7 is out of date! Errors are likely to occur! Reset it or merge the old values to the new file.");
        }

        if (messages.get("file-version", 0) != Utils.getRecommendedMessagesVersion()) {
            log(LogLevel.SEVERE, "File &amessages.yml&7 is out of date! Errors are likely to occur! Reset it or merge the old values to the new file.");
        }

        if (data.get("file-version", 0) != Utils.getRecommendedDataVersion()) {
            log(LogLevel.SEVERE, "File &adata.yml&7 is out of date! Errors are likely to occur! Reset it or merge the old values to the new file.");
        }
    }

    private void registerEvents() {
        //TODO final PluginManager pm = getServer().getPluginManager();
        //TODO balance sign
    }

    private void registerCommands() {
        Objects.requireNonNull(getCommand("economy")).setExecutor(new CEconomy());
        Objects.requireNonNull(getCommand("balance")).setExecutor(new CBalance());
    }

    public void hookVault() {
        if (hookVault) {
            provider = new EconomyImplementer();
            Bukkit.getServicesManager().register(Economy.class, provider, this, ServicePriority.Normal);
            log(LogLevel.INFO, "Vault hooked successfuly.");
        } else {
            log(LogLevel.INFO, "Vault isn't installed, hook aborted.");
        }
    }

    public void unhookVault() {
        if (hookVault) {
            Bukkit.getServicesManager().unregister(Economy.class, provider);
            log(LogLevel.INFO, "Vault unhooked successfuly.");
        } else {
            log(LogLevel.INFO, "Vault isn't installed, unhook aborted.");
        }
    }

    private void checkUpdates() {
        if (settings.get("updater", true)) {
            log(LogLevel.INFO, "&8[&7Update Checker&8] &7Starting version comparison...");
            new UpdateChecker(this, 75053).getVersion(version -> {
                if (getDescription().getVersion().equalsIgnoreCase(version)) {
                    log(LogLevel.INFO, "&8[&7Update Checker&8] &7You're running the latest version.");
                } else {
                    log(LogLevel.WARNING, "&8[&7Update Checker&8] &7There's a new update available: &a" + version + "&7. You're running &a" + getDescription().getVersion() + "&7.");
                }
            });
        }
    }

    public String colorize(final String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg.replaceAll("%arrow%", "►"));
    }

    public void log(final LogLevel level, String msg) {
        final Logger logger = getLogger();
        msg = "&7" + msg;
        switch (level) {
            case INFO:
                logger.info(colorize(msg));
                break;
            case WARNING:
                logger.warning(colorize(msg));
                break;
            case SEVERE:
                logger.severe(colorize(msg));
                break;
        }
    }
}
