package ch.swisssmp.zones.editor;

import org.bukkit.ChatColor;

public enum ActionResult {
	POINT_ADDED(ChatColor.YELLOW+"Punkt hinzugefügt."),
	POINT_REMOVED(ChatColor.GRAY+"Punkt entfernt."),
	ZONE_ADJUSTED(ChatColor.YELLOW+"Zone angepasst."),
	NONE("");
	
	private final String feedback;
	
	private ActionResult(String feedback){
		this.feedback = feedback;
	}
	
	public String getFeedback(){
		return feedback;
	}
}
