package com.mcdr.likeaboss.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
			ReadableByteChannel rbc = Channels.newChannel(rss.openStream());
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
		} catch (IOException e) {
			e.printStackTrace();
		}
		downloadUrl = downloadSite(link);
		timeStamp = System.currentTimeMillis();
		return downloadUrl;
	}
	
	public static String downloadSite(String url) {
	  String link = "";
	  try {   
	    Document doc = Jsoup.connect(url).get();
	    
	    Element element = doc.select("dt:containsOwn(md5) + dd").first();
	    if(element!=null)
	    	md5Hash = element.html();
	    
	    element = doc.getElementsByClass("user-action-download").first();
	    link = element.getElementsByTag("a").attr("href");
	  } catch (Exception e) {
	    e.printStackTrace();
	  }
	  return link;
	}
	
	public static boolean update() {
		if(timeStamp==-1)
			getDownloadUrl();
		try {
			URL website = new URL(downloadUrl);
			ReadableByteChannel rbc = Channels.newChannel(website.openStream());
			new File("plugins", "Likeaboss.jar").renameTo(new File("plugins", "Likeaboss.jar.bak"));
			File outputFile = new File("plugins", "Likeaboss.jar");
			FileOutputStream fos = new FileOutputStream(outputFile);
			fos.getChannel().transferFrom(rbc, 0, 1 << 24);
			fos.close();
			try{
				String md5 = calculateMd5Hash(outputFile);
				if(!md5.equalsIgnoreCase(md5Hash)){
					Likeaboss.l.warning("[Likeaboss] Download failed, hashes did not match: " + md5 + " != " + md5Hash);
					new File("plugins", "Likeaboss.jar.bak").renameTo(new File("plugins", "Likeaboss.jar"));
					return false;
				}
			} catch(Exception e){
				new File("plugins", "Likeaboss.jar.bak").renameTo(new File("plugins", "Likeaboss.jar"));
				return false;
			}
			Likeaboss.l.info("[Likeaboss] Reloading Likeaboss v" + LabUpdateChecker.getLastVersion());
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "reload");
		}
	  catch(Exception e) {
	    e.printStackTrace();
	    new File("plugins", "Likeaboss.jar.bak").renameTo(new File("plugins", "Likeaboss.jar"));
	    return false;
	  }
		return true;
	}
	
	public static String calculateMd5Hash(File f) throws NoSuchAlgorithmException, FileNotFoundException{
		MessageDigest digest = MessageDigest.getInstance("MD5");
		InputStream is = new FileInputStream(f);                
		byte[] buffer = new byte[8192];
		int read = 0;
		try {
		    while( (read = is.read(buffer)) > 0) {
		        digest.update(buffer, 0, read);
		    }       
		    byte[] md5sum = digest.digest();
		    BigInteger bigInt = new BigInteger(1, md5sum);
		    String output = bigInt.toString(16);
		    return output;
		} catch(IOException e) {
		    throw new RuntimeException("Unable to process file for MD5", e);
		} finally {
		    try {
		        is.close();
		    }
		    catch(IOException e) {
		        throw new RuntimeException("Unable to close input stream for MD5 calculation", e);
		    }
		}
	}
}
