package xyz.awesomenetwork.minigametemplate.events;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import xyz.awesomenetwork.minigametemplate.combattag.CombatTagInfo;

public class GamePlayerDeathEvent extends BaseEvent {
    private final Player victim;
    private final CombatTagInfo combatTag;

    private String deathMessage = "";
    private List<ItemStack> victimDrops = new ArrayList<>();

    public GamePlayerDeathEvent(Player victim) {
        this(victim, null);
    }

    public GamePlayerDeathEvent(Player victim, CombatTagInfo combatTag) {
        this.victim = victim;
        this.combatTag = combatTag;

        for (ItemStack item : victim.getInventory().getContents()) {
            if (item != null) victimDrops.add(item);
        }
    }

    public Player getVictim() {
        return victim;
    }

    public boolean hasCombatTag() {
        return combatTag != null;
    }

    public CombatTagInfo getCombatTagInfo() {
        return combatTag;
    }

    public List<ItemStack> getVictimItemDrops() {
        return victimDrops;
    }

    public void setVictimItemDrops(List<ItemStack> victimDrops) {
        this.victimDrops = victimDrops;
    }

    public String getDeathMessage() {
        return deathMessage;
    }

    public void setDeathMessage(String deathMessage) {
        this.deathMessage = deathMessage;
    }
}
