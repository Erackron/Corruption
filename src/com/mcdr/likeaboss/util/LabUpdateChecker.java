package com.mcdr.likeaboss.util;

import java.io.InputStream;
import java.net.URL;
import java.util.regex.Pattern;

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
		
		return isNewerVersion(curVer, lastVersion);
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
	
	/**
	 * Checks if the second input is a newer version than the first input
	 * @param current the current version used
	 * @param lastCheck the latest version available
	 * @return true if second input is a newer version, false if it isn't
	 */
	public static boolean isNewerVersion(String current, String lastCheck) {
        String s1 = normalisedVersion(current);
        String s2 = normalisedVersion(lastCheck);
        int cmp = s1.compareTo(s2);
        //String cmpStr = cmp < 0 ? "<" : cmp > 0 ? ">" : "==";
        return cmp<0;
    }

	private static String normalisedVersion(String version) {
        return normalisedVersion(version, ".", 4);
    }

	private static String normalisedVersion(String version, String sep, int maxWidth) {
        String[] split = Pattern.compile(sep, Pattern.LITERAL).split(version);
        StringBuilder sb = new StringBuilder();
        for (String s : split) {
            sb.append(String.format("%" + maxWidth + 's', s));
        }
        return sb.toString();
	}

}
