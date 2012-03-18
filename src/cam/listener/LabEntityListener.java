package cam.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import cam.Likeaboss;
import cam.Utility;
import cam.boss.Boss;
import cam.boss.BossData;
import cam.boss.BossManager;
import cam.config.GlobalConfig.MessageData;
import cam.config.LabConfig;
import cam.drop.DropCalculator;
import cam.event.BossDamageEvent;
import cam.player.LabPlayer;
import cam.player.LabPlayerManager;

public class LabEntityListener implements Listener {

	private BossManager bossManager = null;
	private DropCalculator dropCalculator = null;
	private LabPlayerManager labPlayerManager = null;
	private LabConfig labConfig = null;
	
	public LabEntityListener(Likeaboss plugin) {
		bossManager = plugin.getBossManager();
		dropCalculator = plugin.getDropCalculator();
		labPlayerManager = plugin.getLabPlayerManager();
		labConfig = plugin.getLabConfig();
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if (event.getSpawnReason() == SpawnReason.CUSTOM)
			return;
		
		LivingEntity livingEntity = event.getEntity();
		
		if (livingEntity instanceof Monster || livingEntity instanceof Slime  && ((Slime) livingEntity).getSize() == 4 || livingEntity instanceof Ghast) {
			BossData bossData = labConfig.getWorldConfig(livingEntity.getWorld()).getBossData(livingEntity.getType());
			
			if (bossData == null)
				return;
			
			if (livingEntity.getLocation().getY() <= bossData.getMaxHeight()) {
				double chance = Math.random() * 100;
				
				if (event.getSpawnReason() == SpawnReason.SPAWNER) {
					if (chance < bossData.getChanceFromSpawner())
						bossManager.AddBoss(livingEntity, bossData);
				}
				
				else if (chance < bossData.getChance())
					bossManager.AddBoss(livingEntity, bossData);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityDeath(EntityDeathEvent event) {
		LivingEntity livingEntity = event.getEntity();
		Boss boss = bossManager.getBoss(livingEntity);
		
		if (boss == null)
			return;
		
		dropCalculator.Process(event.getDrops(), boss, livingEntity.getWorld());
		event.setDroppedExp((int) (event.getDroppedExp() * boss.getBossData().getExpCoef()));
		
		bossManager.KillBoss(boss);
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityExplode(EntityExplodeEvent event) {
		Boss boss = bossManager.getBoss(event.getEntity());
		
		if (boss != null)
			bossManager.RemoveBoss(boss);
	}
		
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent event) {
		//This allows us to ignore event thrown by LivingEntity.damage(0, x), probably unneeded.
		if (event.getDamage() <= 0)
			return;
		
		Entity entity = event.getEntity();
		
		if (entity instanceof Player) {
			Player player = (Player) entity;
			
			if (event instanceof EntityDamageByEntityEvent) {
				Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
				
				if (damager instanceof Projectile) {
					Projectile projectile = (Projectile) damager;
					damager = projectile.getShooter();
				}
				
				Boss boss = bossManager.getBoss(damager);
				
				if (boss == null)
					return;
				
				LabPlayer labPlayer = labPlayerManager.getLabPlayer(player);
				
				if (labPlayer != null) {
					if (labPlayer.getLabPlayerData().getIgnore()) {
						bossManager.RemoveBoss(boss);
						return;
					}
				}
				
				event.setDamage((int) (event.getDamage() * boss.getBossData().getDamageCoef()));
					
				//Found message
				if (!boss.getFound()) {
					boss.setFound(true);
					labPlayerManager.SendFoundMessage(player, false, entity);
				}
			}
		}
		
		//Only affect bosses
		else if (entity instanceof LivingEntity) {
			Boss boss = bossManager.getBoss(entity);
			
			if (boss == null)
				return;
			
			//Simplified Minecraft invulnerability timer
			LivingEntity livingEntity = boss.getLivingEntity();
			if (livingEntity.getNoDamageTicks() > livingEntity.getMaximumNoDamageTicks() / 2.0) {
				event.setCancelled(true);
				return;
			}
			
			//Damager finder
			Entity damager = null;
			if (event instanceof EntityDamageByEntityEvent) {
				damager = ((EntityDamageByEntityEvent) event).getDamager();
				
				if (damager instanceof Projectile) {
					Projectile projectile = (Projectile) damager;
					damager = projectile.getShooter();
					
					if (projectile instanceof Arrow)
						projectile.remove();
				}
			}
			
			Player player = null;
			LabPlayer labPlayer = null;
			if (damager instanceof Player) {
				player = (Player) damager;
				
				//Remove the boss and return if the player has Ignore
				labPlayer = labPlayerManager.getLabPlayer(player);
				if (labPlayer.getLabPlayerData().getIgnore()) {
					bossManager.RemoveBoss(boss);
					event.setCancelled(true);
					return;
				}
				
				//Return if the player is too far
				if (!Utility.IsNear(player.getLocation(), livingEntity.getLocation(), 0, 16)) {
					player.sendMessage(MessageData.TOO_FAR.getMessage().replace('&', ChatColor.COLOR_CHAR));
					event.setCancelled(true);
					return;
				}
				//Proximity message
				else if (!boss.getFound()) {
					boss.setFound(true);
					labPlayerManager.SendFoundMessage(player, true, entity);
				}
			}
			
			int damage = event.getDamage();
			
			//Apply damage
			switch (event.getCause()) {
			case ENTITY_ATTACK:
			case PROJECTILE:
				break;
			case ENTITY_EXPLOSION:
			case BLOCK_EXPLOSION:
				damage /= 2;
				break;
			case MAGIC:
				damage *= 1.25;
				break;
			case POISON:
				if (boss.getHealth() - damage > 0)
					break;
			default:
				event.setCancelled(true);
				return;
			}
			
			//Throw an event
			BossDamageEvent bossDamageEvent = new BossDamageEvent(boss, damager, damage);
			Bukkit.getServer().getPluginManager().callEvent(bossDamageEvent);
			
			//Listen the event
			int newDamage = bossDamageEvent.getDamage();
			if (bossDamageEvent.isCancelled() || newDamage <= 0) {
				event.setCancelled(true);
				return;
			}
			bossManager.DamageBoss(boss, newDamage);
			
			if (labPlayer != null) {
				//Viewer message
				if (labPlayer.getLabPlayerData().getViewer())
					player.sendMessage("Boss Health: " + ChatColor.GRAY + (boss.getHealth()) + " (-" + newDamage + ")");
			}
			
			if (boss.getHealth() <= 0) {
				event.setDamage(livingEntity.getMaxHealth()); //TODO: Xp gain for mcMMO
				livingEntity.setHealth(0); //Because the line above doesn't kill armored foes
			}
			else
				event.setDamage(0);
		}
	}
}