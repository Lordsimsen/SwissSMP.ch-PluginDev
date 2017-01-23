package ch.swisssmp.craftmmo.mmoeffect;

import org.bukkit.Effect;
import org.bukkit.Location;

public enum MmoEffect {
SPAWN,
DEATH,
TORCH_IGNITE,
TORCH_EXTINGUISH,
;
	public static void play(Location location, MmoEffect effect){
		location.add(0.5, 0.5, 0.5);
		switch(effect){
		case SPAWN:
			effect.play(location, 115, 255, 244);
			break;
		case TORCH_IGNITE:
			effect.play(location, 255, 118, 72);
			break;
		case TORCH_EXTINGUISH:
			effect.play(location, 60, 60, 60);
			break;
		default:
			break;
		}
	}
	
    protected void play(Location location, float r, float g, float b) {
		location.getWorld().spigot().playEffect(location, Effect.POTION_SWIRL, 0, 0, r/255, g/255, b/255, 1, 0, 16);
    }    
}
