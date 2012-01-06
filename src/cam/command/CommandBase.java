package cam.command;

import java.util.Comparator;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import cam.Likeaboss;

public class CommandBase {
	
	protected static Likeaboss plugin = null;
	protected static CommandSender sender = null;
	
	public CommandBase(Likeaboss plugin) {
		CommandBase.plugin = plugin;
	}
	
	public boolean Process(CommandSender sender, String label, String[] args) {
		if (!label.equalsIgnoreCase("lab") || args.length == 0)
			return false;
		
		CommandBase.sender = sender;
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
				
			else if (firstArg.equals("list") && sender.hasPermission("lab." + firstArg))
				return ListCommand.Process();
			
			else if (firstArg.equals("spawn") && sender.hasPermission("lab." + firstArg))
				return SpawnCommand.Process(args);
		}
		
		return false;
	}
}

class ValueComparator implements Comparator<Object> {

	private Map<?, ?> base;
	
	public ValueComparator(Map<?, ?> base) {
		this.base = base;
	}
	
	public int compare(Object a, Object b) {
		if ((Double) base.get(a) > (Double) base.get(b)) 
			return 1;
		else if ((Double) base.get(a) == (Double) base.get(b))
			return 0;
		else
			return -1;
	}
}