package ch.swisssmp.customitems;

import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import net.minecraft.server.v1_12_R1.NBTBase;
import net.minecraft.server.v1_12_R1.NBTTagByte;
import net.minecraft.server.v1_12_R1.NBTTagByteArray;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagDouble;
import net.minecraft.server.v1_12_R1.NBTTagFloat;
import net.minecraft.server.v1_12_R1.NBTTagInt;
import net.minecraft.server.v1_12_R1.NBTTagIntArray;
import net.minecraft.server.v1_12_R1.NBTTagList;
import net.minecraft.server.v1_12_R1.NBTTagLong;
import net.minecraft.server.v1_12_R1.NBTTagShort;
import net.minecraft.server.v1_12_R1.NBTTagString;

public class PlayerCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args==null || args.length==0) return false;
		switch(args[0]){
		case "summon":{
			if(args.length<2) return false;
			if(!(sender instanceof Player)){
				sender.sendMessage("[CustomItems] Kann nur ingame verwendet werden.");
				return true;
			}
			CustomItemBuilder customItemBuilder;
			Player player = (Player) sender;
			if(StringUtils.isNumeric(args[1])){
				if(args.length>2 && StringUtils.isNumeric(args[2])){
					customItemBuilder = CustomItems.getCustomItemBuilder(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
				}
				else{
					customItemBuilder = CustomItems.getCustomItemBuilder(Integer.parseInt(args[1]));
				}
			}
			else{
				if(args.length>2 && StringUtils.isNumeric(args[2])){
					customItemBuilder = CustomItems.getCustomItemBuilder(args[1], Integer.parseInt(args[2]));
				}
				else{
					customItemBuilder = CustomItems.getCustomItemBuilder(args[1]);
				}
			}
			if(customItemBuilder==null){
				sender.sendMessage("[CustomItems] Konnte den ItemBuilder nicht generieren.");
				return true;
			}
			ItemStack itemStack = customItemBuilder.build();
			if(itemStack==null){
				sender.sendMessage("[CustomItems] Konnte den ItemStack nicht generieren.");
				return true;
			}
			sender.sendMessage("[CustomItems] "+itemStack.getAmount()+"x "+itemStack.getItemMeta().getDisplayName()+"§r generiert!");
			player.getWorld().dropItem(player.getEyeLocation(), itemStack);
			break;
		}
		case "inspect":{
			if(!(sender instanceof Player)){
				sender.sendMessage("[CustomItems] Kann nur ingame verwendet werden.");
				return true;
			}
			Player player = (Player) sender;
			ItemStack itemStack = player.getInventory().getItemInMainHand();
			if(itemStack==null) itemStack = player.getInventory().getItemInOffHand();
			if(itemStack==null) return true;
			ItemMeta itemMeta = itemStack.getItemMeta();
			String name;
			if(itemMeta!=null){
				name = itemMeta.getDisplayName();
				if(name==null) name = itemMeta.getLocalizedName();
			}
			else{
				name = itemStack.getType().name();
			}
			sender.sendMessage("[CustomItems] Analysiere "+name);
			net.minecraft.server.v1_12_R1.ItemStack craftItemStack = CraftItemStack.asNMSCopy(itemStack);
			if(craftItemStack.hasTag()){
				NBTTagCompound nbtTags = craftItemStack.getTag();
				this.displayNBTTagCompound(nbtTags, player, 0);
			}
			else{
				sender.sendMessage("Keine NBT Daten gefunden.");
			}
			break;
		}
		case "craft":
		case "crafting":
		case "recipe":
		case "recipes":{
			if(!(sender instanceof Player)){
				sender.sendMessage("[CustomItems] Kann nur ingame verwendet werden.");
				return true;
			}
			Player player = (Player) sender;
			ItemStack itemStack = player.getInventory().getItemInMainHand();
			if(itemStack==null) itemStack = player.getInventory().getItemInOffHand();
			if(itemStack==null) return true;
			String name = this.getItemDisplayName(itemStack);
			sender.sendMessage("[CustomItems] Rezepte für "+name);
			for(Recipe recipe : Bukkit.getRecipesFor(itemStack)){
				if(!recipe.getResult().isSimilar(itemStack)) continue;
				if(recipe instanceof ShapelessRecipe){
					ShapelessRecipe shapeless = (ShapelessRecipe)recipe;
					sender.sendMessage("Formloses Rezept:");
					for(ItemStack ingredient : shapeless.getIngredientList()){
						sender.sendMessage("- "+this.getItemDisplayName(ingredient));
					}
				}
				else if(recipe instanceof ShapedRecipe){
					ShapedRecipe shaped = (ShapedRecipe) recipe;
					sender.sendMessage("Formfestes Rezept:");
					sender.sendMessage(this.getOffset(1)+"Form:");
					for(int i = 0; i < shaped.getShape().length; i++){
						sender.sendMessage(this.getOffset(2)+shaped.getShape()[i]);
					}
					sender.sendMessage(this.getOffset(1)+"Zutaten:");
					for(Entry<Character, ItemStack> ingredient : shaped.getIngredientMap().entrySet()){
						sender.sendMessage(this.getOffset(2)+ingredient.getKey()+": "+this.getItemDisplayName(ingredient.getValue()));
					}
				}
				else if(recipe instanceof FurnaceRecipe){
					FurnaceRecipe furnace = (FurnaceRecipe) recipe;
					sender.sendMessage("Schmelz Rezept:");
					sender.sendMessage(this.getOffset(1)+"Zutat: "+this.getItemDisplayName(furnace.getInput()));
				}
			}
			return true;
		}
		default:
			return false;
		}
		return true;
	}
	
	private void displayNBTTag(String label, NBTBase tag, Player player, int depth){
		if(tag instanceof NBTTagCompound){
			if(!label.trim().isEmpty()) player.sendMessage(this.getOffset(depth)+label+":");
			this.displayNBTTagCompound((NBTTagCompound)tag, player, depth+1);
		}
		else if(tag instanceof NBTTagList){
			if(!label.trim().isEmpty()) player.sendMessage(this.getOffset(depth)+label+":");
			this.displayNBTTagList((NBTTagList)tag, player, depth+1);
		}
		else if(tag instanceof NBTTagByteArray){
			this.displayNBTTagByteArray(label, (NBTTagByteArray)tag, player, depth+1);
		}
		else if(tag instanceof NBTTagIntArray){
			this.displayNBTTagIntArray(label, (NBTTagIntArray)tag, player, depth+1);
		}
		else{
			String variableName = this.getOffset(depth)+(!label.trim().isEmpty()?label+": ":"- ");
			if(tag instanceof NBTTagByte){
				player.sendMessage(variableName+((NBTTagByte)tag).g());
			}
			else if(tag instanceof NBTTagDouble){
				player.sendMessage(variableName+((NBTTagDouble)tag).asDouble());
			}
			else if(tag instanceof NBTTagFloat){
				player.sendMessage(variableName+((NBTTagFloat)tag).i());
			}
			else if(tag instanceof NBTTagInt){
				player.sendMessage(variableName+((NBTTagInt)tag).e());
			}
			else if(tag instanceof NBTTagLong){
				player.sendMessage(variableName+((NBTTagLong)tag).d());
			}
			else if(tag instanceof NBTTagShort){
				player.sendMessage(variableName+((NBTTagShort)tag).f());
			}
			else if(tag instanceof NBTTagString){
				player.sendMessage(variableName+((NBTTagString)tag).c_());
			}
			else{
				player.sendMessage(variableName+tag.toString());
			}
		}
	}
	
	private void displayNBTTagCompound(NBTTagCompound compound, Player player, int depth){
		NBTBase tag;
		for(String key : compound.c()){
			tag = compound.get(key);
			this.displayNBTTag(key,tag, player, depth+1);
		}
	}
	
	private void displayNBTTagList(NBTTagList list, Player player, int depth){
		NBTBase tag;
		for(int i = 0; i < list.size(); i++){
			tag = list.i(i);
			this.displayNBTTag(i+"", tag, player, depth+1);
		}
	}
	
	private void displayNBTTagByteArray(String label, NBTTagByteArray array, Player player, int depth){
		player.sendMessage(this.getOffset(depth)+label+": ["+StringUtils.join(array.c(), ",")+"]");
	}
	
	private void displayNBTTagIntArray(String label, NBTTagIntArray array, Player player, int depth){
		player.sendMessage(this.getOffset(depth)+label+": ["+StringUtils.join(array.d(), ",")+"]");
	}
	
	private String getOffset(int depth){
		String result = "";
		for(int i = 0; i < depth; i++){
			result+= "  ";
		}
		return result;
	}
	
	private String getItemDisplayName(ItemStack itemStack){
		if(itemStack==null) return "AIR";
		String name;
		if(itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) name = itemStack.getItemMeta().getDisplayName();
		else name = itemStack.getType().name();
		if(itemStack.getAmount()>1) name+=" x"+itemStack.getAmount();
		return name;
	}
}
