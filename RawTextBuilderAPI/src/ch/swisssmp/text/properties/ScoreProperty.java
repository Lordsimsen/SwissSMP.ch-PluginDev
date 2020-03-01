package ch.swisssmp.text.properties;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ScoreComponent;

/**
 * A player's score in an objective. Displays nothing if the player is not tracked in the given objective.
 */
public class ScoreProperty implements IMainProperty {

	private String name;
	private String objective;
	private String value;

	private ScoreProperty(ScoreProperty template) {
		this.name = template.name;
		this.objective = template.objective;
		this.value = template.value;
	}
	
	/**
	 * Shows the recipient their own score
	 * @param objective: The internal name of the objective to display the player's score in.
	 */
	public ScoreProperty(String objective) {
		this("*", objective, null);
	}
	
	/**
	 * @param name: The name of the player whose score should be displayed. Selectors (such as \@p) can be used, in addition to "fake" player names created by the scoreboard system. In addition, if the name is "*", it shows the reader's own score.
	 * @param objective: The internal name of the objective to display the player's score in.
	 */
	public ScoreProperty(String name, String objective) {
		this(name, objective, null);
	}
	
	/**
	 * @param name: The name of the player whose score should be displayed. Selectors (such as \@p) can be used, in addition to "fake" player names created by the scoreboard system. In addition, if the name is "*", it shows the reader's own score.
	 * @param objective: The internal name of the objective to display the player's score in.
	 * @param value: Optional. If present, this value is used regardless of what the score would have been.
	 */
	public ScoreProperty(String name, String objective, String value) {
		
	}
	
	public ScoreProperty(Player player, Objective objective) {
		this(player, objective, null);
	}
	
	public ScoreProperty(Player player, Objective objective, String value) {
		this(player.getName(), objective.getName(), value);
	}
	
	@Override
	public String getKey() {
		return "score";
	}

	@Override
	public JsonElement serialize() {
		JsonObject result = new JsonObject();
		result.addProperty("name", this.name);
		result.addProperty("objective", this.objective);
		if(value!=null) result.addProperty("value", value);
		return result;
	}

	@Override
	public IProperty duplicate() {
		return new ScoreProperty(this);
	}

	@Override
	public BaseComponent toSpigot() {
		return new ScoreComponent(name, objective, value);
	}

}
