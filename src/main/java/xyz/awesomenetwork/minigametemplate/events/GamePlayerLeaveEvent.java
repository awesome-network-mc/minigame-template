package xyz.awesomenetwork.minigametemplate.events;

import org.bukkit.entity.Player;

public class GamePlayerLeaveEvent extends BaseEvent {
	private final Player player;
	private final int amountOfPlayers;

	public GamePlayerLeaveEvent(Player player, int amountOfPlayers) {
		this.player = player;
		this.amountOfPlayers = amountOfPlayers;
	}

	public Player getPlayer() {
		return player;
	}

	public int getAmountOfPlayers() {
		return amountOfPlayers;
	}
}
