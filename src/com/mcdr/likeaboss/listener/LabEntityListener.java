package com.mcdr.likeaboss.listener;

import java.util.ArrayList;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.LazyMetadataValue;

import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.datatypes.SkillType;
import com.mcdr.likeaboss.Likeaboss;
import com.mcdr.likeaboss.ability.Ability.ActivationCondition;
import com.mcdr.likeaboss.config.WorldConfig;
import com.mcdr.likeaboss.config.GlobalConfig.MessageParam;
import com.mcdr.likeaboss.entity.Boss;
import com.mcdr.likeaboss.entity.BossData;
import com.mcdr.likeaboss.entity.BossData.BossImmunity;
import com.mcdr.likeaboss.entity.SlimeBossData;
import com.mcdr.likeaboss.entity.ZombieBossData;
import com.mcdr.likeaboss.entity.PigZombieBossData;
import com.mcdr.likeaboss.entity.SkeletonBossData;
import com.mcdr.likeaboss.entity.LabEntity;
import com.mcdr.likeaboss.entity.LabEntityManager;
import com.mcdr.likeaboss.player.LabPlayer;
import com.mcdr.likeaboss.player.LabPlayerManager;
import com.mcdr.likeaboss.util.Utility;


public class LabEntityListener implements Listener {
	private final LazyMetadataValue isBoss = new FixedMetadataValue(Likeaboss.in, true);
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if (event.getSpawnReason() == SpawnReason.CUSTOM)
			return;
		
		LivingEntity livingEntity = event.getEntity();
		
		ArrayList<BossData> bossDatas = WorldConfig.getWorldData(livingEntity.getWorld()).getBossData(livingEntity.getType());	
		
		if (bossDatas == null)
			return;
		
		double chance = Utility.random.nextInt(100), curChance = 0;
		
		for(BossData bossData: bossDatas){
			if(bossData.getMaxSpawnLevel() < livingEntity.getLocation().getY())
				return;

			if(bossData instanceof ZombieBossData){
				ZombieBossData zBossData = (ZombieBossData) bossData;
				Zombie zombie = (Zombie) livingEntity;
				if(zombie.isBaby()!=zBossData.isBaby() || zombie.isVillager()!=zBossData.isVillager())
					return;
				if(bossData instanceof PigZombieBossData){
					PigZombieBossData pzBossData = (PigZombieBossData) zBossData;
					PigZombie pigZombie = (PigZombie) zombie;
					pigZombie.setAngry(pzBossData.isAngry());
				}
			} else if(bossData instanceof SkeletonBossData){
				SkeletonBossData sBossData = (SkeletonBossData) bossData;
				Skeleton skeleton = (Skeleton) livingEntity;
				if(skeleton.getSkeletonType()!=sBossData.getSkeletonType())
					return;
			} else if (bossData instanceof SlimeBossData){
				SlimeBossData slBossData = (SlimeBossData) bossData;
				Slime slime = (Slime) livingEntity;
				if(slime.getSize() < slBossData.getMinimalSize() || slime.getSize() > slBossData.getMaximalSize())
					return;
				
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
	}
	
	private void addBoss(LivingEntity livingEntity, BossData bossData){
		Boss boss = new Boss(livingEntity, bossData);
		LabEntityManager.AddBoss(boss);
		livingEntity.setMetadata("isBoss", isBoss);
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
			LabEntityManager.getBosses().remove(boss);
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
						LabPlayerManager.SendFoundMessage(labPlayer, false, player.getLocation(), boss.getBossData().getName());
					}
				default:
					break;
				}
				if(boss.getBossData().useDamageMultiplier())
					event.setDamage((int) (event.getDamage() * boss.getBossData().getDamageCoef()));
				else
					event.setDamage((int) boss.getBossData().getDamageCoef());
				boss.ActivateAbilities((LivingEntity) livingEntity, ActivationCondition.ONATTACK);
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
				if (boss.getBossData().getImmunities().contains(BossImmunity.ATTACK_IMMUNE)) {
					event.setCancelled(true);
					break;
				}
				if (player != null && !boss.getBossData().getImmunities().contains(BossImmunity.ENCHANT_FIRETICK_IMMUNE)) {
					Map<Enchantment, Integer> enchants = player.getItemInHand().getEnchantments();
					
					if (enchants.containsKey(Enchantment.FIRE_ASPECT))
						Bukkit.getScheduler().scheduleSyncDelayedTask(Likeaboss.in, new GetFireEnchantTicks(boss), 0);
				}
				
