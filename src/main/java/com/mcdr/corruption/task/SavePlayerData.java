package com.mcdr.corruption.task;

import com.mcdr.corruption.player.CorPlayerManager;

public class SavePlayerData extends BaseTask {
    @Override
    public void run() {
        CorPlayerManager.initiatePlayerDataSaving();
    }
}
