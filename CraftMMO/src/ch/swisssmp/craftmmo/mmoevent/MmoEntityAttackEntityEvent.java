package ch.swisssmp.craftmmo.mmoevent;

import java.util.HashMap;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import ch.swisssmp.craftmmo.Main;
import ch.swisssmp.craftmmo.mmoattribute.IDurable;
import ch.swisssmp.craftmmo.mmoattribute.MmoAttributes;
import ch.swisssmp.craftmmo.mmoattribute.MmoDurability;
import ch.swisssmp.craftmmo.mmoattribute.MmoElement;
import ch.swisssmp.craftmmo.mmoentity.MmoEntity;
import ch.swisssmp.craftmmo.mmoitem.MmoItemStack;
import ch.swisssmp.craftmmo.mmoitem.MmoPhysicalWeapon;

public class MmoEntityAttackEntityEvent extends Event implements IDamageCalculator{
    private static final HandlerList handlers = new HandlerList();
    
    private final double calculatedDamage;
    private final HashMap<MmoElement, Integer> elementalDebuffs;
    private MmoItemStack usedItemStack;
    
	public MmoEntityAttackEntityEvent(MmoEntity attacker, MmoEntity defender){
		usedItemStack = attacker.mainHand;
		if(usedItemStack==null){
			usedItemStack = attacker.offHand;
		}
		double baseDamage = attacker.baseDamage;
		double durabilityFactor = 1;
		if(usedItemStack!=null && usedItemStack.mmoItem instanceof MmoPhysicalWeapon){
			MmoPhysicalWeapon physicalWeapon = (MmoPhysicalWeapon)usedItemStack.mmoItem;
			baseDamage = physicalWeapon.damage;
			attacker.attributes.elements = physicalWeapon.elements;
			MmoDurability mmoDurability = IDurable.getDurabilityLevel((IDurable)physicalWeapon, usedItemStack.itemStack);
			if(mmoDurability!=null){
				durabilityFactor = mmoDurability.getDamageModifier();
			}
			else{
				durabilityFactor = 0;
			}
		}
		defender.calculateDefensiveElementalAttributes();
		baseDamage+=attacker.attributes.getBonusPhysicalDamage();
		double defenseFactor = defender.calculateDefenseFactor();
		this.elementalDebuffs = MmoAttributes.calculateElementalEffects(attacker.attributes.elements, defender.attributes.elements);
		this.calculatedDamage = baseDamage*durabilityFactor*defenseFactor;
		Main.info("Calculated damage: "+calculatedDamage);
	}
	
	@Override
	public double getCalculatedDamage(){
		return this.calculatedDamage;
	}
	
	@Override
	public HashMap<MmoElement, Integer> getElementalDebuffs(){
		return this.elementalDebuffs;
	}
	
	@Override
	public MmoItemStack getUsedItemStack(){
		return this.usedItemStack;
	}

	@Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
