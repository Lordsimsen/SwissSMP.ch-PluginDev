package ch.swisssmp.archery;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;

public class ItemManager {
	protected static ItemStack quiver;
	protected static ItemStack ironArrow;
	protected static ItemStack burstArrow;
	protected static ItemStack torchArrow;
	protected static ItemStack multiArrow;
	protected static ItemStack flameArrow;
	protected static ItemStack explosiveArrow;
	protected static ItemStack vampireArrow;
	
	public static ItemStack getItemStack(String customEnum){
		switch(customEnum){
		case "QUIVER": return quiver;
		case "NORMAL_ARROW":return new ItemStack(Material.ARROW, 1);
		case "TIPPED_ARROW":return new ItemStack(Material.TIPPED_ARROW, 1);
		case "IRON_ARROW": return ironArrow;
		case "BURST_ARROW": return burstArrow;
		case "TORCH_ARROW": return torchArrow;
		case "MULTI_ARROW": return multiArrow;
		case "FLAME_ARROW": return flameArrow;
		case "EXPLOSIVE_ARROW": return explosiveArrow;
		case "VAMPIRE_ARROW": return vampireArrow;
		default: return null;
		}
	}
	
	public static ItemStack getFirstArrowStack(Inventory inventory){
		int slot;
		int arrowSlot = inventory.first(Material.ARROW);
		int tippedArrowSlot = inventory.first(Material.TIPPED_ARROW);
		if(arrowSlot>=0 && tippedArrowSlot>=0) slot = Math.min(arrowSlot, tippedArrowSlot);
		else if(arrowSlot>=0) slot = arrowSlot;
		else if(tippedArrowSlot>=0) slot = tippedArrowSlot;
		else return null;
		return inventory.getItem(slot);
	}
	
	public static ItemStack getQuiver(Inventory inventory){
		String customEnum;
		if(inventory instanceof PlayerInventory){
			PlayerInventory playerInventory = (PlayerInventory)inventory;
			if(playerInventory.getItemInMainHand()!=null && playerInventory.getItemInMainHand().getType()==Material.WOODEN_SWORD){
				customEnum = CustomItems.getCustomEnum(playerInventory.getItemInMainHand());
				if(customEnum!=null && customEnum.equals("QUIVER")) return playerInventory.getItemInMainHand();
			}
			else if(playerInventory.getItemInOffHand()!=null && playerInventory.getItemInOffHand().getType()==Material.WOODEN_SWORD){
				customEnum = CustomItems.getCustomEnum(playerInventory.getItemInOffHand());
				if(customEnum!=null && customEnum.equals("QUIVER")) return playerInventory.getItemInOffHand();
			}
		}
		HashMap<Integer,? extends ItemStack> quiverSearchResult = inventory.all(Material.WOODEN_SWORD);
		for(ItemStack itemStack : quiverSearchResult.values()){
			if(!itemStack.getItemMeta().isUnbreakable()) continue;
			customEnum = CustomItems.getCustomEnum(itemStack);
			if(customEnum==null || !customEnum.equals("QUIVER")) continue;
			return itemStack;
		}
		return null;
	}
	
	public static String getItemName(ItemStack itemStack){
		if(itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()){
			return itemStack.getItemMeta().getDisplayName();
		}
		if(itemStack.getType()==Material.ARROW) return ChatColor.WHITE+"Pfeil";
		else if(itemStack.getType()==Material.TIPPED_ARROW) return ChatColor.WHITE+"Getränkter Pfeil";
		return itemStack.getType().name();
	}
	
	protected static void registerRecipes(){
		registerQuiver();
		registerIronArrow();
		registerBurstArrow();
		registerTorchArrow();
		registerMultiArrow();
		registerFlameArrow();
		registerExplosiveArrow();
		registerVampireArrow();
	}
	
	private static void registerQuiver(){
		CustomItemBuilder quiverBuilder = CustomItems.getCustomItemBuilder("QUIVER");
		quiverBuilder.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		quiver = quiverBuilder.build();
		quiver.setAmount(1);
		ShapedRecipe quiverRecipe = new ShapedRecipe(new NamespacedKey(Archery.getInstance(),"quiver"), quiver);
		quiverRecipe.shape("sil"," li","l s");
		quiverRecipe.setIngredient('s', Material.STRING);
		quiverRecipe.setIngredient('i', Material.IRON_INGOT);
		quiverRecipe.setIngredient('l', Material.LEATHER);
		Bukkit.getServer().addRecipe(quiverRecipe);
		//Bukkit.getLogger().info("Quiver Recipe has been added");
	}
	
