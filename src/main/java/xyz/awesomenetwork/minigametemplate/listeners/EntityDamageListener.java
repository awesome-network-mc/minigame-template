package xyz.awesomenetwork.minigametemplate.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import xyz.awesomenetwork.minigametemplate.GameManager;
import xyz.awesomenetwork.minigametemplate.enums.GameState;

public class EntityDamageListener implements Listener {
    private final GameManager gameManager;

    public EntityDamageListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler(priority = EventPriority.LOWEST) // Execute first
    public void entityDamage(EntityDamageEvent e) {
        if (gameManager.getGameState() != GameState.STARTED) e.setCancelled(true);
    }
}
