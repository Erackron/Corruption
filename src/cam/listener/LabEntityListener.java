package cam.listener;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Slime;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
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
import cam.ability.Ability.ActivationCondition;
import cam.config.GlobalConfig.MessageParam;
import cam.config.WorldConfig;
import cam.config.GlobalConfig.BossParam;
import cam.entity.Boss;
import cam.entity.BossData;
import cam.entity.LabEntity;
import cam.entity.LabEntityManager;
import cam.player.LabPlayer;
import cam.player.LabPlayerManager;

public class LabEntityListener implements Listener {
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if (event.getSpawnReason() == SpawnReason.CUSTOM)
			return;
		
		LivingEntity livingEntity = event.getEntity();
		BossData bossData = WorldConfig.getWorldData(livingEntity.getWorld()).getBossData(livingEntity.getType());			
		
		if (bossData == null)
			return;
		
		if(bossData.getMaxSpawnLevel() < livingEntity.getLocation().getY())
			return;
		
		if(event.getSpawnReason() == SpawnReason.SLIME_SPLIT)
			if(((Slime) livingEntity).getSize()==1)
				return;
		
		double chance = Utility.random.nextInt(100);
		
		if (event.getSpawnReason() == SpawnReason.SPAWNER) {
			if (chance < bossData.getChanceFromSpawner()) {
				Boss boss = new Boss(livingEntity, bossData);
				
				LabEntityManager.AddBoss(boss);
			}
		}
		else if (chance < bossData.getChance()) {
			Boss boss = new Boss(livingEntity, bossData);
			
			LabEntityManager.AddBoss(boss);
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityDeath(EntityDeathEvent event) {
		LabEntity labEntity = LabEntityManager.getEntity(event.getEntity());
		
		if (labEntity != null)
			labEntity.OnDeath(event);
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityExplode(EntityExplodeEvent event) {
		Boss boss = LabEntityManager.getBoss(event.getEntity());
		
		if (boss != null)
			LabEntityManager.RemoveBoss(boss);
	}
		
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent event) {
		//This allows us to ignore event thrown by LivingEntity.damage(0, x), probably unneeded
		if (event.getDamage() <= 0)
			return;
		
		//Only if the damager is a boss
		if (event instanceof EntityDamageByEntityEvent) {
			Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
			
			if (damager instanceof Projectile) {
				Projectile projectile = (Projectile) damager;
				damager = projectile.getShooter();
			}
			
			Boss boss = LabEntityManager.getBoss(damager);
			
			if (boss != null) {
				Entity entity = event.getEntity();
				
				if (!(entity instanceof LivingEntity))
					return;
				
				LivingEntity livingEntity = (LivingEntity) entity;
				
				switch (livingEntity.getType()) {
				case PLAYER:
					Player player = (Player) livingEntity;
					LabPlayer labPlayer = LabPlayerManager.getLabPlayer(player);
					
					if (labPlayer != null) {
						if (labPlayer.getLabPlayerData().getIgnore()) {
							LabEntityManager.RemoveBoss(boss);
							return;
						}
					}
					
					//Found message
					if (!boss.getFound()) {
						boss.setFound(true);
						LabPlayerManager.SendFoundMessage(labPlayer, false, player.getLocation(), boss.getBossData().getName());
					}
				default:
					break;
				}
				
				event.setDamage((int) (event.getDamage() * boss.getBossData().getDamageCoef()));
				boss.ActivateAbilities(event, livingEntity, ActivationCondition.ONATTACK);
			}
		}
		
		Boss boss = LabEntityManager.getBoss(event.getEntity());
		
		if (boss != null) {
			LivingEntity livingEntity = boss.getLivingEntity();
			
			//Simplified Minecraft invulnerability timer
			if (livingEntity.getNoDamageTicks() > livingEntity.getMaximumNoDamageTicks() / 2.0) {
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
			}
			
			Player player = null;
			LabPlayer labPlayer = null;
			
			if (damager instanceof Player) {
				player = (Player) damager;
				
				labPlayer = LabPlayerManager.getLabPlayer(player);
				
				//Remove the boss and return if the player has Ignore
				if (labPlayer.getLabPlayerData().getIgnore()) {
					LabEntityManager.RemoveBoss(boss);
					event.setCancelled(true);
					return;
				}
				
				//Player notifications
				if (!boss.getFound()) {
					boss.setFound(true);
					LabPlayerManager.SendFoundMessage(labPlayer, true, livingEntity.getLocation(), boss.getBossData().getName());
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
						Bukkit.getScheduler().scheduleSyncDelayedTask(Likeaboss.instance, new GetFireEnchantTicks(boss), 0);
				}
				break;
			case PROJECTILE:
				if (BossParam.PROJECTILE_IMMUNE.getValue()) {
					event.setCancelled(true);
					break;
				}
				if (player != null && !BossParam.ENCHANT_FIRETICK_IMMUNE.getValue()) {
					Map<Enchantment, Integer> enchants = player.getItemInHand().getEnchantments();
					
					if (enchants.containsKey(Enchantment.ARROW_FIRE))
						Bukkit.getScheduler().scheduleSyncDelayedTask(Likeaboss.instance, new GetFireEnchantTicks(boss), 0);
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
				if (BossParam.POISON_IMMUNE.getValue() || boss.getHealth() - damage <= 0)
					event.setCancelled(true);
				break;
			default:
				event.setCancelled(true);
				return;
			}
			
			if (event.isCancelled())
				return;
			
			LabEntityManager.DamageBoss(boss, damage);
			try{
				boss.ActivateAbilities(event, (LivingEntity) damager, ActivationCondition.ONDEFENSE);
			} catch(ClassCastException e){}
			
			
			//Viewer message
			String viewerMsg = null;
			if (boss.getHealth()>0)
				viewerMsg = MessageParam.VIEWERMESSAGE.getMessage();
			else
				viewerMsg = MessageParam.VIEWERDEFEATED.getMessage();
			
			viewerMsg = Utility.parseMessage(viewerMsg, boss, boss.getHealth(), damage);
			
				
			for (LabPlayer labPlayerTemp : LabPlayerManager.getLabPlayers()) {
				if(labPlayerTemp != null && labPlayerTemp.getLabPlayerData().getViewer()){
					player = labPlayerTemp.getPlayer();
					if (Utility.IsNear(player.getLocation(), boss.getLivingEntity().getLocation(), 0, 16)){
						player.sendMessage(viewerMsg);
					}
				}
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
	
	private class GetFireEnchantTicks implements Runnable {
		
		private Boss boss;
		
		public GetFireEnchantTicks(Boss boss) {
			this.boss = boss;
		}
		
		@Override
		public void run() {
			boss.setFireEnchantTick(boss.getLivingEntity().getFireTicks() / 20 - 1);
		}
	}
}
