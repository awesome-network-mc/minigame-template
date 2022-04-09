package xyz.awesomenetwork.minigametemplate.combattag;

import java.util.UUID;

import org.bukkit.World;

public class CombatTagInfo {
	private World combatWorld;
	private long combatStartTimeTicks;
	private UUID uuid;
	private String username;
	private double health;

	public CombatTagInfo(World combatWorld, UUID uuid, String username, double health) {
		this.combatWorld = combatWorld;
		combatStartTimeTicks = combatWorld.getFullTime();
		this.uuid = uuid;
		this.username = username;
		this.health = health;
	}

	public World getCombatWorld() {
		return combatWorld;
	}

	public long getCombatStartTimeInTicks() {
		return combatStartTimeTicks;
	}

	public UUID getUUID() {
		return uuid;
	}

	public String getUsername() {
		return username;
	}

	public double getHealth() {
		return health;
	}

	public void newAttack(long currentTick, UUID uuid, String username, double health) {
		combatStartTimeTicks = currentTick;
		this.uuid = uuid;
		this.username = username;
		this.health = health;
	}
}
