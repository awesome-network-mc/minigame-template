package xyz.awesomenetwork.minigametemplate;

public class GameManagerOptions {

    public final boolean joinStartedGame, autoSpectateOnDeath, autoRespawn, displayDeathMessages;
    public final int maxGameTimeSeconds, maxPlayers, minStartPlayers, gameEndTimeSeconds, autoRespawnTimerSeconds, combatTagTimeSeconds, gameStartCountdownSeconds;

    public GameManagerOptions() {
        this(false, true, 0, 0, 2, 10, false, 5, 5, true, 60);
    }

    public GameManagerOptions(boolean joinStartedGame, boolean autoSpectateOnDeath, int maxGameTimeSeconds, int maxPlayers, int minStartPlayers, int gameEndTimeSeconds, boolean autoRespawn, int autoRespawnTimerSeconds, int combatTagTimeSeconds, boolean displayDeathMessages, int gameStartCountdownSeconds) {
        this.joinStartedGame = joinStartedGame;
        this.autoSpectateOnDeath = autoSpectateOnDeath;
        this.maxGameTimeSeconds = maxGameTimeSeconds;
        this.maxPlayers = maxPlayers;
        this.minStartPlayers = minStartPlayers;
        this.gameEndTimeSeconds = gameEndTimeSeconds;
        this.autoRespawn = autoRespawn;
        this.autoRespawnTimerSeconds = autoRespawnTimerSeconds;
        this.combatTagTimeSeconds = combatTagTimeSeconds;
        this.displayDeathMessages = displayDeathMessages;
        this.gameStartCountdownSeconds = gameStartCountdownSeconds;
    }
}
