package xyz.awesomenetwork.minigametemplate;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.awesomenetwork.minigametemplate.combattag.CombatTagInfo;
import xyz.awesomenetwork.minigametemplate.combattag.CombatTagUtil;
import xyz.awesomenetwork.minigametemplate.enums.GameMetadata;
import xyz.awesomenetwork.minigametemplate.enums.GameState;
import xyz.awesomenetwork.minigametemplate.events.GameCountdownEvent;
import xyz.awesomenetwork.minigametemplate.events.GameEndEvent;
import xyz.awesomenetwork.minigametemplate.events.GamePlayerDeathEvent;
import xyz.awesomenetwork.minigametemplate.events.GamePlayerJoinEvent;
import xyz.awesomenetwork.minigametemplate.events.GamePlayerLeaveEvent;
import xyz.awesomenetwork.minigametemplate.events.GamePlayerRespawnEvent;
import xyz.awesomenetwork.minigametemplate.events.GamePlayerSpectateEvent;
import xyz.awesomenetwork.minigametemplate.events.GamePlayerUnspectateEvent;
import xyz.awesomenetwork.minigametemplate.events.GameRunningTimeEvent;
import xyz.awesomenetwork.minigametemplate.events.GameStartEvent;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;

public class GameManager {

    private final HashMap<Integer, String> COUNTDOWN_TITLE_NUMBERS = new HashMap<Integer, String>() {{
        put(60, "Starting in 60 seconds");
        put(50, "Starting in 50 seconds");
        put(40, "Starting in 40 seconds");
        put(30, "Starting in 30 seconds");
        put(20, "Starting in 20 seconds");
        put(10, "❿");
        put(9, "❾");
        put(8, "❽");
        put(7, "❼");
        put(6, "❻");
        put(5, "❺");
        put(4, "❹");
        put(3, ChatColor.GREEN + "❸");
        put(2, ChatColor.DARK_AQUA + "❷");
        put(1, ChatColor.BLUE + "❶");
    }};
    private final FixedMetadataValue playerDeathMetadata;

    private final JavaPlugin plugin;
    private final GameManagerOptions options;
    private final CombatTagUtil combatTagUtil;

    private final HashSet<Player> inGamePlayers = new HashSet<>();
    private final HashSet<Player> spectatingPlayers = new HashSet<>();

    private GameState gameState = GameState.PREGAME;
    private final HashMap<GameState, Integer> repeatingTasks = new HashMap<>();
    private final HashMap<Player, Integer> respawnTaskIds = new HashMap<>();

    public GameManager(JavaPlugin plugin, GameManagerOptions options, CombatTagUtil combatTagUtil) {
        this.plugin = plugin;
        this.options = options;
        this.combatTagUtil = combatTagUtil;

        playerDeathMetadata = new FixedMetadataValue(plugin, 1);

        startReminderChatMessages();
    }

    private void setPlayerRespawnTask(Player player, int taskId) {
        cancelPlayerRespawnTask(player);
        respawnTaskIds.put(player, taskId);
    }

    private void cancelPlayerRespawnTask(Player player) {
        respawnTaskIds.remove(player);
    }

    private boolean hasRepeatingTask(GameState gameState) {
        return repeatingTasks.containsKey(gameState);
    }

    private void setRepeatingTask(GameState gameState, int taskId) {
        cancelRepeatingTask(gameState);
        repeatingTasks.put(gameState, taskId);
    }

    private void cancelRepeatingTask(GameState gameState) {
        if (!repeatingTasks.containsKey(gameState)) return;
        plugin.getServer().getScheduler().cancelTask(repeatingTasks.get(gameState));
        repeatingTasks.remove(gameState);
    }

    public GameState getGameState() {
        return gameState;
    }
    public GameManagerOptions getOptions() {
        return options;
    }

    public boolean setPlayerInGame(Player player) {
        if ((gameState == GameState.STARTED || gameState == GameState.ENDED) && !options.joinStartedGame) return false;
        if (options.maxPlayers > 0 && inGamePlayers.size() >= options.maxPlayers) return false;

        removePlayer(player);

        boolean addedToGame = inGamePlayers.add(player);
        if (addedToGame) {
            plugin.getServer().getPluginManager().callEvent(new GamePlayerJoinEvent(player, inGamePlayers.size()));
            player.setLevel(0);
            player.setExp(0f);
            startGameCountdown();
        }

        return addedToGame;
    }

