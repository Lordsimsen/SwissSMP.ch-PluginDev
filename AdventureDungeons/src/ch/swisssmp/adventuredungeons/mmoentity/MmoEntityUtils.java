package ch.swisssmp.adventuredungeons.mmoentity;

import java.lang.reflect.Field;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

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
	
	public static String getEntityName(LivingEntity livingEntity) {
		if(livingEntity==null) return "";
		String attackedName = livingEntity.getCustomName();
		if(attackedName==null || attackedName.equals("")){
			return livingEntity.getName();
		}
		return attackedName;
	}
}
