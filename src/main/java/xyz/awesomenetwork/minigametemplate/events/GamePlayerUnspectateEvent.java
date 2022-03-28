package xyz.awesomenetwork.minigametemplate.events;

import org.bukkit.entity.Player;

public class GamePlayerUnspectateEvent extends BaseEvent {
	private final Player player;
	private final int amountOfSpectators;

	public GamePlayerUnspectateEvent(Player player, int amountOfSpectators) {
		this.player = player;
		this.amountOfSpectators = amountOfSpectators;
	}

	public Player getPlayer() {
		return player;
	}

	public int getAmountOfSpectators() {
		return amountOfSpectators;
	}
}
