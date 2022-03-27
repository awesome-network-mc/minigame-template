package xyz.awesomenetwork.minigametemplate.listeners;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.awesomenetwork.minigametemplate.enums.GameMetadata;
import xyz.awesomenetwork.minigametemplate.events.GamePlayerDamageEvent;

public class EntityDamageByEntityListener implements Listener {
    private final JavaPlugin plugin;

    public EntityDamageByEntityListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void entityDamageByEntity(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player victim = (Player) e.getEntity();

        Player attacker = null;
        if (e.getDamager() instanceof Player) attacker = (Player) e.getDamager();
        else if (e.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) e.getDamager();
            if (projectile.getShooter() instanceof Player) attacker = (Player) projectile.getShooter();
        }

        if (attacker == null) return;

        GamePlayerDamageEvent event = new GamePlayerDamageEvent(attacker, victim, e.getDamage());
        plugin.getServer().getPluginManager().callEvent(event);
        e.setCancelled(event.isCancelled());
        if (event.isCancelled()) return;

        FixedMetadataValue combatStartTime = new FixedMetadataValue(plugin, System.currentTimeMillis());

        attacker.setMetadata(GameMetadata.COMBAT_LAST_TAG_MILLIS.name(), combatStartTime);
        attacker.setMetadata(GameMetadata.COMBAT_TAGGER_UUID.name(), new FixedMetadataValue(plugin, victim.getUniqueId()));
        attacker.setMetadata(GameMetadata.COMBAT_TAGGER_HEALTH.name(), new FixedMetadataValue(plugin, victim.getHealth()));

        victim.setMetadata(GameMetadata.COMBAT_TAGGER_HEALTH.name(), combatStartTime);
        victim.setMetadata(GameMetadata.COMBAT_TAGGER_UUID.name(), new FixedMetadataValue(plugin, attacker.getUniqueId()));
        victim.setMetadata(GameMetadata.COMBAT_TAGGER_HEALTH.name(), new FixedMetadataValue(plugin, attacker.getHealth()));
    }
}
