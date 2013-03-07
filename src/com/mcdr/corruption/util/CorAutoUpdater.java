package com.mcdr.corruption.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.mcdr.corruption.Corruption;
import com.mcdr.corruption.config.GlobalConfig;

public class CorAutoUpdater {
	private static final String LAST_VERSION_URL = "http://api.bukget.org/3/plugins/bukkit/corruption/latest";
	public static final String DOWNLOAD_URL = LAST_VERSION_URL + "/download";
	private static String jsonResponse = "";
	public static String md5Hash = "";
	public static long timeStamp = -1;
	
	public static boolean updateMd5Hash(){
		try {
			URL jsonURL = new URL(LAST_VERSION_URL);
			URLConnection con = jsonURL.openConnection();
		    con.setConnectTimeout(1000*2);
		    con.setReadTimeout(1000*60);
		    //con.connect();
		    InputStream ins = con.getInputStream();
		    BufferedReader in = new BufferedReader(new InputStreamReader(ins));
		    String line = in.readLine();
		    jsonResponse = line!=null?line:jsonResponse;
		    in.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		if(jsonResponse=="")
			return false;
		
		timeStamp = System.currentTimeMillis();
		return getMd5Hash(jsonResponse);
	}
	
	private static boolean getMd5Hash(String jsonString){
		try {
			JSONObject json = (JSONObject) new JSONParser().parse(jsonString);
			JSONArray jsonArray = (JSONArray) json.get("versions");
			jsonString = jsonArray.toJSONString();
			json = (JSONObject) new JSONParser().parse(jsonString.substring(1, jsonString.length()-1));
			md5Hash = (String) json.get("md5");
			return true;
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		} catch (ClassCastException e) {
			e.printStackTrace();
			return false;
		} catch (NullPointerException e){
			System.out.println(jsonString);
			return false;
		}
	}
	
	public static boolean update() {
		if(timeStamp==-1)
			if(!updateMd5Hash())
				Corruption.l.info("["+Corruption.pluginName+"] No Md5 hash found to check if the download succeeded.");
			
		File origFile = new File("plugins", "Corruption.jar"),
			 bakFile = new File("plugins", "Corruption.jar.bak");
		try {
			Utility.fileToFile(origFile, bakFile);
			URL website = new URL(DOWNLOAD_URL);
			URLConnection con = website.openConnection();
		    con.setConnectTimeout(1000*2);
		    con.setReadTimeout(1000*60*5);
		    
			ReadableByteChannel rbc = Channels.newChannel(con.getInputStream());
			FileOutputStream fos = new FileOutputStream(origFile);
			fos.getChannel().transferFrom(rbc, 0, 1 << 24);
			fos.close();
			try{
				String md5 = Utility.calculateMd5Hash(origFile);
				if(!md5.equalsIgnoreCase(md5Hash)){
					Corruption.l.warning("[Likeaboss] Download failed, hashes did not match: " + md5 + " != " + md5Hash);
					Corruption.l.warning("[Likeaboss] This means the file wasn't correctly downloaded, please try again.");
					Utility.fileToFile(bakFile, origFile);
					bakFile.delete();
					return false;
				}
			} catch(Exception e){
				Utility.fileToFile(bakFile, origFile);
				bakFile.delete();
				return false;
			}
			if(GlobalConfig.reloadAfterUpdating){
				Corruption.l.info("["+Corruption.pluginName+"] Reloading " + Corruption.pluginName + " v" + CorUpdateChecker.getLastVersion());
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "reload");
			} else {
				Corruption.l.info("["+Corruption.pluginName+"] " + Corruption.pluginName + " v" + CorUpdateChecker.getLastVersion() + " installed. Reload or restart your server for the changes to take effect.");
				Corruption.l.info("["+Corruption.pluginName+"] " + ChatColor.RED + "WARNING: " + ChatColor.RESET + "Don't use a pluginmanager to reload this plugin. This plugin is not responsible for the damage that may occur if you do that.");
			}
	  } catch(Exception e) {
	    e.printStackTrace();
	    try {Utility.fileToFile(bakFile, origFile);bakFile.delete();} catch (IOException e1) {}
	    return false;
	  }
		bakFile.delete();
		return true;
	}
}
