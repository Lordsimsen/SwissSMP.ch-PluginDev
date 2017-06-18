package ch.swisssmp.craftmmo.mmoentity;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import ch.swisssmp.craftmmo.Main;
import ch.swisssmp.craftmmo.pathfinder.PathfinderGoalAnimalMeleeAttack;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_12_R1.EntityAnimal;
import net.minecraft.server.v1_12_R1.EntityCreature;
import net.minecraft.server.v1_12_R1.EntityCreeper;
import net.minecraft.server.v1_12_R1.EntityHorse;
import net.minecraft.server.v1_12_R1.EntityHuman;
import net.minecraft.server.v1_12_R1.EntityInsentient;
import net.minecraft.server.v1_12_R1.EntityIronGolem;
import net.minecraft.server.v1_12_R1.EntityOcelot;
import net.minecraft.server.v1_12_R1.EntityTameableAnimal;
import net.minecraft.server.v1_12_R1.EntityVillager;
import net.minecraft.server.v1_12_R1.EntityWolf;
import net.minecraft.server.v1_12_R1.IRangedEntity;
import net.minecraft.server.v1_12_R1.Item;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.PathfinderGoalArrowAttack;
import net.minecraft.server.v1_12_R1.PathfinderGoalAvoidTarget;
import net.minecraft.server.v1_12_R1.PathfinderGoalBeg;
import net.minecraft.server.v1_12_R1.PathfinderGoalBreakDoor;
import net.minecraft.server.v1_12_R1.PathfinderGoalBreed;
import net.minecraft.server.v1_12_R1.PathfinderGoalDefendVillage;
import net.minecraft.server.v1_12_R1.PathfinderGoalEatTile;
import net.minecraft.server.v1_12_R1.PathfinderGoalFleeSun;
import net.minecraft.server.v1_12_R1.PathfinderGoalFloat;
import net.minecraft.server.v1_12_R1.PathfinderGoalFollowOwner;
import net.minecraft.server.v1_12_R1.PathfinderGoalFollowParent;
import net.minecraft.server.v1_12_R1.PathfinderGoalHurtByTarget;
import net.minecraft.server.v1_12_R1.PathfinderGoalInteract;
import net.minecraft.server.v1_12_R1.PathfinderGoalJumpOnBlock;
import net.minecraft.server.v1_12_R1.PathfinderGoalLeapAtTarget;
import net.minecraft.server.v1_12_R1.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_12_R1.PathfinderGoalLookAtTradingPlayer;
import net.minecraft.server.v1_12_R1.PathfinderGoalMakeLove;
import net.minecraft.server.v1_12_R1.PathfinderGoalMoveIndoors;
import net.minecraft.server.v1_12_R1.PathfinderGoalMoveThroughVillage;
import net.minecraft.server.v1_12_R1.PathfinderGoalMoveTowardsRestriction;
import net.minecraft.server.v1_12_R1.PathfinderGoalMoveTowardsTarget;
import net.minecraft.server.v1_12_R1.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_12_R1.PathfinderGoalOcelotAttack;
import net.minecraft.server.v1_12_R1.PathfinderGoalOfferFlower;
import net.minecraft.server.v1_12_R1.PathfinderGoalOpenDoor;
import net.minecraft.server.v1_12_R1.PathfinderGoalOwnerHurtByTarget;
import net.minecraft.server.v1_12_R1.PathfinderGoalPanic;
import net.minecraft.server.v1_12_R1.PathfinderGoalPlay;
import net.minecraft.server.v1_12_R1.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_12_R1.PathfinderGoalRandomStroll;
import net.minecraft.server.v1_12_R1.PathfinderGoalRandomTargetNonTamed;
import net.minecraft.server.v1_12_R1.PathfinderGoalRestrictOpenDoor;
import net.minecraft.server.v1_12_R1.PathfinderGoalRestrictSun;
import net.minecraft.server.v1_12_R1.PathfinderGoalSelector;
import net.minecraft.server.v1_12_R1.PathfinderGoalSit;
import net.minecraft.server.v1_12_R1.PathfinderGoalSwell;
import net.minecraft.server.v1_12_R1.PathfinderGoalTakeFlower;
import net.minecraft.server.v1_12_R1.PathfinderGoalTame;
import net.minecraft.server.v1_12_R1.PathfinderGoalTempt;
import net.minecraft.server.v1_12_R1.PathfinderGoalTradeWithPlayer;

