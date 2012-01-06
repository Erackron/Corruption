package cam.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.inventory.ItemStack;

import cam.Utility;
import cam.boss.Boss;
import cam.boss.BossManager;
import cam.config.LabConfig;
import cam.player.LabPlayer;
import cam.player.LabPlayerManager;

public class LabEntityListener extends EntityListener {

	private BossManager bossManager = null;
	private LabPlayerManager labPlayerManager = null;
		
	public LabEntityListener(BossManager bossManager, LabPlayerManager labPlayerManager) {
		this.bossManager = bossManager;
		this.labPlayerManager = labPlayerManager;
	}
	
	public void AddToDrops(List<ItemStack> drops, Material material, int quantityMin, int quantityMax) {
		int quantity = quantityMin;
		
		if (quantityMin < quantityMax)
			quantity = Utility.Random(quantityMin, quantityMax);
		
		if (quantity == 0) //Don't drop a ghost item
			return;
		
		ItemStack itemStack = new ItemStack(material, quantity);
		
		drops.add(itemStack);
		bossManager.AddDroped(material, quantity);
	}
	
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if (event.getEntity() instanceof Monster) {
			LivingEntity livingEntity = (LivingEntity) event.getEntity();
			
			if (livingEntity.getLocation().getY() <= LabConfig.Entry.BOSS_SPAWN_MAXHEIGHT.getValue()) {
				if (Math.random() * 100 < LabConfig.Entry.BOSS_SPAWN_CHANCE.getValue())
					bossManager.AddBoss(livingEntity);
			}
		}
	}
	
	public void onEntityDeath(EntityDeathEvent event) {
		Entity entity = event.getEntity();
		
		if (!bossManager.IsBoss(entity))
			return;

		Boss boss = bossManager.getBoss(entity);		
		Location location = entity.getLocation();
		World world = entity.getWorld();
		List<ItemStack> drops = new ArrayList<ItemStack>();
		
		double chance = Math.random() * 100;
		if (chance < 40)
			AddToDrops(drops, Material.COOKED_BEEF, 1, 3);
		else if (chance < 80)
			AddToDrops(drops, Material.GRILLED_PORK, 1, 3);
			
		chance = Math.random() * 100;
		if (chance < 5)
			AddToDrops(drops, Material.DIAMOND, 1, 2);
		else if (chance < 15)
			AddToDrops(drops, Material.GOLD_INGOT, 1, 3);
		else if (chance < 30)
			AddToDrops(drops, Material.IRON_INGOT, 1, 4);
		else if (chance < 50)
			AddToDrops(drops, Material.COAL, 1, 5);
		
		chance = Math.random() * 100;
		if (chance < 3)
			AddToDrops(drops, Material.BOWL, 1, 1);
		else if (chance < 6)
			AddToDrops(drops, Material.GLASS_BOTTLE, 1, 1);
		else if (chance < 9)
			AddToDrops(drops, Material.STICK, 1, 1);
			
		for (ItemStack drop : drops)
			world.dropItemNaturally(location, drop);

		event.setDroppedExp((int) (event.getDroppedExp() * boss.getExpCoef()));
			
		bossManager.RemoveBoss(boss, true);
	}

	public void onEntityDamage(EntityDamageEvent event) {
		Entity entity = event.getEntity();
		
		if (entity instanceof Monster) {
			Monster monster = (Monster) entity;

			//Only affect bosses
			if (!bossManager.IsBoss(monster))
				return;
			
			//Invulnerability timer
			if (monster.getNoDamageTicks() > monster.getMaximumNoDamageTicks() / 2.0) {
				event.setCancelled(true);
				return;
			}
			
			Boss boss = bossManager.getBoss(monster);	
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
					double distance = monster.getLocation().distance(player.getLocation());
						
					if (distance >= 16)
						tooFar = true;

					if (!boss.getFound()) {
						boss.setFound(true);
						List<Entity> nearbyEntities = monster.getNearbyEntities(35, 35, 35);
									
						player.sendMessage(ChatColor.RED + "Oh noes, that's a boss!");
						if (tooFar)
							player.sendMessage(ChatColor.RED + "But it's too far away.");
									
						for (Entity nearbyEntity : nearbyEntities) {
							if (nearbyEntity instanceof Player && nearbyEntity != player)
								((Player) nearbyEntity).sendMessage(ChatColor.WHITE + player.getPlayerListName() + ChatColor.RED + " found a boss!");
						}
					}
					
					if (tooFar) {
						event.setCancelled(true);
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
						else if ((monster instanceof Spider || monster instanceof Silverfish) && enchantment.getId() == Enchantment.DAMAGE_ARTHROPODS.getId() )
							damage += Utility.Random(1, entry.getValue() * 4);
						else if ((monster instanceof Zombie || monster instanceof Skeleton) && enchantment.getId() == Enchantment.DAMAGE_UNDEAD.getId())
							damage += Utility.Random(1, entry.getValue() * 4);
						
						break;
					}
					
					//weapon.setDurability((short) (weapon.getDurability() - 1));
					
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
			else if (event.getCause() == DamageCause.VOID)
				bossManager.DamageBoss(boss, boss.getHealth());
			else {
				event.setCancelled(true);
				return;
			}
					
			//Should the entity die?
			if (bossManager.IsDead(boss))
				event.setDamage(100); //Zombies have armor, need to be superior to maxHealth
			else {
				monster.damage(0, damager);
				event.setCancelled(true); //Otherwise Minecraft adds enchant damage, it could result in OHK
			}
		}
		
		else if (entity instanceof Player) {
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
					List<Entity> nearbyEntities = boss.getLivingEntity().getNearbyEntities(35, 35, 35);
								
					player.sendMessage(ChatColor.RED + "Sneaky boss.");
								
					for (Entity nearbyEntity : nearbyEntities) {
						if (nearbyEntity instanceof Player && nearbyEntity != player)
							((Player) nearbyEntity).sendMessage(ChatColor.RED + "A boss found " + ChatColor.WHITE + player.getPlayerListName() + ChatColor.RED + "!");
					}
				}
			}
		}
	}
}