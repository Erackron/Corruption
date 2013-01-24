package com.mcdr.likeaboss.ability;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;

import com.mcdr.likeaboss.entity.Boss;
import com.mcdr.likeaboss.util.Utility;

public class Teleport extends Ability {
    private int minRange;
    private int maxRange;
    private boolean centeredOnFoe;
    
    public Teleport() {
        activationConditions.add(ActivationCondition.ONDEFENSE);
    }
    
    @Override
    public void Execute(LivingEntity livingEntity, Boss boss) {
        Location location;
        
        if (centeredOnFoe)
            location = livingEntity.getLocation();
        else
            location = boss.getLivingEntity().getLocation();
        
        List<Block> validBlocks = findValidBlocks(location, minRange, maxRange);
        
        if (!validBlocks.isEmpty()) {
            Block block = validBlocks.get(Utility.random.nextInt(validBlocks.size()));
            
            boss.getLivingEntity().teleport(block.getLocation());
            useCooldown(boss);
        }
    }
    
    public void setCenteredOnFoe(boolean centeredOnFoe) {
        this.centeredOnFoe = centeredOnFoe;
    }
}