package com.mcdr.corruption.updater;

import com.mcdr.corruption.Corruption;
import com.mcdr.corruption.config.GlobalConfig;
import com.mcdr.corruption.logger.CorLogger;
import com.mcdr.corruption.util.VersionUtil;
import org.bukkit.plugin.PluginDescriptionFile;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class CorUpdateChecker {
    private static final String SERVERMODS_API = "https://api.curseforge.com/servermods/files?projectIds=48581";
    public static String lastVer;
    public static boolean updateNeeded = false;
    public static long timeStamp = -1;

    public static boolean isUpdateNeeded() {
        return updateNeeded;
    }

    public static boolean checkForUpdate() {
        if (ignoreCache()) {
            PluginDescriptionFile pdf = Corruption.getInstance().getDescription();
            String curVer = pdf.getVersion();
            String lastVersion = getLastVersion();
            if (lastVersion == null) {
                CorLogger.info("Failed to reach api.curseforge.com to check for updates. Is it down?");
                timeStamp = -1;
                return false;
            }
            updateNeeded = VersionUtil.isNewerVersion(lastVersion, curVer);
            timeStamp = System.currentTimeMillis();
        }
        return updateNeeded;
    }

    public static String getLastVersion() {
        JSONObject latest = getLatest();
        // Extract the version number out of the File title or return null. The format is: Corruption-<version>
        return latest == null ? null : latest.get("name").toString().replaceAll("[a-zA-z ]|-", "");
    }

    public static JSONObject getLatest() {
        try {
            // Create a URLConnection to the ServerMods API
            URLConnection con = new URL(SERVERMODS_API).openConnection();

            //Add the API Key to the request if defined in the config
            if (GlobalConfig.APIKey != null) {
                con.addRequestProperty("X-API-Key", GlobalConfig.APIKey);
            }

            // Set the timeout of the requests
            con.setConnectTimeout(1000 * 3);
            con.setReadTimeout(1000 * 30);

            // Add the user-agent to identify Corruption
            con.addRequestProperty("User-Agent", "Corruption/v" + Corruption.getInstance().getDescription().getVersion() + " (by Nauxuron, Erackron)");

            // Read the response. The response is in JSON format and only spans one line.
            final BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String response = reader.readLine();

            // Parse the array of files from the query's response
            JSONArray array = (JSONArray) JSONValue.parse(response);

            if (array.size() > 0) {
                // Return the newest file's details
                return (JSONObject) array.get(array.size() - 1);
            } else {
                CorLogger.warning("No files found for " + Corruption.getPluginName() + ". Please contact the developer.");
                return null;
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public static boolean ignoreCache() {
        return timeStamp == -1 || System.currentTimeMillis() - timeStamp > 1000 * 60 * 30;
    }
}