public class MmoEntityUtils {
	public static Object getPrivateField(String fieldName, Class<?> clazz, Object object)
    {
        Field field;
        Object o = null;
        try
        {
            field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            o = field.get(object);
        }
        catch(NoSuchFieldException e)
        {
            e.printStackTrace();
        }
        catch(IllegalAccessException e)
        {
            e.printStackTrace();
        }
        return o;
    }
	public static Integer getEntityID(EntityType type){
		switch(type){
		case DROPPED_ITEM:
			return 1;
		case EXPERIENCE_ORB:
			return 2;
		case LEASH_HITCH:
			return 8;
		case PAINTING:
			return 9;
		case ARROW:
			return 10;
		case SNOWBALL:
			return 11;
		case FIREBALL:
			return 12;
		case SMALL_FIREBALL:
			return 13;
		case ENDER_PEARL:
			return 14;
		case ENDER_SIGNAL:
			return 15;
		case SPLASH_POTION:
			return 16;
		case THROWN_EXP_BOTTLE:
			return 17;
		case ITEM_FRAME:
			return 18;
		case WITHER_SKULL:
			return 19;
		case PRIMED_TNT:
			return 20;
		case FALLING_BLOCK:
			return 21;
		case FIREWORK:
			return 22;
		case ARMOR_STAND:
			return 30;
		case MINECART_COMMAND:
			return 40;
		case BOAT:
			return 41;
		case MINECART:
			return 42;
		case MINECART_CHEST:
			return 43;
		case MINECART_FURNACE:
			return 44;
		case MINECART_TNT:
			return 45;
		case MINECART_HOPPER:
			return 46;
		case MINECART_MOB_SPAWNER:
			return 47;
		case CREEPER:
			return 50;
		case SKELETON:
			return 51;
		case SPIDER:
			return 52;
		case GIANT:
			return 53;
		case ZOMBIE:
			return 54;
		case SLIME:
			return 55;
		case GHAST:
			return 56;
		case PIG_ZOMBIE:
			return 57;
		case ENDERMAN:
			return 58;
		case CAVE_SPIDER:
			return 59;
		case SILVERFISH:
			return 60;
		case BLAZE:
			return 61;
		case MAGMA_CUBE:
			return 62;
		case ENDER_DRAGON:
			return 63;
		case WITHER:
			return 64;
		case BAT:
			return 65;
		case WITCH:
			return 66;
		case ENDERMITE:
			return 67;
		case GUARDIAN:
			return 68;
		case SHULKER:
			return 69;
		case PIG:
			return 90;
		case SHEEP:
			return 91;
		case COW:
			return 92;
		case CHICKEN:
			return 93;
		case SQUID:
			return 94;
		case WOLF:
			return 95;
		case MUSHROOM_COW:
			return 96;
		case SNOWMAN:
			return 97;
		case OCELOT:
			return 98;
		case IRON_GOLEM:
			return 99;
		case HORSE:
			return 100;
		case RABBIT:
			return 101;
		case VILLAGER:
			return 120;
		case ENDER_CRYSTAL:
			return 200;
		case POLAR_BEAR:
			return 102;
		default:
			return 0;
		}
	}
	
