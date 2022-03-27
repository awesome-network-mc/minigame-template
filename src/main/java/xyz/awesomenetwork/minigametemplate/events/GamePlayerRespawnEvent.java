package xyz.awesomenetwork.minigametemplate.events;

import org.bukkit.entity.Player;

public class GamePlayerRespawnEvent extends BaseEvent {

    private final Player player;

    public GamePlayerRespawnEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
