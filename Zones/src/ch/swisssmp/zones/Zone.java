package ch.swisssmp.zones;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.editor.Removable;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.JsonUtil;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockVector;

import java.io.File;
import java.util.*;

public abstract class Zone implements Removable {

    public static final String ID_PROPERTY = "ZoneId";

    private final ZoneCollection collection;
    private final UUID uid;
    private final String regionId;
    private final ZoneType type;
    private final RegionType regionType;
    private String name;

    private final List<BlockVector> points = new ArrayList<>();

    protected Zone(ZoneCollection collection, UUID uid, String regionId, ZoneType type){
        this.collection = collection;
        this.uid = uid;
        this.regionId = regionId;
        this.type = type;
        this.regionType = type.getRegionType();
    }

    public ZoneCollection getCollection(){
        return collection;
    }

    public ZoneContainer getContainer(){
        return collection.getContainer();
    }

    public World getWorld(){
        return getContainer().getBukkitWorld();
    }

    public UUID getUniqueId(){
        return uid;
    }

    public String getRegionId(){
        return regionId;
    }

    public ZoneType getType(){
        return type;
    }

    public RegionType getRegionType(){
        return regionType;
    }

    public String getName(){return name;}
    public void setName(String name){this.name = name;}

    public List<String> getItemLore(){
        return type.getItemLore(this);
    }

    public abstract BlockVector getMin();

    public abstract BlockVector getMax();

    public ItemStack getItemStack(){
        CustomItemBuilder itemBuilder = CustomItems.getCustomItemBuilder(type.getCustomEnum());
        itemBuilder.setDisplayName(type.getDisplayName(name));
        itemBuilder.setLore(getItemLore());
        itemBuilder.setAmount(1);
        ItemStack result = itemBuilder.build();
        ItemUtil.setString(result, ID_PROPERTY, uid.toString());
        return result;
    }

    public abstract boolean isSetupComplete();

    protected abstract boolean tryLoadWorldGuardRegion();
    protected abstract void updateWorldGuardRegion();
    private void removeWorldGuardRegion(){
        String regionId = uid.toString();
        WorldGuardHandler.removeRegion(getWorld(), regionId);
    }

    public void save(){
        JsonObject json = new JsonObject();
        JsonUtil.set("uuid", uid, json);
        JsonUtil.set("region_id", regionId, json);
        JsonUtil.set("name", name, json);
        json.add("data", saveData());
        File file = getFile();
        JsonUtil.save(file, json);
    }

    public void remove(){
        collection.removeZone(this);
    }

    protected void unlink(){
        File file = getFile();
        if(file.exists()) file.delete();
        removeWorldGuardRegion();
    }

    protected void unload(){
        // maybe do something later
    }

    protected abstract JsonObject saveData();
    protected abstract void loadData(JsonObject json);

    protected static Optional<Zone> load(ZoneCollection collection, JsonObject json){
        UUID uid;
        try{
            String uidString = JsonUtil.getString("uuid", json);
            uid = uidString!=null ? UUID.fromString(uidString) : null;
        }
        catch(Exception e){
            return Optional.empty();
        }

        String regionId = JsonUtil.getString("region_id", json);

        if(uid==null || regionId==null) return Optional.empty();

        ZoneType type = collection.getZoneType();
        Zone zone = type.getRegionType()==RegionType.CUBOID ? new CuboidZone(collection, uid, regionId, type) : new PolygonZone(collection, uid, regionId, type);
        zone.name = JsonUtil.getString("name", json);
        if(json.has("data")) zone.loadData(json.getAsJsonObject("data"));

        return Optional.of(zone);
    }

    private File getFile(){
        return new File(collection.getDirectory(), uid.toString()+".json");
    }
}
