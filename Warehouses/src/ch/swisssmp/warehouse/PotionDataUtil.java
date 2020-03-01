package ch.swisssmp.warehouse;

import java.util.List;

import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;

public class PotionDataUtil {
	public static boolean compare(PotionMeta a, PotionMeta b){
		if(!PotionDataUtil.compare(a.getBasePotionData(), b.getBasePotionData())) return false;
		List<PotionEffect> effects = b.getCustomEffects();
		if(effects.size()!=a.getCustomEffects().size()) return false;
		for(PotionEffect effect : a.getCustomEffects()){
			boolean success = false;
			for(int i = 0; i < effects.size(); i++){
				if(!compare(effect, effects.get(i))) continue;
				effects.remove(i);
				success = true;
				break;
			}
			if(!success) return false;
		}
		return true;
	}
	
	public static boolean compare(PotionData a, PotionData b){
		return a.getType()==b.getType() && (a.isExtended()==b.isExtended()) && (a.isUpgraded()==b.isUpgraded());
	}
	
	public static boolean compare(PotionEffect a, PotionEffect b){
		return a.getDuration()==b.getDuration() && a.getAmplifier()==b.getAmplifier() && a.getType()==b.getType();
	}
}
