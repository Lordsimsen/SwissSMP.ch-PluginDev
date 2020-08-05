package ch.swisssmp.city;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.utils.PlayerData;

public class ItemUtility {
	
	public static void updateItems(){
		for(Player player : Bukkit.getOnlinePlayers()){
			updateItems(player.getInventory());
		}
	}
	
	public static void updateItems(Inventory inventory){
		updateSigilRings(inventory);
		updateCitizenBills(inventory);
	}
	
	private static void updateSigilRings(Inventory inventory){
		for(ItemStack itemStack : inventory){
			if(itemStack==null || itemStack.getType()!=Material.DIAMOND_SWORD) continue;
			SigilRingInfo ringInfo = SigilRingInfo.get(itemStack);
			if(ringInfo==null || ringInfo.getOwner()==null) continue;
			Citizenship citizenship = ringInfo.getCitizenship().orElse(null);
			if(citizenship !=null){
				ringInfo.setRank(citizenship.getRank());
			}
			else {
				ringInfo.invalidate();
			}
			ringInfo.apply(itemStack);
		}
	}
	
	private static void updateCitizenBills(Inventory inventory){
		for(ItemStack itemStack : inventory){
			if(itemStack==null || itemStack.getType()!=Material.WOODEN_SWORD) continue;
			CitizenBill billInfo = CitizenBill.get(itemStack);
			if(billInfo==null || billInfo.getPlayerData()==null || billInfo.getParent()==null) continue;
			Citizenship citizenship = billInfo.getCitizenship().orElse(null);
			if(citizenship !=null){
				billInfo.setSignedByCitizen();
				billInfo.setSignedByParent();
				billInfo.setCitizenRole(citizenship.getRole());
			}
			else if(billInfo.isSignedByCitizen() && billInfo.isSignedByParent()){
				billInfo.invalidate();
			}
			billInfo.apply(itemStack);
		}
	}
	
	public static City getCity(Player player, ItemStack ring){
		SigilRingInfo ringInfo = SigilRingInfo.get(ring);
		if(ringInfo==null) return null;
		City city = ringInfo.getCity();
		if(city==null || (!city.isCitizen(player.getUniqueId()) && !player.hasPermission(CitySystemPermission.ADMIN))){
			return null;
		}
		return city;
	}
	
	public static Color getMaterialColor(Material material){
		switch(material){
		case IRON_INGOT: return Color.fromRGB(180,180,180);
		case GOLD_BLOCK: return Color.fromRGB(255,220,0);
		case OBSIDIAN: return Color.fromRGB(150,0,150);
		case EMERALD_BLOCK:
		case EMERALD: return Color.fromRGB(50,255,50);
		case PRISMARINE_BRICKS: return Color.fromRGB(0,150,150);
		case LAPIS_BLOCK:
		case LAPIS_LAZULI: return Color.fromRGB(20,20,255);
		case REDSTONE_BLOCK:
		case REDSTONE: return Color.fromRGB(255,20,20);
		case QUARTZ_BLOCK: return Color.fromRGB(255,180,180);
		case DIAMOND: return Color.fromRGB(20,180,255);
		case GLOWSTONE_DUST: return Color.fromRGB(255,255,40);
		case OXEYE_DAISY: return Color.fromRGB(255,255,200);
		case BLUE_ORCHID: return Color.fromRGB(70,100,255);
		case NETHER_STAR: return Color.fromRGB(255,255,180);
		default: return Color.WHITE;
		}
	}
	public static int getRequiredBaseAmount(Material material){
		return 4;
	}
	
	public static int getRequiredCoreAmount(Material material){
		return 1;
	}
}
