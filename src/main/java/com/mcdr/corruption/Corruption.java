package com.mcdr.corruption;

import com.mcdr.corruption.CorMetrics.Graph;
import com.mcdr.corruption.command.CommandManager;
import com.mcdr.corruption.config.BossConfig;
import com.mcdr.corruption.config.ConfigManager;
import com.mcdr.corruption.config.GlobalConfig;
import com.mcdr.corruption.entity.CorEntityManager;
import com.mcdr.corruption.handler.HeroesHandler;
import com.mcdr.corruption.listener.CorEntityListener;
import com.mcdr.corruption.listener.CorMagicSpellsListener;
import com.mcdr.corruption.listener.CorPlayerListener;
import com.mcdr.corruption.listener.CorWorldListener;
import com.mcdr.corruption.logger.CorLogger;
import com.mcdr.corruption.permissions.PermissionsManager;
import com.mcdr.corruption.player.CorPlayerManager;
import com.mcdr.corruption.stats.StatsManager;
import com.mcdr.corruption.task.SpawnManager;
import com.mcdr.corruption.task.TaskManager;
import com.mcdr.corruption.updater.CorConfigUpdater;
import com.mcdr.corruption.updater.CorUpdateChecker;
import com.mcdr.corruption.util.VersionUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.IOException;
import java.util.Collections;

public class Corruption extends JavaPlugin {
    private static Corruption instance;
    public static BukkitScheduler scheduler;
    public static boolean msInstalled, mcMMOInstalled, heroesInstalled;
    public PermissionsManager pm;


    public Corruption() {
        instance = this;
        scheduler = Bukkit.getScheduler();
    }

    /**
     * Get the singleton instance of the plugin.
     *
     * @return The singleton instance of this plugin
     */
    public static Corruption getInstance() {
        return instance;
    }

    /**
     * Get the name of the plugin.
     *
     * @return The name of the plugin
     */
    public static String getPluginName() {
        return instance.getName();
    }

    @Override
    public void onEnable() {
        PluginManager pluginManager = getServer().getPluginManager();
        msInstalled = pluginManager.getPlugin("MagicSpells") != null;
        mcMMOInstalled = pluginManager.getPlugin("mcMMO") != null;
        heroesInstalled = HeroesHandler.prepare();

        updateConfigs();
        ConfigManager.Load();
        SpawnManager.initialize(GlobalConfig.getSpawners());
        CorPlayerManager.AddOnlinePlayers();
        TaskManager.start();

        getCommand("corruption").setExecutor(new CommandManager());

        setupPermissionsManager();
        pluginManager.registerEvents(new CorEntityListener(), this);
        pluginManager.registerEvents(new CorPlayerListener(), this);
        pluginManager.registerEvents(new CorWorldListener(), this);

        if (msInstalled) {
            CorLogger.info("MagicSpells detected!");
            pluginManager.registerEvents(new CorMagicSpellsListener(), this);
        }

        if (mcMMOInstalled) {
            CorLogger.info("mcMMO detected!");
            String mcMMOVer = pluginManager.getPlugin("mcMMO").getDescription().getVersion().replaceAll("-(beta|dev)([0-9])+-", "-");
            if (VersionUtil.isOlderVersion(mcMMOVer, "1.4.00-b1612")) {
                mcMMOInstalled = false;
                CorLogger.info("Unsupported mcMMO version (" + mcMMOVer + ") in use.");
                CorLogger.info("Please update mcMMO to 1.4.00-beta3-b1612 or higher!");
                return;
            }
        }

        if (heroesInstalled)
            CorLogger.info("Heroes detected!");

        setupMetrics();

        if (GlobalConfig.checkUpdateOnStartup)
            checkUpdates();

        CorLogger.info("Enabled");
    }

    @Override
    public void onDisable() {
        CorPlayerManager.forcePlayerDataSaving();
        CorEntityManager.purgeAllBosses();
        TaskManager.stop();

        CorLogger.info("Disabled");
    }

    /**
     * Setup the permissions manager
     */
    public void setupPermissionsManager() {
        // Setup the permissions manager
        pm = new PermissionsManager(getServer(), this);
        pm.setup();
    }

    /**
     * Get the permissions manager
     *
     * @return permissions manager
     */
    public PermissionsManager getPermissionsManager() {
        return pm;
    }

    @SuppressWarnings("deprecation")
    private void setupMetrics() {
        try {
            CorMetrics metrics = new CorMetrics(this);

            Graph graphActive = metrics.createGraph("Active bosses");
            for (final EntityType type : BossConfig.getEntityTypesUsed()) {

                graphActive.addPlotter(new CorMetrics.Plotter(type.getName()) { //TODO Find alternative for EntityType.getName()

                    @Override
                    public int getValue() {
                        return Collections.frequency(CorEntityManager.getBossEntityTypes(), type);
                    }
                });

                Graph graphKilled = metrics.createGraph("Bosses killed");
                graphKilled.addPlotter(new CorMetrics.Plotter("Bosses killed") {

                    @Override
                    public int getValue() {
                        return StatsManager.getBossesKilledStats();
                    }
                });
            }
            if (metrics.start())
                CorLogger.info("Sending metrics data");
            else
                CorLogger.info("Disabled sending metrics data");
        } catch (IOException e) {
            CorLogger.info("Failed to contact mcstats.org");
        }
    }

    private void checkUpdates() {
        if (CorUpdateChecker.checkForUpdate()) {
            String lastVer = CorUpdateChecker.getLastVersion();
            CorLogger.info("New version available, version " + lastVer);
            getServer().broadcast(String.format("%s[%s]%s New version available, version %s", ChatColor.GOLD, getName(), ChatColor.WHITE, lastVer), "cor.update");
            getServer().broadcast(String.format("%s[%s]%s To update, use %s/corruption update install", ChatColor.GOLD, getName(), ChatColor.WHITE, ChatColor.GREEN), "cor.update");
        } else {
            CorLogger.info("No update needed, running the latest version (" + instance.getDescription().getVersion() + ")");
        }
    }

    private void updateConfigs() {
        CorConfigUpdater updater = new CorConfigUpdater();
        updater.updateFiles();
    }
}
