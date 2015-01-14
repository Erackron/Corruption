package com.mcdr.corruption.updater;

import com.mcdr.corruption.Corruption;
import com.mcdr.corruption.config.GlobalConfig;
import com.mcdr.corruption.logger.CorLogger;
import com.mcdr.corruption.util.FileUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class CorAutoUpdater {
    public static String DOWNLOAD_URL = null;
    public static String md5Hash = "";

    public static boolean update() {
        JSONObject latest = CorUpdateChecker.getLatest();
        if (latest != null) {
            DOWNLOAD_URL = (String) latest.get("downloadUrl");
            md5Hash = (String) latest.get("md5");
        } else {
            CorLogger.warning("Failed to reach api.curseforge.com to find the download url. Is it down?");
            return false;
        }

        if (DOWNLOAD_URL != null) {
            File origFile = new File("plugins", "Corruption.jar"),
                    bakFile = new File("plugins", "Corruption.jar.bak");
            try {
                FileUtil.fileToFile(origFile, bakFile);
                URL website = new URL(DOWNLOAD_URL);
                URLConnection con = website.openConnection();
                con.setConnectTimeout(1000 * 2);
                con.setReadTimeout(1000 * 60 * 5);

                ReadableByteChannel rbc = Channels.newChannel(con.getInputStream());
                FileOutputStream fos = new FileOutputStream(origFile);
                fos.getChannel().transferFrom(rbc, 0, 1 << 24);
                fos.close();
                try {
                    String md5 = FileUtil.calculateMd5Hash(origFile);
                    if (!md5.equalsIgnoreCase(md5Hash)) {
                        CorLogger.warning("Download failed, hashes did not match: " + md5 + " != " + md5Hash);
                        CorLogger.warning("This means the file wasn't correctly downloaded, please try again.");
                        FileUtil.fileToFile(bakFile, origFile);
                        bakFile.delete();
                        return false;
                    }
                } catch (Exception e) {
                    FileUtil.fileToFile(bakFile, origFile);
                    bakFile.delete();
                    return false;
                }
                if (GlobalConfig.reloadAfterUpdating) {
                    CorLogger.info("Reloading " + Corruption.getPluginName() + " v" + CorUpdateChecker.getLastVersion());
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "reload");
                } else {
                    CorLogger.info("" + Corruption.getPluginName() + " v" + CorUpdateChecker.getLastVersion() + " installed. Reload or restart your server for the changes to take effect.");
                    CorLogger.info("" + ChatColor.RED + "WARNING: " + ChatColor.RESET + "Don't use a pluginmanager to reload this plugin. This plugin is not responsible for the damage that may occur if you do that.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    FileUtil.fileToFile(bakFile, origFile);
                    bakFile.delete();
                } catch (IOException ignored) {
                }
                return false;
            }
            bakFile.delete();
            return true;
        } else {
            CorLogger.warning("Failed to find the download url. Please contact the developer.");
            return false;
        }
    }
}
