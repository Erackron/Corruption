<<<<<<< HEAD:src/com/mcdr/likeaboss/task/SavePlayerData.java
package com.mcdr.likeaboss.task;

import com.mcdr.likeaboss.player.LabPlayerManager;

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
=======
package com.mcdr.likeaboss.task;

import com.mcdr.likeaboss.player.LabPlayerManager;

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
>>>>>>> origin/EquipmentExpansion:src/com/mcdr/likeaboss/task/SavePlayerData.java
