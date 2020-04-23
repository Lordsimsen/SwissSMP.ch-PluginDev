package ch.swisssmp.knightstournament;

import java.util.List;
import java.util.Optional;

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
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.customitems.CreateCustomItemBuilderEvent;
import ch.swisssmp.resourcepack.PlayerResourcePackUpdateEvent;
import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.utils.nbt.NBTTagCompound;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;
import ch.swisssmp.webcore.RequestMethod;

public class EventListener implements Listener {
	
	@EventHandler
	private void onPlayerResourepackUpdate(PlayerResourcePackUpdateEvent event) {
		event.addComponent("knightstournament");
	}

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
		Optional<KnightsArena> arenaQuery = KnightsArena.get(block.getWorld(), arenaName);
		if(!arenaQuery.isPresent()){
			SwissSMPler.get(event.getPlayer()).sendActionBar("§cArena aktuell inaktiv.");
			return;
		}
		KnightsArena arena = arenaQuery.get();
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
	
	@EventHandler
	private void onPlayerInteract(PlayerInteractEvent e) {
		if(e.getItem() == null) {
			return;
		}
		if((e.getAction() != Action.RIGHT_CLICK_AIR) && (e.getAction() != Action.RIGHT_CLICK_BLOCK)) {
			return;
		}
		if(!e.getPlayer().hasPermission("knightstournament.admin")) {
			return;
		}
		KnightsArena arena = KnightsArena.get(e.getItem());
		if(arena == null) {
			return;
		}
		arena.openEditor(e.getPlayer());
	}
	
	@EventHandler
	private void onItemBuilderCreate(CreateCustomItemBuilderEvent event) {
		ConfigurationSection dataSection = event.getConfigurationSection();
		if(!dataSection.contains("tournament_lance")) return;
		ConfigurationSection lanceSection = dataSection.getConfigurationSection("tournament_lance");
		
		LanceColor primary = LanceColor.of(lanceSection.getString("primary_color"));
		LanceColor secondary = LanceColor.of(lanceSection.getString("secondary_color"));		
				
		event.getCustomItemBuilder().addComponent((ItemStack itemStack) -> {
			NBTTagCompound nbt = ItemUtil.getData(itemStack);
			if(nbt==null) nbt = new NBTTagCompound();
			NBTTagCompound lanceNBT = new NBTTagCompound();
			lanceNBT.setString(TournamentLance.primaryColorProperty, primary != null ? primary.toString() : null);
			lanceNBT.setString(TournamentLance.secondaryColorProperty, secondary != null ? secondary.toString() : null);
			nbt.set(TournamentLance.dataProperty, lanceNBT);
			ItemUtil.setData(itemStack, nbt);
		});
	}
	
	@EventHandler
	private void onWorldLoad(WorldLoadEvent event) {
		KnightsArena.load(event.getWorld());
	}
	
	@EventHandler
	private void onWorldUnload(WorldUnloadEvent event) {
		KnightsArena.unload(event.getWorld());
	}
}
