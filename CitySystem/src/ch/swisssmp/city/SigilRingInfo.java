package ch.swisssmp.city;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.querz.nbt.tag.CompoundTag;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.PlayerInfo;

public class SigilRingInfo {
	
	private final City city;
	private final String ring_type;
	private PlayerInfo owner;
	private CitizenRank rank;
	
	private boolean invalid = false;
	
	public SigilRingInfo(City city, String ring_type){
		this.city = city;
		this.ring_type = ring_type;
	}
	
	public void apply(ItemStack itemStack){
		if(itemStack==null) return;
		CompoundTag nbtTag = ItemUtil.getData(itemStack);
		if(nbtTag==null) nbtTag = new CompoundTag();
		nbtTag.putString("city_tool", "sigil_ring");
		nbtTag.putString("ring_type", ring_type);
		nbtTag.remove("customEnum");
		if(city!=null) nbtTag.putInt("city_id", city.getId());
		else if(nbtTag.containsKey("city_id")) nbtTag.remove("city_id");
		if(owner!=null){
			nbtTag.putString("owner", owner.getUniqueId().toString());
			nbtTag.putString("owner_name", owner.getName());
		}
		if(rank!=null) nbtTag.putString("citizen_rank", rank.toString());
		ItemUtil.setData(itemStack, nbtTag);
		ItemMeta itemMeta = itemStack.getItemMeta();
		if(itemMeta==null)return;
		
		if(invalid){
			itemMeta.setDisplayName(ChatColor.GRAY+"Alter Siegelring");
		}
		else{
			CustomItemBuilder templateBuilder = CustomItems.getCustomItemBuilder(ring_type);
			ItemStack template = templateBuilder.build();
			itemMeta.setDisplayName(template.getItemMeta().getDisplayName());
		}
		if(rank==CitizenRank.MAYOR && !itemMeta.hasEnchant(Enchantment.DURABILITY)){
			itemMeta.addEnchant(Enchantment.DURABILITY, 1, false);
		}
		else if(rank!=CitizenRank.MAYOR && itemMeta.hasEnchant(Enchantment.DURABILITY)){
			itemMeta.removeEnchant(Enchantment.DURABILITY);
		}
		itemMeta.setLore(getRingDescription());
		itemStack.setItemMeta(itemMeta);
	}
	
	public List<String> getRingDescription(){
		List<String> result = new ArrayList<String>();
		if(owner!=null){
			result.add(ChatColor.GRAY+"Eigentum von "+this.owner.getDisplayName()+(rank!=null ? "," : ""));
			if(rank!=null){
				String display_rank = (invalid ? "Ehemal. " : "") + rank.getDisplayName();
				result.add(ChatColor.GRAY+display_rank+" von "+city.getName());
			}
		}
		return result;
	}
	
	public City getCity(){
		return city;
	}
	
	public void setOwner(PlayerInfo playerInfo){
		this.owner = playerInfo;
	}
	
	public PlayerInfo getOwner(){
		return owner;
	}
	
	public void setRank(CitizenRank rank){
		this.rank = rank;
	}
	
	public CitizenRank getCitizenRrank(){
		return rank;
	}
	
	public void invalidate(){
		this.invalid = true;
	}
	
	public static SigilRingInfo get(ItemStack ring){
		if(ring==null) return null;
		CompoundTag nbtTag = ItemUtil.getData(ring);
		if(nbtTag==null) return null;
		String city_tool = nbtTag.getString("city_tool");
		if(city_tool==null || !city_tool.equals("sigil_ring")) return null;
		int city_id = nbtTag.getInt("city_id");
		City city = City.get(city_id);
		if(city==null) return null;
		String ring_type = nbtTag.getString("ring_type");
		if(ring_type==null) ring_type = "metal_ring";
		SigilRingInfo result = new SigilRingInfo(city, ring_type);
		if(nbtTag.containsKey("owner") && nbtTag.containsKey("owner_name")){
			UUID owner_uuid = UUID.fromString(nbtTag.getString("owner"));
			String owner_name = nbtTag.getString("owner_name");
			result.setOwner(new PlayerInfo(owner_uuid, owner_name, owner_name));
		}
		if(nbtTag.containsKey("citizen_rank")){
			result.setRank(CitizenRank.get(nbtTag.getString("citizen_rank")));
		}
		return result;
	}
}
