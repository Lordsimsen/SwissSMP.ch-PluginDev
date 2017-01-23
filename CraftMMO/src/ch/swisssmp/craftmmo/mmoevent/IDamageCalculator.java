package ch.swisssmp.craftmmo.mmoevent;

import java.util.HashMap;

import ch.swisssmp.craftmmo.mmoattribute.MmoElement;
import ch.swisssmp.craftmmo.mmoitem.MmoItemStack;

public interface IDamageCalculator {
	public double getCalculatedDamage();
	public HashMap<MmoElement, Integer> getElementalDebuffs();
	public MmoItemStack getUsedItemStack();
}
