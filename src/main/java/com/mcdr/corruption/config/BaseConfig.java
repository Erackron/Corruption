package com.mcdr.corruption.config;

import com.mcdr.corruption.Corruption;
import com.mcdr.corruption.logger.CorLogger;
import com.mcdr.corruption.util.FileUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public abstract class BaseConfig {
    protected final static char SEPERATOR = File.separatorChar;
    protected final static String DATAFOLDER = Corruption.getInstance().getDataFolder().getPath();

    protected static void copyResource(File file, String resourcePath) {
        InputStream inputStream = Corruption.getInstance().getResource(resourcePath);

        if (inputStream == null) {
            CorLogger.severe("Missing resource file: '" + resourcePath + "', please notify the plugin authors");
            Bukkit.getPluginManager().disablePlugin(Corruption.getInstance());
        } else {
            CorLogger.info("Creating default config file: " + file.getName());

            try {
                file.getParentFile().mkdirs();
                FileUtil.streamToFile(inputStream, file);
            } catch (Exception e) {
                e.printStackTrace();
                Bukkit.getPluginManager().disablePlugin(Corruption.getInstance());
            }
        }
    }

    protected static YamlConfiguration loadConfig(File file) {
        YamlConfiguration yamlConfig = new YamlConfiguration();

        try {
            yamlConfig.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return yamlConfig;
    }

    protected static void saveConfig(YamlConfiguration yamlConfig, String fileName) {
        saveConfig(yamlConfig, fileName, false);
    }

    protected static void saveConfig(YamlConfiguration yamlConfig, String fileName, boolean inWorldsFolder) {
        try {
            yamlConfig.save(new File(DATAFOLDER + SEPERATOR + (inWorldsFolder ? "Worlds" + SEPERATOR : "") + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
