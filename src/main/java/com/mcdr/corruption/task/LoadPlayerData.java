package com.mcdr.corruption.task;

import com.mcdr.corruption.player.CorPlayerManager;

public class LoadPlayerData extends BaseTask {
    @Override
    public void run() {
        CorPlayerManager.initiatePlayerDataLoading();
    }
}
