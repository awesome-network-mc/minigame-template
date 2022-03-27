package xyz.awesomenetwork.minigametemplate;

import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.awesomenetwork.minigametemplate.listeners.*;

public class MinigameTemplate extends JavaPlugin {

    public static final String PREFIX_INFO = ChatColor.GOLD + "● " + ChatColor.YELLOW;
    public static final String PREFIX_ERROR = ChatColor.DARK_RED + "● " + ChatColor.RED;

    private boolean reloadDetector = false;

    public void onEnable() {
        if (reloadDetector) {
            getLogger().info("can u not reload thx");
            return;
        }
        reloadDetector = true;

        getServer().getServicesManager().register(MinigameTemplate.class, this, this, ServicePriority.Normal);
    }

    public GameManager createGameManager(GameManagerOptions options) {
        GameManager gameManager = new GameManager(this, options);

        final PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(new EntityDamageByEntityListener(this), this);
        pm.registerEvents(new EntityDamageListener(gameManager), this);
        pm.registerEvents(new PlayerDeathListener(this, gameManager), this);
        pm.registerEvents(new PlayerJoinListener(gameManager), this);
        pm.registerEvents(new PlayerQuitListener(gameManager), this);

        return gameManager;
    }
}
