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

public class ItemManager {
	
	public static ItemStack createRing(String ringType){
		return createRing(ringType, null);
	}
	
	public static ItemStack createRing(String ringType, City city){
		return createRing(ringType, null, null);
	}
	
	public static ItemStack createRing(String ringType, City city, PlayerData player){
		boolean isMayor = (city != null && player != null) && player.getUniqueId().equals(city.getMayor());
		CustomItemBuilder ringBuilder = CustomItems.getCustomItemBuilder(ringType);
		ringBuilder.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		ringBuilder.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		ringBuilder.setAttackDamage(0);
		ItemStack ring = ringBuilder.build();
		SigilRingInfo ringInfo = new SigilRingInfo(city.getUniqueId(),ringType);
		ringInfo.setOwner(player);
		ringInfo.setRank(isMayor ? CitizenRank.MAYOR : CitizenRank.FOUNDER);
		ringInfo.apply(ring);
		return ring;
	}
	
	public static ItemStack createCitizenBill(CitizenBill billInfo){
		CustomItemBuilder billBuilder = CustomItems.getCustomItemBuilder("contract");
		if(billBuilder==null) return null;
		
		billBuilder.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		ItemStack result = billBuilder.build();
		billInfo.apply(result);
		return result;
	}
	
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
	public static String getSigilType(Material frame, Material core){
		if(frame==null || core==null) return null;
		switch(frame){
		case GOLD_BLOCK:
			switch(core){
				case DIAMOND: return "gold_diamond_ring";
				case EMERALD: return "gold_emerald_ring";
				case NETHER_STAR: return "gold_nether_star_ring";
				case REDSTONE: return "gold_redstone_ring";
				case LAPIS_LAZULI: return "gold_lapis_ring";
				case GLOWSTONE_DUST: return "gold_glowstone_ring";
				case OXEYE_DAISY: return "gold_daisy_ring";
				case BLUE_ORCHID: return "gold_orchid_ring";
				default: return null;
			}
		case EMERALD_BLOCK:
			switch(core){
				case DIAMOND: return "emerald_diamond_ring";
				case EMERALD: return "emerald_emerald_ring";
				case NETHER_STAR: return "emerald_nether_star_ring";
				case REDSTONE: return "emerald_redstone_ring";
				case LAPIS_LAZULI: return "emerald_lapis_ring";
				case GLOWSTONE_DUST: return "emerald_glowstone_ring";
				case OXEYE_DAISY: return "emerald_daisy_ring";
				case BLUE_ORCHID: return "emerald_orchid_ring";
				default: return null;
			}
		case IRON_BLOCK:
			switch(core){
				case DIAMOND: return "iron_diamond_ring";
				case EMERALD: return "iron_emerald_ring";
				case NETHER_STAR: return "iron_nether_star_ring";
				case REDSTONE: return "iron_redstone_ring";
				case LAPIS_LAZULI: return "iron_lapis_ring";
				case GLOWSTONE_DUST: return "iron_glowstone_ring";
				case OXEYE_DAISY: return "iron_daisy_ring";
				case BLUE_ORCHID: return "iron_orchid_ring";
				default: return null;
			}
		case OBSIDIAN:
			switch(core){
				case DIAMOND: return "obsidian_diamond_ring";
				case EMERALD: return "obsidian_emerald_ring";
				case NETHER_STAR: return "obsidian_nether_star_ring";
				case REDSTONE: return "obsidian_redstone_ring";
				case LAPIS_LAZULI: return "obsidian_lapis_ring";
				case GLOWSTONE_DUST: return "obsidian_glowstone_ring";
				case OXEYE_DAISY: return "obsidian_daisy_ring";
				case BLUE_ORCHID: return "obsidian_orchid_ring";
				default: return null;
			}
		case PRISMARINE_BRICKS:
			switch(core){
				case DIAMOND: return "prismarine_diamond_ring";
				case EMERALD: return "prismarine_emerald_ring";
				case NETHER_STAR: return "prismarine_nether_star_ring";
				case REDSTONE: return "prismarine_redstone_ring";
				case LAPIS_LAZULI: return "prismarine_lapis_ring";
				case GLOWSTONE_DUST: return "prismarine_glowstone_ring";
				case OXEYE_DAISY: return "prismarine_daisy_ring";
				case BLUE_ORCHID: return "prismarine_orchid_ring";
				default: return null;
			}
		case QUARTZ_BLOCK:
			switch(core){
				case DIAMOND: return "quartz_diamond_ring";
				case EMERALD: return "quartz_emerald_ring";
				case NETHER_STAR: return "quartz_nether_star_ring";
				case REDSTONE: return "quartz_redstone_ring";
				case LAPIS_LAZULI: return "quartz_lapis_ring";
				case GLOWSTONE_DUST: return "quartz_glowstone_ring";
				case OXEYE_DAISY: return "quartz_daisy_ring";
				case BLUE_ORCHID: return "quartz_orchid_ring";
				default: return null;
			}
		case REDSTONE_BLOCK:
			switch(core){
				case DIAMOND: return "redstone_diamond_ring";
				case EMERALD: return "redstone_emerald_ring";
				case NETHER_STAR: return "redstone_nether_star_ring";
				case REDSTONE: return "redstone_redstone_ring";
				case LAPIS_LAZULI: return "redstone_lapis_ring";
				case GLOWSTONE_DUST: return "redstone_glowstone_ring";
				case OXEYE_DAISY: return "redstone_daisy_ring";
				case BLUE_ORCHID: return "redstone_orchid_ring";
				default: return null;
			}
		case LAPIS_BLOCK:
			switch(core){
				case DIAMOND: return "lapis_diamond_ring";
				case EMERALD: return "lapis_emerald_ring";
				case NETHER_STAR: return "lapis_nether_star_ring";
				case REDSTONE: return "lapis_redstone_ring";
				case LAPIS_LAZULI: return "lapis_lapis_ring";
				case GLOWSTONE_DUST: return "lapis_glowstone_ring";
				case OXEYE_DAISY: return "lapis_daisy_ring";
				case BLUE_ORCHID: return "lapis_orchid_ring";
				default: return null;
			}
		default: return null;
		}
	}
}
