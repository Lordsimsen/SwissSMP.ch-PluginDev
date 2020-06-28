package ch.swisssmp.text;

import com.google.gson.JsonObject;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.querz.nbt.io.SNBTUtil;
import net.querz.nbt.tag.CompoundTag;

import java.io.IOException;

public class ShowAdvancementEvent extends HoverEvent {

    private String id;

    protected ShowAdvancementEvent(String id) {
        super(HoverEvent.SHOW_ACHIEVEMENT);
        this.id = id;
    }

    public ShowAdvancementEvent id(String id){
        this.id = id;
        return this;
    }

    public String id(){
        return id;
    }

    @Override
    protected net.md_5.bungee.api.chat.HoverEvent spigot() {
        CompoundTag compound = new CompoundTag();
        if(id!=null) compound.putString("id", id);
        String value;
        try {
            value = SNBTUtil.toSNBT(compound);
        } catch (IOException e) {
            e.printStackTrace();
            value = "";
        }
        return new net.md_5.bungee.api.chat.HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_ACHIEVEMENT, new ComponentBuilder(value).create());
    }

    @Override
    protected void applyContents(JsonObject json) {
        JsonObject contents = new JsonObject();
        if(id!=null) contents.addProperty("id", id);
        json.add("contents", contents);
    }
}
