package com.mcdr.corruption.entity;

import com.mcdr.corruption.Corruption;
import com.mcdr.corruption.CorruptionAPI;
import com.mcdr.corruption.ability.Ability;
import com.mcdr.corruption.ability.Ability.ActivationCondition;
import com.mcdr.corruption.ability.ArmorPierce;
import com.mcdr.corruption.config.GlobalConfig;
import com.mcdr.corruption.config.GlobalConfig.BossParam;
import com.mcdr.corruption.config.WorldConfig;
import com.mcdr.corruption.drop.DropCalculator;
import com.mcdr.corruption.entity.data.BossData;
import com.mcdr.corruption.entity.data.WitherBossData;
import com.mcdr.corruption.player.CorPlayer;
import com.mcdr.corruption.stats.StatsManager;
import com.mcdr.corruption.util.CorLogger;
import com.mcdr.corruption.util.Utility;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.Map.Entry;


public class Boss extends CorEntity implements CommandSender {
    private BossData bossData;
    private double health;
    private double maxHealth;
    private double originalMaxHealth;
    private Map<Ability, Boolean> abilities = new HashMap<Ability, Boolean>();
    private int fireEnchantTick;
    private CorPlayer killer;
    private boolean found;
    private int lastTimeNotified;
    private int spawner = 0;

    public Boss(LivingEntity livingEntity, BossData bossData) {
        this.livingEntity = livingEntity;
        this.bossData = bossData;
        this.originalMaxHealth = livingEntity.getMaxHealth();
        if (bossData.useHealthMultiplier())
            health = (int) (livingEntity.getMaxHealth() * bossData.getHealthCoef());
        else
            health = (int) bossData.getHealthCoef();

        livingEntity.setMaxHealth(health);
        livingEntity.setHealth(health);
        this.maxHealth = health;

        //Set the start health if this is a Wither, so it can regenerate like a vanilla Wither
        if (livingEntity.getType() == EntityType.WITHER) {
            int startHealth = (int) (health / 3.75);
            livingEntity.setHealth(startHealth);
            setHealth(startHealth);
        }

        updateCustomName(true);

        AddAbilities();

        bossData.setRandomEquipment(livingEntity);
    }

    public void updateCustomName() {
        updateCustomName(false);
    }

    public void updateCustomName(boolean force) {
        if (GlobalConfig.MessageParam.CUSTOMBOSSNAME.getMessage().equalsIgnoreCase("false")) return;
        if (livingEntity.isCustomNameVisible() || force)
            livingEntity.setCustomName(Utility.parseMessage(GlobalConfig.MessageParam.CUSTOMBOSSNAME.getMessage(), this));
        livingEntity.setCustomNameVisible(this.found);
    }

    private void AddAbilities() {
        abilities.clear();
        for (Ability ability : WorldConfig.getWorldData(livingEntity.getWorld()).getAbilities()) {
            if (Utility.random.nextInt(100) < ability.getAssignationChance())
                abilities.put(ability.clone(), true);
        }

        for (Ability ability : bossData.getAbilities()) {
            if (Utility.random.nextInt(100) < ability.getAssignationChance())
                abilities.put(ability.clone(), true);
        }
    }

    public List<String> abilityList() {
        List<String> al = new ArrayList<String>();
        for (Ability a : abilities.keySet()) {
            al.add(a.getName());
        }
        return al;
    }

    public void ActivateAbilities(LivingEntity livingEntity, ActivationCondition activationCondition) {
        ActivateAbilities(livingEntity, activationCondition, null);
    }

    public void ActivateOnDeathAbilities(LivingEntity targetEntity, Location lastLoc) {
        for (Entry<Ability, Boolean> entry : abilities.entrySet()) {
            Ability ability = entry.getKey();
            if (Utility.isNear(livingEntity.getLocation(), lastLoc, ability.getMinRange(), ability.getMaxRange())
                    && ability.getActivationConditions().contains(ActivationCondition.ONDEATH)) {
                ability.Execute(targetEntity, lastLoc, this);
            }
        }
    }

    public void ActivateAbilities(LivingEntity livingEntity, ActivationCondition activationCondition, EntityDamageEvent event) {
        if (livingEntity == null)
            return;

        for (Entry<Ability, Boolean> entry : abilities.entrySet()) {
            if (!entry.getValue())
                continue;

            Ability ability = entry.getKey();

            if (Utility.isNear(livingEntity.getLocation(), getLivingEntity().getLocation(), ability.getMinRange(), ability.getMaxRange())
                    && ability.getActivationConditions().contains(activationCondition)) {
                if (ability instanceof ArmorPierce && event != null)
                    ((ArmorPierce) ability).Execute(livingEntity, this, event);
                else
                    ability.Execute(livingEntity, this);
            }
        }
    }

    public BossData getBossData() {
        return bossData;
    }

    public double getHealth() {
        return health;
    }

    public int getFireEnchantTick() {
        return fireEnchantTick;
    }

    public CorPlayer getKiller() {
        return killer;
    }

    public boolean getFound() {
        return found;
    }

    public int getLastTimeNotified() {
        return lastTimeNotified;
    }

    public void setBossData(BossData bossData) {
        double curDamage = maxHealth - health;
        this.bossData = bossData;

        livingEntity.setMaxHealth(this.originalMaxHealth);

        if (bossData.useHealthMultiplier())
            maxHealth = (livingEntity.getMaxHealth() * bossData.getHealthCoef());
        else
            maxHealth = bossData.getHealthCoef();

        this.health = maxHealth - curDamage;
        livingEntity.setMaxHealth(maxHealth);
        livingEntity.setHealth(health);

        updateCustomName(true);

        AddAbilities();
        bossData.setRandomEquipment(livingEntity);
    }

