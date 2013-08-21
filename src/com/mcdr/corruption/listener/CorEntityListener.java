package com.mcdr.corruption.listener;

import java.util.ArrayList;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import com.mcdr.corruption.Corruption;
import com.mcdr.corruption.CorruptionAPI;
import com.mcdr.corruption.ability.Ability.ActivationCondition;
import com.mcdr.corruption.config.BossConfig;
import com.mcdr.corruption.config.GlobalConfig;
import com.mcdr.corruption.config.WorldConfig;
import com.mcdr.corruption.entity.Boss;
import com.mcdr.corruption.entity.CorEntity;
import com.mcdr.corruption.entity.CorEntityManager;
import com.mcdr.corruption.entity.data.BossData;
import com.mcdr.corruption.entity.data.GhastBossData;
import com.mcdr.corruption.entity.data.PigZombieBossData;
import com.mcdr.corruption.entity.data.SkeletonBossData;
import com.mcdr.corruption.entity.data.SlimeBossData;
import com.mcdr.corruption.entity.data.ZombieBossData;
import com.mcdr.corruption.entity.data.BossData.BossImmunity;
import com.mcdr.corruption.handler.mcMMOHandler;
import com.mcdr.corruption.player.CorPlayer;
import com.mcdr.corruption.player.CorPlayerManager;
import com.mcdr.corruption.task.ProcessEntityDamage;
import com.mcdr.corruption.util.CorLogger;
import com.mcdr.corruption.util.Utility;


public class CorEntityListener implements Listener {	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		boolean debug = CorLogger.debugEnabled();
		if(debug){
			CorLogger.d(" __________________________________________________");
			CorLogger.d("|Event:\t\tSpawnEvent\t\t\t|");
			CorLogger.d("|EntityType:\t"+event.getEntityType().getName()+(event.getEntityType().getName().length()<8?"\t":"")+"\t\t\t|");
			CorLogger.d("|SpawnReason:\t"+event.getSpawnReason()+"\t\t\t\t|");
		}
		
		if (event.getSpawnReason() == SpawnReason.CUSTOM)
			return;
		
		LivingEntity livingEntity = event.getEntity();
		
		ArrayList<BossData> bossDatas = WorldConfig.getWorldData(livingEntity.getWorld()).getBossData(livingEntity.getType());	
		
		if (bossDatas == null)
			return;
		
		double chance = Utility.random.nextInt(100), curChance = 0;
		
