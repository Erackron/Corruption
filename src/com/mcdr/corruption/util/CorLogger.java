package com.mcdr.corruption.util;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;

import com.mcdr.corruption.Corruption;

public abstract class CorLogger {
	private static int level = Level.INFO.intValue();
	private static Logger l = Bukkit.getLogger();
	
	public static Level parseLogLevel() {
		return Level.parse(level+"");
	}
	
	public static int getLogLevel(){
		return level;
	}
	
	public static void setLogLevel(Level lvl) {
		level = lvl.intValue();
	}
	
	public static void log(Level level, String msg){
		if(shouldLog(level)){
			l.log(level, "["+Corruption.pluginName+"] "+msg);
		}
	}
	
	public static void info(String msg) {
		log(LogLevel.INFO, msg);
	}
	
	public static void debug(String msg){
		log(LogLevel.INFO, "(DEBUG) "+msg);
	}
	
	public static void warning(String msg){
		log(LogLevel.WARNING, msg);
	}
	
	public static void severe(String msg){
		log(LogLevel.SEVERE, msg);
	}
	
	private static boolean shouldLog(Level level){
		if(level.equals(LogLevel.OFF))
			return false;
		return level.intValue()>=CorLogger.level;
	}
	
	public static boolean debugEnabled(){
		return shouldLog(LogLevel.DEBUG);
	}
	
	// Shortcuts
	public static void l(Level level, String msg){log(level, msg);}
	public static void i(String msg){info(msg);}
	public static void d(String msg){debug(msg);}
	public static void w(String msg){warning(msg);}
	public static void s(String msg){severe(msg);}
	
	//The LogLevel class, a subclass of java.util.logging.Level to enable extra/custom logging levels.
	public static class LogLevel extends Level{
		private static final long serialVersionUID = 1L;
		
		private static ArrayList<Level> known = new ArrayList<Level>();
		
		public static final LogLevel DEBUG = new LogLevel("DEBUG", 200);
		
		protected LogLevel(String name, int value) {
			super(name, value);
			synchronized (LogLevel.class) {
				known.add(this);
			}
		}
		
		/**
		 * Uses {@link java.util.logging.Level#parse(String name)}
		 */
		public static synchronized Level parse(String name) throws IllegalArgumentException {
			try{
				return Level.parse(name);
			} catch (IllegalArgumentException e){
				// Look for a known Level with the given non-localized name.
				for (int i = 0; i < known.size(); i++) {
					Level l = known.get(i);
					if (name.equals(l.getName())) {
						return l;
					}
				}

				// Now, check if the given name is an integer.  If so,
				// first look for a Level with the given value and then
				// if necessary create one.
				try {
					int x = Integer.parseInt(name);
					for (int i = 0; i < known.size(); i++) {
						Level l = known.get(i);
						if (l.intValue() == x) {
							return l;
						}
					}
					// Create a new Level.
					return new LogLevel(name, x);
				} catch (NumberFormatException ex) {
					// Not an integer.
					// Drop through.
				}

				// Finally, look for a known level with the given localized name,
				// in the current default locale.
				// This is relatively expensive, but not excessively so.
				for (int i = 0; i < known.size(); i++) {
					Level l =  known.get(i);
					if (name.equals(l.getLocalizedName())) {
						return l;
					}
				}

				// OK, we've tried everything and failed
				throw new IllegalArgumentException("Bad level \"" + name + "\"");
			}
		}
		
		
	}
}