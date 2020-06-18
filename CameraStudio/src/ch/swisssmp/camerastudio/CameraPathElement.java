package ch.swisssmp.camerastudio;

import ch.swisssmp.editor.Removable;
import ch.swisssmp.utils.ItemUtil;
import com.google.gson.JsonObject;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.UUID;

public abstract class CameraPathElement implements Removable {

    public static final String UID_PROPERTY = "cameraStudioElementId";

    private final CameraStudioWorld world;
    private final UUID elementUid;
    private String name;

    protected CameraPathElement(CameraStudioWorld world, UUID elementUid, String name) {
        this.world = world;
        this.elementUid = elementUid;
        this.name = name;
    }

    public UUID getUniqueId(){
        return this.elementUid;
    }

    public CameraStudioWorld getWorld(){
        return world;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    public abstract ItemStack getItemStack();
    public abstract void remove();

    protected JsonObject save(){
        JsonObject result = new JsonObject();
        result.addProperty("uid", this.getUniqueId().toString());
        result.addProperty("name", this.getName());
        return result;
    }

    public static Optional<CameraPathElement> find(UUID elementUid){
        return CameraStudioWorlds.getElement(elementUid);
    }

    public static Optional<CameraPathElement> find(ItemStack itemStack){
        try{
            String uidString = ItemUtil.getString(itemStack, UID_PROPERTY);
            if(uidString==null) return Optional.empty();
            return find(UUID.fromString(uidString));
        }
        catch(Exception e){
            return Optional.empty();
        }

    }
}
