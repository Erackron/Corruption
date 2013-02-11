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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.mcdr.corruption.Corruption;

public class CorAutoUpdater {
	private static final String LAST_VERSION_URL = "http://api.bukget.org/3/plugins/bukkit/likeaboss-mcdr/latest";
	private static String jsonResponse = "";
	public static String downloadUrl = "";
	public static String md5Hash = "";
	public static long timeStamp = -1;
	
	public static String getDownloadUrl(){
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
		} catch (IOException e) {e.printStackTrace();}
		if(jsonResponse=="")
			return "";
		downloadUrl = getFileDownloadUrl(jsonResponse);
		
		timeStamp = System.currentTimeMillis();
		return downloadUrl;
	}
	
	private static String getFileDownloadUrl(String jsonString){
		try {
			JSONObject json = (JSONObject) new JSONParser().parse(jsonString);
			JSONArray jsonArray = (JSONArray) json.get("versions");
			jsonString = jsonArray.toJSONString();
			json = (JSONObject) new JSONParser().parse(jsonString.substring(1, jsonString.length()-1));
			md5Hash = (String) json.get("md5");
			return (String) json.get("download");
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		} catch (ClassCastException e) {
			e.printStackTrace();
			return null;
		} catch (NullPointerException e){
			System.out.println(jsonString);
			return null;
		}
	}
	
	public static boolean update() {
		if(timeStamp==-1)
			getDownloadUrl();
		if(downloadUrl==null){
			Corruption.l.info("["+Corruption.in.getName()+"] No download link found, please try again.");
		}
			
		File origFile = new File("plugins", "Likeaboss.jar"),
			 bakFile = new File("plugins", "Likeaboss.jar.bak");
		try {
			Utility.fileToFile(origFile, bakFile);
			URL website = new URL(downloadUrl);
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
			Corruption.l.info("[Likeaboss] Reloading Likeaboss v" + CorUpdateChecker.getLastVersion());
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "reload");
	  } catch(Exception e) {
	    e.printStackTrace();
	    try {Utility.fileToFile(bakFile, origFile);bakFile.delete();} catch (IOException e1) {}
	    return false;
	  }
		bakFile.delete();
		return true;
	}
}
