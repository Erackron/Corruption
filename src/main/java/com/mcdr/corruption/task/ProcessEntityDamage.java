package com.mcdr.corruption.task;

import com.mcdr.corruption.Corruption;
import com.mcdr.corruption.config.GlobalConfig.MessageParam;
import com.mcdr.corruption.entity.Boss;
import com.mcdr.corruption.entity.CorEntityManager;
import com.mcdr.corruption.handler.HeroesHandler;
import com.mcdr.corruption.player.CorPlayer;
import com.mcdr.corruption.player.CorPlayerManager;
import com.mcdr.corruption.util.MathUtil;
import com.mcdr.corruption.util.MessageUtil;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;


public class ProcessEntityDamage extends BaseTask {
    private Entity damager;
    private CorPlayer corPlayer = null;
    private Boss boss;
    private double healthBefore;
    private Location lastLoc;

    public ProcessEntityDamage(Entity damager, Boss boss, double d, Location lastLoc) {
        this.damager = damager;
        this.corPlayer = CorPlayerManager.getCorPlayer(damager);
        this.boss = boss;
        this.healthBefore = d;
        this.lastLoc = lastLoc;
    }

    @Override
    public void run() {
        if (damager == null || boss == null)
            return;

        double entityHealth = boss.getLivingEntity().getHealth(),
                damageTaken = 0;
        Player player;

        damageTaken = healthBefore - entityHealth;

        CorEntityManager.damageBoss(boss, damageTaken);

        // Generating viewer message
        String viewerMsg = MessageUtil.parseMessage((boss.getHealth() > 0) ?
                        (damageTaken > 0 ? MessageParam.VIEWERMESSAGE.getMessage() : MessageParam.VIEWERDAMAGEABSORBED.getMessage()) :
                        MessageParam.VIEWERDEFEATED.getMessage(),
                boss, damageTaken);

        //Sending viewer message to attacker
        if (corPlayer != null && corPlayer.getCorPlayerData().getViewer())
            corPlayer.getPlayer().sendMessage(viewerMsg);

        //Sending viewer message to nearby players
        for (CorPlayer corPlayerTemp : CorPlayerManager.getCorPlayers()) {
            if (corPlayerTemp != null && corPlayerTemp.getCorPlayerData().getViewer()) {
                player = corPlayerTemp.getPlayer();
                if (corPlayer != null && player.equals(corPlayer.getPlayer()))
                    continue;
                if (MathUtil.isNear(player.getLocation(), boss.getLivingEntity().getLocation(), 0, 16)) {
                    player.sendMessage(viewerMsg);
                }
            }
        }

        if (boss.getHealth() <= 0) {
            if (damager instanceof LivingEntity)
                boss.ActivateOnDeathAbilities((LivingEntity) damager, lastLoc);
            if (boss.getBossData().getHeroesXPBonus() > 0.0 && Corruption.heroesInstalled)
                HeroesHandler.addXP(corPlayer, boss.getBossData().getHeroesXPBonus(), boss.getLivingEntity());
            boss.setKiller(corPlayer);
        } else
            boss.updateCustomName();
    }
}
