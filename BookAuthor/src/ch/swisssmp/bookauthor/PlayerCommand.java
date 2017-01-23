package ch.swisssmp.bookauthor;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.BookMeta.Generation;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;

public class PlayerCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args==null) return false;
		else if(args.length<1) return false;
		String value = "";
		for(int i = 0; i < args.length; i++){
			if(i>0) value+=" ";
			value+=args[i];
		}
		if(!(sender instanceof Player)){
			sender.sendMessage("Can only be used from within the game.");
			return true;
		}
		Player player = (Player) sender;
		ItemStack itemStack = player.getInventory().getItemInMainHand();
		if(itemStack==null){
			player.sendMessage(ChatColor.RED+"Kein Buch in der Hand.");
			return true;
		}
		else if(itemStack.getType()!=Material.WRITTEN_BOOK){
			player.sendMessage(ChatColor.RED+"Kein Buch in der Hand.");
			return true;
		}
		ItemMeta itemMeta = itemStack.getItemMeta();
		BookMeta bookMeta = (BookMeta) itemMeta;
		switch(label){
		case "bookauthor":
		{
			bookMeta.setAuthor(value);
			break;
		}
		case "booktitle":{
			bookMeta.setTitle(value);
			break;
		}
		case "booktype":{
			switch(value.toLowerCase()){
			case "o":{
				bookMeta.setGeneration(Generation.ORIGINAL);
				break;
			}
			case "co":{
				bookMeta.setGeneration(Generation.COPY_OF_ORIGINAL);
				break;
			}
			case "cc":{
				bookMeta.setGeneration(Generation.COPY_OF_COPY);
				break;
			}
			case "t":{
				bookMeta.setGeneration(Generation.TATTERED);
				break;
			}
			default: break;
			}
			break;
		}
		default: break;
		}
		itemStack.setItemMeta(bookMeta);
		return true;
	}

}
