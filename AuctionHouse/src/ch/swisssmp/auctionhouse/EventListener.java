package ch.swisssmp.auctionhouse;

import java.util.List;

import ch.swisssmp.addonabnahme.AddonInstanceGuide;
import ch.swisssmp.addonabnahme.AddonInstanceInfo;
import ch.swisssmp.addonabnahme.AddonManager;
import ch.swisssmp.npc.NPCInstance;
import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.webcore.HTTPRequest;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class EventListener implements Listener{
	@EventHandler(ignoreCancelled=true)
	public void onSignChange(SignChangeEvent event){
		if(event.getBlock().getWorld()!= Bukkit.getWorlds().get(0)) return;
		Player player = event.getPlayer();
		String[] lines = event.getLines(); //get sign message

		//check if first line on sign equals [auktion] and player has "auktion" permissions else do nothing.
		if((!lines[0].toLowerCase().equals("[auktion]")  || !player.hasPermission("auction.admin"))) {
			return;
		}

		AddonInstanceInfo signInfo = AddonInstanceInfo.get(lines);
		Sign sign = (Sign) event.getBlock().getState();

		String addon = lines[1];
		SwissSMPler.get(player).sendActionBar(addon);
		Location location = sign.getLocation();
		NPCInstance npc = NPCInstance.create(EntityType.VILLAGER, location);

		// check jsondata first and override with new json object
		npc.setJsonData();


//		HTTPRequest request = DataSource.getResponse(AuctionHouse.getInstance(), "auction/sign.php", new String[]{
//				"player="+player.getUniqueId(),
//				"addon="+URLEncoder.encode(addon),
//				"world="+URLEncoder.encode(player.getWorld().getName()),
//				"x="+(int)Math.round(player.getLocation().getX()),
//				"y="+(int)Math.round(player.getLocation().getY()),
//				"z="+(int)Math.round(player.getLocation().getZ()),
//		});
//
//		AddonManager.createAddonGuide(player, sign, signInfo, request.getYamlResponse());
//		request.onFinish(()->{
//			YamlConfiguration yamlConfiguration = request.getYamlResponse();
//			if(yamlConfiguration==null) return;
//			if(yamlConfiguration.contains("message")){
//				player.sendMessage(yamlConfiguration.getString("message"));
//			}
//			if(yamlConfiguration.contains("lines")){
//				List<String> linesSection = yamlConfiguration.getStringList("lines");
//				for(int i = 0; i < linesSection.size() && i < 4; i++){
//					event.setLine(i,linesSection.get(i));
//				}
//			}
//		});
	}
	@EventHandler(ignoreCancelled=true)
	private void onSignInteract(PlayerInteractEvent event){
		if(event.getAction()!=Action.RIGHT_CLICK_BLOCK) return;
		Block block = event.getClickedBlock();
		BlockState state = block.getState();
		if(!(state instanceof Sign)) return;
		Sign sign = (Sign)block.getState();
		if(!sign.getLine(1).equals("§5[Auktion]")) return;
		String addon = sign.getLine(2);
		ItemStack itemStack = event.getItem();
		if(itemStack!=null && itemStack.getType()==Material.DIAMOND){
			AuctionHouse.bid(event.getPlayer(), addon, itemStack);
		}
		else{
			AuctionHouse.info(event.getPlayer(), addon);
		}
	}
}