    public void setHealth(double curHealth) {
        this.health = curHealth;
        this.updateCustomName();
    }

    public void setFireEnchantTick(int fireEnchantTick) {
        this.fireEnchantTick = fireEnchantTick;
    }

    public void setKiller(CorPlayer killer) {
        this.killer = killer;
    }

    public void setFound(boolean found) {
        this.found = found;
        this.updateCustomName();
    }

    public void setLastTimeNotified(int lastTimeNotified) {
        this.lastTimeNotified = lastTimeNotified;
    }


    public void OnDeath(EntityDeathEvent event) {
        //Prepare drops and exp
        List<ItemStack> drops = DropCalculator.CreateDrops(getBossData(), WorldConfig.getWorldData(livingEntity.getWorld()));
        int exp;
        if (getBossData().useExperienceMultiplier())
            exp = (int) (event.getDroppedExp() * getBossData().getExpCoef());
        else
            exp = (int) getBossData().getExpCoef();

        //Update drops and exp
        List<ItemStack> originalDrops = event.getDrops();
        if (BossParam.OVERWRITE_DROPS.getValue()) {
            if (getBossData().hasEquipment()) {
                EntityEquipment equips = event.getEntity().getEquipment();
                ArrayList<ItemStack> items = new ArrayList<ItemStack>();
                items.add(equips.getHelmet());
                items.add(equips.getChestplate());
                items.add(equips.getLeggings());
                items.add(equips.getBoots());
                items.add(equips.getItemInHand());
                originalDrops.retainAll(items);
            } else
                originalDrops.clear();

        }

        originalDrops.addAll(drops);
        event.setDroppedExp(exp);

        CorEntityManager.getBosses().remove(this);

        if (spawner != 0) {
            Spawner spawner = GlobalConfig.getSpawner(getSpawner());
            if (spawner != null) {
                spawner.removeBoss(this);
            }
        }

        if (killer != null) {
            killer.addBossKilled(bossData.getName(), 1);
            StatsManager.AddBossKilled(1);
        }
    }

    public void ChangeAbilityStatus(Ability ability, boolean status) {
        for (Entry<Ability, Boolean> entry : abilities.entrySet()) {
            if (entry.getKey() != ability)
                continue;

            entry.setValue(status);
            return;
        }
    }

    public int getRegenPerSecond() {
        if (bossData instanceof WitherBossData) {
            return ((WitherBossData) bossData).getRegenPerSecond();
        } else
            return 0;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(double curMaxHealth) {
        this.maxHealth = curMaxHealth;
    }

    public String getRawName() {
        return getBossData().getName();
    }

    /**
     * @return The DisplayName of this boss
     */
    public String getName() {
        return Utility.parseMessage("{BOSSNAME}", getRawName());
    }

    public static Boss restoreBoss(LivingEntity livingEntity, BossData bossData) {
        double curHealth = livingEntity.getHealth(), curMaxHealth = livingEntity.getMaxHealth();
        Boss boss = new Boss(livingEntity, bossData);
        livingEntity.setMaxHealth(curMaxHealth);
        livingEntity.setHealth(curHealth);
        boss.setMaxHealth(curMaxHealth);
        boss.setHealth(curHealth);
        String spawnId = CorruptionAPI.getMetaTag(livingEntity, "spawner");
        if (spawnId != null) {
            try {
                Spawner spawner = GlobalConfig.getSpawner(Integer.parseInt(spawnId));
                if (spawner != null) {
                    boss.setSpawner(spawner);
                    spawner.restoreBoss(boss);
                }
            } catch (NumberFormatException ignored) {
            }
        }
        return boss;
    }

    public int getSpawner() {
        return spawner;
    }

    protected void setSpawner(Spawner spawner) {
        this.spawner = spawner.getId();
        livingEntity.setMetadata("spawner", new FixedMetadataValue(Corruption.in, spawner.getId()));
    }

    /**
     * ComandSender methods **
     */
    public boolean hasPermission(String arg0) {
        return true;
    }

    public boolean hasPermission(Permission arg0) {
        return true;
    }

    public boolean isPermissionSet(String arg0) {
        return true;
    }

    public boolean isPermissionSet(Permission arg0) {
        return true;
    }

    public boolean isOp() {
        return true;
    }

    public Server getServer() {
        return Bukkit.getServer();
    }

    /**
     * Required Stub methods **
     */
    public void sendMessage(String message) {
        CorLogger.i(getName() + ": " + message);
    }

    public void sendMessage(String[] messages) {
        for (String message : messages) sendMessage(message);
    }

    public void setOp(boolean arg0) {
    }

    public PermissionAttachment addAttachment(Plugin arg0) {
        return null;
    }

    public PermissionAttachment addAttachment(Plugin arg0, int arg1) {
        return null;
    }

    public PermissionAttachment addAttachment(Plugin arg0, String arg1, boolean arg2) {
        return null;
    }

    public PermissionAttachment addAttachment(Plugin arg0, String arg1, boolean arg2, int arg3) {
        return null;
    }

    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return Bukkit.getConsoleSender().getEffectivePermissions();
    }

    public void recalculatePermissions() {
    }

    public void removeAttachment(PermissionAttachment arg0) {
    }
}
