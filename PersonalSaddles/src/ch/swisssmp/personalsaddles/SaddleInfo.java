package ch.swisssmp.personalsaddles;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.querz.nbt.tag.CompoundTag;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import ch.swisssmp.utils.ItemUtil;

public class SaddleInfo {

	private UUID owner;
	private String owner_name;
	
	public SaddleInfo(UUID owner, String name){
		this.owner = owner;
		this.owner_name = name;
	}
	
	public void apply(ItemStack itemStack){
		itemStack.setType(Material.SADDLE);
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		List<String> description = new ArrayList<String>();
		if(owner!=null){
			description.add(ChatColor.GRAY+"Eigentum von " + owner_name);
			if(!itemMeta.hasEnchant(Enchantment.DURABILITY)) itemMeta.addEnchant(Enchantment.DURABILITY, 1, false);
		}
		itemMeta.setLore(description);
		itemStack.setItemMeta(itemMeta);
		CompoundTag nbtTag = ItemUtil.getData(itemStack);
		if(nbtTag==null) nbtTag = new CompoundTag();
		nbtTag.putBoolean("private_saddle", true);
		if(owner!=null){
			nbtTag.putString("player_uuid", owner.toString());
			nbtTag.putString("player_name", owner_name);
		}
		ItemUtil.setData(itemStack, nbtTag);
	}
	
	public boolean isOwner(Entity entity){
		return owner!=null && owner.equals(entity.getUniqueId());
	}
	
	public UUID getOwner(){
		return owner;
	}
	
	public String getOwnerName(){
		return owner_name;
	}
	
	public void setOwner(Player player){
		this.owner = player.getUniqueId();
		this.owner_name = player.getName();
	}
	
	public void setOwner(UUID owner){
		this.owner = owner;
	}
	
	public void setOwnerName(String owner_name){
		this.owner_name = owner_name;
	}
	
	public static SaddleInfo get(Entity vehicle){
		if(!(vehicle instanceof Horse)) return null;
		Horse horse = (Horse) vehicle;
		return get(horse.getInventory().getSaddle());
	}
	
	public static SaddleInfo get(ItemStack itemStack){
		if(itemStack == null)
			return null;
		if(itemStack.getType() != Material.SADDLE){
			return null;
		}
		CompoundTag nbtTag = ItemUtil.getData(itemStack);
		if(nbtTag==null || !nbtTag.containsKey("private_saddle")) return null;
		UUID owner = nbtTag.containsKey("player_uuid") ? UUID.fromString(nbtTag.getString("player_uuid")) : null;
		String owner_name = nbtTag.containsKey("player_name") ? nbtTag.getString("player_name") : null;
		return new SaddleInfo(owner, owner_name);
	}
}
