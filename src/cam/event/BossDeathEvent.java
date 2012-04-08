package cam.event;

import java.util.List;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import cam.boss.Boss;

public class BossDeathEvent extends Event implements Cancellable {
	
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;
	private Boss boss;
	private List<ItemStack> drops;
	private int exp;
	
	public BossDeathEvent(Boss boss, List<ItemStack> drops, int exp) {
		this.boss = boss;
		this.drops = drops;
		this.exp = exp;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	@Override
	public boolean isCancelled() {
		return cancelled;
	}
	
	public Boss getBoss() {
		return boss;
	}
	
	public List<ItemStack> getDrops() {
		return drops;
	}
	
	public int getExp() {
		return exp;
	}
	
	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	
	public void setDrops(List<ItemStack> drops) {
		this.drops = drops;
	}
	
	public void setExp(int exp) {
		this.exp = exp;
	}
}