				if(player != null && Likeaboss.mcMMOInstalled){
					ItemStack weapon = player.getItemInHand();
					
					switch(weapon.getTypeId()){
					case 0:
						ExperienceAPI.addXP(player, SkillType.UNARMED, boss.getBossData().getMCMMOXPBonus());
						break;
					case 267:
					case 268:
					case 272:
					case 276:
					case 283:
						ExperienceAPI.addXP(player, SkillType.SWORDS, boss.getBossData().getMCMMOXPBonus());
						break;
					case 271:
					case 275:
					case 279:
					case 286:
					case 258:
						ExperienceAPI.addXP(player, SkillType.AXES, boss.getBossData().getMCMMOXPBonus());
						break;
					}
				}
				break;
			case PROJECTILE:
				if (boss.getBossData().getImmunities().contains(BossImmunity.PROJECTILE_IMMUNE)) {
					event.setCancelled(true);
					break;
				}
				if (player != null && !boss.getBossData().getImmunities().contains(BossImmunity.ENCHANT_FIRETICK_IMMUNE)) {
					Map<Enchantment, Integer> enchants = player.getItemInHand().getEnchantments();
					
					if (enchants.containsKey(Enchantment.ARROW_FIRE))
						Bukkit.getScheduler().scheduleSyncDelayedTask(Likeaboss.in, new GetFireEnchantTicks(boss), 0);
				}
				if(player != null && Likeaboss.mcMMOInstalled){
					ExperienceAPI.addXP(player, SkillType.ARCHERY, boss.getBossData().getMCMMOXPBonus());
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
				if (boss.getFireEnchantTick() > 0)
					boss.setFireEnchantTick(boss.getFireEnchantTick() - 1);
				else if (boss.getBossData().getImmunities().contains(BossImmunity.ENVIRONMENTAL_FIRETICK_IMMUNE)) {
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
			
			LabEntityManager.DamageBoss(boss, damage);
			try{
				boss.ActivateAbilities((LivingEntity) damager, ActivationCondition.ONDEFENSE);
			} catch(ClassCastException e){}
			
			
			// Generating viewer message	
			String viewerMsg = Utility.parseMessage((boss.getHealth()>0)?
													   MessageParam.VIEWERMESSAGE.getMessage():
													   MessageParam.VIEWERDEFEATED.getMessage(),
													 boss, boss.getHealth(), damage);
			
			//Sending viewer message to attacker
			if (labPlayer != null && labPlayer.getLabPlayerData().getViewer())
				player.sendMessage(viewerMsg);
			
			//Sending viewer message to nearby players
			for (LabPlayer labPlayerTemp : LabPlayerManager.getLabPlayers()) {
				if(labPlayerTemp != null && labPlayerTemp.getLabPlayerData().getViewer()){
					player = labPlayerTemp.getPlayer();
					if(labPlayer!=null && player.equals(labPlayer.getPlayer()))
						continue;
					if (Utility.isNear(player.getLocation(), boss.getLivingEntity().getLocation(), 0, 16)){
						player.sendMessage(viewerMsg);
					}
				}
			}
			
			if (boss.getHealth() <= 0) {
				boss.setKiller(labPlayer);
				event.setDamage(livingEntity.getMaxHealth()); //Kill the entity
				livingEntity.setHealth(1); //Needed for armored foes (must not be set to 0 otherwise Bukkit starts to do weird things)
			}
			else {
				livingEntity.setHealth(boss.getHealth()+damage);
				event.setDamage(damage);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onEntityRegainHealth(EntityRegainHealthEvent event){
		if(event.getEntity() instanceof Player)
			return;
		Boss boss = LabEntityManager.getBoss(event.getEntity());
		if(boss==null)
			return;
		LivingEntity livingEntity = boss.getLivingEntity();
		
		int regainAmount = -1;
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
				LabPlayer labPlayer = LabPlayerManager.getLabPlayer((Player) t);
				if (labPlayer != null && labPlayer.getLabPlayerData().getIgnore()) {
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
