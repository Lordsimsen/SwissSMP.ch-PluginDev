package ch.swisssmp.text;

import com.google.gson.JsonObject;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;

public class NBTValueObject extends RawBase {

    // Content: NBT Values *
    private String nbt;
    private Boolean interpret;
    private String block;
    private String entity;
    private String storage;

    /**
     * The NBT path used for looking up NBT values from an entity, a block entity or an NBT storage. NBT strings display
     * their contents. Other NBT values are displayed as SNBT with no spacing or linebreaks. How values are displayed
     * depends on the value of  interpret. If more than one NBT value is found, either by selecting multiple entities or
     * by using a multi-value path, they are displayed in the form "Value1, Value2, Value3, Value4". Requires one of
     * block, entity, or storage. Having more than one is allowed, but only one will be used.
     */
    public NBTValueObject setNbt(String nbt){
        this.nbt = nbt;
        return this;
    }

    public String getNbt(){
        return nbt;
    }

    /**
     * Optional, defaults to false. If true, the game will try to parse the text of each NBT value as a raw JSON text
     * component. This usually only works if the value is an NBT string containing JSON, since JSON and SNBT are not
     * compatible. If parsing fails, displays nothing. Ignored if  nbt is not present.
     */
    public NBTValueObject setInterpret(boolean interpret){
        this.interpret = interpret;
        return this;
    }

    public boolean getInterpret(){
        return interpret;
    }

    /**
     * A string specifying the coordinates of the block entity from which the NBT value is obtained. The coordinates can
     * be absolute or relative. Ignored if  nbt is not present.
     */
    public NBTValueObject setBlock(String block){
        this.block = block;
        return this;
    }

    public String getBlock(){
        return block;
    }

    /**
     * A string specifying the target selector for the entity or entities from which the NBT value is obtained. Ignored
     * if  nbt is not present.
     */
    public NBTValueObject setEntity(String entity){
        this.entity = entity;
        return this;
    }

    public String getEntity(){
        return entity;
    }

    /**
     * A string specifying the namespaced ID of the command storage from which the NBT value is obtained. Ignored if  nbt is not present.
     */
    public NBTValueObject setStorage(String storage){
        this.storage = storage;
        return this;
    }

    public String getStorage(){
        return storage;
    }

    @Override
    protected BaseComponent createSpigotComponent() {
        Bukkit.getLogger().warning("[RawTextBuilderAPI] NBTValueObject hat keine Spigot-Konvertierung.");
        return null;
    }

    @Override
    protected void apply(JsonObject json) {
        if(nbt!=null) json.addProperty("nbt", nbt);
        if(interpret!=null) json.addProperty("interpret", interpret);
        if(block!=null) json.addProperty("block", block);
        if(entity!=null) json.addProperty("entity", entity);
        if(storage!=null) json.addProperty("storage", storage);
    }
}
