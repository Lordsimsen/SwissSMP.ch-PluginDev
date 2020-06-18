package ch.swisssmp.customportals;

import ch.swisssmp.utils.JsonUtil;
import ch.swisssmp.world.WorldManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.GameMode;
import org.bukkit.World;

import java.io.File;
import java.util.*;

public class CustomPortalContainer {

    private final World world;
    private final HashSet<CustomPortal> portals = new HashSet<>();

    private CustomPortalContainer(World world){
        this.world = world;
    }

    public World getWorld(){
        return world;
    }

    public Optional<CustomPortal> getPortal(String regionId){
        return portals.stream().filter(p->regionId.equals(p.getRegionId())).findAny();
    }

    public Optional<CustomPortal> getPortal(UUID uid){
        return portals.stream().filter(p->p.getUniqueId().equals(uid)).findAny();
    }

    public boolean hasPortal(UUID uid){
        return portals.stream().anyMatch(p->p.getUniqueId().equals(uid));
    }

    public CustomPortal create(String name){
        CustomPortal portal = new CustomPortal(this, UUID.randomUUID(), name);
        portal.setAllowedGameModes(Arrays.asList(GameMode.values()));
        portals.add(portal);
        return portal;
    }

    protected void unload(){
        portals.clear();
    }

    public void save(){
        File file = getFile(world);
        JsonObject json = new JsonObject();
        JsonArray portalsArray = new JsonArray();
        for(CustomPortal p : portals){
            portalsArray.add(p.save());
        }
        json.add("portals", portalsArray);
        JsonUtil.save(file, json);
    }

    public Collection<CustomPortal> getAllPortals(){
        return portals;
    }

    protected void remove(CustomPortal portal){
        portals.remove(portal);
    }

    protected static CustomPortalContainer load(World world){
        File file = getFile(world);
        CustomPortalContainer container = new CustomPortalContainer(world);
        if(!file.exists()){
            return container;
        }

        JsonObject json = JsonUtil.parse(file);
        if(json==null || !json.has("portals")){
            return container;
        }

        for(JsonElement element : json.getAsJsonArray("portals")){
            if(!element.isJsonObject()) continue;
            JsonObject portalData = element.getAsJsonObject();
            CustomPortal portal = CustomPortal.load(container, portalData);
            if(portal==null) continue;
            container.portals.add(portal);
        }

        return container;
    }

    public static CustomPortalContainer get(World world){
        return CustomPortalContainers.get(world);
    }

    private static File getFile(World world){
        return new File(WorldManager.getPluginDirectory(CustomPortalsPlugin.getInstance(), world), "portals.json");
    }
}
