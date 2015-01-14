package com.mcdr.corruption.command;

import com.mcdr.corruption.Corruption;
import com.mcdr.corruption.player.CorPlayer;
import com.mcdr.corruption.task.TaskManager;
import com.mcdr.corruption.util.MathUtil;
import com.mcdr.corruption.util.MessageUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.Map.Entry;


public abstract class StatsCommand extends BaseCommand {
    public static void process() {
        if (!checkPermission("cor.stats", true))
            return;

        if (args.length < 2) {
            Corruption.scheduler.runTask(Corruption.getInstance(), new GlobalStatsDisplayer(sender));
        } else {
            CorPlayer corPlayer = new CorPlayer(Corruption.getInstance().getServer().getOfflinePlayer(args[1]));

            Corruption.scheduler.runTask(Corruption.getInstance(), new IndividualStatsDisplayer(sender, corPlayer));
        }
    }

    private static class GlobalStatsDisplayer extends StatsDisplayer {
        public GlobalStatsDisplayer(CommandSender sender) {
            super(sender);
        }

        public void run() {
            if (!RetrievePlayerData()) {
                Corruption.scheduler.runTaskLater(Corruption.getInstance(), this, 5L);
                return;
            }

            for (CorPlayer CorPlayer : this.CorPlayerList) {
                int amount = CorPlayer.getTotalBossesKilled();

                if (amount > 0) {
                    this.unsortedMap.put(CorPlayer.getName(), amount);
                }
            }

            this.sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + "Leaderboard (" + ChatColor.GREEN + "Bosses Killed" + ChatColor.WHITE + ")");
            DisplayStats();
            this.CorPlayerList.clear();
        }
    }

    private static class IndividualStatsDisplayer extends StatsDisplayer {
        private String playerName;

        public IndividualStatsDisplayer(CommandSender sender, CorPlayer CorPlayer) {
            super(sender);
            this.playerName = CorPlayer.getName();
            this.CorPlayerList = Arrays.asList(CorPlayer);
        }

        public void run() {
            if (!RetrievePlayerData()) {
                Corruption.scheduler.runTaskLater(Corruption.getInstance(), this, 5L);
                return;
            }

            if (this.CorPlayerList.isEmpty()) {
                this.sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.GRAY + this.playerName + ChatColor.WHITE + " isn't a valid player or doesn't have any stats yet.");
                return;
            }

            CorPlayer CorPlayer = this.CorPlayerList.get(0);

            String bossName;

            for (Entry<String, Integer> entry : CorPlayer.getBossesKilled().entrySet()) {
                bossName = MessageUtil.parseMessage("{BOSSNAME}", entry.getKey());
                if (this.unsortedMap.containsKey(bossName))
                    this.unsortedMap.put(bossName, this.unsortedMap.get(bossName) + entry.getValue());
                else
                    this.unsortedMap.put(bossName, entry.getValue());
            }

            this.sender.sendMessage(ChatColor.GOLD + "[" + Corruption.getPluginName() + "] " + ChatColor.WHITE + this.playerName + " (" + ChatColor.GREEN + "Bosses Killed" + ChatColor.WHITE + ")");
            DisplayStats();
            this.CorPlayerList.clear();
        }
    }

    private static abstract class StatsDisplayer
            implements Runnable {
        protected CommandSender sender;
        protected List<CorPlayer> CorPlayerList;
        protected short requestId;
        Map<String, Integer> unsortedMap = new HashMap<String, Integer>();

        public StatsDisplayer(CommandSender sender) {
            this.sender = sender;
        }

        protected boolean RetrievePlayerData() {
            if (this.requestId == 0) {
                this.requestId = TaskManager.getCorPlayerFileAccessor().initiatePlayerDataLoading(this.CorPlayerList);

                return false;
            }

            this.CorPlayerList = TaskManager.getCorPlayerFileAccessor().getResult(this.requestId);

            return this.CorPlayerList != null;

        }

        protected void DisplayStats() {
            Iterator<Entry<String, Integer>> it = MathUtil.sortEntriesByValues(this.unsortedMap, false).iterator();

            for (int i = 1; (i <= 10) && (it.hasNext()); i++) {
                Entry<String, Integer> entry = it.next();
                String message = ChatColor.GRAY + String.valueOf(i) + ". " + ChatColor.WHITE + entry.getKey() + " (" + ChatColor.GREEN + entry.getValue() + ChatColor.WHITE + ")";

                this.sender.sendMessage(message);
            }
        }
    }
}
