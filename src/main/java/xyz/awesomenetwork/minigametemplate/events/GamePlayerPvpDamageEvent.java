package xyz.awesomenetwork.minigametemplate.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class GamePlayerPvpDamageEvent extends BaseEvent implements Cancellable {

	private final Player attacker, victim;
	private final double damage;

	private boolean cancelled = false;

	public GamePlayerPvpDamageEvent(Player attacker, Player victim, double damage) {
		this.attacker = attacker;
		this.victim = victim;
		this.damage = damage;
	}

	public Player getAttacker() {
		return attacker;
	}

	public Player getVictim() {
		return victim;
	}

	public double getDamage() {
		return damage;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
}
