package com.mcdr.corruption.ability;

import com.mcdr.corruption.entity.Boss;
import com.mcdr.corruption.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class CommandAbility extends Ability {
    private String command;

    public CommandAbility clone() {
        CommandAbility ca = new CommandAbility();
        copySettings(ca);
        ca.setCommand(this.command);
        return ca;
    }

    public void setCommand(String command) {
        this.command = command.startsWith("/") ? command.substring(1) : command;
    }

    /**
     * OnDeath Execute
     */
    public boolean Execute(LivingEntity livingEntity, Location lastLoc, Boss boss) {
        if (!super.Execute(livingEntity, lastLoc, boss))
            return false;

        dispatchCommand(boss, livingEntity);
        sendAreaMessage(lastLoc, boss.getName(), livingEntity);

        return true;
    }

    /**
     * Normal Execute
     */
    public boolean Execute(LivingEntity livingEntity, Boss boss) {
        if (!super.Execute(livingEntity, boss))
            return false;

        dispatchCommand(boss, livingEntity);
        sendAreaMessage(boss, livingEntity);
        useCooldown(boss);

        return true;
    }

    private void dispatchCommand(Boss boss, LivingEntity le) {
        if (le instanceof Player)
            command = command.replace("{PLAYER}", ((Player) le).getName());

        Bukkit.getServer().dispatchCommand(boss, MessageUtil.parseMessage(command, boss.getName()));
    }
}
