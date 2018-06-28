package ch.swisssmp.customitems;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.minecraft.server.v1_12_R1.NBTTagCompound;

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
				for(String tag : nbtTags.c()){
					sender.sendMessage("- "+tag);
				}
			}
			else{
				sender.sendMessage("Keine NBT Daten gefunden.");
			}
			break;
		}
		default:
			return false;
		}
		return true;
	}

}
