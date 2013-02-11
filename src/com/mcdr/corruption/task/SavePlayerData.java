package com.mcdr.corruption.task;

import com.mcdr.corruption.player.CorPlayerManager;

public class SavePlayerData extends BaseTask {
	@Override
	public void run() {
		try {
			CorPlayerManager.SavePlayerData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
