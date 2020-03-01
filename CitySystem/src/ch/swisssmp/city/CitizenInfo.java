package ch.swisssmp.city;

import java.util.UUID;

import org.bukkit.inventory.ItemStack;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.PlayerInfo;

public class CitizenInfo {
	private final PlayerInfo playerInfo;
	private CitizenRank rank;
	private UUID parent;
	private String role;
	
	protected CitizenInfo(PlayerInfo playerInfo, CitizenRank rank, UUID parent, String role){
		this.playerInfo = playerInfo;
		this.rank = rank;
		this.parent = parent;
		this.role = role;
	}
	
	public PlayerInfo getPlayerInfo(){
		return playerInfo;
	}
	
	public UUID getUniqueId(){
		return playerInfo.getUniqueId();
	}
	
	public String getDisplayName(){
		return playerInfo.getDisplayName();
	}
	
	public String getName(){
		return playerInfo.getName();
	}
	
	public ItemStack getHead(){
		return playerInfo.getHead();
	}
	
	public CitizenRank getRank(){
		return rank;
	}
	
	public void setRank(CitizenRank rank){
		this.rank = rank;
	}
	
	public UUID getParent(){
		return parent;
	}
	
	public String getRole(){
		return role;
	}
	
	protected void setRole(String role){
		this.role = role;
	}
	
	public static CitizenInfo get(ConfigurationSection dataSection){
		PlayerInfo playerInfo = PlayerInfo.get(dataSection);
		CitizenRank rank = CitizenRank.get(dataSection.getString("rank"));
		String role = dataSection.getString("role");
		UUID parent;
		try{
			parent = UUID.fromString(dataSection.getString("parent_uuid"));
		}
		catch(Exception e){
			parent = null;
		}
		return new CitizenInfo(playerInfo, rank, parent, role);
	}
}
