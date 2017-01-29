package ch.swisssmp.craftbank;

import ch.swisssmp.utils.SwissSMPler;

public class ExperienceManager {
	public static int getExperience(SwissSMPler player){
		int level = player.getLevel();
		float levelProgress = player.getExp();
		int levelExperience = convertLevelToExperience(level);
		int progressExperience = (int) Math.floor(getExperienceForLevel(level+1)*levelProgress);
		return levelExperience+progressExperience;
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
	private static int getExperienceForLevel(int level){
		if(level<=16){
			return 2*level+7;
		}
		else if(level<=31){
			return 5*level-39;//its actually 38
		}
		else{
			return 9*level-158;//its actually 158
		}
	}
}
