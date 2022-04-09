package xyz.awesomenetwork.minigametemplate.events;

public class GameCountdownEvent extends BaseEvent {
	private final long countdownSeconds;

	public GameCountdownEvent(long countdownSeconds) {
		this.countdownSeconds = countdownSeconds;
	}

	public long getCountdownSeconds() {
		return countdownSeconds;
	}
}