		for(BossData bossData: bossDatas){
			if(bossData.getMaxSpawnLevel() < livingEntity.getLocation().getY() || bossData.getMinSpawnLevel() > livingEntity.getLocation().getY()){
				if(debug){
					CorLogger.d("|Skipping '"+bossData.getName()+"' due to height limit:\t|");
					CorLogger.d("|MinSpawnLevel:\tSpawnLevel:\tMaxSpawnLevel:\t|");
					CorLogger.d("|"+bossData.getMinSpawnLevel()+"\t\t"+livingEntity.getLocation().getY()+"\t\t"+bossData.getMaxSpawnLevel()+"\t\t|");
				}
				continue;
			}
				
			if(GlobalConfig.BossParam.ENABLE_BIOMES.getValue())
				if(!bossData.getBiomes().contains(event.getLocation().getBlock().getBiome())){
					if(debug){
						CorLogger.d("|Skipping '"+bossData.getName()+"' because of biome restrictions, '"+event.getLocation().getBlock().getBiome().name()+"' is not allowed.|");
					}
					continue;
				}

			if(bossData instanceof ZombieBossData){
				ZombieBossData zBossData = (ZombieBossData) bossData;
				Zombie zombie = (Zombie) livingEntity;
				if(zombie.isBaby()!=zBossData.isBaby() || zombie.isVillager()!=zBossData.isVillager())
					continue;
				if(bossData instanceof PigZombieBossData){
					PigZombieBossData pzBossData = (PigZombieBossData) zBossData;
					PigZombie pigZombie = (PigZombie) zombie;
					pigZombie.setAngry(pzBossData.isAngry());
				}
			} else if(bossData instanceof SkeletonBossData){
				SkeletonBossData sBossData = (SkeletonBossData) bossData;
				Skeleton skeleton = (Skeleton) livingEntity;
				if(skeleton.getSkeletonType()!=sBossData.getSkeletonType())
					continue;
			} else if (bossData instanceof SlimeBossData){
				SlimeBossData slBossData = (SlimeBossData) bossData;
				Slime slime = (Slime) livingEntity;
				if(slime.getSize() < slBossData.getMinimumSize() || slime.getSize() > slBossData.getMaximumSize())
					continue;
				
			}
			
			if(event.getSpawnReason() == SpawnReason.SPAWNER){
				if (chance < bossData.getChanceFromSpawner()+curChance) {
					addBoss(livingEntity, bossData);
					break;
				} else {
					curChance += bossData.getChanceFromSpawner();
				}
			} else if(chance < bossData.getChance()+curChance){
				addBoss(livingEntity, bossData);
				break;
			} else {
				curChance += bossData.getChance();
			}
		}
		if(debug) CorLogger.d("|__________________________________________________|");
	}
	
	private void addBoss(LivingEntity livingEntity, BossData bossData){
		Boss boss = new Boss(livingEntity, bossData);
		CorEntityManager.addBoss(boss);
		
		if(CorLogger.debugEnabled()){
			CorLogger.d("|BossData selection finished:\t\t\t|");
			CorLogger.d("|BossName:\t\t"+bossData.getName());
			CorLogger.d("|Abilities assigned:\t\t\t\t|");
			for(String aName: boss.abilityList())
				CorLogger.d("|"+aName);
		}
	}
	
	@EventHandler
	public void onChunkLoad(ChunkLoadEvent event){
		Entity[] eA = event.getChunk().getEntities();
		for(Entity e: eA){
			if(CorruptionAPI.hasBossMetatag(e) && CorEntityManager.getBoss(e)==null && e instanceof LivingEntity){
				BossData bossData = BossConfig.getBossesData().get(CorruptionAPI.getBossMetatag(e));
				if(bossData!=null){
					CorEntityManager.addBoss(Boss.restoreBoss((LivingEntity) e, bossData));
				} else {
					e.remove();
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityDeath(EntityDeathEvent event) {
		CorEntity corEntity = CorEntityManager.getEntity(event.getEntity());
		
		if (corEntity != null)
			corEntity.OnDeath(event);
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityExplode(EntityExplodeEvent event) {
		Boss boss = CorEntityManager.getBoss(event.getEntity());
		
		if (boss != null)
			CorEntityManager.getBosses().remove(boss);
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
			
			Boss boss = CorEntityManager.getBoss(damager);
			
			if (boss != null) {
				Entity entity = event.getEntity();
				
				if (!(entity instanceof LivingEntity))
					return;
				
				LivingEntity livingEntity = (LivingEntity) entity;
				
				switch (livingEntity.getType()) {
				case PLAYER:
					Player player = (Player) livingEntity;
					CorPlayer corPlayer = CorPlayerManager.getCorPlayer(player);
					
					if (corPlayer != null) {
						if (corPlayer.getCorPlayerData().getIgnore()) {
							if(damager instanceof Creature){
								((Creature)damager).setTarget((LivingEntity)damager);
								((Creature)damager).setTarget(null);
							}
							if(!event.isCancelled())
								event.setCancelled(true);
							return;
						}
					}
					
					//Found message
					if (!boss.getFound()) {
						boss.setFound(true);
						CorPlayerManager.SendFoundMessage(corPlayer, false, player.getLocation(), boss.getBossData().getName());
					}
				default:
					break;
				}
				if(boss.getBossData().useDamageMultiplier())
					event.setDamage((int) (event.getDamage() * boss.getBossData().getDamageCoef()));
				else
					event.setDamage((int) boss.getBossData().getDamageCoef());
				boss.ActivateAbilities((LivingEntity) livingEntity, ActivationCondition.ONATTACK, event);
			}
		}
		
		Boss boss = CorEntityManager.getBoss(event.getEntity());
		
		if (boss != null) {
			LivingEntity livingEntity = boss.getLivingEntity();
			
			//Simplified Minecraft invulnerability timer
			if (livingEntity.getNoDamageTicks() > livingEntity.getMaximumNoDamageTicks() / 2.0) {
				event.setCancelled(true);
				return;
			}
			
			Entity damager = null;
			Projectile projectile = null;
			
			//Damager finder
			if (event instanceof EntityDamageByEntityEvent) {
				damager = ((EntityDamageByEntityEvent) event).getDamager();
				
				if (damager instanceof Projectile) {
					projectile = (Projectile) damager;
					damager = projectile.getShooter();
					
					if (projectile instanceof Arrow)
						projectile.remove();
				}
			}
			
			Player player = null;
			CorPlayer corPlayer = null;
			
			if (damager instanceof Player) {
				player = (Player) damager;
				
				corPlayer = CorPlayerManager.getCorPlayer(player);
				
				//Player notifications
				if (!boss.getFound()) {
					boss.setFound(true);
					CorPlayerManager.SendFoundMessage(corPlayer, true, livingEntity.getLocation(), boss.getRawName());
				}
				
			}
			
			//Apply damage
			double damage = event.getDamage();
			
			switch (event.getCause()) {
			case ENTITY_ATTACK:	
				if (boss.getBossData().getImmunities().contains(BossImmunity.ATTACK_IMMUNE)) {
					event.setCancelled(true);
					break;
				}
				if (player != null) {
					Map<Enchantment, Integer> enchants = player.getItemInHand().getEnchantments();
					
					if (enchants.containsKey(Enchantment.FIRE_ASPECT))
						if(boss.getBossData().getImmunities().contains(BossImmunity.ENCHANT_FIRETICK_IMMUNE)){
							boss.setFireEnchantTick(1);
						} else
							Bukkit.getScheduler().scheduleSyncDelayedTask(Corruption.in, new GetFireEnchantTicks(boss), 0);
				}
				
				if(player != null && Corruption.mcMMOInstalled){
					mcMMOHandler.addMeleeXP(player, boss);
				}
				break;
			case PROJECTILE:
				if (boss.getBossData().getImmunities().contains(BossImmunity.PROJECTILE_IMMUNE)) {
					event.setCancelled(true);
					break;
				}
				
				if(projectile instanceof LargeFireball)
					if(boss.getLivingEntity().getType() == EntityType.GHAST)
						if(((GhastBossData) boss.getBossData()).isReturnToSenderImmune())
							event.setCancelled(true);
				
				if (player != null) {
					Map<Enchantment, Integer> enchants = player.getItemInHand().getEnchantments();
					
					if (enchants.containsKey(Enchantment.ARROW_FIRE))
						if(boss.getBossData().getImmunities().contains(BossImmunity.ENCHANT_FIRETICK_IMMUNE)){
							boss.setFireEnchantTick(1);
						} else
							Bukkit.getScheduler().scheduleSyncDelayedTask(Corruption.in, new GetFireEnchantTicks(boss), 0);
				}
				if(player != null && Corruption.mcMMOInstalled){
					mcMMOHandler.addRangeXP(player, boss);
				}
				break;
			case BLOCK_EXPLOSION:
				if (boss.getBossData().getImmunities().contains(BossImmunity.BLOCK_EXPLOSION_IMMUNE))
					event.setCancelled(true);
				else
					damage /= 2;
				break;
			case ENTITY_EXPLOSION:
				if (boss.getBossData().getImmunities().contains(BossImmunity.ENTITY_EXPLOSION_IMMUNE))
					event.setCancelled(true);
				else
					damage /= 2;
				break;
			case FIRE:
				if (boss.getBossData().getImmunities().contains(BossImmunity.FIRE_IMMUNE))
					event.setCancelled(true);
				break;
			case LAVA:
				if (boss.getBossData().getImmunities().contains(BossImmunity.LAVA_IMMUNE))
					event.setCancelled(true);
				break;
			case FIRE_TICK:
				//Handle fire enchants
				if (boss.getFireEnchantTick() > 0){
					if(boss.getBossData().getImmunities().contains(BossImmunity.ENCHANT_FIRETICK_IMMUNE)){
						boss.setFireEnchantTick(0);
						boss.getLivingEntity().setFireTicks(-20);
						event.setCancelled(true);
					} else {
						boss.setFireEnchantTick(boss.getFireEnchantTick() - 1);
					}
				} else if (boss.getBossData().getImmunities().contains(BossImmunity.ENVIRONMENTAL_FIRETICK_IMMUNE)) {
					livingEntity.setFireTicks(0);
					event.setCancelled(true);
				}
				break;
			case FALL:
				if (boss.getBossData().getImmunities().contains(BossImmunity.FALL_IMMUNE))
					event.setCancelled(true);
				break;
			case DROWNING:
				if (boss.getBossData().getImmunities().contains(BossImmunity.DROWNING_IMMUNE))
					event.setCancelled(true);
				break;
			case CONTACT:
				if (boss.getBossData().getImmunities().contains(BossImmunity.CONTACT_IMMUNE))
					event.setCancelled(true);
				break;
			case LIGHTNING:
				if (boss.getBossData().getImmunities().contains(BossImmunity.LIGHTNING_IMMUNE))
					event.setCancelled(true);
				break;
			case SUFFOCATION:
				if (boss.getBossData().getImmunities().contains(BossImmunity.SUFFOCATION_IMMUNE))
					event.setCancelled(true);
				break;
			case MAGIC:
				if (boss.getBossData().getImmunities().contains(BossImmunity.MAGIC_IMMUNE))
					event.setCancelled(true);
				else
					damage *= 1.25;
				break;
			case POISON:
				if (boss.getBossData().getImmunities().contains(BossImmunity.POISON_IMMUNE) || boss.getHealth() - damage <= 0)
					event.setCancelled(true);
				break;
			default:
				event.setCancelled(true);
				return;
			}
			
			if (event.isCancelled())
				return;
			
			if(damager instanceof LivingEntity)
				boss.ActivateAbilities((LivingEntity) damager, ActivationCondition.ONDEFENSE);
			
			
			event.setDamage(damage);
			
			if (livingEntity.getHealth()-damage <= 0)
				boss.setKiller(corPlayer); //Possibly obsolete
			
			Corruption.scheduler.scheduleSyncDelayedTask(Corruption.in, new ProcessEntityDamage(damager, boss, boss.getHealth(), livingEntity.getLocation()), 2L);
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onEntityRegainHealth(EntityRegainHealthEvent event){
		if(event.getEntity() instanceof Player)
			return;
		Boss boss = CorEntityManager.getBoss(event.getEntity());
		if(boss==null)
			return;
		LivingEntity livingEntity = boss.getLivingEntity();
		
		double regainAmount = -1;
		switch(event.getRegainReason()){
			case WITHER_SPAWN:
				regainAmount = event.getAmount()==10?livingEntity.getMaxHealth()/30:boss.getRegenPerSecond();
			case CUSTOM:
			case WITHER:
			case MAGIC:
			case MAGIC_REGEN:
				regainAmount = regainAmount==-1?event.getAmount():regainAmount;
				if(boss.getHealth() + regainAmount > livingEntity.getMaxHealth())
					regainAmount = livingEntity.getMaxHealth() - boss.getHealth();
				if(livingEntity.getHealth()!=boss.getHealth())
					livingEntity.setHealth(boss.getHealth());
				event.setAmount(regainAmount);
				boss.setHealth(boss.getHealth()+regainAmount);
				break;
			default:
				event.setCancelled(true);
				break;
		}
	}
	
	@EventHandler
	public void onEntityTarget(EntityTargetEvent event) {
		Entity t = event.getTarget();
		if(event.getEntity() instanceof LivingEntity){
			if(t instanceof Player) {
				CorPlayer corPlayer = CorPlayerManager.getCorPlayer((Player) t);
				if (corPlayer != null && corPlayer.getCorPlayerData().getIgnore()) {
					event.setCancelled(true);
				}
			}
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