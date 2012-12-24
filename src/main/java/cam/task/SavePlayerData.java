package cam.task;

import cam.player.LabPlayerManager;

public class SavePlayerData extends BaseTask {
	public void run() {
		try {
			LabPlayerManager.SavePlayerData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
