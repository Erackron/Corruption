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
			ClearCommand.process();
		else if(firstArg.equalsIgnoreCase("help"))
			HelpCommand.process();
		else if(firstArg.equalsIgnoreCase("ignore"))
			IgnoreCommand.process();
		else if(firstArg.equalsIgnoreCase("info"))
			InfoCommand.process();
		else if(firstArg.equalsIgnoreCase("list"))
			ListCommand.process();
		else if(firstArg.equalsIgnoreCase("loglevel"))
			LogCommand.process();
		else if(firstArg.equalsIgnoreCase("reload"))
			ReloadCommand.process();
		else if(firstArg.equalsIgnoreCase("spawn"))
			SpawnCommand.process();
		else if(firstArg.equalsIgnoreCase("stats"))
			StatsCommand.process();
		else if(firstArg.equalsIgnoreCase("viewer"))
			ViewerCommand.process();
		else if(firstArg.equalsIgnoreCase("update"))
			UpdateCommand.process();
		else if(firstArg.toLowerCase().matches("v|ver|version"))
			VersionCommand.process();
				
			
		return BaseCommand.processed;
	}
}
