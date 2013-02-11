package com.mcdr.corruption.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandManager implements CommandExecutor{
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0)
			return false;
		
		BaseCommand.processed = false;
		BaseCommand.sender = sender;
		BaseCommand.args = args;
		BaseCommand.label = label;
		
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
