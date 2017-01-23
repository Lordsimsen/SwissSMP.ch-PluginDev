package ch.swisssmp.craftmmo.mmoattribute;

import org.bukkit.attribute.Attribute;

public class MmoAttribute {
	public static String getAttributeName(Attribute attribute){
		switch(attribute){
		case GENERIC_ATTACK_DAMAGE:
			return "generic.attackDamage";
		case GENERIC_ATTACK_SPEED:
			return "generic.attackSpeed";
		case GENERIC_ARMOR:
			return "generic.armor";
		case GENERIC_MAX_HEALTH:
			return "generic.maxHealth";
		case GENERIC_MOVEMENT_SPEED:
			return "generic.movementSpeed";
		case GENERIC_LUCK:
			return "generic.luck";
		case GENERIC_KNOCKBACK_RESISTANCE:
			return "generic.knockbackResistance";
		case GENERIC_FOLLOW_RANGE:
			return "generic.followRange";
		default:
			return "";
		}
	}
	public static int getOperation(Attribute attribute){
		switch(attribute){
		case GENERIC_ATTACK_DAMAGE:
			return 0;
		case GENERIC_ATTACK_SPEED:
			return 0;
		case GENERIC_ARMOR:
			return 0;
		case GENERIC_MAX_HEALTH:
			return 0;
		case GENERIC_MOVEMENT_SPEED:
			return 0;
		case GENERIC_LUCK:
			return 0;
		case GENERIC_KNOCKBACK_RESISTANCE:
			return 0;
		case GENERIC_FOLLOW_RANGE:
			return 0;
		default:
			return 0;
		}
	}
}
