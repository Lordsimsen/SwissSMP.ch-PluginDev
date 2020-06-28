package ch.swisssmp.text;

import com.google.gson.JsonObject;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ScoreComponent;

/**
 * Displays a score holder's current score in an objective. Displays nothing if the given score holder or the given
 * objective do not exist, or if the score holder is not tracked in the objective.
 */
public class ScoreboardValue extends RawBase {
    private String name;
    private String objective;
    private String value;

    public ScoreboardValue(String name, String objective) {
        this.name = name;
        this.objective = objective;
    }

    public ScoreboardValue(String name, String objective, String value) {
        this(name, objective);
        this.value = value;
    }

    public ScoreboardValue setName(String name) {
        this.name = name;
        return this;
    }

    public String getName(){
        return name;
    }

    public ScoreboardValue setObjective(String objective) {
        this.objective = objective;
        return this;
    }

    public String getObjective(){
        return objective;
    }

    public ScoreboardValue setValue(String value) {
        this.value = value;
        return this;
    }

    public String getValue(){
        return value;
    }

    @Override
    protected BaseComponent createSpigotComponent() {
        return new ScoreComponent(name, objective, value);
    }

    @Override
    protected void apply(JsonObject json) {
        JsonObject scoreSection = new JsonObject();
        if(name!=null) scoreSection.addProperty("name", name);
        if(objective!=null) scoreSection.addProperty("objective", objective);
        if(value!=null) scoreSection.addProperty("value", value);
    }
}
