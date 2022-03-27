package xyz.awesomenetwork.minigametemplate.events;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class GamePlayerDeathEvent extends BaseEvent {

    private final Player victim;
    private final OfflinePlayer attacker;
    private final double attackerHealth;

    public GamePlayerDeathEvent(Player victim) {
        this.victim = victim;
        attacker = null;
        attackerHealth = 0.0;
    }

    public GamePlayerDeathEvent(Player victim, OfflinePlayer attacker, double attackerHealth) {
        this.attacker = attacker;
        this.attackerHealth = attackerHealth;
        this.victim = victim;
    }

    public Player getVictim() {
        return victim;
    }

    public boolean isSuicide() {
        return attacker == null;
    }

    public OfflinePlayer getAttacker() {
        return attacker;
    }

    public double getAttackerHealth() {
        return attackerHealth;
    }
}
