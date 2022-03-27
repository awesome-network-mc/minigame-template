package xyz.awesomenetwork.minigametemplate;

public class GameManagerOptions {
    public final boolean joinStartedGame, autoSpectateOnDeath, autoRespawn, displayDeathMessages;
    public final int maxGameTimeSeconds, maxPlayers, minStartPlayers, gameEndTimeSeconds, autoRespawnTimerSeconds, combatTagTimeSeconds, gameStartCountdownSeconds;

    public GameManagerOptions(GameManagerOptionsBuilder options) {
        this.joinStartedGame = options.joinStartedGame;
        this.autoSpectateOnDeath = options.autoSpectateOnDeath;
        this.autoRespawn = options.autoRespawn;
        this.displayDeathMessages = options.displayDeathMessages;
        this.maxGameTimeSeconds = options.maxGameTimeSeconds;
        this.maxPlayers = options.maxPlayers;
        this.minStartPlayers = options.minStartPlayers;
        this.gameEndTimeSeconds = options.gameEndTimeSeconds;
        this.autoRespawnTimerSeconds = options.autoRespawnTimerSeconds;
        this.combatTagTimeSeconds = options.combatTagTimeSeconds;
        this.gameStartCountdownSeconds = options.gameStartCountdownSeconds;
    }

    public static class GameManagerOptionsBuilder {
        private boolean joinStartedGame = false;
        private boolean autoSpectateOnDeath = true;
        private boolean autoRespawn = false;
        private boolean displayDeathMessages = true;
        private int maxGameTimeSeconds = 0;
        private int maxPlayers = 100;
        private int minStartPlayers = 2;
        private int gameEndTimeSeconds = 10;
        private int autoRespawnTimerSeconds = 5;
        private int combatTagTimeSeconds = 5;
        private int gameStartCountdownSeconds = 60;

        public GameManagerOptionsBuilder canJoinStartedGame(boolean joinStartedGame) {
            this.joinStartedGame = joinStartedGame;
            return this;
        }

        public GameManagerOptionsBuilder shouldAutoSpectateOnDeath(boolean autoSpectateOnDeath) {
            this.autoSpectateOnDeath = autoSpectateOnDeath;
            return this;
        }

        public GameManagerOptionsBuilder canAutoRespawn(boolean autoRespawn) {
            this.autoRespawn = autoRespawn;
            return this;
        }

        public GameManagerOptionsBuilder displayDeathMessages(boolean displayDeathMessages) {
            this.displayDeathMessages = displayDeathMessages;
            return this;
        }

        public GameManagerOptionsBuilder maxGameTimerSeconds(int maxGameTimeSeconds) {
            this.maxGameTimeSeconds = maxGameTimeSeconds;
            return this;
        }

        public GameManagerOptionsBuilder maxIngamePlayers(int maxPlayers) {
            this.maxPlayers = maxPlayers;
            return this;
        }

        public GameManagerOptionsBuilder playersToStartGameCountdown(int minStartPlayers) {
            this.minStartPlayers = minStartPlayers;
            return this;
        }

        public GameManagerOptionsBuilder gameEndTimerSeconds(int gameEndTimeSeconds) {
            this.gameEndTimeSeconds = gameEndTimeSeconds;
            return this;
        }

        public GameManagerOptionsBuilder autoRespawnTimerSeconds(int autoRespawnTimerSeconds) {
            this.autoRespawnTimerSeconds = autoRespawnTimerSeconds;
            return this;
        }

        public GameManagerOptionsBuilder combatTagTimeSeconds(int combatTagTimeSeconds) {
            this.combatTagTimeSeconds = combatTagTimeSeconds;
            return this;
        }

        public GameManagerOptionsBuilder gameStartCountdownTimeSeconds(int gameStartCountdownSeconds) {
            this.gameStartCountdownSeconds = gameStartCountdownSeconds;
            return this;
        }

        public GameManagerOptions build() {
            return new GameManagerOptions(this);
        }
    }
}
