package ch.swisssmp.adventuredungeons;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.Position;

public class EventListener implements Listener{
	@EventHandler
	private void onPlayerInteract(PlayerInteractEvent event){
		if(event.getItem()==null) return;
		String tool = ItemUtil.getString(event.getItem(), "dungeon_tool");
		if(tool==null) return;
		if(event.getAction()==Action.RIGHT_CLICK_AIR || event.getAction()==Action.RIGHT_CLICK_BLOCK){
			switch(tool){
			case "DUNGEON": this.onDungeonTokenUse(event); break;
			case "POSITION": this.onPositionTokenUse(event); break;
			default: return;
			}
		}
		else if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction()==Action.LEFT_CLICK_BLOCK){
			switch(tool){
			case "POSITION": this.onPositionTokenTeleport(event); break;
			default: return;
			}
		}
		event.setCancelled(true);
	}
	
	private void onDungeonTokenUse(PlayerInteractEvent event){
		int dungeon_id = ItemManager.getDungeonId(event.getItem());
		if(dungeon_id<0) return;
		Dungeon dungeon = Dungeon.get(dungeon_id);
		if(dungeon==null) return;
		if(!event.getPlayer().hasPermission("dungeons.admin")) return;
		DungeonEditorView.open(dungeon, event.getPlayer());
		event.setCancelled(true);
	}
	
	private void onPositionTokenUse(PlayerInteractEvent event){
		Position position = new Position(event.getPlayer().getLocation());
		ItemStack itemStack = event.getItem();
		ItemUtil.setPosition(itemStack, "position", position);
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setLore(ItemManager.getPositionTokenLore(position));
		itemStack.setItemMeta(itemMeta);
	}
	
	private void onPositionTokenTeleport(PlayerInteractEvent event){
		Position position = ItemUtil.getPosition(event.getItem(), "position");
		if(position==null) return;
		event.getPlayer().teleport(position.getLocation(event.getPlayer().getWorld()));
	}
}
