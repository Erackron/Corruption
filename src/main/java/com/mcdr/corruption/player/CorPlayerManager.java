package com.mcdr.corruption.player;

import com.mcdr.corruption.Corruption;
import com.mcdr.corruption.config.GlobalConfig.MessageParam;
import com.mcdr.corruption.logger.CorLogger;
import com.mcdr.corruption.task.TaskManager;
import com.mcdr.corruption.util.MathUtil;
import com.mcdr.corruption.util.MessageUtil;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public abstract class CorPlayerManager {
    private static List<CorPlayer> corPlayers = new ArrayList<CorPlayer>();
    private static List<CorPlayer> seenCorPlayers = new ArrayList<CorPlayer>();
    private static List<CorPlayer> corPlayersToLoad = new ArrayList<CorPlayer>();
    private static short requestId;

    public static void AddOnlinePlayers() {
        Collection<? extends Player> players = Corruption.getInstance().getServer().getOnlinePlayers();

        for (Player player : players)
            addCorPlayer(player);
    }

    public static CorPlayer addCorPlayer(Player player) {
        for (CorPlayer corPlayer : seenCorPlayers) {
            if (corPlayer.getName().equals(player.getName())) {
                corPlayer.setPlayer(player);
                corPlayers.add(corPlayer);
                seenCorPlayers.remove(corPlayer);
                return corPlayer;
            }
        }

        CorPlayer corPlayer = new CorPlayer(player);

        corPlayersToLoad.add(corPlayer);
        return corPlayer;
    }

    public static void removeCorPlayer(Player player) {
        for (CorPlayer corPlayer : corPlayers) {
            if (corPlayer.getName().equals(player.getName())) {
                corPlayers.remove(corPlayer);
                seenCorPlayers.add(corPlayer);
                return;
            }
        }

        for (CorPlayer corPlayer : corPlayersToLoad) {
            if (corPlayer.getName().equals(player.getName())) {
                corPlayersToLoad.remove(corPlayer);
                return;
            }
        }
    }

    public static void SendFoundMessage(CorPlayer corPlayer, boolean isFinder, Location location, String bossName) {
        String toPlayer = null;
        String toOthers = null;

        if (corPlayer == null)
            return;

        if (isFinder) {
            toPlayer = MessageParam.PLAYER_FOUND_BOSS_1.getMessage();
            toOthers = MessageParam.PLAYER_FOUND_BOSS_2.getMessage();
        } else {
            toPlayer = MessageParam.BOSS_FOUND_PLAYER_1.getMessage();
            toOthers = MessageParam.BOSS_FOUND_PLAYER_2.getMessage();
        }

        if (toPlayer.length() > 0) {
            toPlayer = MessageUtil.parseMessage(toPlayer, bossName);

            try {
                corPlayer.getPlayer().sendMessage(toPlayer);
            } catch (NullPointerException npe) {
                //Player object is null, this probably means the player has somehow already logged out.
            }
        }

        if (toOthers.length() > 0) {
            corPlayers.remove(corPlayer);

            toOthers = MessageUtil.parseMessage(toOthers, bossName).replace("{PLAYER}", corPlayer.getPlayer().getDisplayName());

            for (CorPlayer otherCorPlayer : corPlayers) {
                Player otherPlayer = otherCorPlayer.getPlayer();

                if (otherPlayer == null)
                    continue;

                if (MathUtil.isNear(otherPlayer.getLocation(), location, 0, 35))
                    otherPlayer.sendMessage(toOthers);
            }

            corPlayers.add(corPlayer);
        }
    }

    public static CorPlayer getCorPlayer(Entity entity) {
        for (CorPlayer corPlayer : corPlayers) {
            if (corPlayer.getPlayer() == entity)
                return corPlayer;
        }

        return null;
    }

    public static List<CorPlayer> getCorPlayers() {
        return corPlayers;
    }

    public static List<CorPlayer> getSeenCorPlayers() {
        return seenCorPlayers;
    }

    private static List<CorPlayer> getPlayersToSave() {
        List<CorPlayer> labPlayersToSave = new ArrayList<CorPlayer>(corPlayers.size() + seenCorPlayers.size());

        labPlayersToSave.addAll(corPlayers);
        labPlayersToSave.addAll(seenCorPlayers);

        return labPlayersToSave;
    }

    public static void initiatePlayerDataSaving() {
        if (TaskManager.getCorPlayerFileAccessor().initiatePlayerDataSaving(getPlayersToSave()))
            seenCorPlayers.clear();
    }

    public static void forcePlayerDataSaving() {
        CorLogger.info("Force saving player data.");
        TaskManager.getCorPlayerFileAccessor().forcePlayerDataSaving(getPlayersToSave());
        seenCorPlayers.clear();
    }

    public static void initiatePlayerDataLoading() {
        if (requestId != 0) {
            List<CorPlayer> loadedPlayers = TaskManager.getCorPlayerFileAccessor().getResult(requestId);

            if (loadedPlayers != null) {
                corPlayers.addAll(loadedPlayers);
                corPlayersToLoad.removeAll(loadedPlayers);
                loadedPlayers.clear();

                if (!corPlayersToLoad.isEmpty()) {
                    corPlayers.addAll(corPlayersToLoad);
                    corPlayersToLoad.clear();
                }

                requestId = 0;
            }
        } else if (!corPlayersToLoad.isEmpty()) {
            requestId = TaskManager.getCorPlayerFileAccessor().initiatePlayerDataLoading(corPlayersToLoad);
        }
    }
}
