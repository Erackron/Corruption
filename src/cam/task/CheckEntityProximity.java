package cam.task;

import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import cam.Utility;
import cam.ability.Ability.ActivationCondition;
import cam.config.GlobalConfig.MessageParam;
import cam.entity.Boss;
import cam.entity.LabEntityManager;
import cam.player.LabPlayer;
import cam.player.LabPlayerManager;

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
