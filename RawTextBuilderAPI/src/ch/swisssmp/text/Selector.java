package ch.swisssmp.text;

import com.google.gson.JsonObject;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.SelectorComponent;

public class Selector extends RawBase {

    // Content: Entity Names *
    private String selector;

    public Selector(String selector){
        this.selector = selector;
    }

    /**
     * A string containing a selector. Displayed as the name of the player or entity found by the selector. If more than
     * one player or entity is found by the selector, their names are displayed in either the form "Name1 and Name2" or
     * the form "Name1, Name2, Name3, and Name4". Hovering over a name will show a tooltip with the name, type, and UUID
     * of the target. Clicking a player's name suggests a command to whisper to that player. Shift-clicking a player's
     * name inserts that name into chat. Shift-clicking a non-player entity's name inserts its UUID into chat.
     */
    public Selector setSelector(String selector){
        this.selector = selector;
        return this;
    }

    public String getSelector(){
        return selector;
    }

    @Override
    protected BaseComponent createSpigotComponent() {
        return new SelectorComponent(selector);
    }

    @Override
    protected void apply(JsonObject json) {
        if(selector!=null) json.addProperty("selector", selector);
    }
}
