package com.mcdr.corruption.command;

import com.mcdr.corruption.Corruption;
import com.mcdr.corruption.config.BossConfig;
import com.mcdr.corruption.config.ConfigManager;
import com.mcdr.corruption.entity.Boss;
import com.mcdr.corruption.entity.CorEntityManager;
import com.mcdr.corruption.entity.data.BossData;
import com.mcdr.corruption.task.TaskManager;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public abstract class ReloadCommand extends BaseCommand {
    public static void process() {
        if (!checkPermission("cor.reload", true))
            return;

        ConfigManager.Load();


        Map<String, BossData> bossesData = BossConfig.getBossesData();
        List<Boss> bossesToRemove = new ArrayList<Boss>();
        for (Boss boss : CorEntityManager.getBosses()) {
            boss.updateCustomName();
            if (bossesData.containsKey(boss.getRawName()))
                boss.setBossData(bossesData.get(boss.getRawName()));
            else {
                bossesToRemove.add(boss);
            }
        }

        CorEntityManager.purgeBosses(bossesToRemove);

        TaskManager.restart();

        sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "Reloaded");
    }
}
