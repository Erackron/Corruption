package cam.task;

import cam.Likeaboss;

public class SavePlayerFiles extends BaseTask implements Runnable {
	
	@Override
	public void run() {
		try {
			Likeaboss.instance.getLabPlayerManager().SavePlayerData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
