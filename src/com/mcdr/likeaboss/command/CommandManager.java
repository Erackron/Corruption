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
		
		if(firstArg.equalsIgnoreCase("clear"))
			ClearCommand.Process();
		else if(firstArg.equalsIgnoreCase("help"))
			HelpCommand.Process();
		else if(firstArg.equalsIgnoreCase("ignore"))
			IgnoreCommand.Process();
		else if(firstArg.equalsIgnoreCase("info"))
			InfoCommand.Process();
		else if(firstArg.equalsIgnoreCase("list"))
			ListCommand.Process();
		else if(firstArg.equalsIgnoreCase("reload"))
			ReloadCommand.Process();
		else if(firstArg.equalsIgnoreCase("spawn"))
			SpawnCommand.Process();
		else if(firstArg.equalsIgnoreCase("stats"))
			StatsCommand.Process();
		else if(firstArg.equalsIgnoreCase("viewer"))
			ViewerCommand.Process();
		else if(firstArg.equalsIgnoreCase("update"))
			UpdateCommand.Process();

		return BaseCommand.processed;
	}
}
