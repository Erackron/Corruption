package cam.event;

import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import cam.boss.Boss;

public class BossDamageEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled = false;
	private Boss boss = null;
	private Entity damager = null;
	private int damage = 0;
	
	public BossDamageEvent(Boss boss, Entity damager, int damage) {
		this.boss = boss;
		this.damager = damager;
		this.damage = damage;
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
	
	public Entity getDamager() {
		return damager;
	}
	
	public int getDamage() {
		return damage;
	}
	
	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	
	public void setDamage(int damage) {
		this.damage = damage;
	}
}
