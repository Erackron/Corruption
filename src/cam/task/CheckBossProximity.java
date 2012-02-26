package cam.task;

import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import cam.Likeaboss;
import cam.Utility;
import cam.boss.Boss;
import cam.boss.BossManager;
import cam.config.MessageData;
import cam.player.LabPlayer;
import cam.player.LabPlayerManager;

class CheckBossProximity implements Runnable {

	private LabPlayerManager labPlayerManager = null;
	private BossManager bossManager = null;
	
	public CheckBossProximity(Likeaboss plugin) {
		this.labPlayerManager = plugin.getLabPlayerManager();
		this.bossManager = plugin.getBossManager();
	}
	
	@Override
	public void run() {
		Object[] tempLabPlayer = labPlayerManager.getLabPlayers().toArray();
		
		for (Object objectLabPlayer : tempLabPlayer) {
			LabPlayer labPlayer = (LabPlayer) objectLabPlayer;
			
			if (labPlayer.getLabPlayerData().getIgnore())
				continue;
			
			Player player = ((LabPlayer) objectLabPlayer).getPlayer();
			
			if (player.isSprinting())
				continue;
			
			int playerTicksLived = player.getTicksLived();
			
			if (playerTicksLived - labPlayer.getLastTimeNotified() < 20)
				continue;
			
			int maxNotifyRange = 15;
			int minNotifyRange = 3;
			
			Object[] tempBosses = bossManager.getBosses().toArray();
			
			for (Object objectBoss : tempBosses) {
				Boss boss = (Boss) objectBoss;
				
				if (boss.getFound())
					continue;
				
				LivingEntity livingEntity = boss.getLivingEntity();
				int bossTicksLived = livingEntity.getTicksLived();
				
				if (bossTicksLived - boss.getLastTimeNotified() < 300)
					continue;
				
				if (Utility.IsNear(player.getLocation(), livingEntity.getLocation(), minNotifyRange, maxNotifyRange)) {
					if (!labPlayer.getWarmingUp()) {
						labPlayer.setWarmingUp(true);
						labPlayer.setWarmingUpStartTime(playerTicksLived);
					}
					
					else if (playerTicksLived - labPlayer.getWarmingUpStartTime() >= 50) {
						labPlayer.setLastTimeNotified(playerTicksLived);
						boss.setLastTimeNotified(bossTicksLived);
						player.sendMessage(MessageData.PROXIMITY.getMessage().replace('&', ChatColor.COLOR_CHAR));
					}
					
					return;
				}
			}
			
			labPlayer.setWarmingUp(false);
		}
	}
}
