package ch.swisssmp.travel;

import ch.swisssmp.utils.Mathf;

public class TimeUtil {
	public static String format(long time){
		int seconds = Mathf.ceilToInt(time/20f);
		int minutes = Mathf.floorToInt(seconds/60f);
		return String.format("%02d", minutes)+":"+String.format("%02d", seconds - minutes * 60);
	}
}
