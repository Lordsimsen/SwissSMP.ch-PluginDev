package ch.swisssmp.craftbank;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import ch.swisssmp.utils.SwissSMPler;

public class ExperienceManager {
    // Give or take EXP
	public static int changePlayerExp(SwissSMPler player, int exp){
	    // Get player's current exp
	    int currentExp = getExperience(player);
	   
	    // Reset player's current exp to 0
	    player.setExp(0);
	    player.setLevel(0);
	   
	    // Give the player their exp back, with the difference
	    int newExp = currentExp + exp;
	    player.giveExp(newExp);
	   
	    // Return the player's new exp amount
	    return newExp;
	}
	public static int getExperience(SwissSMPler swisssmpler){
		int level = swisssmpler.getLevel();
		Player player = Bukkit.getPlayer(swisssmpler.getUniqueId());
		int levelExperience = convertLevelToExperience(level+1);
		return levelExperience-player.getExpToLevel();
	}
	private static int convertLevelToExperience(int level){
		if(level<=16){
			return (int) (Math.pow((double)level, (double)2)+6*level);
		}
		else if(level<=31){
			return (int) Math.floor(2.5*Math.pow((double) level, (double)2)-40.5*level+360);
		}
		else{
			return (int) Math.floor(4.5*Math.pow((double) level, (double)2)-162.5*level+2220);
		}
	}
	/*
	private static int getExperienceForLevel(int level){
		if(level<=15){
			return 2*level+7;
		}
		else if(level<=30){
			return 5*level-38;//its actually 38
		}
		else{
			return 9*level-158;//its actually 158
		}
	}
	*/
}
