package ch.swisssmp.event.remotelisteners;

import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.event.Event;

import ch.swisssmp.adventuredungeons.event.ItemDiscoveredEvent;
import ch.swisssmp.event.remotelisteners.filter.ItemFilter;
import ch.swisssmp.event.remotelisteners.filter.PlayerFilter;
import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.webcore.DataSource;
import net.minecraft.server.v1_12_R1.NBTTagCompound;

public class ItemDiscoveredEventListener extends DungeonEventListener implements PlayerFilter,ItemFilter{

	private final static String[] noun_cases = new String[]{"Nominativ", "nominativ", "Akkusativ", "akkusativ", "Dativ", "dativ", "Genitiv", "genitiv"};
	
	public ItemDiscoveredEventListener(ConfigurationSection dataSection) {
		super(dataSection);
	}

	@Override
	public void trigger(Event event) {
		if(!(event instanceof ItemDiscoveredEvent)) return;
		ItemDiscoveredEvent itemDiscoveredEvent = (ItemDiscoveredEvent) event;
		if(!checkDungeon(this.dataSection, itemDiscoveredEvent)) return;
		if(!checkWorld(this.dataSection, itemDiscoveredEvent.getInstance().getWorld())) return;
		if(!checkPlayer(this.dataSection, itemDiscoveredEvent.getPlayer())) return;
		if(!checkItem(this.dataSection, itemDiscoveredEvent.getItemStack())) return;
		super.trigger(event, itemDiscoveredEvent.getPlayer());
	}

	
	@Override
	protected String insertArguments(String command, Event event){
		command = super.insertArguments(command, event);
		ItemDiscoveredEvent itemDiscoveredEvent = (ItemDiscoveredEvent) event;
		for(String noun_case : noun_cases){
			if(command.contains("{"+noun_case+"}")){
				int item_id = -1;

				net.minecraft.server.v1_12_R1.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemDiscoveredEvent.getItemStack());
				if(nmsItemStack.hasTag()){
					NBTTagCompound nbtTag = nmsItemStack.getTag();
					if(nbtTag.hasKey("item_id")){
						item_id = nbtTag.getInt("item_id");
					}
				}
				command = command.replace("{"+noun_case+"}", DataSource.getResponse("items/article.php", new String[]{
					"item_id="+item_id,
					"material="+itemDiscoveredEvent.getItemStack().getType(),
					"durability="+itemDiscoveredEvent.getItemStack().getDurability(),
					"case="+noun_case
				}));
			}
		}
		command = command.replace("{Item}", itemDiscoveredEvent.getItemStack().getItemMeta().getDisplayName()+"§r");
		return command;
	}
}
