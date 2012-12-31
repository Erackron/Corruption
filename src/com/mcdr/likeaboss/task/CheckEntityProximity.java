package com.mcdr.likeaboss.task;

import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.mcdr.likeaboss.Utility;
import com.mcdr.likeaboss.ability.Ability.ActivationCondition;
import com.mcdr.likeaboss.config.GlobalConfig.MessageParam;
import com.mcdr.likeaboss.entity.Boss;
import com.mcdr.likeaboss.entity.LabEntityManager;
import com.mcdr.likeaboss.player.LabPlayer;
import com.mcdr.likeaboss.player.LabPlayerManager;


public class CheckEntityProximity extends BaseTask {
	@Override
	public void run() {
		for (LabPlayer labPlayer : LabPlayerManager.getLabPlayers()) {
			Player player = labPlayer.getPlayer();
			
			for (Boss boss : LabEntityManager.getBosses()) {
				LivingEntity livingEntity = boss.getLivingEntity();
				
				if (Utility.isNear(player.getLocation(), livingEntity.getLocation(), 0, 16)) {
					boss.ActivateAbilities(null, livingEntity, ActivationCondition.ONPROXIMITY);
					
					if (!boss.getFound()) {
						if (labPlayer.getLabPlayerData().getIgnore())
							continue;
						
						int bossTicksLived = livingEntity.getTicksLived();
						
						if (bossTicksLived - boss.getLastTimeNotified() < 300)
							continue;
						
						int playerTicksLived = player.getTicksLived();
						
						if (playerTicksLived - labPlayer.getLastTimeNotified() < 150)
							continue;
						
						if (!labPlayer.getWarmingUp()) {
							labPlayer.setWarmingUp(true);
							labPlayer.setWarmingUpStartTime(playerTicksLived);
						}
						else if (playerTicksLived - labPlayer.getWarmingUpStartTime() >= 50) {
							labPlayer.setWarmingUp(false);
							labPlayer.setLastTimeNotified(playerTicksLived);
							boss.setLastTimeNotified(bossTicksLived);
							player.sendMessage(MessageParam.PROXIMITY.getMessage().replace('&', ChatColor.COLOR_CHAR));
						}
					}
				}
			}
		}
	}
}
