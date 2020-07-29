package ch.swisssmp.city;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import ch.swisssmp.utils.nbt.NBTUtil;
import net.querz.nbt.tag.CompoundTag;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.PlayerData;

public class CitizenBill {
	
	private final UUID cityId;
	private PlayerData playerData;
	private PlayerData parentInfo;
	private String role;
	
	private boolean signedByCitizen = false;
	private boolean signedByParent = false;
	
	private boolean invalid = false;

	public CitizenBill(){
		this.cityId = null;
	}

	public CitizenBill(City city){
		this(city.getUniqueId());
	}

	public CitizenBill(UUID cityId){
		this.cityId = cityId;
	}

	public UUID getCityId(){
		return cityId;
	}

	public City getCity(){
		return CitySystem.getCity(cityId).orElse(null);
	}

	public Optional<Citizenship> getCitizenship(){
		UUID playerUid = playerData.getUniqueId();
		return CitySystem.getCitizenship(cityId, playerUid);
	}

	public void apply(ItemStack itemStack){
		if(itemStack==null) return;
		CompoundTag nbtTag = ItemUtil.getData(itemStack);
		if(nbtTag==null) nbtTag = new CompoundTag();
		nbtTag.putString("city_tool", "citizen_bill");
		City city = getCity();
		if(city!=null && !invalid) NBTUtil.set("city_id", cityId, nbtTag);
		else if(nbtTag.containsKey("city_id")) nbtTag.remove("city_id");
		if(playerData !=null && parentInfo!=null){
			nbtTag.putString("citizen", playerData.getUniqueId().toString());
			nbtTag.putString("citizen_name", playerData.getName());
			nbtTag.putString("citizen_parent", parentInfo.getUniqueId().toString());
			nbtTag.putString("citizen_parent_name", parentInfo.getName());
			nbtTag.putBoolean("signed_by_citizen", signedByCitizen);
			nbtTag.putBoolean("signed_by_parent", signedByParent);
		}
		if(role!=null) nbtTag.putString("citizen_role", role);
		ItemUtil.setData(itemStack, nbtTag);
		ItemMeta itemMeta = itemStack.getItemMeta();
		if(itemMeta!=null){
			if(signedByCitizen && signedByParent){
				if(invalid){
					itemMeta.setDisplayName(ChatColor.GRAY+"Alter Bürgerschein");
				}
				else{
					itemMeta.setDisplayName(ChatColor.AQUA+"Bürgerschein ("+ playerData.getName()+")");
				}
			}
			else if(playerData ==null || parentInfo==null){
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
		City city = getCity();
		List<String> result = new ArrayList<String>();
		if(city!=null) result.add(cityColor+city.getName());
		if(playerData ==null || parentInfo==null)return result;
		result.add(headerColor+"Ausgestellt:");
		result.add(ChatColor.GRAY+"an "+nameColor+ playerData.getName());
		result.add(ChatColor.GRAY+"durch "+nameColor+parentInfo.getName());
		if(role!=null && !role.isEmpty()) result.add(ChatColor.GRAY+"Rolle: "+role);
		if(signedByCitizen || signedByParent) result.add(headerColor+"Unterschrieben:");
		else result.add(ChatColor.GRAY+"(Unterschriften ausstehend)");
		if(signedByCitizen) result.add(nameColor+ playerData.getName());
		if(signedByParent) result.add(nameColor+parentInfo.getName());
		return result;
	}
	
	public void setPlayerData(PlayerData playerData){
		this.playerData = playerData;
	}
	
	public PlayerData getPlayerData(){
		return playerData;
	}
	
	public void setParent(PlayerData parentInfo){
		this.parentInfo = parentInfo;
	}
	
	public PlayerData getParent(){
		return parentInfo;
	}
	
	public void setCitizenRole(String role){
		this.role = role;
	}
	
	public String getRole(){
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
	
	public static CitizenBill get(ItemStack bill){
		if(bill==null) return null;
		CompoundTag nbtTag = ItemUtil.getData(bill);
		if(nbtTag==null) return null;
		String cityTool = nbtTag.getString("city_tool");
		if(cityTool==null || !cityTool.equals("citizen_bill")) return null;
		UUID cityId = NBTUtil.getUUID("city_id", nbtTag);
		City city = CitySystem.getCity(cityId).orElse(null);
		if(city==null) return null;
		CitizenBill result = new CitizenBill(city);
		if(nbtTag.containsKey("citizen") && nbtTag.containsKey("citizen_name")){
			UUID citizen_uuid = UUID.fromString(nbtTag.getString("citizen"));
			String citizen_name = nbtTag.getString("citizen_name");
			result.setPlayerData(new PlayerData(citizen_uuid, citizen_name, citizen_name));
		}
		if(nbtTag.containsKey("citizen_parent") && nbtTag.containsKey("citizen_parent_name")){
			UUID parent_uuid = UUID.fromString(nbtTag.getString("citizen_parent"));
			String parent_name = nbtTag.getString("citizen_parent_name");
			result.setParent(new PlayerData(parent_uuid, parent_name, parent_name));
		}
		if(nbtTag.getBoolean("signed_by_parent")){
			result.setSignedByParent();
		}
		if(nbtTag.getBoolean("signed_by_citizen")){
			result.setSignedByCitizen();
		}
		if(nbtTag.containsKey("citizen_role")){
			result.setCitizenRole(nbtTag.getString("citizen_role"));
		}
		return result;
	}
}
