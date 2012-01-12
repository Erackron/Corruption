package cam.listener;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.inventory.ItemStack;

import cam.Likeaboss;
import cam.Utility;
import cam.boss.Boss;
import cam.boss.BossManager;
import cam.config.LabConfig;
import cam.player.LabPlayer;
import cam.player.LabPlayerManager;

public class LabEntityListener extends EntityListener {

	private BossManager bossManager = null;
	private LabPlayerManager labPlayerManager = null;
	
	public LabEntityListener(Likeaboss plugin) {
		bossManager = plugin.getBossManager();
		labPlayerManager = plugin.getLabPlayerManager();
	}
	
	@Override
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		Entity entity = event.getEntity();
		
		if (entity instanceof Monster || entity instanceof Slime  && ((Slime) entity).getSize() == 4 || entity instanceof Ghast) {
			LivingEntity livingEntity = (LivingEntity) entity;
			
			if (livingEntity.getLocation().getY() <= LabConfig.BossesData.SPAWN_MAXHEIGHT.getValue()) {
				if (event.getSpawnReason() == SpawnReason.SPAWNER && LabConfig.BossesData.SPAWN_FROMMOBSPAWNER.getValue() == 0)
					return;
				
				if (Math.random() * 100 < LabConfig.BossesData.SPAWN_CHANCE.getValue())
					bossManager.AddBoss(livingEntity);
			}
		}
	}
	
	@Override
	public void onEntityDeath(EntityDeathEvent event) {
		Entity entity = event.getEntity();
		
		if (!bossManager.IsBoss(entity))
			return;

		Boss boss = bossManager.getBoss(entity);		

		event.setDroppedExp((int) (event.getDroppedExp() * boss.getExpCoef()));
		bossManager.RemoveBoss(boss, true);
	}
	
	@Override
	public void onEntityExplode(EntityExplodeEvent event) {
		Entity entity = event.getEntity();
		
		if (bossManager.IsBoss(entity)) {
			Boss boss = bossManager.getBoss(entity);
			
			bossManager.RemoveBoss(boss, false);
		}
	}
		
	@Override
	public void onEntityDamage(EntityDamageEvent event) {
		Entity entity = event.getEntity();
		
		if (entity instanceof Player) {
			Player player = (Player) entity;
			
			if (event instanceof EntityDamageByEntityEvent) {
				Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
				
				if (!bossManager.IsBoss(damager))
					return;
				
				Boss boss = bossManager.getBoss(damager);
					
				event.setDamage((int) (event.getDamage() * boss.getDamageCoef()));
					
				//Found message
				if (!boss.getFound()) {
					boss.setFound(true);
					player.sendMessage(ChatColor.RED + "Sneaky boss.");
					
					List<Entity> nearbyEntities = boss.getLivingEntity().getNearbyEntities(35, 35, 35);
					
					for (Entity nearbyEntity : nearbyEntities) {
						if (nearbyEntity instanceof Player && nearbyEntity != player)
							((Player) nearbyEntity).sendMessage(ChatColor.RED + "A boss found " + ChatColor.WHITE + player.getPlayerListName() + ChatColor.RED + "!");
					}
				}
			}
		}
		
		//Only affect bosses
		else if (bossManager.IsBoss(entity)) {			
			LivingEntity livingEntity = (LivingEntity) entity;
			
			//Invulnerability timer
			if (livingEntity.getNoDamageTicks() > livingEntity.getMaximumNoDamageTicks() / 2.0) {
				event.setDamage(0); //Because other plugins may uncancel the event
				event.setCancelled(true);
				return;
			}
			
			Boss boss = bossManager.getBoss(livingEntity);	
			Entity damager = null;
			boolean tooFar = false;
						
			//Damager finder
			if (event instanceof EntityDamageByEntityEvent) {
				damager = ((EntityDamageByEntityEvent) event).getDamager();
					
				if (damager instanceof Projectile) 
					damager = ((Projectile) damager).getShooter();
					
				//If player found, calculate distance and send message
				if (damager instanceof Player) {
					Player player = (Player) damager;
					double distance = livingEntity.getLocation().distance(player.getLocation());
						
					if (distance >= 16)
						tooFar = true;

					if (!boss.getFound()) {
						boss.setFound(true);
						List<Entity> nearbyEntities = livingEntity.getNearbyEntities(35, 35, 35);
						
						player.sendMessage(ChatColor.RED + "Oh noes, that's a boss!");
						if (tooFar)
							player.sendMessage(ChatColor.RED + "But it's too far away.");
									
						for (Entity nearbyEntity : nearbyEntities) {
							if (nearbyEntity instanceof Player && nearbyEntity != player)
								((Player) nearbyEntity).sendMessage(ChatColor.WHITE + player.getPlayerListName() + ChatColor.RED + " found a boss!");
						}
					}
					
					if (tooFar) {
						event.setDamage(0);
						return;
					}
				}
			}
			
			int damage = event.getDamage();
								
			//Apply damage
			if (event.getCause() == DamageCause.ENTITY_ATTACK || event.getCause() == DamageCause.PROJECTILE) {
				//Check for enchantment
				if (damager instanceof Player) {
					Player player = (Player) damager;
					ItemStack weapon = player.getItemInHand();
					Map<Enchantment, Integer> enchantments = weapon.getEnchantments();
					
					for (Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
						Enchantment enchantment = entry.getKey();
						
						//Let us assume that the weapon isn't hacked
						if (enchantment.getId() == Enchantment.DAMAGE_ALL.getId())
							damage += Utility.Random(1, entry.getValue() * 3);
						else if (enchantment.getId() == Enchantment.DAMAGE_ARTHROPODS.getId() && (livingEntity instanceof Spider || livingEntity instanceof Silverfish))
							damage += Utility.Random(1, entry.getValue() * 4);
						else if (enchantment.getId() == Enchantment.DAMAGE_UNDEAD.getId() && (livingEntity instanceof Zombie || livingEntity instanceof Skeleton))
							damage += Utility.Random(1, entry.getValue() * 4);
					}
					
					//Message
					if (labPlayerManager.IsLabPlayer(player)) {
						LabPlayer labPlayer = labPlayerManager.getLabPlayer(player);

						if (labPlayer.getViewer())
							player.sendMessage("Boss Health: " + ChatColor.GRAY + (boss.getHealth() - damage) + " (-" + damage + ")");
					}
				}
				
				bossManager.DamageBoss(boss, damage);
			}
			else if (event.getCause() == DamageCause.ENTITY_EXPLOSION || event.getCause() == DamageCause.BLOCK_EXPLOSION)
				bossManager.DamageBoss(boss, (int) (damage / 2.0));
			else {
				event.setDamage(0);
				event.setCancelled(true);
				return;
			}
					
			//Should the entity die?
			if (bossManager.IsDead(boss)) {
				//Dirty fix to keep exp gain in mcMMO
				double healthCoef = LabConfig.BossesData.STATS_HEALTHCOEF.getValue();
				
				if (healthCoef < 1) {
					if (livingEntity instanceof Zombie)
						healthCoef = 1.1; //Zombies have armor
					else
						healthCoef = 1;
				}
				
				event.setDamage((int) (healthCoef * livingEntity.getMaxHealth())); //Zombie has armor, need to be superior to maxHealth
			}
			else {
				event.setDamage(0); //Otherwise Minecraft adds enchant damage, it could result in OHK
				livingEntity.damage(0, damager); //To have a knockback and red flash
			}
		}
	}
}