package ch.swisssmp.city;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.PlayerInfo;
import ch.swisssmp.utils.nbt.NBTTagCompound;

public class CitizenBillInfo {
	
	private final City city;
	private PlayerInfo citizen;
	private PlayerInfo parentInfo;
	private String role;
	
	private boolean signedByCitizen = false;
	private boolean signedByParent = false;
	
	private boolean invalid = false;
	
	public CitizenBillInfo(City city){
		this.city = city;
	}
	
	public void apply(ItemStack itemStack){
		if(itemStack==null) return;
		NBTTagCompound nbtTag = ItemUtil.getData(itemStack);
		if(nbtTag==null) nbtTag = new NBTTagCompound();
		nbtTag.setString("city_tool", "citizen_bill");
		if(city!=null && !invalid) nbtTag.setInt("city_id", city.getId());
		else if(nbtTag.hasKey("city_id")) nbtTag.remove("city_id");
		if(citizen!=null && parentInfo!=null){
			nbtTag.setString("citizen", citizen.getUniqueId().toString());
			nbtTag.setString("citizen_name", citizen.getName());
			nbtTag.setString("citizen_parent", parentInfo.getUniqueId().toString());
			nbtTag.setString("citizen_parent_name", parentInfo.getName());
			nbtTag.setBoolean("signed_by_citizen", signedByCitizen);
			nbtTag.setBoolean("signed_by_parent", signedByParent);
		}
		if(role!=null) nbtTag.setString("citizen_role", role);
		ItemUtil.setData(itemStack, nbtTag);
		ItemMeta itemMeta = itemStack.getItemMeta();
		if(itemMeta!=null){
			if(signedByCitizen && signedByParent){
				if(invalid){
					itemMeta.setDisplayName(ChatColor.GRAY+"Alter Bürgerschein");
				}
				else{
					itemMeta.setDisplayName(ChatColor.AQUA+"Bürgerschein ("+citizen.getName()+")");
				}
			}
			else if(citizen==null || parentInfo==null){
				itemMeta.setDisplayName("Leerer Bürgerschein");
			}
			else{
				itemMeta.setDisplayName("Bürgerschein (pendent)");
			}
			itemMeta.setLore(getBillDescription());
			itemStack.setItemMeta(itemMeta);
		}
	}
	
	public List<String> getBillDescription(){
		ChatColor cityColor = invalid ? ChatColor.WHITE : ChatColor.LIGHT_PURPLE;
		ChatColor headerColor = invalid ? ChatColor.DARK_GRAY : ChatColor.GREEN;
		ChatColor nameColor = invalid ? ChatColor.GRAY : ChatColor.WHITE;
		List<String> result = new ArrayList<String>();
		if(city!=null) result.add(cityColor+city.getName());
		if(citizen==null || parentInfo==null)return result;
		result.add(headerColor+"Ausgestellt:");
		result.add(ChatColor.GRAY+"an "+nameColor+citizen.getName());
		result.add(ChatColor.GRAY+"durch "+nameColor+parentInfo.getName());
		if(role!=null && !role.isEmpty()) result.add(ChatColor.GRAY+"Rolle: "+role);
		if(signedByCitizen || signedByParent) result.add(headerColor+"Unterschrieben:");
		else result.add(ChatColor.GRAY+"(Unterschriften ausstehend)");
		if(signedByCitizen) result.add(nameColor+citizen.getName());
		if(signedByParent) result.add(nameColor+parentInfo.getName());
		return result;
	}
	
	public City getCity(){
		return city;
	}
	
	public void setCitizen(PlayerInfo playerInfo){
		this.citizen = playerInfo;
	}
	
	public PlayerInfo getCitizen(){
		return citizen;
	}
	
	public void setParent(PlayerInfo parentInfo){
		this.parentInfo = parentInfo;
	}
	
	public PlayerInfo getParent(){
		return parentInfo;
	}
	
	public void setCitizenRole(String role){
		this.role = role;
	}
	
	public String getCitizenRole(){
		return role;
	}
	
	public void setSignedByCitizen(){
		signedByCitizen = true;
	}
	
	public boolean isSignedByCitizen(){
		return signedByCitizen;
	}
	
	public void setSignedByParent(){
		signedByParent = true;
	}
	
	public boolean isSignedByParent(){
		return signedByParent;
	}
	
	public void invalidate(){
		this.invalid = true;
	}
	
	public static CitizenBillInfo get(ItemStack bill){
		if(bill==null) return null;
		NBTTagCompound nbtTag = ItemUtil.getData(bill);
		if(nbtTag==null) return null;
		String city_tool = nbtTag.getString("city_tool");
		if(city_tool==null || !city_tool.equals("citizen_bill")) return null;
		int city_id = nbtTag.getInt("city_id");
		City city = City.get(city_id);
		if(city==null) return null;
		CitizenBillInfo result = new CitizenBillInfo(city);
		if(nbtTag.hasKey("citizen") && nbtTag.hasKey("citizen_name")){
			UUID citizen_uuid = UUID.fromString(nbtTag.getString("citizen"));
			String citizen_name = nbtTag.getString("citizen_name");
			result.setCitizen(new PlayerInfo(citizen_uuid, citizen_name, citizen_name));
		}
		if(nbtTag.hasKey("citizen_parent") && nbtTag.hasKey("citizen_parent_name")){
			UUID parent_uuid = UUID.fromString(nbtTag.getString("citizen_parent"));
			String parent_name = nbtTag.getString("citizen_parent_name");
			result.setParent(new PlayerInfo(parent_uuid, parent_name, parent_name));
		}
		if(nbtTag.getBoolean("signed_by_parent")){
			result.setSignedByParent();
		}
		if(nbtTag.getBoolean("signed_by_citizen")){
			result.setSignedByCitizen();
		}
		if(nbtTag.hasKey("citizen_role")){
			result.setCitizenRole(nbtTag.getString("citizen_role"));
		}
		return result;
	}
}
