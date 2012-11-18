package cam.ability;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

import cam.Likeaboss;
import cam.Utility;
import cam.entity.Boss;
import cam.entity.LabEntityManager;
import cam.entity.Minion;

public class Minions extends Ability {
	private EntityType entityType = EntityType.SILVERFISH;
	private int amount = 3;
	private int areaRadius = 2;
	private double cooldown = 7.5;
	
	public Minions() {
		activationConditions.add(ActivationCondition.ONATTACK);
		activationConditions.add(ActivationCondition.ONDEFENSE);
	}
	
	@Override
	public void Execute(EntityDamageEvent event, LivingEntity livingEntity, Boss boss) {
		if(checkChance()){
			if (PrepareMinionsSpawn(FindValidBlocks(boss.getLivingEntity().getLocation()), boss)) {
				useCooldown(boss);
				sendMessage(boss);
			}
		}
	}
	
	private List<Block> FindValidBlocks(Location location) {
		List<Block> validBlocks = new ArrayList<Block>();
		World world = location.getWorld();
		Vector direction = new Vector(1.0, 0.0, 0.0);
		int maxTurns = areaRadius * 4 + 1;
		
		for (int turn = 0 ; turn < maxTurns ; turn++) {
			int blocksInLine = turn / 2 + 1;
			
			if (turn == maxTurns - 1)
				blocksInLine--;
			
			for (int currentBlock = 0 ; currentBlock < blocksInLine ; currentBlock++) {
				Block block = world.getBlockAt(location.add(direction));
				
				if (block.isEmpty() && block.getRelative(BlockFace.UP).isEmpty() && !block.getRelative(BlockFace.DOWN).isEmpty())
					validBlocks.add(block);
				else {
					Location blockLocation = block.getLocation();
					double minY = blockLocation.getY() - areaRadius;
					double maxY = blockLocation.getY() + areaRadius;
					
					for (double i = minY ; i < maxY ; i++) {
						blockLocation.setY(i);
						
						Block nextBlock = world.getBlockAt(blockLocation);
						
						if (nextBlock.isEmpty() && nextBlock.getRelative(BlockFace.UP).isEmpty() && !nextBlock.getRelative(BlockFace.DOWN).isEmpty()) {
							validBlocks.add(nextBlock);
							break;
						}
					}
				}
			}
			
			direction = new Vector(-direction.getZ(), 0.0, direction.getX());
		}
		
		return validBlocks;
	}
	
	private boolean PrepareMinionsSpawn(List<Block> validBlocks, Boss boss) {
		if (validBlocks.isEmpty())
			return false;
		
		for (int i = 0 ; i < amount ; i++) {
			Block block = validBlocks.get(Utility.random.nextInt(validBlocks.size()));
				
			block.getWorld().playEffect(block.getLocation(), Effect.SMOKE, 4);
			Likeaboss.scheduler.scheduleSyncDelayedTask(Likeaboss.instance, new SpawnMinion(entityType, boss, block), 15);
		}
		
		return true;
	}
	
	public void setEntityType(EntityType entityType) {
		this.entityType = entityType;
	}
	
	public void setAmount(int amount) {
		this.amount = amount;
	}
	
	public void setAreaRadius(int areaRadius) {
		this.areaRadius = areaRadius;
	}
	
	private class SpawnMinion implements Runnable {
		private Block block;
		private EntityType entityType;
		private Boss boss;
		
		public SpawnMinion(EntityType entityType, Boss boss, Block block) {
			this.entityType = entityType;
			this.boss = boss;
			this.block = block;
		}
		
		@Override
		public void run() {
			LivingEntity livingEntity = (LivingEntity) block.getWorld().spawnEntity(block.getLocation(), entityType);
			LabEntityManager.AddMinion(new Minion(livingEntity, boss));
		}
	}
}
