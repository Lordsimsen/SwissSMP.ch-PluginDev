package ch.swisssmp.text;

import com.google.gson.JsonObject;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * A string containing plain text to display directly. Can also be a number or boolean that is displayed directly.
 */
public class RawText extends RawBase {
    // Content: Plain Text
    private String text;

    public RawText(){

    }

    public RawText(RawBase... extra){
        super(extra);
    }

    public RawText(String text){
        this.text = text;
    }

    public RawText(String text, RawBase... extra){
        super(extra);
        this.text = text;
    }

    public RawText text(String text){
        this.text = text;
        return this;
    }

    public String text(){
        return text;
    }

    @Override
    protected BaseComponent createSpigotComponent() {
        return text!=null ? new TextComponent(text) : new TextComponent();
    }

    @Override
    protected void apply(JsonObject json) {
        json.addProperty("text", text!=null ? text : "");
    }
}
