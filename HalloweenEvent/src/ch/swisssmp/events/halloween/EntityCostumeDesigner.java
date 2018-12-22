package ch.swisssmp.events.halloween;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.utils.Random;

public class EntityCostumeDesigner {
	private static Random random = new Random();
	
	protected static void applyCostume(Entity entity){
		switch(entity.getType()){
		case ZOMBIE:
		case PIG_ZOMBIE:
		case SKELETON:
			if(random.nextDouble()>0.3) EntityCostumeDesigner.applyPumpkin((LivingEntity)entity); break;
		default:return;
		}
	}
	protected static void applyPumpkin(LivingEntity entity){
		EntityEquipment equipment = entity.getEquipment();
		if(equipment.getHelmet()!=null && equipment.getHelmet().getType()!=Material.AIR) return;
		equipment.setHelmet(new ItemStack(Material.PUMPKIN));
	}
}