	public static void applyPathfinderGoals(MmoAI ai, EntityInsentient entity){
		try{
			if(ai==null){
				throw new NullPointerException("Cannot apply PathfinderGoals, MmoAI is null!");
			}
			Main.info("Applying AI to mob!");
			PathfinderGoalSelector goalSelector = entity.goalSelector;
			goalSelector.a();
			PathfinderGoalSelector targetSelector = entity.targetSelector;
			ConfigurationSection dataSection = ai.dataSection;
			if(ai.arrowAttack && (entity instanceof IRangedEntity)){
				ConfigurationSection goalSection = dataSection.getConfigurationSection("arrowAttack");
				int priority = goalSection.getInt("priority"); //skeleton default is 20
				double speed = goalSection.getDouble("speed"); //skeleton default is 20
				int arg2 = goalSection.getInt("arg2"); //skeleton default is 20
				int arg3 = goalSection.getInt("arg3");
				float range = (float) goalSection.getDouble("range");
				goalSelector.a(priority, new PathfinderGoalArrowAttack((IRangedEntity) entity, speed, arg2, arg3, range));
					Main.info("Eigenschaft "+goalSection.getName()+" hinzugefügt!");
			}
			if(ai.avoidTarget && (entity instanceof EntityCreature)){
				ConfigurationSection goalSection = dataSection.getConfigurationSection("avoidTarget");
				int priority = goalSection.getInt("priority");
				float arg2 = (float) goalSection.getDouble("arg2");
				double arg3 = goalSection.getInt("arg3");
				double arg4 = goalSection.getInt("arg4");
				//TODO find out what params do
				goalSelector.a(priority, new PathfinderGoalAvoidTarget<EntityInsentient>((EntityCreature) entity, EntityInsentient.class, arg2, arg3, arg4));
				Main.info("Eigenschaft "+goalSection.getName()+" hinzugefügt!");
			}
			if(ai.beg && (entity instanceof EntityWolf)){
				ConfigurationSection goalSection = dataSection.getConfigurationSection("beg");
				int priority = goalSection.getInt("priority");
				float arg1 = (float) goalSection.getDouble("arg1");
				//TODO find out what params do
				goalSelector.a(priority, new PathfinderGoalBeg((EntityWolf) entity, arg1));
				Main.info("Eigenschaft "+goalSection.getName()+" hinzugefügt!");
			}
			if(ai.breakDoor){
				ConfigurationSection goalSection = dataSection.getConfigurationSection("breakDoor");
				int priority = goalSection.getInt("priority");
				//TODO find out what params do
				goalSelector.a(priority, new PathfinderGoalBreakDoor(entity));
				Main.info("Eigenschaft "+goalSection.getName()+" hinzugefügt!");
			}
			if(ai.breed && (entity instanceof EntityAnimal)){
				ConfigurationSection goalSection = dataSection.getConfigurationSection("breed");
				int priority = goalSection.getInt("priority");
				float arg1 = (float) goalSection.getDouble("arg1");
				//TODO find out what params do
				goalSelector.a(priority, new PathfinderGoalBreed((EntityAnimal) entity, arg1));
				Main.info("Eigenschaft "+goalSection.getName()+" hinzugefügt!");
			}
			if(ai.defendVillage && (entity instanceof EntityIronGolem)){
				ConfigurationSection goalSection = dataSection.getConfigurationSection("defendVillage");
				int priority = goalSection.getInt("priority");
				//TODO find out what params do
				goalSelector.a(priority, new PathfinderGoalDefendVillage((EntityIronGolem) entity));
				Main.info("Eigenschaft "+goalSection.getName()+" hinzugefügt!");
			}
			if(ai.eatTile){
				ConfigurationSection goalSection = dataSection.getConfigurationSection("eatTile");
				int priority = goalSection.getInt("priority");
				//TODO find out what params do
				goalSelector.a(priority, new PathfinderGoalEatTile(entity));
				Main.info("Eigenschaft "+goalSection.getName()+" hinzugefügt!");
			}
			if(ai.fleeSun && (entity instanceof EntityCreature)){
				ConfigurationSection goalSection = dataSection.getConfigurationSection("fleeSun");
				int priority = goalSection.getInt("priority");
				float speed = (float) goalSection.getDouble("speed");
				//TODO find out what params do
				goalSelector.a(priority, new PathfinderGoalFleeSun((EntityCreature) entity, speed));
				Main.info("Eigenschaft "+goalSection.getName()+" hinzugefügt!");
			}
			if(ai.followOwner && (entity instanceof EntityTameableAnimal)){
				ConfigurationSection goalSection = dataSection.getConfigurationSection("followOwner");
				int priority = goalSection.getInt("priority");
				double arg1 = goalSection.getDouble("arg1");
				float arg2 = (float) goalSection.getDouble("arg2");
				float arg3 = (float) goalSection.getDouble("arg3");
				//TODO find out what params do
				goalSelector.a(priority, new PathfinderGoalFollowOwner((EntityTameableAnimal) entity, arg1, arg2, arg3));
				Main.info("Eigenschaft "+goalSection.getName()+" hinzugefügt!");
			}
			if(ai.followParent && (entity instanceof EntityAnimal)){
				ConfigurationSection goalSection = dataSection.getConfigurationSection("followParent");
				int priority = goalSection.getInt("priority");
				double arg1 = goalSection.getDouble("arg1");
				//TODO find out what params do
				goalSelector.a(priority, new PathfinderGoalFollowParent((EntityAnimal) entity, arg1));
				Main.info("Eigenschaft "+goalSection.getName()+" hinzugefügt!");
			}
			if(ai.hurtByTarget && (entity instanceof EntityCreature)){
				ConfigurationSection goalSection = dataSection.getConfigurationSection("hurtByTarget");
				int priority = goalSection.getInt("priority");
				boolean arg1 = goalSection.getBoolean("arg1");
				//TODO find out what params do
				targetSelector.a(priority, new PathfinderGoalHurtByTarget((EntityCreature) entity, arg1));
				Main.info("Eigenschaft "+goalSection.getName()+" hinzugefügt!");
			}
			if(ai.idle_float){
				ConfigurationSection goalSection = dataSection.getConfigurationSection("idle_float");
				int priority = goalSection.getInt("priority");
				//TODO find out what params do
				goalSelector.a(priority, new PathfinderGoalFloat(entity));
				Main.info("Eigenschaft "+goalSection.getName()+" hinzugefügt!");
			}
			if(ai.interact){
				ConfigurationSection goalSection = dataSection.getConfigurationSection("interact");
				int priority = goalSection.getInt("priority");
				float arg2 = (float) goalSection.getDouble("arg2");
				float arg3 = (float) goalSection.getDouble("arg3");
				//TODO find out what params do
				goalSelector.a(priority, new PathfinderGoalInteract(entity, EntityInsentient.class, arg2, arg3));
				Main.info("Eigenschaft "+goalSection.getName()+" hinzugefügt!");
			}
			if(ai.jumpOnBlock && (entity instanceof EntityOcelot)){
				ConfigurationSection goalSection = dataSection.getConfigurationSection("jumpOnBlock");
				int priority = goalSection.getInt("priority");
				double arg1 = goalSection.getDouble("arg1");
				//TODO find out what params do
				goalSelector.a(priority, new PathfinderGoalJumpOnBlock((EntityOcelot) entity, arg1));
				Main.info("Eigenschaft "+goalSection.getName()+" hinzugefügt!");
			}
			if(ai.leapAtTarget){
				ConfigurationSection goalSection = dataSection.getConfigurationSection("leapAtTarget");
				int priority = goalSection.getInt("priority");
				float arg1 = (float)goalSection.getDouble("arg1");
				//TODO find out what params do
				goalSelector.a(priority, new PathfinderGoalLeapAtTarget(entity, arg1));
				Main.info("Eigenschaft "+goalSection.getName()+" hinzugefügt!");
			}
			if(ai.lookAtPlayer){
				ConfigurationSection goalSection = dataSection.getConfigurationSection("lookAtPlayer");
				int priority = goalSection.getInt("priority");
				float arg2 = (float) goalSection.getDouble("arg2");
				//first is the entity itself, second is the class to look at, third is the maximum distance
				goalSelector.a(priority, new PathfinderGoalLookAtPlayer(entity, EntityHuman.class, arg2));
				Main.info("Eigenschaft "+goalSection.getName()+" hinzugefügt!");
			}
			if(ai.lookAtTradingPlayer && (entity instanceof EntityVillager)){
				ConfigurationSection goalSection = dataSection.getConfigurationSection("lookAtTradingPlayer");
				int priority = goalSection.getInt("priority");
				//TODO find out what params do
				goalSelector.a(priority, new PathfinderGoalLookAtTradingPlayer((EntityVillager) entity));
				Main.info("Eigenschaft "+goalSection.getName()+" hinzugefügt!");
			}
			if(ai.makeLove && (entity instanceof EntityVillager)){
				ConfigurationSection goalSection = dataSection.getConfigurationSection("makeLove");
				int priority = goalSection.getInt("priority");
				//TODO find out what params do
				goalSelector.a(priority, new PathfinderGoalMakeLove((EntityVillager) entity));
				Main.info("Eigenschaft "+goalSection.getName()+" hinzugefügt!");
			}
			if(ai.meleeAttack && (entity instanceof EntityCreature)){
				ConfigurationSection goalSection = dataSection.getConfigurationSection("meleeAttack");
				int priority = goalSection.getInt("priority");
				double speed = goalSection.getDouble("speed");
				double damage = goalSection.getDouble("damage");
				boolean arg2 = goalSection.getBoolean("arg2");
				//TODO find out what params do :entity instanceof EntityAnimal
				goalSelector.a(priority, new PathfinderGoalAnimalMeleeAttack((EntityCreature) entity, speed, damage, arg2));
				Main.info("Eigenschaft "+goalSection.getName()+" hinzugefügt!");
			}
			if(ai.moveIndoors && (entity instanceof EntityCreature)){
				ConfigurationSection goalSection = dataSection.getConfigurationSection("moveIndoors");
				int priority = goalSection.getInt("priority");
				//TODO find out what params do
				goalSelector.a(priority, new PathfinderGoalMoveIndoors((EntityCreature) entity));
				Main.info("Eigenschaft "+goalSection.getName()+" hinzugefügt!");
			}
			if(ai.moveThroughVillage && (entity instanceof EntityCreature)){
				ConfigurationSection goalSection = dataSection.getConfigurationSection("moveThroughVillage");
				int priority = goalSection.getInt("priority");
				double arg1 = goalSection.getDouble("arg1");
				boolean arg2 = goalSection.getBoolean("arg2");
				//TODO find out what params do
				goalSelector.a(priority, new PathfinderGoalMoveThroughVillage((EntityCreature) entity, arg1, arg2));
				Main.info("Eigenschaft "+goalSection.getName()+" hinzugefügt!");
			}
			if(ai.moveTowardsRestriction && (entity instanceof EntityCreature)){
				ConfigurationSection goalSection = dataSection.getConfigurationSection("moveTowardsRestriction");
				int priority = goalSection.getInt("priority");
				double arg1 = goalSection.getDouble("arg1");
				//TODO find out what params do
				goalSelector.a(priority, new PathfinderGoalMoveTowardsRestriction((EntityCreature) entity, arg1));
				Main.info("Eigenschaft "+goalSection.getName()+" hinzugefügt!");
			}
			if(ai.moveTowardsTarget && (entity instanceof EntityCreature)){
				ConfigurationSection goalSection = dataSection.getConfigurationSection("moveTowardsTarget");
				int priority = goalSection.getInt("priority");
				double arg1 = goalSection.getDouble("arg1");
				float arg2 = (float)goalSection.getDouble("arg2");
				//TODO find out what params do
				goalSelector.a(priority, new PathfinderGoalMoveTowardsTarget((EntityCreature) entity, arg1, arg2));
				Main.info("Eigenschaft "+goalSection.getName()+" hinzugefügt!");
			}
			if(ai.nearestAttackableTarget && (entity instanceof EntityCreature)){
				ConfigurationSection goalSection = dataSection.getConfigurationSection("nearestAttackableTarget");
				int priority = goalSection.getInt("priority");
				int arg1 = goalSection.getInt("arg1");
				boolean arg2 = goalSection.getBoolean("arg2");
				boolean arg3 = goalSection.getBoolean("arg3");
				//TODO find out what params do
				targetSelector.a(priority, new PathfinderGoalNearestAttackableTarget<EntityHuman>((EntityCreature) entity, EntityHuman.class, arg1, arg2, arg3, null));
				Main.info("Eigenschaft "+goalSection.getName()+" hinzugefügt!");
			}
			if(ai.ocelotAttack){
				ConfigurationSection goalSection = dataSection.getConfigurationSection("ocelotAttack");
				int priority = goalSection.getInt("priority");
				//TODO find out what params do
				goalSelector.a(priority, new PathfinderGoalOcelotAttack(entity));
				Main.info("Eigenschaft "+goalSection.getName()+" hinzugefügt!");
			}
			if(ai.offerFlower && (entity instanceof EntityIronGolem)){
				ConfigurationSection goalSection = dataSection.getConfigurationSection("offerFlower");
				int priority = goalSection.getInt("priority");
				//TODO find out what params do
				goalSelector.a(priority, new PathfinderGoalOfferFlower((EntityIronGolem) entity));
				Main.info("Eigenschaft "+goalSection.getName()+" hinzugefügt!");
			}
			if(ai.openDoor){
				ConfigurationSection goalSection = dataSection.getConfigurationSection("openDoor");
				int priority = goalSection.getInt("priority");
				boolean arg1 = goalSection.getBoolean("arg1");
				//TODO find out what params do
				goalSelector.a(priority, new PathfinderGoalOpenDoor(entity, arg1));
				Main.info("Eigenschaft "+goalSection.getName()+" hinzugefügt!");
			}
			if(ai.ownerHurtByTarget && (entity instanceof EntityTameableAnimal)){
				ConfigurationSection goalSection = dataSection.getConfigurationSection("ownerHurtByTarget");
				int priority = goalSection.getInt("priority");
				//TODO find out what params do
				goalSelector.a(priority, new PathfinderGoalOwnerHurtByTarget((EntityTameableAnimal) entity));
				Main.info("Eigenschaft "+goalSection.getName()+" hinzugefügt!");
			}
			if(ai.panic && (entity instanceof EntityCreature)){
				ConfigurationSection goalSection = dataSection.getConfigurationSection("panic");
				int priority = goalSection.getInt("priority");
				double arg1 = goalSection.getDouble("arg1");
				//TODO find out what params do
				goalSelector.a(priority, new PathfinderGoalPanic((EntityCreature) entity, arg1));
				Main.info("Eigenschaft "+goalSection.getName()+" hinzugefügt!");
			}
			if(ai.play && (entity instanceof EntityVillager)){
				ConfigurationSection goalSection = dataSection.getConfigurationSection("play");
				int priority = goalSection.getInt("priority");
				double arg1 = goalSection.getDouble("arg1");
				//TODO find out what params do
				goalSelector.a(priority, new PathfinderGoalPlay((EntityVillager) entity, arg1));
				Main.info("Eigenschaft "+goalSection.getName()+" hinzugefügt!");
			}
			if(ai.randomLookaround){
				ConfigurationSection goalSection = dataSection.getConfigurationSection("randomLookaround");
				int priority = goalSection.getInt("priority");
				//TODO find out what params do
				goalSelector.a(priority, new PathfinderGoalRandomLookaround(entity));
				Main.info("Eigenschaft "+goalSection.getName()+" hinzugefügt!");
			}
			if(ai.randomStroll && (entity instanceof EntityCreature)){
				ConfigurationSection goalSection = dataSection.getConfigurationSection("randomStroll");
				int priority = goalSection.getInt("priority");
				double arg1 = goalSection.getDouble("arg1");
				//TODO find out what params do
				goalSelector.a(priority, new PathfinderGoalRandomStroll((EntityCreature) entity, arg1));
				Main.info("Eigenschaft "+goalSection.getName()+" hinzugefügt!");
			}
			if(ai.randomTargetNonTamed && (entity instanceof EntityTameableAnimal)){
				ConfigurationSection goalSection = dataSection.getConfigurationSection("randomTargetNonTamed");
				int priority = goalSection.getInt("priority");
				boolean arg2 = goalSection.getBoolean("arg2");
				//TODO find out what params do
				goalSelector.a(priority, new PathfinderGoalRandomTargetNonTamed<EntityInsentient>((EntityTameableAnimal) entity, null, arg2, null));
				Main.info("Eigenschaft "+goalSection.getName()+" hinzugefügt!");
			}
			if(ai.restrictOpenDoor && (entity instanceof EntityCreature)){
				ConfigurationSection goalSection = dataSection.getConfigurationSection("restrictOpenDoor");
				int priority = goalSection.getInt("priority");
				//TODO find out what params do
				goalSelector.a(priority, new PathfinderGoalRestrictOpenDoor((EntityCreature) entity));
				Main.info("Eigenschaft "+goalSection.getName()+" hinzugefügt!");
			}
			if(ai.restrictSun && (entity instanceof EntityCreature)){
				ConfigurationSection goalSection = dataSection.getConfigurationSection("restrictSun");
				int priority = goalSection.getInt("priority");
				//TODO find out what params do
				goalSelector.a(priority, new PathfinderGoalRestrictSun((EntityCreature) entity));
				Main.info("Eigenschaft "+goalSection.getName()+" hinzugefügt!");
			}
			if(ai.sit && (entity instanceof EntityTameableAnimal)){
				ConfigurationSection goalSection = dataSection.getConfigurationSection("sit");
				int priority = goalSection.getInt("priority");
				//TODO find out what params do
				goalSelector.a(priority, new PathfinderGoalSit((EntityTameableAnimal) entity));
				Main.info("Eigenschaft "+goalSection.getName()+" hinzugefügt!");
			}
			if(ai.swell && (entity instanceof EntityCreeper)){
				ConfigurationSection goalSection = dataSection.getConfigurationSection("swell");
				int priority = goalSection.getInt("priority");
				//TODO find out what params do
				goalSelector.a(priority, new PathfinderGoalSwell((EntityCreeper) entity));
				Main.info("Eigenschaft "+goalSection.getName()+" hinzugefügt!");
			}
			if(ai.takeFlower && (entity instanceof EntityVillager)){
				ConfigurationSection goalSection = dataSection.getConfigurationSection("takeFlower");
				int priority = goalSection.getInt("priority");
				//TODO find out what params do
				goalSelector.a(priority, new PathfinderGoalTakeFlower((EntityVillager) entity));
				Main.info("Eigenschaft "+goalSection.getName()+" hinzugefügt!");
			}
			if(ai.tame && (entity instanceof EntityHorse)){
				ConfigurationSection goalSection = dataSection.getConfigurationSection("tame");
				int priority = goalSection.getInt("priority");
				double arg1 = goalSection.getDouble("arg1");
				//TODO find out what params do
				goalSelector.a(priority, new PathfinderGoalTame((EntityHorse) entity, arg1));
				Main.info("Eigenschaft "+goalSection.getName()+" hinzugefügt!");
			}
			if(ai.tempt && (entity instanceof EntityCreature)){
				ConfigurationSection goalSection = dataSection.getConfigurationSection("tempt");
				int priority = goalSection.getInt("priority");
				double arg1 = goalSection.getDouble("arg1");
				boolean arg2 = goalSection.getBoolean("arg2");
				List<Integer> arg3 = goalSection.getIntegerList("arg3");
				Set<Item> items = new HashSet<Item>();
				for(Integer i : arg3){
					items.add(Item.getById(i));
				}
				//TODO find out what params do
				goalSelector.a(priority, new PathfinderGoalTempt((EntityCreature) entity, arg1, arg2, items));
				Main.info("Eigenschaft "+goalSection.getName()+" hinzugefügt!");
			}
			if(ai.tradeWithPlayer && (entity instanceof EntityVillager)){
				ConfigurationSection goalSection = dataSection.getConfigurationSection("tradeWithPlayer");
				int priority = goalSection.getInt("priority");
				//TODO find out what params do
				goalSelector.a(priority, new PathfinderGoalTradeWithPlayer((EntityVillager) entity));
				Main.info("Eigenschaft "+goalSection.getName()+" hinzugefügt!");
			}
			if(ai.stayNearHome && (entity instanceof EntityCreature) && MmoAI.tempHome!=null){
				ConfigurationSection goalSection = dataSection.getConfigurationSection("randomStroll");
				int priority = goalSection.getInt("priority");
				//double maxDistance = goalSection.getInt("maxDistance");
				double speed = goalSection.getInt("speed");
				//TODO find out what params do
				//double x = MmoAI.tempHome.getX();
				//double y = MmoAI.tempHome.getY();
				//double z = MmoAI.tempHome.getZ();
				//goalSelector.a(priority, new PathfinderGoalRandomStrollNearHome((EntityCreature)entity, speed, x, y, z, maxDistance));
				goalSelector.a(priority, new PathfinderGoalRandomStroll((EntityCreature)entity, speed));
				Main.info("Eigenschaft "+goalSection.getName()+" hinzugefügt!");
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	public static void applyDefaultStuffOnCreate(IControllable iControllable, MmoMob template){
		try{
			iControllable.setSaveData(new MmoEntitySaveData(template.mmo_mob_id));
			if(!template.ai.isMobile()){
				Runnable runnable = new DelayedLocationLockingTask(iControllable);
				Bukkit.getScheduler().runTaskLater(Main.plugin, runnable, 5L);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void doDefaultSaveStuff(IControllable iControllable, NBTTagCompound nbttagcompound){
		try{
			iControllable.getSaveData().saveTo(nbttagcompound);
			if(iControllable.getSaveData().spawnpoint!=null){
				Entity entity = iControllable.getEntity().getBukkitEntity();
				int[] spawnpoint = iControllable.getSaveData().spawnpoint;
				entity.teleport(new Location(entity.getWorld(),spawnpoint[0]-0.5,spawnpoint[1]+0.5,spawnpoint[2]-0.5));
				MmoMob mmoMob = MmoMob.get(iControllable.getSaveData().mmo_mob_id);
				if(mmoMob!=null){
					iControllable.setMmoAI(mmoMob.ai);
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void doDefaultLoadStuff(IControllable iControllable, NBTTagCompound nbttagcompound){
		try{
			iControllable.setSaveData(MmoEntitySaveData.load(iControllable.getEntity(), nbttagcompound));
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public static String damageFeedback(LivingEntity livingEntity, double damage){
		String attackedName = MmoEntityUtils.getEntityName(livingEntity);
		return attackedName+": "+ChatColor.GREEN+String.format("%.2f", livingEntity.getHealth())+ChatColor.RED+" -"+String.format("%.2f", damage);
	}
	public static String getEntityName(LivingEntity livingEntity) {
		if(livingEntity==null) return "";
		String attackedName = livingEntity.getCustomName();
		if(attackedName==null || attackedName.equals("")){
			return livingEntity.getName();
		}
		return attackedName;
	}
}
