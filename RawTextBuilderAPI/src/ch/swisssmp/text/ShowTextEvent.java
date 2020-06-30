package ch.swisssmp.text;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.md_5.bungee.api.chat.BaseComponent;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ShowTextEvent extends HoverEvent {

    private RawBase[] text;

    protected ShowTextEvent(String text){
        this(new RawText(text));
    }

    protected ShowTextEvent(RawBase... text) {
        super(HoverEvent.SHOW_TEXT);
        this.text = text;
    }

    public ShowTextEvent text(String text){
        this.text = new RawText[]{new RawText(text)};
        return this;
    }

    public ShowTextEvent text(RawText... text){
        this.text = text;
        return this;
    }

    public RawBase[] text(){
        return text;
    }

    @Override
    protected net.md_5.bungee.api.chat.HoverEvent spigot() {
        BaseComponent[] value = new BaseComponent[text.length];
        Arrays.stream(text).map(RawBase::spigot).collect(Collectors.toList()).toArray(value);
        return new net.md_5.bungee.api.chat.HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, value);
    }

    @Override
    protected void applyContents(JsonObject json) {
        if(text==null || text.length==0) return;
        JsonElement contents;
        if(text.length>1){
            JsonArray textArray = new JsonArray();
            for(RawBase t : text){
                textArray.add(t.toJson());
            }
            contents = textArray;
        }
        else{
            contents = text[0].toJson();
        }
        json.add("contents", contents);
    }
}
