package ch.swisssmp.taxcollector;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.SwissSMPUtils;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class TributeInventory extends TaxInventory{
	private static HashMap<Integer,TributeInventory> blockedInventories = new HashMap<Integer,TributeInventory>();
	
	private final int temple_id;
	
	public TributeInventory(Player player, Inventory inventory, ConfigurationSection dataSection) {
		super(player, inventory, dataSection);
		this.temple_id = dataSection.getInt("temple_id");
		blockedInventories.put(this.temple_id, this);
	}

	@Override
	public void close() {
		
		ArrayList<String> arguments = new ArrayList<String>();
		try{
			String argument;
			for(ItemStack itemStack : this.inventory){
				argument = SwissSMPUtils.encodeItemStack(itemStack);
				if(argument==null) continue;
				arguments.add("items[]="+argument);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		this.inventory.clear();
		blockedInventories.remove(this.temple_id);
		YamlConfiguration response = DataSource.getYamlResponse("taxes/pay_tribute.php", new String[]{
				"player="+this.player.getUniqueId(),
				"temple="+this.temple_id,
				String.join("&", arguments)
		});
		this.finish(response);
	}
	protected static TributeInventory get(int temple_id){
		return blockedInventories.get(temple_id);
	}
}
