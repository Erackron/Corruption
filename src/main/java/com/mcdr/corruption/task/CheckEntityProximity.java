package com.mcdr.corruption.task;

import com.mcdr.corruption.ability.Ability.ActivationCondition;
import com.mcdr.corruption.config.GlobalConfig.MessageParam;
import com.mcdr.corruption.entity.Boss;
import com.mcdr.corruption.entity.CorEntityManager;
import com.mcdr.corruption.player.CorPlayer;
import com.mcdr.corruption.player.CorPlayerManager;
import com.mcdr.corruption.util.MathUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;


public class CheckEntityProximity extends BaseTask {
    @Override
    public void run() {
        for (CorPlayer corPlayer : CorPlayerManager.getCorPlayers()) {
            Player player = corPlayer.getPlayer();

            for (Boss boss : CorEntityManager.getBosses()) {
                LivingEntity livingEntity = boss.getLivingEntity();

                if (MathUtil.isNear(player.getLocation(), livingEntity.getLocation(), 0, 16)) {
                    boss.ActivateAbilities(livingEntity, ActivationCondition.ONPROXIMITY);

                    if (!boss.getFound()) {
                        if (corPlayer.getCorPlayerData().getIgnore())
                            continue;

                        int bossTicksLived = livingEntity.getTicksLived();

                        if (bossTicksLived - boss.getLastTimeNotified() < 300)
                            continue;

                        int playerTicksLived = player.getTicksLived();

                        if (playerTicksLived - corPlayer.getLastTimeNotified() < 150)
                            continue;

                        if (!corPlayer.getWarmingUp()) {
                            corPlayer.setWarmingUp(true);
                            corPlayer.setWarmingUpStartTime(playerTicksLived);
                        } else if (playerTicksLived - corPlayer.getWarmingUpStartTime() >= 50) {
                            corPlayer.setWarmingUp(false);
                            corPlayer.setLastTimeNotified(playerTicksLived);
                            boss.setLastTimeNotified(bossTicksLived);
                            player.sendMessage(MessageParam.PROXIMITY.getMessage().replace('&', ChatColor.COLOR_CHAR));
                        }
                    }
                }
            }
        }
    }
}
