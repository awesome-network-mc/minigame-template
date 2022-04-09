package xyz.awesomenetwork.minigametemplate.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import xyz.awesomenetwork.minigametemplate.GameManager;
import xyz.awesomenetwork.minigametemplate.enums.GameState;

public class EntityDamageListener implements Listener {
	private final GameManager gameManager;

	public EntityDamageListener(GameManager gameManager) {
		this.gameManager = gameManager;
	}

	@EventHandler
	public void entityDamage(EntityDamageEvent e) {
		if (gameManager.getGameState() != GameState.STARTED) e.setCancelled(true);
		if (!(e.getEntity() instanceof Player)) return;
		if (e.isCancelled()) return;

		// Get victim player
		Player victim = (Player) e.getEntity();

		// Is victim dead?
		if (victim.getHealth() - e.getFinalDamage() > 0.0) return;

		e.setCancelled(true);
		gameManager.handlePlayerDeath(victim);
	}
}
