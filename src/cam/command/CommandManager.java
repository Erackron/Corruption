package cam.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import cam.Likeaboss;

public class CommandManager {
	
	public CommandManager(Likeaboss plugin) {
		CommandBase.plugin = plugin;
	}
	
	public boolean Process(CommandSender sender, String label, String[] args) {
		if (!label.equalsIgnoreCase("lab") || args.length == 0)
			return false;
		
		CommandBase.sender = sender;
		CommandBase.args = args;
		String firstArg = args[0].toLowerCase();
		
		if (firstArg.equals("clear") && sender.hasPermission("lab." + firstArg))
			return ClearCommand.Process();
			
		else if (firstArg.equals("reload") && sender.hasPermission("lab." + firstArg))
			return ReloadCommand.Process();
			
		else if (firstArg.equals("info") && sender.hasPermission("lab." + firstArg))
			return InfoCommand.Process();
			
		else if (sender instanceof Player) {
			if (firstArg.equals("viewer") && sender.hasPermission("lab." + firstArg))
				return ViewerCommand.Process();
			
			else if (firstArg.equals("ignore") && sender.hasPermission("lab." + firstArg))
				return IgnoreCommand.Process();
				
			else if (firstArg.equals("list") && sender.hasPermission("lab." + firstArg))
				return ListCommand.Process();
			
			else if (firstArg.equals("spawn") && sender.hasPermission("lab." + firstArg))
				return SpawnCommand.Process();
		}
		
		return false;
	}
}
