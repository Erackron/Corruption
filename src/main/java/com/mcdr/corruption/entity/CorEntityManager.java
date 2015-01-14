package com.mcdr.corruption.entity;

import com.mcdr.corruption.Corruption;
import com.mcdr.corruption.entity.data.*;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class CorEntityManager {
    private static final List<Boss> bosses = Collections.synchronizedList(new ArrayList<Boss>());
    private static List<EntityType> bossEntityTypes = new ArrayList<EntityType>();

    public static synchronized Boss spawnBossEntity(Location location, EntityType entityType, BossData bossData) {
        return spawnBossEntity(location, entityType, bossData, null);
    }

    public static synchronized Boss spawnBossEntity(Location location, EntityType entityType, BossData bossData, Spawner spawner) {
        LivingEntity spawnedCreature;
        World world = location.getWorld();
        Entity spawnedEntity = world.spawnEntity(location, entityType);
        if (spawnedEntity.isValid())
            spawnedCreature = (LivingEntity) spawnedEntity;
        else
            return null;

        adjustSpecificEntities(spawnedCreature, bossData, entityType);

        Boss boss = new Boss(spawnedCreature, bossData);

        addBoss(boss, spawner);
        return boss;
    }

    public static void adjustSpecificEntities(LivingEntity livingEntity, BossData bossData, EntityType entityType) {
        //Check and set the size of a slime
        if (Slime.class.isAssignableFrom(entityType.getEntityClass())) {
            Slime slime = (Slime) livingEntity;
            slime.setSize(((SlimeBossData) bossData).getMaximumSize());
        }

        //Check and set if it has to be a baby or villager zombie
        if (Zombie.class.isAssignableFrom(entityType.getEntityClass())) {
            Zombie zombie = (Zombie) livingEntity;
            zombie.setBaby(((ZombieBossData) bossData).isBaby());
            zombie.setVillager(((ZombieBossData) bossData).isVillager());
            if (PigZombie.class.isAssignableFrom(entityType.getEntityClass())) {
                PigZombie pigZombie = (PigZombie) zombie;
                pigZombie.setAngry(((PigZombieBossData) bossData).isAngry());
            }
        }

        //Check and set if it has to be a normal or wither skeleton
        if (Skeleton.class.isAssignableFrom(entityType.getEntityClass())) {
            Skeleton skeleton = (Skeleton) livingEntity;
            skeleton.setSkeletonType(((SkeletonBossData) bossData).getSkeletonType());
        }
    }

    public static synchronized void addBoss(Boss boss) {
        addBoss(boss, null);
    }

    public static synchronized void addBoss(Boss boss, Spawner spawner) {
        boss.getLivingEntity().setMetadata("isBoss", new FixedMetadataValue(Corruption.getInstance(), boss.getRawName()));
        if (spawner != null) {
            boss.setSpawner(spawner);
        }
        bosses.add(boss);
    }

    public static void damageBoss(Boss boss, double damageTaken) {
        boss.setHealth(boss.getHealth() - damageTaken);
    }

    public static boolean isDead(Boss boss) {
        return boss.getHealth() <= 0;
    }

    public static synchronized void clear() {
        bosses.clear();
    }

    public static synchronized Boss getBoss(Entity entity) {
        for (Boss boss : bosses) {
            if (boss.getLivingEntity() == entity)
                return boss;
        }
        return null;
    }

    public static synchronized List<Boss> getBosses() {
        return bosses;
    }

    public static synchronized List<EntityType> getBossEntityTypes() {
        for (Boss boss : bosses)
            bossEntityTypes.add(boss.getLivingEntity().getType());
        return bossEntityTypes;
    }

    public static synchronized CorEntity getEntity(LivingEntity livingEntity) {
        return getBoss(livingEntity);
    }

    public static synchronized void purgeAllBosses() {
        for (Boss boss : bosses) {
            boss.getLivingEntity().remove();
        }

        bosses.clear();
    }

    public static synchronized void purgeBosses(World world) {
        List<Boss> removeList = new ArrayList<Boss>();
        for (Boss boss : bosses) {
            if (world.getName().equalsIgnoreCase(boss.livingEntity.getWorld().getName())) {
                removeList.add(boss);
                boss.livingEntity.remove();
            }
        }
        bosses.removeAll(removeList);
    }

    public static synchronized void purgeBosses(Collection<Boss> bosses) {
        for (Boss boss : bosses) {
            boss.livingEntity.remove();
        }
        CorEntityManager.bosses.removeAll(bosses);
    }
}
