package ch.swisssmp.craftmmo.mmoentity;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
 
import net.minecraft.server.v1_12_R1.BiomeBase;
import net.minecraft.server.v1_12_R1.Entity;
import net.minecraft.server.v1_12_R1.EntityBat;
import net.minecraft.server.v1_12_R1.EntityBlaze;
import net.minecraft.server.v1_12_R1.EntityCaveSpider;
import net.minecraft.server.v1_12_R1.EntityChicken;
import net.minecraft.server.v1_12_R1.EntityCow;
import net.minecraft.server.v1_12_R1.EntityCreeper;
import net.minecraft.server.v1_12_R1.EntityEnderDragon;
import net.minecraft.server.v1_12_R1.EntityEnderman;
import net.minecraft.server.v1_12_R1.EntityEndermite;
import net.minecraft.server.v1_12_R1.EntityGhast;
import net.minecraft.server.v1_12_R1.EntityGiantZombie;
import net.minecraft.server.v1_12_R1.EntityGuardian;
import net.minecraft.server.v1_12_R1.EntityHorse;
import net.minecraft.server.v1_12_R1.BiomeBase.BiomeMeta;
import net.minecraft.server.v1_12_R1.EntityInsentient;
import net.minecraft.server.v1_12_R1.EntityIronGolem;
import net.minecraft.server.v1_12_R1.EntityMagmaCube;
import net.minecraft.server.v1_12_R1.EntityMushroomCow;
import net.minecraft.server.v1_12_R1.EntityOcelot;
import net.minecraft.server.v1_12_R1.EntityPig;
import net.minecraft.server.v1_12_R1.EntityPigZombie;
import net.minecraft.server.v1_12_R1.EntityPolarBear;
import net.minecraft.server.v1_12_R1.EntityRabbit;
import net.minecraft.server.v1_12_R1.EntitySheep;
import net.minecraft.server.v1_12_R1.EntityShulker;
import net.minecraft.server.v1_12_R1.EntitySilverfish;
import net.minecraft.server.v1_12_R1.EntitySkeleton;
import net.minecraft.server.v1_12_R1.EntitySlime;
import net.minecraft.server.v1_12_R1.EntitySnowman;
import net.minecraft.server.v1_12_R1.EntitySpider;
import net.minecraft.server.v1_12_R1.EntitySquid;
import net.minecraft.server.v1_12_R1.EntityTypes;
import net.minecraft.server.v1_12_R1.EntityVillager;
import net.minecraft.server.v1_12_R1.EntityWitch;
import net.minecraft.server.v1_12_R1.EntityWolf;
import net.minecraft.server.v1_12_R1.EntityZombie;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
 
