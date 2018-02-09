package ch.swisssmp.adventuredungeons.player;

import org.bukkit.entity.Player;

import ch.swisssmp.adventuredungeons.sound.AdventureSound;
import ch.swisssmp.adventuredungeons.world.Dungeon;
import ch.swisssmp.adventuredungeons.world.DungeonInstance;

public abstract class AdventurePlayer{
    public static void updateMusic(Player player){
    	if(AdventureSound.musicLoops.containsKey(player.getUniqueId())){
    		return;
    	}
    	Dungeon dungeon = Dungeon.get(player);
    	if(dungeon!=null){
    		DungeonInstance instance = Dungeon.getInstance(player);
    		if(instance.isRunning() && dungeon.background_music>0){
    			AdventureSound.playMusic(player, dungeon.background_music, dungeon.looptime);
    		}
    	}
    }
}
