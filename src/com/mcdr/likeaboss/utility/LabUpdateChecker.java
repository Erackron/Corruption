package com.mcdr.likeaboss.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Pattern;

import org.bukkit.plugin.PluginDescriptionFile;

import com.mcdr.likeaboss.Likeaboss;

public class LabUpdateChecker {

	public LabUpdateChecker() {
	}
	
	public boolean checkUpdates() {
		PluginDescriptionFile pdf = Likeaboss.in.getDescription();
		String curVer = pdf.getVersion();
		String lastVersion = getLastVersion();
		
		return isNewerVersion(curVer, lastVersion);
	}
	
	public String getLastVersion() {
		try {
		    // Create a URL for the desired page
		    URL url = new URL("http://127.0.0.1/index.php");

		    URLConnection con = url.openConnection();
		    con.setConnectTimeout(2000);
		    con.setReadTimeout(2000);
		    InputStream ins = con.getInputStream();

		    // Read all the text returned by the server
		    BufferedReader in = new BufferedReader(new InputStreamReader(ins));
		    String str;
		    while ((str = in.readLine()) != null) {
				return str;
		    }
		    in.close();
		} catch (MalformedURLException e) {
		} catch (IOException e) { }
		return Likeaboss.in.getDescription().getVersion();
	}
	
	private boolean isNewerVersion(String current, String lastCheck) {
        String s1 = normalisedVersion(current);
        String s2 = normalisedVersion(lastCheck);
        int cmp = s1.compareTo(s2);
        //String cmpStr = cmp < 0 ? "<" : cmp > 0 ? ">" : "==";
        if(cmp < 0) {
        	return true;
        }
        return false;
    }

	private String normalisedVersion(String version) {
        return normalisedVersion(version, ".", 4);
    }

	private String normalisedVersion(String version, String sep, int maxWidth) {
        String[] split = Pattern.compile(sep, Pattern.LITERAL).split(version);
        StringBuilder sb = new StringBuilder();
        for (String s : split) {
            sb.append(String.format("%" + maxWidth + 's', s));
        }
        return sb.toString();
    }

}
