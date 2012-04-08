package cam.listener;

import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
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
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;

import cam.Likeaboss;
import cam.Utility;
import cam.boss.Boss;
import cam.boss.BossData;
import cam.boss.BossManager;
import cam.config.GlobalConfig.BossParam;
import cam.config.GlobalConfig.MessageParam;
import cam.config.WorldConfig;
import cam.event.BossDamageEvent;
import cam.event.BossDeathEvent;
import cam.event.BossSpawnEvent;
import cam.player.LabPlayer;
import cam.player.LabPlayerManager;

public class LabEntityListener implements Listener {
	
	private BossManager bossManager;
	private LabPlayerManager labPlayerManager;
	private PluginManager pluginManager;
	
	public LabEntityListener() {
		bossManager = Likeaboss.instance.getBossManager();
		labPlayerManager = Likeaboss.instance.getLabPlayerManager();
		pluginManager = Bukkit.getServer().getPluginManager();
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if (event.getSpawnReason() == SpawnReason.CUSTOM)
			return;
		
		LivingEntity livingEntity = event.getEntity();
		
		if (livingEntity instanceof Monster || livingEntity instanceof Slime  && ((Slime) livingEntity).getSize() == 4 || livingEntity instanceof Ghast) {
			BossData bossData = Likeaboss.instance.getLabConfig().getWorldConfig(livingEntity.getWorld()).getBossData(livingEntity.getType());
			if (bossData == null)
				return;
			
			if (livingEntity.getLocation().getY() <= bossData.getMaxHeight()) {
				double chance = Math.random() * 100;
				
				if (event.getSpawnReason() == SpawnReason.SPAWNER && chance < bossData.getChanceFromSpawner() ||
					chance < bossData.getChance()) {
					Boss boss = new Boss(livingEntity, bossData);
					
					BossSpawnEvent bossSpawnEvent = new BossSpawnEvent(boss);
					pluginManager.callEvent(bossSpawnEvent);
					
					if (!bossSpawnEvent.isCancelled())
						bossManager.AddBoss(boss);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityDeath(EntityDeathEvent event) {
		LivingEntity livingEntity = event.getEntity();
		Boss boss = bossManager.getBoss(livingEntity);
		if (boss == null)
			return;
		
		//Prepare drops and exp
		BossData bossData = boss.getBossData();
		WorldConfig worldConfig = Likeaboss.instance.getLabConfig().getWorldConfig(livingEntity.getWorld());
		List<ItemStack> drops = Likeaboss.instance.getDropCalculator().CreateDrops(bossData, worldConfig);
		int exp = (int) (event.getDroppedExp() * boss.getBossData().getExpCoef());
		
		//Throw an event
		BossDeathEvent bossDeathEvent = new BossDeathEvent(boss, drops, exp);
		pluginManager.callEvent(bossDeathEvent);
		if (bossDeathEvent.isCancelled())
			return;
		
		//Update drops and exp
		List<ItemStack> originalDrops = event.getDrops();
		if (BossParam.OVERWRITE_DROPS.getValue())
			originalDrops.clear();
		originalDrops.addAll(drops);
		event.setDroppedExp(bossDeathEvent.getExp());
		
		bossManager.KillBoss(boss);
		
		LabPlayer killer = boss.getKiller();
		if (killer != null) {
			killer.AddBossKilled(livingEntity.getType(), 1);
			Likeaboss.instance.getStats().AddBossKilled(1);
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityExplode(EntityExplodeEvent event) {
		Boss boss = bossManager.getBoss(event.getEntity());
		if (boss != null)
			bossManager.RemoveBoss(boss);
	}
		
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent event) {
		//This allows us to ignore event thrown by LivingEntity.damage(0, x), probably unneeded
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
				
				//Player notifications
				if (!Utility.IsNear(player.getLocation(), livingEntity.getLocation(), 0, 16)) {
					player.sendMessage(MessageParam.TOO_FAR.getMessage().replace('&', ChatColor.COLOR_CHAR));
					event.setCancelled(true);
					return;
				}
				else if (!boss.getFound()) {
					boss.setFound(true);
					labPlayerManager.SendFoundMessage(player, true, entity);
				}
			}
			
			//Apply damage
			int damage = event.getDamage();
			
			switch (event.getCause()) {
			case ENTITY_ATTACK:
				if (BossParam.ATTACK_IMMUNE.getValue()) {
					event.setCancelled(true);
					break;
				}
				if (player != null && !BossParam.ENCHANT_FIRETICK_IMMUNE.getValue()) {
					Map<Enchantment, Integer> enchants = player.getItemInHand().getEnchantments();
					if (enchants.containsKey(Enchantment.FIRE_ASPECT))
						Likeaboss.instance.getServer().getScheduler().scheduleSyncDelayedTask(Likeaboss.instance, new GetFireEnchantTicks(boss), 0);
				}
				break;
			case PROJECTILE:
				if (BossParam.PROJECTILE_IMMUNE.getValue()) {
					event.setCancelled(true);
					break;
				}
				if (player != null && !BossParam.ENCHANT_FIRETICK_IMMUNE.getValue()) {
					Map<Enchantment, Integer> enchants = player.getItemInHand().getEnchantments();
					if (enchants.containsKey(Enchantment.FIRE_ASPECT))
						Likeaboss.instance.getServer().getScheduler().scheduleSyncDelayedTask(Likeaboss.instance, new GetFireEnchantTicks(boss), 0);
				}
				break;
			case BLOCK_EXPLOSION:
				if (BossParam.BLOCK_EXPLOSION_IMMUNE.getValue())
					event.setCancelled(true);
				else
					damage /= 2;
				break;
			case ENTITY_EXPLOSION:
				if (BossParam.ENTITY_EXPLOSION_IMMUNE.getValue())
					event.setCancelled(true);
				else
					damage /= 2;
				break;
			case FIRE:
				if (BossParam.FIRE_IMMUNE.getValue())
					event.setCancelled(true);
				break;
			case LAVA:
				if (BossParam.LAVA_IMMUNE.getValue())
					event.setCancelled(true);
				break;
			case FIRE_TICK:
				//Handle fire enchants
				if (boss.getFireEnchantTick() > 0)
					boss.setFireEnchantTick(boss.getFireEnchantTick() - 1);
				else if (BossParam.ENVIRONMENTAL_FIRETICK_IMMUNE.getValue()) {
					livingEntity.setFireTicks(0);
					event.setCancelled(true);
				}
				break;
			case FALL:
				if (BossParam.FALL_IMMUNE.getValue())
					event.setCancelled(true);
				break;
			case DROWNING:
				if (BossParam.DROWNING_IMMUNE.getValue())
					event.setCancelled(true);
				break;
			case CONTACT:
				if (BossParam.CONTACT_IMMUNE.getValue())
					event.setCancelled(true);
				break;
			case LIGHTNING:
				if (BossParam.LIGHTNING_IMMUNE.getValue())
					event.setCancelled(true);
				break;
			case SUFFOCATION:
				if (BossParam.SUFFOCATION_IMMUNE.getValue())
					event.setCancelled(true);
				break;
			case MAGIC:
				if (BossParam.MAGIC_IMMUNE.getValue())
					event.setCancelled(true);
				else
					damage *= 1.25;
				break;
			case POISON:
				if (BossParam.POISON_IMMUNE.getValue() || boss.getHealth() - damage > 0)
					event.setCancelled(true);
				break;
			default:
				event.setCancelled(true);
				return;
			}
			
			if (event.isCancelled())
				return;
			
			//Throw an event
			BossDamageEvent bossDamageEvent = new BossDamageEvent(boss, damager, damage);
			pluginManager.callEvent(bossDamageEvent);
			
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
				boss.setKiller(labPlayer);
				event.setDamage(livingEntity.getMaxHealth()); //Kill the entity
				livingEntity.setHealth(1); //Needed for armored foes (must not be set to 0 otherwise Bukkit starts to do weird things)
			}
			else
				event.setDamage(0);
		}
	}
	
	class GetFireEnchantTicks implements Runnable {
		
		Boss boss;
		
		public GetFireEnchantTicks(Boss boss) {
			this.boss = boss;
		}
		
		@Override
		public void run() {
			boss.setFireEnchantTick(boss.getLivingEntity().getFireTicks() / 20 - 1);
		}
	}
}
