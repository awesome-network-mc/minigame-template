package xyz.awesomenetwork.minigametemplate.listeners;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.awesomenetwork.minigametemplate.combattag.CombatTagInfo;
import xyz.awesomenetwork.minigametemplate.enums.GameMetadata;
import xyz.awesomenetwork.minigametemplate.events.GamePlayerPvpDamageEvent;

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

        GamePlayerPvpDamageEvent event = new GamePlayerPvpDamageEvent(attacker, victim, e.getDamage());
        plugin.getServer().getPluginManager().callEvent(event);
        e.setCancelled(event.isCancelled());
        if (event.isCancelled()) return;

        CombatTagInfo attackerCombatTag = new CombatTagInfo(victim.getWorld(), victim.getUniqueId(), victim.getName(), victim.getHealth());
        attacker.setMetadata(GameMetadata.COMBAT_TAG.name(), new FixedMetadataValue(plugin, attackerCombatTag));

        CombatTagInfo victimCombatTag = new CombatTagInfo(attacker.getWorld(), attacker.getUniqueId(), attacker.getName(), attacker.getHealth());
        victim.setMetadata(GameMetadata.COMBAT_TAG.name(), new FixedMetadataValue(plugin, victimCombatTag));
    }
}
