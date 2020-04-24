package ch.swisssmp.auctionhouse;

import java.util.List;

import ch.swisssmp.webcore.HTTPRequest;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
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
	private void onSignPlace(SignChangeEvent event){
		Player player = event.getPlayer();
		String[] lines = event.getLines();
		String addon;
		if(lines[0].toLowerCase().equals("[auktion]")){
			addon = lines[1];
		}
		else if(lines[1].toLowerCase().equals("[auktion]")){
			addon = lines[2];
		}
		else return;
		HTTPRequest request = DataSource.getResponse(AuctionHouse.getInstance(), "auction/sign.php", new String[]{
				"player="+player.getUniqueId(),
				"addon="+URLEncoder.encode(addon),
				"world="+URLEncoder.encode(player.getWorld().getName()),
				"x="+(int)Math.round(player.getLocation().getX()),
				"y="+(int)Math.round(player.getLocation().getY()),
				"z="+(int)Math.round(player.getLocation().getZ()),
		});
		request.onFinish(()->{
			YamlConfiguration yamlConfiguration = request.getYamlResponse();
			if(yamlConfiguration==null) return;
			if(yamlConfiguration.contains("message")){
				player.sendMessage(yamlConfiguration.getString("message"));
			}
			if(yamlConfiguration.contains("lines")){
				List<String> linesSection = yamlConfiguration.getStringList("lines");
				for(int i = 0; i < linesSection.size() && i < 4; i++){
					event.setLine(i,linesSection.get(i));
				}
			}
		});
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
