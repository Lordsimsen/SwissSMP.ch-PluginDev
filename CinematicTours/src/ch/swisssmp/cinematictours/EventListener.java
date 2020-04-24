package ch.swisssmp.cinematictours;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import ch.swisssmp.utils.SwissSMPler;

public class EventListener implements Listener {
	@EventHandler
	private void onBlockPlace(SignChangeEvent event){
		if(!event.getLine(0).equals("[Sightseeing]")) return;
		if(!event.getPlayer().hasPermission("cinematictours.admin")){
			event.getPlayer().sendMessage("["+ChatColor.DARK_GREEN+"Sightseeing"+ChatColor.RESET+"] Du kannst keine Besichtigungstouren einrichten.");
			return;
		}
		event.setLine(0, ChatColor.DARK_GREEN+"[Sightseeing]");
		event.setLine(2, ChatColor.DARK_GRAY+"Für Tour");
		event.setLine(3, ChatColor.DARK_GRAY+"anklicken.");
	}
	@EventHandler
	private void onBlockInteract(PlayerInteractEvent event){
		if(event.getPlayer().getGameMode()==GameMode.SPECTATOR) return;
		if(event.getAction()!=Action.RIGHT_CLICK_BLOCK) return;
		BlockState state = event.getClickedBlock().getState();
		if(!(state instanceof Sign))
			return;
		Sign sign = (Sign) state;
		if(!sign.getLine(0).toLowerCase().contains(ChatColor.DARK_GREEN+"[sightseeing]"))
			return;
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cam sequence Sightseeing_"+sign.getLine(1)+" "+event.getPlayer().getName());
		SwissSMPler.get(event.getPlayer()).sendTitle("", sign.getLine(1));
	}
}
