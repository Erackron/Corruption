package cam.player;

import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import cam.Likeaboss;
import cam.Utility;
import cam.boss.Boss;
import cam.boss.BossManager;
import cam.config.LabConfig;

public class LabPlayerTaskManager {
	
	private Likeaboss plugin = null;
	private BukkitScheduler bukkitScheduler = null;
	private int taskId = 0;
	
	public LabPlayerTaskManager(Likeaboss plugin) {
		this.plugin = plugin;
	}
	
	public void Start() {
		double checkBossProximity = LabConfig.TasksData.CHECK_BOSS_PROXIMITY.getValue();
		bukkitScheduler = plugin.getServer().getScheduler();
		
		if (checkBossProximity > 0)
			taskId = bukkitScheduler.scheduleAsyncRepeatingTask(plugin, new CheckBossProximity(plugin, bukkitScheduler), 0, (long) (checkBossProximity * 20));
	}
	
	public void Stop() {
		if (taskId != 0)
			bukkitScheduler.cancelTask(taskId);
	}
	
	public void Restart() {
		Stop();
		Start();
	}
}

class CheckBossProximity implements Runnable {

	private LabPlayerManager labPlayerManager = null;
	private BossManager bossManager = null;
	
	public CheckBossProximity(Likeaboss plugin, BukkitScheduler bukkitScheduler) {
		this.labPlayerManager = plugin.getLabPlayerManager();
		this.bossManager = plugin.getBossManager();
	}
	
	@Override
	public void run() {
		Object[] tempLabPlayer = labPlayerManager.getLabPlayers().toArray();
		
		for (Object objectLabPlayer : tempLabPlayer) {
			LabPlayer labPlayer = (LabPlayer) objectLabPlayer;
			Player player = ((LabPlayer) objectLabPlayer).getPlayer();
			
			if (player.isSprinting())
				return;
			
			int playerTicksLived = player.getTicksLived();
			
			if (playerTicksLived - labPlayer.getLastTimeNotified() < 20)
				return;
			
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
						player.sendMessage(ChatColor.DARK_RED + "You feel an evil presence...");
					}
					
					return;
				}
			}
			
			labPlayer.setWarmingUp(false);
		}
	}
}
