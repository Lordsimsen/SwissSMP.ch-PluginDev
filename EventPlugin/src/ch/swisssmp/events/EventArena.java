package ch.swisssmp.events;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.Position;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class EventArena {
    private final World world;
    private final UUID arena_id;
    private EventArenas arenas;

    private String name;

    private EventArena(World world, UUID arena_id, String name) {
        this.world = world;
        this.arena_id = arena_id;
        this.name = name;
    }

    private EventArena(World world, ConfigurationSection dataSection) {
        this.world = world;
        this.arena_id = UUID.fromString(dataSection.getString("id"));
        this.name = dataSection.getString("name");
    }


    public World getWorld(){
        return this.world;
    }

    public UUID getArena_id(){
        return this.arena_id;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /*
    Tokenstack to open editor with. Must be tagged with the UUID as string under identifier "arena".
     */
    public abstract ItemStack getTokenStack();

    public ItemStack getStartNowItem() {
        CustomItemBuilder itemBuilder = CustomItems.getCustomItemBuilder("EMBARK_NOW");
        itemBuilder.setDisplayName(ChatColor.AQUA + this.name);
        itemBuilder.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        List<String> description = new ArrayList<String>();
        description.add(ChatColor.YELLOW+"Klicke um sofort");
        description.add(ChatColor.YELLOW+"zu starten");
        itemBuilder.setLore(description);
        ItemStack result = itemBuilder.build();
        ItemUtil.setString(result, "arena", arena_id.toString());
        return result;
    }

    public void updateTokens() {
        ItemStack tokenStack = this.getTokenStack();
        for (Player p : Bukkit.getOnlinePlayers()) {
            for(ItemStack itemStack : p.getInventory()) {
                if (itemStack == null) {
                    continue;
                }
                EventArena arena = arenas.get(itemStack);
                if(arena != this) {
                    continue;
                }
                itemStack.setItemMeta(tokenStack.getItemMeta());
            }
        }
    }

    public abstract EventArenaEditor openEditor(Player player);

    public abstract EventArena create(World world, String name);

    public abstract EventArena create(World world, UUID arena_id, String name);

    public void save(ConfigurationSection dataSection) {
        dataSection.set("name", this.name);
        dataSection.set("id", this.arena_id.toString());
        dataSection.set("world", this.world.toString());
    }

    private static void savePosition(ConfigurationSection dataSection, String label, Position position){
        ConfigurationSection positionSection = dataSection.createSection(label);
        positionSection.set("x", position.getX());
        positionSection.set("y", position.getY());
        positionSection.set("z", position.getZ());
        positionSection.set("yaw", position.getYaw());
        positionSection.set("pitch", position.getPitch());
    }

    public static EventArena load(World world, ConfigurationSection dataSection) {
        return null;
    }

    public void remove(){
        arenas.remove(this);
    }
}
