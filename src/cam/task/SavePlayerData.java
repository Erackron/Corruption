package cam.task;

import cam.player.LabPlayerManager;

public class SavePlayerData extends BaseTask {
	@Override
	public void run() {
		try {
			LabPlayerManager.SavePlayerData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
