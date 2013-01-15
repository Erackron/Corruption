package com.mcdr.likeaboss.command;

import org.bukkit.command.CommandSender;

public abstract class CommandManager {
	public static boolean Process(CommandSender sender, String label, String[] args) {
		if (!label.equalsIgnoreCase("lab") || args.length == 0)
			return false;
		
		BaseCommand.processed = false;
		BaseCommand.sender = sender;
		BaseCommand.args = args;
		
		String firstArg = args[0].toLowerCase();
		
		switch(firstArg){
			case "clear":
				ClearCommand.Process();
				break;
			case "help":
				HelpCommand.Process();
				break;
			case "ignore":
				IgnoreCommand.Process();
				break;
			case "info":
				InfoCommand.Process();
				break;
			case "list":
				ListCommand.Process();
				break;
			case "reload":
				ReloadCommand.Process();
				break;
			case "spawn":
				SpawnCommand.Process();
				break;
			case "stats":
				StatsCommand.Process();
				break;
			case "viewer":
				ViewerCommand.Process();
				break;
			case "update":
				UpdateCommand.Process();
				break;
			default:
		}
		
		return BaseCommand.processed;
	}
}
