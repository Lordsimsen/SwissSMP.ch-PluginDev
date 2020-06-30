package ch.swisssmp.text;

import ch.swisssmp.utils.ItemUtil;
import com.google.gson.JsonObject;
import net.md_5.bungee.api.chat.BaseComponent;
import net.querz.nbt.io.SNBTUtil;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public abstract class HoverEvent {

    public static final String SHOW_ACHIEVEMENT = "show_achievement";
    public static final String SHOW_ENTITY = "show_entity";
    public static final String SHOW_ITEM = "show_item";
    public static final String SHOW_TEXT = "show_text";

    private String action;

    protected HoverEvent(String action){
        this.action = action;
    }

    public HoverEvent action(String action){
        this.action = action;
        return this;
    }

    public String action(){
        return action;
    }

    public void apply(BaseComponent component){
        component.setHoverEvent(spigot());
    }

    protected abstract net.md_5.bungee.api.chat.HoverEvent spigot();

    public void apply(JsonObject json){
        JsonObject clickSection = new JsonObject();
        if(action!=null) clickSection.addProperty("action", action);
        applyContents(clickSection);
        json.add("hoverEvent", clickSection);
    }

    protected abstract void applyContents(JsonObject json);

    public static ShowTextEvent showText(String text){
        return new ShowTextEvent(text);
    }

    public static ShowTextEvent showText(RawBase... text){
        return new ShowTextEvent(text);
    }

    public static ShowAdvancementEvent showAdvancement(String id){
        return new ShowAdvancementEvent(id);
    }

    public static ShowItemEvent showItem(String id){
        return new ShowItemEvent(id);
    }

    public static ShowItemEvent showItem(String id, int count){
        return new ShowItemEvent(id, count);
    }

    public static ShowItemEvent showItem(String id, int count, String tag){
        return new ShowItemEvent(id, count, tag);
    }

    public static ShowItemEvent showItem(ItemStack itemStack){
        String tag;
        try{
            tag = SNBTUtil.toSNBT(ItemUtil.getData(itemStack));
        }
        catch (Exception ignored){
            tag = null;
        }
        return new ShowItemEvent(itemStack.getType().getKey().toString(), itemStack.getAmount(), tag);
    }

    public static ShowEntityEvent showEntity(String type, UUID id){
        return new ShowEntityEvent(type, id);
    }

    public static ShowEntityEvent showEntity(String type, UUID id, String name){
        return new ShowEntityEvent(type, id, name);
    }

    public static ShowEntityEvent showEntity(Entity entity){
        return new ShowEntityEvent(entity.getType().getKey().toString(), entity.getUniqueId(), entity instanceof Player ? entity.getName() : entity.getCustomName());
    }
}
