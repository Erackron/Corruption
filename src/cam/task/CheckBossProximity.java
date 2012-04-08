package cam.task;

import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import cam.Likeaboss;
import cam.Utility;
import cam.boss.Boss;
import cam.config.GlobalConfig.MessageParam;
import cam.player.LabPlayer;
import cam.player.LabPlayerManager;

public class CheckBossProximity extends BaseTask implements Runnable {

	private LabPlayerManager labPlayerManager;
	
	public CheckBossProximity() {
		this.labPlayerManager = Likeaboss.instance.getLabPlayerManager();
	}
	
	@Override
	public void run() {
		LabPlayer[] tempLabPlayer = (LabPlayer[]) labPlayerManager.getLabPlayers().toArray(new LabPlayer[0]);
		
		for (LabPlayer labPlayer : tempLabPlayer) {
			if (labPlayer.getLabPlayerData().getIgnore())
				continue;
			
			Player player = labPlayer.getPlayer();
			
			if (player.isSprinting())
				continue;
			
			int playerTicksLived = player.getTicksLived();
			
			if (playerTicksLived - labPlayer.getLastTimeNotified() < 150)
				continue;
			
			for (Boss boss : tempBosses) {
				if (boss.getFound())
					continue;
				
				LivingEntity livingEntity = boss.getLivingEntity();
				int bossTicksLived = livingEntity.getTicksLived();
				
				if (bossTicksLived - boss.getLastTimeNotified() < 300)
					continue;
				
				if (Utility.IsNear(player.getLocation(), livingEntity.getLocation(), 3, 15)) {
					if (!labPlayer.getWarmingUp()) {
						labPlayer.setWarmingUp(true);
						labPlayer.setWarmingUpStartTime(playerTicksLived);
					}
					
					else if (playerTicksLived - labPlayer.getWarmingUpStartTime() >= 50) {
						labPlayer.setLastTimeNotified(playerTicksLived);
						boss.setLastTimeNotified(bossTicksLived);
						player.sendMessage(MessageParam.PROXIMITY.getMessage().replace('&', ChatColor.COLOR_CHAR));
					}
					
					return;
				}
			}
			
			labPlayer.setWarmingUp(false);
		}
	}
}
