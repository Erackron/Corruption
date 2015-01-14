package com.mcdr.corruption.ability;

import com.mcdr.corruption.entity.Boss;
import com.mcdr.corruption.util.MathUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.List;

public class Teleport extends Ability {
    private boolean centeredOnFoe;

    public Teleport clone() {
        Teleport tp = new Teleport();
        copySettings(tp);
        tp.setCenteredOnFoe(this.centeredOnFoe);
        return tp;
    }

    /**
     * Normal Execute
     */
    public boolean Execute(LivingEntity livingEntity, Boss boss) {
        if (!super.Execute(livingEntity, boss))
            return false;

        Location location;

        if (centeredOnFoe)
            location = livingEntity.getLocation();
        else
            location = boss.getLivingEntity().getLocation();

        List<Block> validBlocks = findValidBlocks(location, 1, 5);

        if (!validBlocks.isEmpty()) {
            Block block = validBlocks.get(MathUtil.random.nextInt(validBlocks.size()));

            if (centeredOnFoe) {
                boss.getLivingEntity().teleport(block.getLocation());
                Vector v = livingEntity.getEyeLocation().clone().add(.5, .0, .5).subtract(boss.getLivingEntity().getEyeLocation()).toVector().normalize();
                MathUtil.setFacing(boss.getLivingEntity(), v);
            } else {
                livingEntity.teleport(block.getLocation());
                Vector v = boss.getLivingEntity().getEyeLocation().clone().add(.5, .0, .5).subtract(livingEntity.getEyeLocation()).toVector().normalize();
                MathUtil.setFacing(livingEntity, v);
            }
            useCooldown(boss);
            return true;
        }
        return false;
    }

    public void setCenteredOnFoe(boolean centeredOnFoe) {
        this.centeredOnFoe = centeredOnFoe;
    }
}