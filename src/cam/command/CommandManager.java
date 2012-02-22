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
		String perm = "lab." + firstArg;
		
		if (firstArg.equals("clear") && sender.hasPermission(perm))
			return ClearCommand.Process();
			
		else if (firstArg.equals("reload") && sender.hasPermission(perm))
			return ReloadCommand.Process();
			
		else if (firstArg.equals("info") && sender.hasPermission(perm))
			return InfoCommand.Process();
			
		else if (sender instanceof Player) {
			if (firstArg.equals("viewer") && sender.hasPermission(perm))
				return ViewerCommand.Process();
			
			else if (firstArg.equals("ignore") && sender.hasPermission(perm))
				return IgnoreCommand.Process(sender.hasPermission(perm + ".immediate"));
				
			else if (firstArg.equals("list") && sender.hasPermission(perm))
				return ListCommand.Process();
			
			else if (firstArg.equals("spawn") && sender.hasPermission(perm))
				return SpawnCommand.Process();
		}
		
		return false;
	}
}
