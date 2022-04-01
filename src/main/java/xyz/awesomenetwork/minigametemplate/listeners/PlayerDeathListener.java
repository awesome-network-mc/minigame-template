package xyz.awesomenetwork.minigametemplate.listeners;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.awesomenetwork.minigametemplate.GameManager;
import xyz.awesomenetwork.minigametemplate.GameManagerOptions;
import xyz.awesomenetwork.minigametemplate.enums.GameMetadata;
import xyz.awesomenetwork.minigametemplate.enums.GameState;
import xyz.awesomenetwork.minigametemplate.events.GamePlayerDeathEvent;
import xyz.awesomenetwork.minigametemplate.events.GamePlayerRespawnEvent;

import java.time.Instant;
import java.util.UUID;

public class PlayerDeathListener implements Listener {

    private final JavaPlugin plugin;
    private final GameManager gameManager;

    private int respawnTaskId = -1;

    public PlayerDeathListener(JavaPlugin plugin, GameManager gameManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
    }

    private void cancelRespawnTask() {
        if (respawnTaskId == -1) return;
        plugin.getServer().getScheduler().cancelTask(respawnTaskId);
    }

    @EventHandler
    public void playerDeath(PlayerDeathEvent e) {
        long deathTimeMillis = System.currentTimeMillis();
        final Player victim = e.getEntity();
        String victimName = victim.getCustomName() != null ? victim.getCustomName() : ChatColor.WHITE + victim.getName();

        if (!gameManager.isPlayerAlive(victim)) return;

        GameManagerOptions options = gameManager.getOptions();

        String title;
        boolean killedByAttacker = false;
        if (victim.hasMetadata(GameMetadata.COMBAT_LAST_TAG_MILLIS.name())) {
            long combatLastTagMillis = victim.getMetadata(GameMetadata.COMBAT_LAST_TAG_MILLIS.name()).get(0).asLong();
            killedByAttacker = deathTimeMillis - combatLastTagMillis <= options.combatTagTimeSeconds * 1000;
        }

        if (killedByAttacker) {
            double attackerHealth = victim.getMetadata(GameMetadata.COMBAT_TAGGER_HEALTH.name()).get(0).asDouble();
            UUID attackerUuid = (UUID) victim.getMetadata(GameMetadata.COMBAT_TAGGER_UUID.name()).get(0).value();
            OfflinePlayer attacker = plugin.getServer().getOfflinePlayer(attackerUuid);

            plugin.getServer().getPluginManager().callEvent(new GamePlayerDeathEvent(victim, attacker, attackerHealth));

            ChatColor heartColour = ChatColor.DARK_RED;
            if (attackerHealth >= 15) heartColour = ChatColor.GREEN;
            else if (attackerHealth >= 10) heartColour = ChatColor.GOLD;
            else if (attackerHealth >= 5) heartColour = ChatColor.RED;

            String attackerName = ChatColor.WHITE + attacker.getName();
            if (attacker.isOnline() && attacker.getPlayer().getCustomName() != null) attackerName = attacker.getPlayer().getCustomName();

            title = attackerName + " " + heartColour + (Math.round(attackerHealth * 10.0) / 10.0) + "❤ " + ChatColor.RED + "killed you!";

            if (options.displayDeathMessages) e.setDeathMessage(ChatColor.RED + "✖ " + victimName + ChatColor.RED + " was killed by " + attackerName + " " + heartColour + (Math.round(attackerHealth * 10.0) / 10.0) + "❤");
        } else {
            plugin.getServer().getPluginManager().callEvent(new GamePlayerDeathEvent(victim));

            title = ChatColor.RED + "You died!";

            if (options.displayDeathMessages) e.setDeathMessage(ChatColor.RED + "✖ " + victimName + ChatColor.RED + " died");
        }

        victim.spigot().respawn();
        
        if (options.autoRespawn) {
            long timerStart = Instant.now().getEpochSecond();

            respawnTaskId = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                long secondsRemaining = options.autoRespawnTimerSeconds - (Instant.now().getEpochSecond() - timerStart);

                if (gameManager.getGameState() == GameState.ENDED) {
                    cancelRespawnTask();
                    return;
                }

                if (secondsRemaining <= 0) {
                    plugin.getServer().getPluginManager().callEvent(new GamePlayerRespawnEvent(victim));
                    cancelRespawnTask();
                    return;
                }

                String subtitle = ChatColor.GRAY + "Respawning in " + secondsRemaining;
                victim.sendTitle(title, subtitle, 0, 30, 10);

            }, 0, 20);
        } else {
            long seconds = victim.getMetadata(GameMetadata.LIFE_START_SECONDS.name()).get(0).asLong();
            long hours = seconds / 3600;
            seconds -= hours * 3600;
            long minutes = seconds / 60;
            seconds -= minutes * 60;

            String aliveTime = "";
            if (hours > 0) aliveTime += hours + "h ";
            if (minutes > 0) aliveTime += minutes + "m ";
             if (seconds > 0) aliveTime += seconds + "s";

             victim.sendTitle(title, ChatColor.GRAY + "Your life lasted " + aliveTime, 10, 100, 10);

            if (options.autoSpectateOnDeath) {
                gameManager.setPlayerSpectating(victim);
            }
        }
    }
}
