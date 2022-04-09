package xyz.awesomenetwork.minigametemplate.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import xyz.awesomenetwork.minigametemplate.GameManager;

public class PlayerQuitListener implements Listener {
	private final GameManager gameManager;

	public PlayerQuitListener(GameManager gameManager) {
		this.gameManager = gameManager;
	}

	@EventHandler
	public void playerQuit(PlayerQuitEvent e) {
		gameManager.removePlayer(e.getPlayer());
	}
}