    public boolean setPlayerSpectating(Player player) {
        removePlayer(player);
        boolean spectating = spectatingPlayers.add(player);
        if (spectating) plugin.getServer().getPluginManager().callEvent(new GamePlayerSpectateEvent(player, spectatingPlayers.size()));
        return spectating;
    }

    public boolean removePlayer(Player player) {
        if (inGamePlayers.remove(player)) {
            // Check whether player was in combat
            CombatTagInfo combatTag = combatTagUtil.getPlayerCombatTag(player);
            if (combatTag != null) {
                handlePlayerDeath(player);
            }

            plugin.getServer().getPluginManager().callEvent(new GamePlayerLeaveEvent(player, inGamePlayers.size()));
            return true;
        }
        if (spectatingPlayers.remove(player)) {
            plugin.getServer().getPluginManager().callEvent(new GamePlayerUnspectateEvent(player, spectatingPlayers.size()));
            return true;
        }
        return false;
    }

    public boolean isPlayerAlive(Player player) {
        return inGamePlayers.contains(player);
    }

    private void startReminderChatMessages() {
        gameState = GameState.PREGAME;
        setRepeatingTask(GameState.PREGAME, plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if (plugin.getServer().getOnlinePlayers().size() == 0) return;
            plugin.getServer().broadcastMessage(MinigameTemplate.PREFIX_INFO + "Game starts with " + options.minStartPlayers + " players");
        }, 100, 300));
    }

    public boolean startGameCountdown() {
        if (inGamePlayers.size() < options.minStartPlayers) return false;
        if (hasRepeatingTask((GameState.COUNTDOWN))) return false;

        cancelRepeatingTask(GameState.PREGAME);
        gameState = GameState.COUNTDOWN;

        long countdownStart = Instant.now().getEpochSecond();

        setRepeatingTask(GameState.COUNTDOWN, plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if (inGamePlayers.size() < options.minStartPlayers) {
                cancelRepeatingTask(GameState.COUNTDOWN);
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    player.setLevel(0);
                    player.setExp(0f);
                    player.sendTitle(MinigameTemplate.PREFIX_ERROR + "Not enough players to start", "", 0, 25, 5);
                }
                startReminderChatMessages();
                return;
            }

            long timeRemaining = options.gameStartCountdownSeconds - (Instant.now().getEpochSecond() - countdownStart);
            plugin.getServer().getPluginManager().callEvent(new GameCountdownEvent(timeRemaining));

            final boolean showTitle = COUNTDOWN_TITLE_NUMBERS.containsKey((int) timeRemaining);
            final float exp = 0.99f / options.gameStartCountdownSeconds * timeRemaining;
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                player.setLevel((int) timeRemaining);
                player.setExp(exp);

                if (!showTitle) continue;
                String title = COUNTDOWN_TITLE_NUMBERS.get((int) timeRemaining);
                player.sendTitle(title, "", 0, 25, 5);
            }

            if (timeRemaining <= 0) {
                cancelRepeatingTask(GameState.COUNTDOWN);
                startGame();
                return;
            }
        }, 0, 20));

        return true;
    }

    public void startGame() {
        gameState = GameState.STARTED;

        long gameStart = Instant.now().getEpochSecond();

        FixedMetadataValue lifeStartSecondsMetadata = new FixedMetadataValue(plugin, gameStart);
        inGamePlayers.forEach(player -> {
            player.setMetadata(GameMetadata.LIFE_START_SECONDS.name(), lifeStartSecondsMetadata);
        });

        plugin.getServer().getPluginManager().callEvent(new GameStartEvent());

        setRepeatingTask(GameState.STARTED, plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if (gameState == GameState.ENDED) {
                cancelRepeatingTask(GameState.STARTED);
                return;
            }

            long gameRunningTimeSeconds = Instant.now().getEpochSecond() - gameStart;
            plugin.getServer().getPluginManager().callEvent(new GameRunningTimeEvent(gameRunningTimeSeconds));

            if (inGamePlayers.size() < options.minStartPlayers) {
                cancelRepeatingTask(GameState.STARTED);
                endGame();
                return;
            }

            if (options.maxGameTimeSeconds <= 0) return;
            if (gameRunningTimeSeconds >= options.maxGameTimeSeconds) endGame();
        }, 0, 20));
    }

    public void endGame() {
        gameState = GameState.ENDED;

        long gameEnd = Instant.now().getEpochSecond();

        plugin.getServer().getPluginManager().callEvent(new GameEndEvent());

        setRepeatingTask(GameState.ENDED, plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            long gameEndRunningTimeSeconds = Instant.now().getEpochSecond() - gameEnd;

            if (plugin.getServer().getOnlinePlayers().size() == 0) {
                cancelRepeatingTask(GameState.ENDED);
                gameEndRunningTimeSeconds = Long.MAX_VALUE; // Slightly drastic but makes sure the game ends
            }

            if (gameEndRunningTimeSeconds >= options.gameEndTimeSeconds) {
                cancelRepeatingTask(GameState.ENDED);

                plugin.getServer().getOnlinePlayers().forEach(player -> {
                    player.kickPlayer("this is where you'd be send to lobby");
                });

                plugin.getServer().shutdown();
            }

        }, 0, 20));
    }

    public void handlePlayerDeath(Player player) {
        if (player.hasMetadata(GameMetadata.PLAYER_DEATH.name())) return;
        player.setMetadata(GameMetadata.PLAYER_DEATH.name(), playerDeathMetadata);

        player.setHealth(20.0);

        String title;
        String deathMessage = null;
        CombatTagInfo combatInfo = combatTagUtil.getPlayerCombatTag(player);
        if (combatInfo != null) {
            ChatColor heartColour = ChatColor.DARK_RED;
            if (combatInfo.getHealth() >= 15) heartColour = ChatColor.GREEN;
            else if (combatInfo.getHealth() >= 10) heartColour = ChatColor.GOLD;
            else if (combatInfo.getHealth() >= 5) heartColour = ChatColor.RED;

            String attackerName = ChatColor.WHITE + combatInfo.getUsername();
            title = attackerName + " " + heartColour + (Math.round(combatInfo.getHealth() * 10.0) / 10) + "❤ " + ChatColor.RED + "killed you!";
            deathMessage = ChatColor.RED + "✖ " + player.getName() + ChatColor.RED + " was killed by " + attackerName + " " + heartColour + (Math.round(combatInfo.getHealth() * 10.0) / 10) + "❤";
        } else {
            title = ChatColor.RED + "You died!";
            deathMessage = ChatColor.RED + "✖ " + player.getName() + ChatColor.RED + " died";
        }

        if (options.displayDeathMessages) plugin.getServer().broadcastMessage(deathMessage);

        if (options.dropItemsOnDeath) {
            World world = player.getWorld();
            Location location = player.getLocation();
            for (ItemStack item : player.getInventory().getContents()) {
                if (item == null) continue;
                world.dropItemNaturally(location, item);
            }
        }

        plugin.getServer().getPluginManager().callEvent(new GamePlayerDeathEvent(player));

        if (options.autoRespawn) {
            long timerStart = Instant.now().getEpochSecond();

            setPlayerRespawnTask(player, plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                long secondsRemaining = options.autoRespawnTimerSeconds - (Instant.now().getEpochSecond() - timerStart);

                if (getGameState() == GameState.ENDED) {
                    cancelPlayerRespawnTask(player);
                    return;
                }

                if (secondsRemaining == 0) {
                    player.removeMetadata(GameMetadata.PLAYER_DEATH.name(), plugin);
                    plugin.getServer().getPluginManager().callEvent(new GamePlayerRespawnEvent(player));
                    cancelPlayerRespawnTask(player);
                    return;
                }

                String subtitle = ChatColor.GRAY + "Respawning in " + secondsRemaining;
                player.sendTitle(title, subtitle, 0, 30, 10);
            }, 0, 20));
        } else {
            long seconds = Instant.now().getEpochSecond() - player.getMetadata(GameMetadata.LIFE_START_SECONDS.name()).get(0).asLong();
            long hours = seconds / 3600;
            seconds -= hours * 3600;
            long minutes = seconds / 60;
            seconds -= minutes * 60;

            String aliveTime = "";
            if (hours > 0) aliveTime += hours + "h ";
            if (minutes > 0) aliveTime += minutes + "m ";
             if (seconds > 0) aliveTime += seconds + "s";

             player.sendTitle(title, ChatColor.GRAY + "Your life lasted " + aliveTime, 10, 100, 10);

            if (options.autoSpectateOnDeath) {
                setPlayerSpectating(player);
            }
        }
    }
}
