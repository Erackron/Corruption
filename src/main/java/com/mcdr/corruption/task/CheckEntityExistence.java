package com.mcdr.corruption.task;

import com.mcdr.corruption.entity.Boss;
import com.mcdr.corruption.entity.CorEntityManager;

import java.util.Iterator;
import java.util.List;


public class CheckEntityExistence extends BaseTask {
    @Override
    public void run() {
        List<Boss> bosses = CorEntityManager.getBosses();

        for (Iterator<Boss> it = bosses.iterator(); it.hasNext(); ) {
            if (!it.next().IsEntityAlive())
                it.remove();
        }
    }
}
