package xyz.awesomenetwork.minigametemplate.combattag;

import org.bukkit.entity.Player;

import xyz.awesomenetwork.minigametemplate.enums.GameMetadata;

public class CombatTagUtil {
	private long combatTagTimeTicks;

	public CombatTagUtil(long combatTagTimeTicks) {
		this.combatTagTimeTicks = combatTagTimeTicks;;
	}

	public CombatTagInfo getPlayerCombatTag(Player player) {
		CombatTagInfo combatTag = player.hasMetadata(GameMetadata.COMBAT_TAG.name()) ? (CombatTagInfo) player.getMetadata(GameMetadata.COMBAT_TAG.name()).get(0).value() : null;
		if (combatTag != null) {
			if (player.getWorld().getFullTime() - combatTag.getCombatStartTimeInTicks() >= combatTagTimeTicks) combatTag = null; // Combat tag has expired
		}
		return combatTag;
	}
}
