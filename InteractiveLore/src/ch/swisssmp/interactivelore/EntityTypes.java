package ch.swisssmp.interactivelore;

import java.util.Map;

import org.bukkit.Location;

import net.minecraft.server.v1_10_R1.Entity;

public enum EntityTypes
{
    //NAME("Entity name", Entity ID, yourcustomclass.class);
    CUSTOM_VILLAGER("Villager", 120, Character.class); //You can add as many as you want.
	

    private EntityTypes(String name, int id, Class<? extends Entity> custom)
    {
        addToMaps(custom, name, id);
    }

  public static void spawnEntity(Entity entity, Location loc)
   {
     entity.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
     ((org.bukkit.craftbukkit.v1_10_R1.CraftWorld)loc.getWorld()).getHandle().addEntity(entity);
   }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	private static void addToMaps(Class<? extends Entity> clazz, String name, int id)
    {
        //getPrivateField is the method from above.
        //Remove the lines with // in front of them if you want to override default entities (You'd have to remove the default entity from the map first though).
        ((Map)Utils.getPrivateField("c", net.minecraft.server.v1_10_R1.EntityTypes.class, null)).put(name, clazz);
        ((Map)Utils.getPrivateField("d", net.minecraft.server.v1_10_R1.EntityTypes.class, null)).put(clazz, name);
        //((Map)getPrivateField("e", net.minecraft.server.v1_7_R4.EntityTypes.class, null)).put(Integer.valueOf(id), clazz);
        ((Map)Utils.getPrivateField("f", net.minecraft.server.v1_10_R1.EntityTypes.class, null)).put(clazz, Integer.valueOf(id));
        //((Map)getPrivateField("g", net.minecraft.server.v1_7_R4.EntityTypes.class, null)).put(name, Integer.valueOf(id));
    }
}