	private static void registerIronArrow(){
		CustomItemBuilder arrowBuilder = CustomItems.getCustomItemBuilder("IRON_ARROW");
		ironArrow = arrowBuilder.build();
		ironArrow.setAmount(2);
		ShapedRecipe arrowRecipe = new ShapedRecipe(new NamespacedKey(Archery.getInstance(),"iron_arrow"), ironArrow);
		arrowRecipe.shape(" n "," s "," f ");
		arrowRecipe.setIngredient('n', Material.IRON_NUGGET);
		arrowRecipe.setIngredient('s', Material.STICK);
		arrowRecipe.setIngredient('f', Material.FEATHER);
		Bukkit.getServer().addRecipe(arrowRecipe);
		//Bukkit.getLogger().info("Iron Arrow Recipe has been added");
	}
	
	private static void registerBurstArrow(){
		CustomItemBuilder arrowBuilder = CustomItems.getCustomItemBuilder("BURST_ARROW");
		burstArrow = arrowBuilder.build();
		burstArrow.setAmount(4);
		ShapedRecipe arrowRecipe = new ShapedRecipe(new NamespacedKey(Archery.getInstance(),"burst_arrow"), burstArrow);
		arrowRecipe.shape(" a ","aca"," a ");
		arrowRecipe.setIngredient('a', Material.ARROW);
		arrowRecipe.setIngredient('c', Material.CACTUS);
		Bukkit.getServer().addRecipe(arrowRecipe);
		//Bukkit.getLogger().info("Burst Arrow Recipe has been added");
	}
	
	private static void registerTorchArrow(){
		CustomItemBuilder arrowBuilder = CustomItems.getCustomItemBuilder("TORCH_ARROW");
		torchArrow = arrowBuilder.build();
		torchArrow.setAmount(1);
		ShapelessRecipe arrowRecipe = new ShapelessRecipe(new NamespacedKey(Archery.getInstance(),"torch_arrow"), torchArrow);
		arrowRecipe.addIngredient(1, Material.ARROW);
		arrowRecipe.addIngredient(1, Material.TORCH);
		Bukkit.getServer().addRecipe(arrowRecipe);
		//Bukkit.getLogger().info("Torch Arrow Recipe has been added");
	}
	
	private static void registerMultiArrow(){
		CustomItemBuilder arrowBuilder = CustomItems.getCustomItemBuilder("MULTI_ARROW");
		multiArrow = arrowBuilder.build();
		multiArrow.setAmount(1);
		ShapelessRecipe arrowRecipe = new ShapelessRecipe(new NamespacedKey(Archery.getInstance(),"multi_arrow"), multiArrow);
		arrowRecipe.addIngredient(3, Material.ARROW);
		arrowRecipe.addIngredient(1, Material.STRING);
		Bukkit.getServer().addRecipe(arrowRecipe);
		//Bukkit.getLogger().info("Multi Arrow Recipe has been added");
	}
	
	private static void registerFlameArrow(){
		CustomItemBuilder arrowBuilder = CustomItems.getCustomItemBuilder("FLAME_ARROW");
		flameArrow = arrowBuilder.build();
		flameArrow.setAmount(4);
		ShapedRecipe arrowRecipe = new ShapedRecipe(new NamespacedKey(Archery.getInstance(),"flame_arrow"), flameArrow);
		arrowRecipe.shape(" a ","aba"," a ");
		arrowRecipe.setIngredient('a', Material.ARROW);
		arrowRecipe.setIngredient('b', Material.BLAZE_POWDER);
		Bukkit.getServer().addRecipe(arrowRecipe);
		//Bukkit.getLogger().info("Flame Arrow Recipe has been added");
	}
	
	private static void registerExplosiveArrow(){
		CustomItemBuilder arrowBuilder = CustomItems.getCustomItemBuilder("EXPLOSIVE_ARROW");
		explosiveArrow = arrowBuilder.build();
		explosiveArrow.setAmount(4);
		ShapedRecipe arrowRecipe = new ShapedRecipe(new NamespacedKey(Archery.getInstance(),"explosive_arrow"), explosiveArrow);
		arrowRecipe.shape(" a ","ata"," a ");
		arrowRecipe.setIngredient('a', Material.ARROW);
		arrowRecipe.setIngredient('t', Material.TNT);
		Bukkit.getServer().addRecipe(arrowRecipe);
		//Bukkit.getLogger().info("Explosive Arrow Recipe has been added");
	}
	
	private static void registerVampireArrow(){
		CustomItemBuilder arrowBuilder = CustomItems.getCustomItemBuilder("VAMPIRE_ARROW");
		vampireArrow = arrowBuilder.build();
		vampireArrow.setAmount(4);
		ShapedRecipe arrowRecipe = new ShapedRecipe(new NamespacedKey(Archery.getInstance(),"vampire_arrow"), vampireArrow);
		arrowRecipe.shape(" a ","ana"," a ");
		arrowRecipe.setIngredient('a', Material.ARROW);
		arrowRecipe.setIngredient('n', Material.NETHER_WART_BLOCK);
		Bukkit.getServer().addRecipe(arrowRecipe);
		//Bukkit.getLogger().info("Vampire Arrow Recipe has been added");
	}
}