public enum MmoEntityType {

MMO_CITIZEN("Dorfbewohner", MmoEntityUtils.getEntityID(EntityType.VILLAGER), EntityType.VILLAGER, EntityVillager.class, MmoCitizen.class),
MMO_MERCHANT_AGENT("Handelsagent", MmoEntityUtils.getEntityID(EntityType.VILLAGER), EntityType.VILLAGER, EntityVillager.class, MmoMerchantAgent.class),
MMO_ZOMBIE("Untoter", MmoEntityUtils.getEntityID(EntityType.ZOMBIE), EntityType.ZOMBIE, EntityZombie.class, MmoZombie.class),
MMO_SKELETON("Skelett", MmoEntityUtils.getEntityID(EntityType.SKELETON), EntityType.SKELETON, EntitySkeleton.class, MmoSkeleton.class),
MMO_DRAGON("Drache", MmoEntityUtils.getEntityID(EntityType.ENDER_DRAGON), EntityType.ENDER_DRAGON, EntityEnderDragon.class, MmoDragon.class),
MMO_GOLEM("Golem", MmoEntityUtils.getEntityID(EntityType.IRON_GOLEM), EntityType.IRON_GOLEM, EntityIronGolem.class, MmoGolem.class),
MMO_CAVE_SPIDER("Spinne", MmoEntityUtils.getEntityID(EntityType.CAVE_SPIDER), EntityType.CAVE_SPIDER, EntityCaveSpider.class, MmoCaveSpider.class),
MMO_SPIDER("Riesenspinne", MmoEntityUtils.getEntityID(EntityType.SPIDER), EntityType.SPIDER, EntitySpider.class, MmoSpider.class),
MMO_HORSE("Pferd", MmoEntityUtils.getEntityID(EntityType.HORSE), EntityType.HORSE, EntityHorse.class, MmoHorse.class),
MMO_SLIME("Schleim", MmoEntityUtils.getEntityID(EntityType.SLIME), EntityType.SLIME, EntitySlime.class, MmoSlime.class),
MMO_CREEPER("Goblin", MmoEntityUtils.getEntityID(EntityType.CREEPER), EntityType.CREEPER, EntityCreeper.class, MmoCreeper.class),
MMO_GHAST("Geist", MmoEntityUtils.getEntityID(EntityType.GHAST), EntityType.GHAST, EntityGhast.class, MmoGhast.class),
MMO_PIG_ZOMBIE("Soldat", MmoEntityUtils.getEntityID(EntityType.PIG_ZOMBIE), EntityType.PIG_ZOMBIE, EntityPigZombie.class, MmoPigZombie.class),
MMO_ENDERMAN("Riese", MmoEntityUtils.getEntityID(EntityType.ENDERMAN), EntityType.ENDERMAN, EntityEnderman.class, MmoEnderman.class),
MMO_SILVERFISH("Maus", MmoEntityUtils.getEntityID(EntityType.SILVERFISH), EntityType.SILVERFISH, EntitySilverfish.class, MmoSilverfish.class),
MMO_BLAZE("Elementargeist", MmoEntityUtils.getEntityID(EntityType.BLAZE), EntityType.BLAZE, EntityBlaze.class, MmoBlaze.class),
MMO_MAGMA_CUBE("Elementarschleim", MmoEntityUtils.getEntityID(EntityType.MAGMA_CUBE), EntityType.MAGMA_CUBE, EntityMagmaCube.class, MmoMagmaCube.class),
MMO_BAT("Vogel", MmoEntityUtils.getEntityID(EntityType.BAT), EntityType.BAT, EntityBat.class, MmoBat.class),
MMO_WITCH("Hexe", MmoEntityUtils.getEntityID(EntityType.WITCH), EntityType.WITCH, EntityWitch.class, MmoWitch.class),
MMO_ENDERMITE("Ratte", MmoEntityUtils.getEntityID(EntityType.ENDERMITE), EntityType.ENDERMITE, EntityEndermite.class, MmoEndermite.class),
MMO_GUARDIAN("Hydra", MmoEntityUtils.getEntityID(EntityType.GUARDIAN), EntityType.GUARDIAN, EntityGuardian.class, MmoGuardian.class),
MMO_SHULKER("Muschel", MmoEntityUtils.getEntityID(EntityType.SHULKER), EntityType.SHULKER, EntityShulker.class, MmoShulker.class),
MMO_BOAR("Wildschwein", MmoEntityUtils.getEntityID(EntityType.PIG), EntityType.PIG, EntityPig.class, MmoBoar.class),
MMO_SHEEP("Schaf", MmoEntityUtils.getEntityID(EntityType.SHEEP), EntityType.SHEEP, EntitySheep.class, MmoSheep.class),
MMO_COW("Kuh", MmoEntityUtils.getEntityID(EntityType.COW), EntityType.COW, EntityCow.class, MmoCow.class),
MMO_CHICKEN("Huhn", MmoEntityUtils.getEntityID(EntityType.CHICKEN), EntityType.CHICKEN, EntityChicken.class, MmoChicken.class),
MMO_SQUID("Tintenfisch", MmoEntityUtils.getEntityID(EntityType.SQUID), EntityType.SQUID, EntitySquid.class, MmoSquid.class),
MMO_WOLF("Wolf", MmoEntityUtils.getEntityID(EntityType.WOLF), EntityType.WOLF, EntityWolf.class, MmoWolf.class),
MMO_MUSHROOM_COW("Pilzkuh", MmoEntityUtils.getEntityID(EntityType.MUSHROOM_COW), EntityType.MUSHROOM_COW, EntityMushroomCow.class, MmoMushroomCow.class),
MMO_SNOWMAN("Schneegolem", MmoEntityUtils.getEntityID(EntityType.SNOWMAN), EntityType.SNOWMAN, EntitySnowman.class, MmoSnowman.class),
MMO_OCELOT("Katze", MmoEntityUtils.getEntityID(EntityType.OCELOT), EntityType.OCELOT, EntityOcelot.class, MmoOcelot.class),
MMO_RABBIT("Hase", MmoEntityUtils.getEntityID(EntityType.RABBIT), EntityType.RABBIT, EntityRabbit.class, MmoRabbit.class),
MMO_GIANT("Gigant", MmoEntityUtils.getEntityID(EntityType.GIANT), EntityType.GIANT, EntityGiantZombie.class, MmoGiant.class),
MMO_BEAR("Baer", MmoEntityUtils.getEntityID(EntityType.POLAR_BEAR), EntityType.POLAR_BEAR, EntityPolarBear.class, MmoBear.class),
;
 
private String name;
private int id;
private EntityType entityType;
private Class<? extends EntityInsentient> nmsClass;
private Class<? extends EntityInsentient> customClass;
 
private MmoEntityType(String name, int id, EntityType entityType, Class<? extends EntityInsentient> nmsClass,
Class<? extends EntityInsentient> customClass) {
this.name = name;
this.id = id;
this.entityType = entityType;
this.nmsClass = nmsClass;
this.customClass = customClass;
}
 
public String getName() {
return name;
}
 
public int getID() {
return id;
}
 
public EntityType getEntityType() {
return entityType;
}
 
public Class<? extends EntityInsentient> getNMSClass() {
return nmsClass;
}
 
public Class<? extends EntityInsentient> getCustomClass() {
return customClass;
}
 
/**
* Register our entities.
*/
public static void registerEntities() {
for (MmoEntityType entity : values())
a(entity.getCustomClass(), entity.getName(), entity.getID());
 
// BiomeBase#biomes became private.
BiomeBase[] biomes;
try {
biomes = (BiomeBase[]) getPrivateStatic(BiomeBase.class, "biomes");
} catch (Exception exc) {
// Unable to fetch.
return;
}
for (BiomeBase biomeBase : biomes) {
if (biomeBase == null)
break;
 
// This changed names from J, K, L and M.
for (String field : new String[] { "as", "at", "au", "av" })
try {
Field list = BiomeBase.class.getDeclaredField(field);
list.setAccessible(true);
@SuppressWarnings("unchecked")
List<BiomeMeta> mobList = (List<BiomeMeta>) list.get(biomeBase);
 
// Write in our custom class.
for (BiomeMeta meta : mobList)
for (MmoEntityType entity : values())
if (entity.getNMSClass().equals(meta.b))
meta.b = entity.getCustomClass();
} catch (Exception e) {
e.printStackTrace();
}
}
}
 
/**
* Unregister our entities to prevent memory leaks. Call on disable.
*/
@SuppressWarnings("rawtypes")
public static void unregisterEntities() {
for (MmoEntityType entity : values()) {
// Remove our class references.
try {
((Map) getPrivateStatic(EntityTypes.class, "d")).remove(entity.getCustomClass());
} catch (Exception e) {
e.printStackTrace();
}
 
try {
((Map) getPrivateStatic(EntityTypes.class, "f")).remove(entity.getCustomClass());
} catch (Exception e) {
e.printStackTrace();
}
}
 
for (MmoEntityType entity : values())
try {
// Unregister each entity by writing the NMS back in place of the custom class.
a(entity.getNMSClass(), entity.getName(), entity.getID());
} catch (Exception e) {
e.printStackTrace();
}
 
// Biomes#biomes was made private so use reflection to get it.
BiomeBase[] biomes;
try {
biomes = (BiomeBase[]) getPrivateStatic(BiomeBase.class, "biomes");
} catch (Exception exc) {
// Unable to fetch.
return;
}
for (BiomeBase biomeBase : biomes) {
if (biomeBase == null)
break;
 
// The list fields changed names but update the meta regardless.
for (String field : new String[] { "as", "at", "au", "av" })
try {
Field list = BiomeBase.class.getDeclaredField(field);
list.setAccessible(true);
@SuppressWarnings("unchecked")
List<BiomeMeta> mobList = (List<BiomeMeta>) list.get(biomeBase);
 
// Make sure the NMS class is written back over our custom class.
for (BiomeMeta meta : mobList)
for (MmoEntityType entity : values())
if (entity.getCustomClass().equals(meta.b))
meta.b = entity.getNMSClass();
} catch (Exception e) {
e.printStackTrace();
}
}
}
 
/**
* A convenience method.
* @param clazz The class.
* @param f The string representation of the private static field.
* @return The object found
* @throws Exception if unable to get the object.
*/
private static Object getPrivateStatic(@SuppressWarnings("rawtypes") Class clazz, String f) throws Exception {
Field field = clazz.getDeclaredField(f);
field.setAccessible(true);
return field.get(null);
}
 
/*
* Since 1.7.2 added a check in their entity registration, simply bypass it and write to the maps ourself.
*/
@SuppressWarnings({ "rawtypes", "unchecked" })
private static void a(Class paramClass, String paramString, int paramInt) {
try {
((Map) getPrivateStatic(EntityTypes.class, "c")).put(paramString, paramClass);
((Map) getPrivateStatic(EntityTypes.class, "d")).put(paramClass, paramString);
//((Map) getPrivateStatic(EntityTypes.class, "e")).put(Integer.valueOf(paramInt), paramClass);
((Map) getPrivateStatic(EntityTypes.class, "f")).put(paramClass, Integer.valueOf(paramInt));
((Map) getPrivateStatic(EntityTypes.class, "g")).put(paramString, Integer.valueOf(paramInt));
} catch (Exception exc) {
// Unable to register the new class.
}
}

public static void spawnEntity(Entity entity, Location loc)
	{
	entity.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
	((org.bukkit.craftbukkit.v1_12_R1.CraftWorld)loc.getWorld()).getHandle().addEntity(entity);
	}
}