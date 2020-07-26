package ch.swisssmp.addonabnahme;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.EquipmentSlot;

import ch.swisssmp.city.City;
import ch.swisssmp.npc.NPCInstance;
import ch.swisssmp.npc.conversations.NPCConversation;
import ch.swisssmp.npc.event.PlayerInteractNPCEvent;
import ch.swisssmp.resourcepack.PlayerResourcePackUpdateEvent;
import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;

public class EventListener implements Listener {
	
	@EventHandler
	private void onResourcepackUpdate(PlayerResourcePackUpdateEvent event){
		event.addComponent("addons");
	}
	
	@EventHandler
	public void onSignChange(SignChangeEvent event){
		if(event.getBlock().getWorld()!=Bukkit.getWorlds().get(0)) return;
		Player player = event.getPlayer();
		String[] lines = event.getLines();
		if((!lines[0].toLowerCase().equals("[addonabnahme]") && !lines[0].toLowerCase().equals("[addon]")) || !player.hasPermission("addonabnahme.request")){
			return;
		}
		AddonInstanceInfo signInfo = AddonInstanceInfo.get(lines);
		Sign sign = (Sign) event.getBlock().getState();
		if(signInfo==null){
			City city = City.find(lines[1]);
			if(city==null){
				SwissSMPler.get(player).sendActionBar(ChatColor.RED+"Stadt "+lines[1]+" nicht gefunden.");
			}
			else{
				SwissSMPler.get(player).sendActionBar(ChatColor.RED+"Addon "+lines[2]+" nicht gefunden.");
			}
			return;
		}
		if(!signInfo.getCity().isCitizen(player.getUniqueId()) && !player.hasPermission("addonabnahme.admin")){
			SwissSMPler.get(player).sendActionBar(ChatColor.RED+"Du bist kein Bürger von "+signInfo.getCity().getName()+".");
			event.setCancelled(true);
			return;
		}
		HTTPRequest request = DataSource.getResponse(AddonAbnahme.getInstance(), "register_addon.php", new String[]{
				"player="+player.getUniqueId().toString(),
				"city="+signInfo.getCity().getId(),
				"addon="+URLEncoder.encode(signInfo.getAddonInfo().getAddonId()),
				"techtree="+URLEncoder.encode(signInfo.getCity().getTechtreeId()),
				"world="+URLEncoder.encode(event.getBlock().getWorld().getName()),
				"x="+event.getBlock().getX(),
				"y="+event.getBlock().getY(),
				"z="+event.getBlock().getZ()
		});
		request.onFinish(()->{
			AddonManager.createAddonGuide(player, sign, signInfo, request.getJsonResponse());
		});
	}
	
	@EventHandler(ignoreCancelled=true)
	private void onNPCInteract(PlayerInteractNPCEvent event){
		if(event.getHand()!=EquipmentSlot.HAND) return;
		NPCInstance npc = event.getNPC();
		String identifier = npc.getIdentifier();
		if(identifier==null || !identifier.equals("addon_instance_guide")) return;
		NPCConversation c = NPCConversation.get(event.getPlayer());
		if(c!=null && c.getNPC().getNPCId().equals(event.getNPC().getNPCId())) return;
		if(c!=null) c.cancel();
		Villager villager = (event.getNPC().getEntity() instanceof Villager ? (Villager) event.getNPC().getEntity() : null);
		if(villager!=null && villager.isTrading()) return;
		AddonInstanceInfo info = AddonInstanceInfo.get(npc);
		if(info==null){
			npc.remove(); //some auto cleanup
			return;
		}
		City city = info.getCity();
		if(event.getPlayer().isSneaking() && city.isCitizen(event.getPlayer().getUniqueId())){
			AddonInstanceGuide.openGuideView(event.getPlayer(), npc);
			return;
		}
		AddonInstanceGuide.startConversation(event.getPlayer(), npc);
	}
	
	@EventHandler(ignoreCancelled=true,priority=EventPriority.MONITOR)
	private void onChunkLoad(ChunkLoadEvent event){
		AddonInstanceGuides.updateAll(event.getChunk());
	}
}
