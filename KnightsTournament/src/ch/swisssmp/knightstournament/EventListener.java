package ch.swisssmp.knightstournament;

import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;
import ch.swisssmp.webcore.RequestMethod;

public class EventListener implements Listener {

	@EventHandler
	private void onCraft(PrepareItemCraftEvent event){
		ItemStack result = event.getInventory().getResult();
		if(result==null) return;
		if(!result.hasItemMeta()) return;
		if(!result.getItemMeta().hasDisplayName()) return;
		if(!result.getItemMeta().getDisplayName().equals("§bTurnierlanze")) return;
		if(!(event.getView().getPlayer() instanceof Player)) return;
		Player player = (Player) event.getView().getPlayer();
		if(!player.hasPermission("knightstournament.craft")){
			event.getInventory().setResult(null);
		}
	}

	@EventHandler(ignoreCancelled=true)
	private void onSignPlace(SignChangeEvent event){
		Player player = event.getPlayer();
		String[] lines = event.getLines();
		if(!lines[0].toLowerCase().equals("[ritterspiele]")){
			return;
		}
		if(!player.hasPermission("knightstournament.admin")){
			return;
		}
		HTTPRequest request = DataSource.getResponse(KnightsTournamentPlugin.getInstance(), "sign.php", new String[]{
				"arena="+URLEncoder.encode(lines[1]),
				"action="+URLEncoder.encode(lines[2])
		}, RequestMethod.POST_SYNC);
		YamlConfiguration yamlConfiguration = new YamlConfiguration();
		try{
			yamlConfiguration.loadFromString(request.getResponse());
			if(yamlConfiguration.contains("message")){
				player.sendMessage(yamlConfiguration.getString("message"));
			}
			if(yamlConfiguration.contains("lines")){
				List<String> linesSection = yamlConfiguration.getStringList("lines");
				for(int i = 0; i < linesSection.size() && i < 4; i++){
					event.setLine(i,linesSection.get(i));
				}
			}
		}
		catch(Exception e){
			System.out.print(e);
		}
	}
	
	@EventHandler(ignoreCancelled=true)
	private void onSignInteract(PlayerInteractEvent event){
		if(event.getAction()!=Action.RIGHT_CLICK_BLOCK) return;
		Block block = event.getClickedBlock();
		BlockState state = block.getState();
		if(!(state instanceof Sign)) return;
		Sign sign = (Sign)block.getState();
		if(!sign.getLine(0).equals("§4Ritterspiele")){
			return;
		}
		String arenaName = sign.getLine(1);
		KnightsArena arena = KnightsArena.get(arenaName);
		if(arena==null){
			SwissSMPler.get(event.getPlayer()).sendActionBar("§cArena aktuell inaktiv.");
			return;
		}
		Tournament tournament = arena.getTournament();
		if(tournament==null){
			if(sign.getLine(2).equals("Turnier öffnen")){
				if(event.getPlayer().hasPermission("knightstournament.host")){
					Tournament.initialize(arena, event.getPlayer());
				}
				else{
					SwissSMPler.get(event.getPlayer()).sendActionBar("§cKeine Berechtigung.");
				}
			}
			else{
				SwissSMPler.get(event.getPlayer()).sendActionBar("§cKein laufendes Turnier.");
				return;
			}
		}
		if(sign.getLine(2).equals("Teilnehmen") && event.getPlayer().hasPermission("knightstournament.participate")){
			tournament.join(event.getPlayer());
		}
		else if(sign.getLine(2).equals("Verlassen")){
			tournament.leave(event.getPlayer());
		}
		else if(sign.getLine(2).equals("Turnier starten")){
			if(tournament.getMaster().getUniqueId()!=event.getPlayer().getUniqueId()){
				SwissSMPler.get(event.getPlayer()).sendActionBar("§cKeine Berechtigung.");
				return;
			}
			tournament.start();
		}
	}
}
