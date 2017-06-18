package ch.swisssmp.craftmmo.mmoentity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import ch.swisssmp.craftmmo.mmoattribute.MmoAttributes;
import ch.swisssmp.craftmmo.mmoattribute.MmoElement;
import ch.swisssmp.craftmmo.mmoitem.MmoItem;
import ch.swisssmp.craftmmo.mmoitem.MmoItemStack;
import net.minecraft.server.v1_12_R1.NBTTagCompound;

public class MmoEntity {
	public final LivingEntity entity;
	public final MmoAttributes attributes;
	public final ArrayList<MmoItemStack> activeItems = new ArrayList<MmoItemStack>();
	public final ArrayList<MmoItemStack> armor = new ArrayList<MmoItemStack>();
	public final MmoItemStack mainHand;
	public final MmoItemStack offHand;
	
	public final double baseDamage;
	
	public MmoEntity(LivingEntity entity){
		this.entity = entity;
		MmoMob mmoMob = MmoMob.get(entity);
		if(mmoMob!=null){
			this.baseDamage = mmoMob.base_damage;
		}
		else{
			this.baseDamage = 0.5;
		}
		this.attributes = new MmoAttributes();
		EntityEquipment equipment = entity.getEquipment();
		for(ItemStack itemStack : equipment.getArmorContents()){
			MmoItem mmoItem = MmoItem.get(itemStack);
			if(mmoItem==null){
				continue;
			}
			MmoItemStack mmoItemStack = MmoItemStack.get(mmoItem, itemStack);
			activeItems.add(mmoItemStack);
			armor.add(mmoItemStack);
		}
		mainHand = MmoItemStack.get(equipment.getItemInMainHand());
		if(mainHand!=null){
			activeItems.add(mainHand);
		}
		offHand = MmoItemStack.get(equipment.getItemInOffHand());
		if(offHand!=null){
			activeItems.add(offHand);
		}
	}
	
	public double calculateDefenseFactor(){
		double defense = attributes.getBonusDefense();
		return 1-(defense/(defense+100));
	}
	
	public void calculateDefensiveElementalAttributes(){
		attributes.elements = new HashMap<MmoElement, Integer>();
		for(MmoItemStack mmoItemStack : armor){
			HashMap<MmoElement, Integer> elements = mmoItemStack.mmoItem.elements;
			for(Entry<MmoElement, Integer> entry : elements.entrySet()){
				if(!attributes.elements.containsKey(entry.getKey())){
					attributes.elements.put(entry.getKey(), entry.getValue());
				}
				else{
					attributes.elements.put(entry.getKey(), attributes.elements.get(entry.getKey())+entry.getValue());
				}
			}
		}
	}
	
	public void applyElementalEffects(HashMap<MmoElement, Integer> elements){
		for(Entry<MmoElement, Integer> entry : elements.entrySet()){
			applyElementalEffect(entry.getKey(), entry.getValue());
		}
	}
	
	public void applyElementalEffect(MmoElement element, Integer strength){
		switch(element){
		case FIRE:{
			int oldTicks = entity.getFireTicks();
			if(strength>oldTicks){
				entity.setFireTicks(strength);
			}
			break;
		}
		case EARTH:{
			PotionEffect potionEffect = new PotionEffect(PotionEffectType.SLOW, strength, 1, true, true);
			entity.addPotionEffect(potionEffect);
			break;
		}
		case AIR:{
			PotionEffect potionEffect = new PotionEffect(PotionEffectType.CONFUSION, strength, 0, true, true);
			entity.addPotionEffect(potionEffect);
			break;
		}
		case WATER:{
			PotionEffect potionEffect = new PotionEffect(PotionEffectType.POISON, strength, 0, true, true);
			entity.addPotionEffect(potionEffect);
			break;
		}
		case LIGHT:{
			PotionEffect potionEffect = new PotionEffect(PotionEffectType.GLOWING, strength, 0, true, true);
			entity.addPotionEffect(potionEffect);
			break;
		}
		case DARKNESS:{
			PotionEffect potionEffect = new PotionEffect(PotionEffectType.WITHER, strength, 0, true, true);
			entity.addPotionEffect(potionEffect);
			break;
		}
		default:
			break;
		}
	}
	
	public double getMaxHealth(){
		int strength = 0;
		for(MmoItemStack mmoItemStack : armor){
			if(mmoItemStack==null){
				continue;
			}
			if(mmoItemStack.itemStack!=null){
				NBTTagCompound saveData = MmoItem.getSaveData(mmoItemStack.itemStack);
				if(saveData.hasKey("strength")){
					strength+=saveData.getInt("strength");
				}
			}
		}
		return MmoAttributes.bonusHealthMultiplier*strength;
	}
}
