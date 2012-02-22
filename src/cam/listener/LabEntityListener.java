package cam.listener;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
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
import cam.config.BossData;
import cam.config.LabConfig;
import cam.config.MessageData;
import cam.drop.DropCalculator;
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
		SpawnReason spawnReason = event.getSpawnReason();
		
		if (spawnReason == SpawnReason.CUSTOM)
			return;
		
		Entity entity = event.getEntity();
		
		if (entity instanceof Monster || entity instanceof Slime  && ((Slime) entity).getSize() == 4 || entity instanceof Ghast) {
			LivingEntity livingEntity = (LivingEntity) entity;
			
			BossData bossData = labConfig.getBossData(livingEntity);
			
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
		Entity entity = event.getEntity();
		Boss boss = bossManager.getBoss(entity);
		
		if (boss == null)
			return;
		
		List<ItemStack> drops = event.getDrops();
		dropCalculator.Process(drops, boss, entity.getWorld());
		event.setDroppedExp((int) (event.getDroppedExp() * boss.getBossData().getExpCoef()));
		
		bossManager.KillBoss(boss);
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityExplode(EntityExplodeEvent event) {
		Entity entity = event.getEntity();
		Boss boss = bossManager.getBoss(entity);
		
		if (boss != null)
			bossManager.RemoveBoss(boss);
	}
		
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
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
					if (labPlayer.getCommandStatus().getIgnore()) {
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
			
			LivingEntity livingEntity = (LivingEntity) entity;
			
			//Invulnerability timer
			if (livingEntity.getNoDamageTicks() > livingEntity.getMaximumNoDamageTicks() / 2.0 && event.getDamage() <= boss.getLastDamage()) {
				event.setDamage(0);
				event.setCancelled(true);
				return;
			}
			
			Entity damager = null;
						
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
						if (labPlayer.getCommandStatus().getIgnore()) {
							bossManager.RemoveBoss(boss);
							return;
						}
					}
						
					if (!Utility.IsNear(player.getLocation(), livingEntity.getLocation(), 0, 16)) {
						player.sendMessage(MessageData.TOOFAR.getMessage());
						event.setDamage(0);
						event.setCancelled(true);
						return;
					}
					
					if (!boss.getFound()) {
						boss.setFound(true);
						labPlayerManager.SendFoundMessage(player, true, entity);
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
					
					//else {
						for (Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
							Enchantment enchantment = entry.getKey();
							
							if (enchantment.getId() == Enchantment.DAMAGE_ALL.getId())
								damage += Utility.Random(1, entry.getValue() * 3);
							else if (enchantment.getId() == Enchantment.DAMAGE_UNDEAD.getId() && (livingEntity instanceof Zombie || livingEntity instanceof Skeleton))
								damage += Utility.Random(1, entry.getValue() * 4);
							else if (enchantment.getId() == Enchantment.DAMAGE_ARTHROPODS.getId() && (livingEntity instanceof Spider || livingEntity instanceof Silverfish))
								damage += Utility.Random(1, entry.getValue() * 4);
						}
					//}
					
					//Message
					LabPlayer labPlayer = labPlayerManager.getLabPlayer(player);
					
					if (labPlayer != null && labPlayer.getCommandStatus().getViewer())
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
			
			if (boss.getHealth() <= 0) {
				//Dirty fix to keep correct exp gain from mcMMO
				double healthCoef = boss.getBossData().getHealthCoef();
				
				if (healthCoef < 1) {
					if (livingEntity instanceof Zombie)
						healthCoef = 1.1; //Zombies have armor
					else
						healthCoef = 1;
				}
				
				event.setDamage((int) (healthCoef * livingEntity.getMaxHealth()));
			}
			else {
				//Durability loss of tools and swords, needed when event damage = 0
				if (weapon != null) {
					short maxDurability = weapon.getType().getMaxDurability();
					
					if (maxDurability == 33 || maxDurability == 66 || maxDurability == 132 || maxDurability == 251 || maxDurability == 1562)
						weapon.setDurability((short) (weapon.getDurability() + 1));
				}
				
				event.setDamage(0); //Otherwise Minecraft adds enchant damage, it could result in OHK
				livingEntity.damage(0, damager); //Needed for a knockback and red flash
			}
		}
	}
}