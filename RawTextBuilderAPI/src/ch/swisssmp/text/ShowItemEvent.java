package ch.swisssmp.text;

import com.google.gson.JsonObject;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.querz.nbt.io.SNBTUtil;
import net.querz.nbt.tag.CompoundTag;

import java.io.IOException;
import java.util.UUID;

public class ShowItemEvent extends HoverEvent {
    private String id;
    private Integer count;
    private String tag;

    protected ShowItemEvent(String id){
        this(id, null, null);
    }

    protected ShowItemEvent(String id, Integer count){
        this(id, count, null);
    }

    protected ShowItemEvent(String id, Integer count, String tag) {
        super(HoverEvent.SHOW_ITEM);
        this.id = id;
        this.count = count;
        this.tag = tag;
    }

    /**
     * The namespaced item ID. Present minecraft:air if invalid.
     */
    public ShowItemEvent id(String id){
        this.id = id;
        return this;
    }

    public String id(){
        return id;
    }

    /**
     * Optional. Size of the item stack.
     */
    public ShowItemEvent count(int count){
        this.count = count;
        return this;
    }

    public int count(){
        return count;
    }

    /**
     * Optional. A string containing the serialized NBT of the additional information about the item, discussed more in
     * the subsections of the player format page.
     * https://minecraft.gamepedia.com/Player.dat_format#Item_structure
     */
    public ShowItemEvent tag(String tag){
        this.tag = tag;
        return this;
    }

    public String tag(){
        return tag;
    }

    @Override
    protected net.md_5.bungee.api.chat.HoverEvent spigot() {
        CompoundTag compound = new CompoundTag();
        if(id!=null) compound.putString("id", id);
        if(count!=null) compound.putInt("count", count);
        if(tag!=null) compound.putString("tag", tag);
        String value;
        try {
            value = SNBTUtil.toSNBT(compound);
        } catch (IOException e) {
            e.printStackTrace();
            value = "";
        }
        return new net.md_5.bungee.api.chat.HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_ITEM, new ComponentBuilder(value).create());
    }

    @Override
    protected void applyContents(JsonObject json) {
        JsonObject contents = new JsonObject();
        if(id!=null) contents.addProperty("id", id);
        if(count!=null) contents.addProperty("count", count);
        if(tag!=null) contents.addProperty("tag", tag);
        json.add("contents", contents);
    }
}
