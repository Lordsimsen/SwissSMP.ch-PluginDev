package ch.swisssmp.text;

import com.google.gson.JsonObject;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.querz.nbt.io.SNBTUtil;
import net.querz.nbt.tag.CompoundTag;

import java.io.IOException;
import java.util.UUID;

public class ShowEntityEvent extends HoverEvent {

    private String name;
    private String type;
    private UUID id;

    protected ShowEntityEvent(String type, UUID id) {
        this(type, id, null);
    }

    protected ShowEntityEvent(String type, UUID id, String name) {
        super(HoverEvent.SHOW_ENTITY);
        this.name = name;
        this.type = type;
        this.id = id;
    }

    /**
     * Optional. Hidden if not present. A raw JSON text that is displayed as the name of the entity.
     */
    public ShowEntityEvent name(String name) {
        this.name = name;
        return this;
    }

    public String name() {
        return name;
    }

    /**
     * A string containing the type of the entity. Should be a namespaced entity ID. Present minecraft:pig if invalid.
     */
    public ShowEntityEvent type(String type) {
        this.type = type;
        return this;
    }

    public String type() {
        return type;
    }

    /**
     * A string containing the UUID of the entity in the hyphenated hexadecimal format. Should be a valid UUID.
     */
    public ShowEntityEvent id(UUID id) {
        this.id = id;
        return this;
    }

    public UUID id() {
        return id;
    }

    @Override
    protected net.md_5.bungee.api.chat.HoverEvent spigot() {
        CompoundTag compound = new CompoundTag();
        if (name != null) compound.putString("name", new RawText(name).toString());
        if (type != null) compound.putString("type", type);
        if (id != null) compound.putString("id", id.toString());
        String value;
        try {
            value = SNBTUtil.toSNBT(compound);
        } catch (IOException e) {
            e.printStackTrace();
            value = "";
        }
        return new net.md_5.bungee.api.chat.HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_ENTITY, new ComponentBuilder(value).create());
    }

    @Override
    protected void applyContents(JsonObject json) {
        JsonObject contents = new JsonObject();
        if (name != null) contents.addProperty("name", name);
        if (type != null) contents.addProperty("type", type);
        if (id != null) contents.addProperty("id", id.toString());
        json.add("contents", contents);
    }
}
