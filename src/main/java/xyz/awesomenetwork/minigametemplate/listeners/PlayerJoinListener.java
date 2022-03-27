package xyz.awesomenetwork.minigametemplate.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import xyz.awesomenetwork.minigametemplate.GameManager;

public class PlayerJoinListener implements Listener {
    private final GameManager gameManager;

    public PlayerJoinListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent e) {
        final Player player = e.getPlayer();

        if (gameManager.setPlayerInGame(player)) return;
        if (gameManager.setPlayerSpectating(player)) return;

        e.getPlayer().kickPlayer("An unknown error occurred while joining the game.");
    }
}
