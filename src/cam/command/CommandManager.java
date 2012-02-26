package cam.command;

import org.bukkit.command.CommandSender;

import cam.Likeaboss;

public class CommandManager {
	
	public CommandManager(Likeaboss plugin) {
		CommandBase.plugin = plugin;
	}
	
	public boolean Process(CommandSender sender, String label, String[] args) {
		if (!label.equalsIgnoreCase("lab") || args.length == 0)
			return false;
		
		CommandBase.processed = false;
		CommandBase.sender = sender;
		CommandBase.args = args;
		String firstArg = args[0].toLowerCase();
		
		if (firstArg.equals("clear"))
			ClearCommand.Process();
		else if (firstArg.equals("reload"))
			ReloadCommand.Process();
		else if (firstArg.equals("info"))
			InfoCommand.Process();
		else if (firstArg.equals("viewer"))
			ViewerCommand.Process();
		else if (firstArg.equals("ignore"))
			IgnoreCommand.Process();
		else if (firstArg.equals("list"))
			ListCommand.Process();
		else if (firstArg.equals("spawn"))
			SpawnCommand.Process();
		
		if (CommandBase.processed)
			return true;
		
		return false;
	}
}
