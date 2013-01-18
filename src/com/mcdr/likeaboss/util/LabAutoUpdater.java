package com.mcdr.likeaboss.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Scanner;
import org.bukkit.Bukkit;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.mcdr.likeaboss.Likeaboss;

public class LabAutoUpdater {
	private static final String LAST_VERSION_URL = "http://dev.bukkit.org/server-mods/likeaboss-mcdr/files.rss";
	public static String downloadUrl = "";
	public static String md5Hash = "";
	public static long timeStamp = -1;
	
	public static String getDownloadUrl(){
		String link = "";
		try {
			URL rss = new URL(LAST_VERSION_URL);
			URLConnection con = rss.openConnection();
		    con.setConnectTimeout(1000*2);
		    con.setReadTimeout(1000*60);
		    
			ReadableByteChannel rbc = Channels.newChannel(con.getInputStream());
			Likeaboss.in.getDataFolder().mkdir();
			File outputFile = new File(Likeaboss.in.getDataFolder(), Likeaboss.in.getDescription().getName() + ".tmp");
			outputFile.createNewFile();
			FileOutputStream fos = new FileOutputStream(outputFile);
			fos.getChannel().transferFrom(rbc, 0, 1 << 24);
			fos.close();
			Scanner s = new Scanner(outputFile);
			int line = 0;
			while(s.hasNextLine()) {
				link = s.nextLine();
		    	if(link.contains("<link>")) {
		    		line++;
		    	}
		    	if(line == 2) {
		    		link = link.substring(link.indexOf(">") + 1);
		    		link = link.substring(0, link.indexOf("<"));
		    		break;
		    	}
			}
			s.close();
			outputFile.delete();
		} catch(SocketTimeoutException e){
			Likeaboss.l.info("["+Likeaboss.in.getName()+"] Failed to reach dev.bukkit.org to find the download page. Is it down?");
			timeStamp = -1;
			return null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		downloadUrl = getFileDownloadUrl(link);
		timeStamp = System.currentTimeMillis();
		return downloadUrl;
	}
	
	private static String getFileDownloadUrl(String url) {
		String link = "";
		try {   
			Document doc = Jsoup.connect(url).timeout(1000*2).get();
			
			Element element = doc.select("dt:containsOwn(md5) + dd").first();
			if(element!=null)
				md5Hash = element.html();
	    
			element = doc.getElementsByClass("user-action-download").first();
			link = element.getElementsByTag("a").attr("href");
		} catch(IOException e){ 
			Likeaboss.l.info("["+Likeaboss.in.getName()+"] Failed to reach dev.bukkit.org to find the download link. Is it down?");
			timeStamp = -1;
			return null;  
		} catch (Exception e) {
			e.printStackTrace();
		}
		return link;
	}
	
	public static boolean update() {
		if(timeStamp==-1)
			getDownloadUrl();
		if(downloadUrl==null){
			Likeaboss.l.info("["+Likeaboss.in.getName()+"] No download link found, please try again.");
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
					Likeaboss.l.warning("[Likeaboss] Download failed, hashes did not match: " + md5 + " != " + md5Hash);
					Likeaboss.l.warning("[Likeaboss] This means the file wasn't correctly downloaded, please try again.");
					Utility.fileToFile(bakFile, origFile);
					bakFile.delete();
					return false;
				}
			} catch(Exception e){
				Utility.fileToFile(bakFile, origFile);
				bakFile.delete();
				return false;
			}
			Likeaboss.l.info("[Likeaboss] Reloading Likeaboss v" + LabUpdateChecker.getLastVersion());
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
