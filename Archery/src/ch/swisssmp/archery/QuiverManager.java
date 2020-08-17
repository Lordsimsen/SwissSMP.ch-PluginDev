package ch.swisssmp.archery;

import java.util.ArrayList;
import java.util.List;

import ch.swisssmp.utils.ItemUtil;
import net.querz.nbt.tag.CompoundTag;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import ch.swisssmp.customitems.CustomItems;

public class QuiverManager {
	protected static void openQuiver(Player player, ItemStack quiver){
		player.openInventory(new QuiverView(player,quiver));
	}
	
	protected static ItemStack[] getQuiverContents(ItemStack quiver){
		CompoundTag nbtData = ItemUtil.getData(quiver);
		ItemStack[] result = new ItemStack[QuiverManager.getQuiverSize()];
		if(nbtData.containsKey("contents")){
			CompoundTag contentsSection = nbtData.getCompoundTag("contents");
			CompoundTag contentSection;
			String arrowType;
			ItemStack arrowStack;
			for(int i = 0; i < QuiverManager.getQuiverSize(); i++){
				if(!contentsSection.containsKey("slot_"+i))continue;
				contentSection = contentsSection.getCompoundTag("slot_"+i);
				arrowType = contentSection.getString("t");
				if(contentSection.containsKey("stack")){
					YamlConfiguration yamlConfiguration = new YamlConfiguration();
					try {
						yamlConfiguration.loadFromString(contentSection.getString("stack"));
					} catch (InvalidConfigurationException e) {
						Bukkit.getLogger().info("[Archery] ItemStack "+arrowType+" had an invalid Stack Section.");
						Bukkit.getLogger().info("[Archery] Stack Section: "+contentSection.getString("stack"));
						continue;
					}
					arrowStack = yamlConfiguration.getItemStack("item");
					if(arrowStack==null){
						Bukkit.getLogger().info("[Archery] Could not load Arrow Stack with enum "+arrowType);
						continue;
					}
				}
				else{
					arrowStack = ItemManager.getItemStack(arrowType);
					if(arrowStack==null){
						Bukkit.getLogger().info("[Archery] Could not load Arrow Stack with enum "+arrowType);
						continue;
					}
					arrowStack = arrowStack.clone();
					arrowStack.setAmount(contentSection.getInt("c"));
				}
				result[i] = arrowStack;
			}
		}
		return result;
	}
	
	protected static void setQuiverContents(ItemStack quiver, ItemStack[] contents){
		CompoundTag nbtData = ItemUtil.getData(quiver);
		CompoundTag contentsSection = new CompoundTag();
		CompoundTag contentSection;
		ItemStack itemStack;
		String arrowType;
		List<String> itemInfo = new ArrayList<String>();
		String infoString;
		for(int i = 0; i < contents.length; i++){
			itemStack = contents[i];
			if(itemStack==null)continue;
			contentSection = new CompoundTag();
			arrowType = CustomItems.getCustomEnum(itemStack);
			if(arrowType==null || arrowType.isEmpty()){
				if(itemStack.getType()==Material.ARROW) arrowType = "NORMAL_ARROW";
				else if(itemStack.getType()==Material.TIPPED_ARROW) arrowType = "TIPPED_ARROW";
				else{
					//Bukkit.getLogger().info("[Archery] Not going to save "+itemStack.getType());
					continue;
				}
				//Bukkit.getLogger().info("[Archery] Arrow Type is "+arrowType);
			}
			contentSection.putString("t", arrowType);
			contentSection.putInt("c", itemStack.getAmount());
			if(itemStack.getType() == Material.TIPPED_ARROW){
				YamlConfiguration yamlConfiguration = new YamlConfiguration();
				yamlConfiguration.set("item", itemStack);
				contentSection.putString("stack", yamlConfiguration.saveToString());
			}
			contentsSection.put("slot_"+i, contentSection);
			infoString = ItemManager.getItemName(itemStack);
			infoString+=" x"+itemStack.getAmount();
			itemInfo.add(infoString);
		}
		if(nbtData.containsKey("contents")){
			nbtData.remove("contents");
		}
		if(itemInfo.size()>0){
			nbtData.put("contents", contentsSection);
		}
		ItemUtil.setData(quiver, nbtData);
		ItemMeta itemMeta = quiver.getItemMeta();
		if(itemInfo.size()>5){
			itemInfo.set(5, ChatColor.RESET.toString()+ChatColor.GRAY.toString()+ChatColor.ITALIC.toString()+"und "+(itemInfo.size()-5)+" mehr...");
			itemInfo = itemInfo.subList(0, 6);
		}
		itemMeta.setLore(itemInfo);
		quiver.setItemMeta(itemMeta);
		//Bukkit.getLogger().info("[Archery] Quiver saved.");
	}
	
	protected static void refillFromQuiver(ItemStack quiver, ItemStack arrowStack){
		int maxStackSize = arrowStack.getMaxStackSize()-1;
		if(arrowStack.getAmount()>=maxStackSize) return;
		ItemStack[] quiverContents = QuiverManager.getQuiverContents(quiver);
		if(quiverContents==null) return;
		ItemStack quiverContent;
		int remainingSpace = maxStackSize-arrowStack.getAmount();
		for(int i = 0; i < quiverContents.length; i++){
			quiverContent = quiverContents[i];
			if(quiverContent==null || !quiverContent.isSimilar(arrowStack))continue;
			if(quiverContent.getAmount()>remainingSpace){
				arrowStack.setAmount(maxStackSize);
				quiverContent.setAmount(quiverContent.getAmount()-remainingSpace);
				break;
			}
			else{
				arrowStack.setAmount(arrowStack.getAmount()+quiverContent.getAmount());
				remainingSpace-=quiverContent.getAmount();
				quiverContents[i] = null;
			}
		}
		QuiverManager.setQuiverContents(quiver, quiverContents);
	}
	
	protected static ItemStack refillQuiver(ItemStack quiver, ItemStack arrowStack){
		if(arrowStack==null) return null;
		int initialAmount = arrowStack.getAmount();
		ItemStack[] quiverContents = QuiverManager.getQuiverContents(quiver);
		if(quiverContents==null) return arrowStack;
		for(int i = 0; i < quiverContents.length; i++){
			if(quiverContents[i]==null){
				quiverContents[i] = arrowStack;
				arrowStack = null;
				break;
			}
			else if(!quiverContents[i].isSimilar(arrowStack)) continue;
			if(quiverContents[i].getAmount()+arrowStack.getAmount()>quiverContents[i].getMaxStackSize()){
				int difference = quiverContents[i].getMaxStackSize()-quiverContents[i].getAmount();
				quiverContents[i].setAmount(quiverContents[i].getMaxStackSize());
				arrowStack.setAmount(arrowStack.getAmount()-difference);
			}
			else{
				quiverContents[i].setAmount(quiverContents[i].getAmount()+arrowStack.getAmount());
				arrowStack = null;
				break;
			}
		}
		if(arrowStack==null || arrowStack.getAmount()!=initialAmount){
			QuiverManager.setQuiverContents(quiver, quiverContents);
		}
		return arrowStack;
	}
	protected static int getQuiverSize(){
		return 9;
	}
}
