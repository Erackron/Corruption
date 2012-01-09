package cam.command;

import org.bukkit.command.CommandSender;

import cam.Likeaboss;

public abstract class CommandBase {
	
	protected static Likeaboss plugin = null;
	protected static CommandSender sender = null;
	protected static String[] args = null;
}