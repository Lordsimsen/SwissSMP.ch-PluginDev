package ch.swisssmp.craftmmo.mmoattribute;

import java.util.HashMap;
import java.util.Map.Entry;

public class MmoAttributes {
	//strength
	public static double bonusHealthMultiplier = 0.1;
	public static double physicalDamageMultiplier = 0.2;
	//agility
	public static double movementSpeedMultiplier = 0.05;
	public static double defenseMultiplier = 0.2;
	//intelligence
	public static double bonusManaMultiplier = 0.1;
	public static double magicDamageMultiplier = 0.2;
	
	public int strength = 0;
	public int agility = 0;
	public int intelligence = 0;
	
	public HashMap<MmoElement, Integer> elements = new HashMap<MmoElement, Integer>();
	
	public double getBonusHealth(){
		return this.strength*bonusHealthMultiplier;
	}
	public double getBonusPhysicalDamage(){
		return this.strength*physicalDamageMultiplier;
	}
	public double getBonusMovementSpeed(){
		return this.strength*movementSpeedMultiplier;
	}
	public double getBonusDefense(){
		return this.strength*defenseMultiplier;
	}
	public double getBonusMana(){
		return this.strength*bonusManaMultiplier;
	}
	public double getBonusMagicDamage(){
		return this.strength*magicDamageMultiplier;
	}
	
	public static HashMap<MmoElement, Integer> calculateElementalEffects(HashMap<MmoElement, Integer> attackerElements, HashMap<MmoElement, Integer> defenderElements){
		HashMap<MmoElement, Integer> result = new HashMap<MmoElement, Integer>();
		for(Entry<MmoElement, Integer> entry : attackerElements.entrySet()){
			Integer strength = entry.getValue();
			if(defenderElements.containsKey(attackerElements)){
				strength = Math.max(0, strength-defenderElements.get(entry.getKey()));
			}
			if(strength>0){
				result.put(entry.getKey(), strength);
			}
		}
		return result;
	}
}
