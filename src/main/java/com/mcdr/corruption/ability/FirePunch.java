package com.mcdr.corruption.ability;

import com.mcdr.corruption.entity.Boss;
import org.bukkit.entity.LivingEntity;


public class FirePunch extends Ability {
    private int ticks = 2;

    public FirePunch clone() {
        FirePunch fp = new FirePunch();
        copySettings(fp);
        fp.setTicks(this.ticks);
        return fp;
    }

    /**
     * Normal Execute
     */
    public boolean Execute(LivingEntity livingEntity, Boss boss) {
        if (!super.Execute(livingEntity, boss))
            return false;

        int fireTicks = livingEntity.getFireTicks();

        //Somehow getFireTicks returns -20 when not on fire
        if (fireTicks < 0)
            livingEntity.setFireTicks(ticks * 20);
        else
            livingEntity.setFireTicks(fireTicks + ticks * 20);

        useCooldown(boss);
        sendMessage(boss, livingEntity);
        return true;
    }

    public void setTicks(int ticks) {
        //+1 because the first tick doesn't do any damage
        this.ticks = ticks + 1;
    }
}
