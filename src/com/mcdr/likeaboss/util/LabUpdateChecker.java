package com.mcdr.likeaboss.util;

import java.io.InputStream;
import java.net.URL;
import javax.xml.parsers.DocumentBuilderFactory;

import org.bukkit.plugin.PluginDescriptionFile;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.mcdr.likeaboss.Likeaboss;

public class LabUpdateChecker {
	private static final String LAST_VERSION_URL = "http://dev.bukkit.org/server-mods/likeaboss-mcdr/files.rss";
	public static String lastVer;
	
	public static boolean updateNeeded() {
		PluginDescriptionFile pdf = Likeaboss.in.getDescription();
		String curVer = pdf.getVersion();
		String lastVersion = getLastVersion();
		
		return Utility.isNewerVersion(lastVersion, curVer);
	}
	
	public static String getLastVersion() {
		try {
		    // Create a URL for the desired page
		    URL url = new URL(LAST_VERSION_URL);
		    
		    InputStream input = url.openStream();
		    
		    Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(input);
		    
		    Node latestFile = doc.getElementsByTagName("item").item(0);
		    NodeList children = latestFile.getChildNodes();
		    
		    String version = children.item(1).getTextContent().replaceAll("[a-zA-z ]|-", "");
		    
		    return version;
		} catch (Exception e) {}
		return Likeaboss.in.getDescription().getVersion();
	}
}
