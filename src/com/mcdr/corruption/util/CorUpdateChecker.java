package com.mcdr.corruption.util;

import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilderFactory;

import org.bukkit.plugin.PluginDescriptionFile;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.mcdr.corruption.Corruption;

public class CorUpdateChecker {
	private static final String LAST_VERSION_URL = "http://dev.bukkit.org/server-mods/corruption/files.rss";
	public static String lastVer;
	public static boolean updateNeeded = false;
	public static long timeStamp = -1;
	
	public static boolean updateNeeded() {
		if(timeStamp==-1 || System.currentTimeMillis()-timeStamp>1000*60*30){
			PluginDescriptionFile pdf = Corruption.in.getDescription();
			String curVer = pdf.getVersion();
			String lastVersion = getLastVersion();
			if(lastVersion==null){
				CorLogger.i("Failed to reach dev.bukkit.org to check for updates. Is it down?");
				timeStamp = -1;
				return false;
			}
			updateNeeded = Utility.isNewerVersion(lastVersion, curVer);
			timeStamp = System.currentTimeMillis();
		}
		return updateNeeded;
	}
	
	public static String getLastVersion() {
		try {
		    // Create a URL for the desired page
		    URL url = new URL(LAST_VERSION_URL);
		    
		    URLConnection con = url.openConnection();
		    con.setConnectTimeout(1000*2);
		    con.setReadTimeout(1000*30);
		    
		    InputStream input = con.getInputStream();
		    
		    Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(input);
		    
		    Node latestFile = doc.getElementsByTagName("item").item(0);
		    NodeList children = latestFile.getChildNodes();
		    
		    String version = children.item(1).getTextContent().replaceAll("[a-zA-z ]|-", "");
		    
		    return version;
		} catch(SocketTimeoutException e){
			return null;
		} catch (Exception e) {}
		return Corruption.in.getDescription().getVersion();
	}
}
