package cam.listener;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
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
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;

import cam.Likeaboss;
import cam.Utility;
import cam.boss.Boss;
import cam.boss.BossManager;
import cam.boss.BossManager.BossParams;
import cam.player.LabPlayer;
import cam.player.LabPlayerManager;

public class LabEntityListener implements Listener {

	private BossManager bossManager = null;
	private LabPlayerManager labPlayerManager = null;
	
	public LabEntityListener(Likeaboss plugin) {
		bossManager = plugin.getBossManager();
		labPlayerManager = plugin.getLabPlayerManager();
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		Entity entity = event.getEntity();
		
		if (entity instanceof Monster || entity instanceof Slime  && ((Slime) entity).getSize() == 4 || entity instanceof Ghast) {
			LivingEntity livingEntity = (LivingEntity) entity;
			
			BossParams bossParams = bossManager.getBossParams(livingEntity);
			
			if (livingEntity.getLocation().getY() <= bossParams.getMaxHeight()) {
				if (event.getSpawnReason() == SpawnReason.SPAWNER && bossParams.getFromMobSpawner() == 0)
					return;
				
				if (Math.random() * 100 < bossParams.getChance())
					bossManager.AddBoss(livingEntity);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDeath(EntityDeathEvent event) {
		Entity entity = event.getEntity();
		Boss boss = bossManager.getBoss(entity);
		
		if (boss == null)
			return;
		
		event.setDroppedExp((int) (event.getDroppedExp() * boss.getExpCoef()));
		bossManager.RemoveBoss(boss, true);
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityExplode(EntityExplodeEvent event) {
		Entity entity = event.getEntity();
		Boss boss = bossManager.getBoss(entity);
		
		if (boss != null)
			bossManager.RemoveBoss(boss, false);
	}
		
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDamage(EntityDamageEvent event) {
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
					if (labPlayer.getIgnore()) {
						bossManager.RemoveBoss(boss, false);
						return;
					}
				}
				
				event.setDamage((int) (event.getDamage() * boss.getDamageCoef()));
					
				//Found message
				if (!boss.getFound()) {
					boss.setFound(true);
					player.sendMessage(ChatColor.RED + "Sneaky boss.");
					
					List<LabPlayer> labPlayers = labPlayerManager.getLabPlayers();
					
					for (LabPlayer nearbyLabPlayer : labPlayers) {
						Player nearbyPlayer = nearbyLabPlayer.getPlayer();
						
						if (nearbyPlayer == player)
							continue;
						
						if (Utility.IsNear(nearbyPlayer.getLocation(), entity.getLocation(), 0, 35))
							nearbyPlayer.sendMessage(ChatColor.WHITE + player.getPlayerListName() + ChatColor.RED + " found a boss!");
					}
				}
			}
		}
		
		//Only affect bosses
		else if (entity instanceof LivingEntity) {
			Boss boss = bossManager.getBoss(entity);
			
			if (boss == null)
				return;
			
			LivingEntity livingEntity = (LivingEntity) entity;
			
			//Invulnerability timer
			if (livingEntity.getNoDamageTicks() > livingEntity.getMaximumNoDamageTicks() / 2.0 && event.getDamage() <= boss.getLastDamage()) {
				event.setDamage(0);
				event.setCancelled(true);
				return;
			}
			
			Entity damager = null;
			boolean tooFar = false;
						
			//Damager finder
			if (event instanceof EntityDamageByEntityEvent) {
				damager = ((EntityDamageByEntityEvent) event).getDamager();
					
				if (damager instanceof Projectile) {
					Projectile projectile = (Projectile) damager;
					damager = projectile.getShooter();
					
					if (projectile instanceof Arrow)
						projectile.remove();
				}
				
				//If player found, calculate distance and send message
				if (damager instanceof Player) {
					Player player = (Player) damager;
					LabPlayer labPlayer = labPlayerManager.getLabPlayer(player);
					
					if (labPlayer != null) {
						if (labPlayer.getIgnore()) {
							bossManager.RemoveBoss(boss, false);
							return;
						}
					}
						
					if (!Utility.IsNear(player.getLocation(), livingEntity.getLocation(), 0, 16))
						tooFar = true;
					
					if (!boss.getFound()) {
						player.sendMessage(ChatColor.RED + "Oh noes, that's a boss!");
						
						if (tooFar) {
							player.sendMessage(ChatColor.RED + "But it's too far away.");
							event.setDamage(0);
							event.setCancelled(true);
							return;
						}
						
						boss.setFound(true);
						
						List<LabPlayer> labPlayers = labPlayerManager.getLabPlayers();
						
						for (LabPlayer nearbyLabPlayer : labPlayers) {
							Player nearbyPlayer = nearbyLabPlayer.getPlayer();
							
							if (nearbyPlayer == player)
								continue;
							
							if (Utility.IsNear(nearbyPlayer.getLocation(), livingEntity.getLocation(), 0, 35))
								nearbyPlayer.sendMessage(ChatColor.WHITE + player.getPlayerListName() + ChatColor.RED + " found a boss!");
						}
					}
				}
			}
			
			ItemStack weapon = null;
			int damage = event.getDamage();
								
			//Apply damage
			if (event.getCause() == DamageCause.ENTITY_ATTACK || event.getCause() == DamageCause.PROJECTILE) {
				//Check for enchantment
				if (damager instanceof Player) {
					Player player = (Player) damager;
					weapon = player.getItemInHand();
					Map<Enchantment, Integer> enchantments = weapon.getEnchantments();
					
					//if (weapon.getType() == Material.BOW) {
					//	for (Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
					//		Enchantment enchantment = entry.getKey();
							
					//		if (enchantment.getId() == Enchantment.ARROW_DAMAGE.getId()) {
					//			double coef = 1.5;
					//			int level = entry.getValue();
					//			if (level > 1)
					//				coef += 0.25 * (level - 1);
					//			damage *= coef;
					//			break;
					//		}
					//	}
					//}
					
					if (weapon.getType() == Material.DIAMOND_SWORD) {
						for (Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
							Enchantment enchantment = entry.getKey();
							
							if (enchantment.getId() == Enchantment.DAMAGE_ALL.getId())
								damage += Utility.Random(1, entry.getValue() * 3);
							else if (enchantment.getId() == Enchantment.DAMAGE_UNDEAD.getId() && (livingEntity instanceof Zombie || livingEntity instanceof Skeleton))
								damage += Utility.Random(1, entry.getValue() * 4);
							else if (enchantment.getId() == Enchantment.DAMAGE_ARTHROPODS.getId() && (livingEntity instanceof Spider || livingEntity instanceof Silverfish))
								damage += Utility.Random(1, entry.getValue() * 4);
						}
					}
					
					//Message
					LabPlayer labPlayer = labPlayerManager.getLabPlayer(player);
					
					if (labPlayer != null && labPlayer.getViewer())
						player.sendMessage("Boss Health: " + ChatColor.GRAY + (boss.getHealth() - damage) + " (-" + damage + ")");
				}
				
				bossManager.DamageBoss(boss, damage);
			}
			else if (event.getCause() == DamageCause.ENTITY_EXPLOSION || event.getCause() == DamageCause.BLOCK_EXPLOSION)
				bossManager.DamageBoss(boss, (int) (damage / 2.0));
			else if (event.getCause() == DamageCause.MAGIC || event.getCause() == DamageCause.POISON)
				bossManager.DamageBoss(boss, (int) (damage * 1.25));
			else {
				event.setDamage(0);
				event.setCancelled(true);
				return;
			}
			
			//Should the entity die?
			if (bossManager.IsDead(boss)) {
				//Dirty fix to keep exp gain in mcMMO
				double healthCoef = boss.getHealthCoef();
				
				if (healthCoef < 1) {
					if (livingEntity instanceof Zombie)
						healthCoef = 1.1; //Zombies have armor
					else
						healthCoef = 1;
				}
				
				event.setDamage((int) (healthCoef * livingEntity.getMaxHealth())); //Zombies have armor, need to be superior to maxHealth
			}
			else {
				//Handle the loss of durability, needed when event damage = 0;
				if (weapon != null && weapon.getType() != Material.BOW)
					weapon.setDurability((short) (weapon.getDurability() + 1));
				
				event.setDamage(0); //Otherwise Minecraft adds enchant damage, it could result in OHK
				livingEntity.damage(0, damager); //To have a knockback and red flash
			}
		}
	}
}