package ch.swisssmp.customportals;

import ch.swisssmp.utils.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class CustomPortalContainers {
    private static final HashMap<World, CustomPortalContainer> containers = new HashMap<>();

    protected static CustomPortalContainer load(World world){
        CustomPortalContainer existing = get(world);
        if(existing!=null){
            return existing;
        }

        CustomPortalContainer container = CustomPortalContainer.load(world);
        containers.put(world,container);

        updateTokens();
        return container;
    }

    protected static void unload(World world){
        CustomPortalContainer container = get(world);
        if(container!=null) container.unload();
        containers.remove(world);
    }

    protected static void loadAll(){
        for(World world : Bukkit.getWorlds()){
            load(world);
        }
    }

    protected static void unloadAll(){
        for(CustomPortalContainer container : containers.values()){
            container.unload();
        }

        containers.clear();
    }

    protected static Optional<CustomPortal> findPortal(UUID uid){
        return containers.values().stream().filter(c->c.hasPortal(uid)).map(c->c.getPortal(uid).orElse(null)).findAny();
    }

    protected static CustomPortalContainer get(World world){
        return containers.getOrDefault(world, null);
    }

    protected static void updateTokens(){
        HashMap<UUID,ItemMeta> templates = getTokenTemplates();
        for(Player player : Bukkit.getOnlinePlayers()){
            updateTokens(player, templates);
        }
    }

    protected static void updateTokens(Player player){
        HashMap<UUID,ItemMeta> templates = getTokenTemplates();
        updateTokens(player, templates);
    }

    protected static void updateTokens(Inventory inventory){
        HashMap<UUID,ItemMeta> templates = getTokenTemplates();
        updateTokens(inventory, templates);
    }

    private static void updateTokens(Player player, HashMap<UUID, ItemMeta> templates){
        updateTokens(player.getInventory(), templates);
        if(player.getOpenInventory()!=null) updateTokens(player.getOpenInventory().getTopInventory(), templates);
    }

    private static void updateTokens(Inventory inventory, HashMap<UUID, ItemMeta> templates){
        for(ItemStack itemStack : inventory){
            if(itemStack==null || itemStack.getType()== Material.AIR) continue;
            String portalIdString = ItemUtil.getString(itemStack, CustomPortal.ID_PROPERTY);
            if(portalIdString==null) continue;
            UUID portalUid;
            try{
                portalUid = UUID.fromString(portalIdString);
            }
            catch(Exception e){
                continue;
            }
            ItemMeta itemMeta = templates.getOrDefault(portalUid, null);
            if(itemMeta==null) continue;
            itemStack.setItemMeta(itemMeta);
        }
    }

    private static HashMap<UUID,ItemMeta> getTokenTemplates(){
        HashMap<UUID,ItemMeta> result = new HashMap<>();
        for(CustomPortalContainer container : containers.values()){
            for(CustomPortal portal : container.getAllPortals()){
                result.put(portal.getUniqueId(),portal.getItemStack().getItemMeta());
            }
        }
        return result;
    }
}
