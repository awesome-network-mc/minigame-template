package xyz.awesomenetwork.minigametemplate.events;

public class GameRunningTimeEvent extends BaseEvent {

	private long gameRunningTimeSeconds;

	public GameRunningTimeEvent(long gameRunningTimeSeconds) {
		this.gameRunningTimeSeconds = gameRunningTimeSeconds;
	}

	public long getGameRunningTimeInSeconds() {
		return gameRunningTimeSeconds;
	}

}
