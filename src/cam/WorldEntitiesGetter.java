package cam;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import org.bukkit.World;
import org.bukkit.entity.Entity;

public class WorldEntitiesGetter implements Callable<Set<Entity>> {

	Likeaboss plugin = null;
	
	public WorldEntitiesGetter(Likeaboss plugin) {
		this.plugin = plugin;
	}
	
	public Set<Entity> call() {
		Set<Entity> entities = new HashSet<Entity>();
		List<World> worlds = plugin.getServer().getWorlds();
		
		for (World world : worlds)
			entities.addAll(world.getEntities());
		
		return entities;
	}
}
