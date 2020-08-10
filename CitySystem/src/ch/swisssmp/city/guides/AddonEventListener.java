package ch.swisssmp.city.guides;

import ch.swisssmp.city.*;
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

import ch.swisssmp.npc.NPCInstance;
import ch.swisssmp.npc.conversations.NPCConversation;
import ch.swisssmp.npc.event.PlayerInteractNPCEvent;
import ch.swisssmp.resourcepack.PlayerResourcePackUpdateEvent;
import ch.swisssmp.utils.SwissSMPler;

public class AddonEventListener implements Listener {
	
	@EventHandler
	public void onSignChange(SignChangeEvent event){
		if(event.getBlock().getWorld()!=Bukkit.getWorlds().get(0)) return;
		Player player = event.getPlayer();
		String[] lines = event.getLines();
		if((!lines[0].toLowerCase().equals("[addonabnahme]") && !lines[0].toLowerCase().equals("[addon]")) || !player.hasPermission("addonabnahme.request")){
			return;
		}
		Addon addon = CitySystem.findAddon(lines, true).orElse(null);
		Sign sign = (Sign) event.getBlock().getState();
		City city;
		if(addon!=null){
			city = addon.getCity();
		}
		else{
			city = CitySystem.findCity(lines[1]).orElse(null);
			if(city==null){
				SwissSMPler.get(player).sendActionBar(ChatColor.RED+"Stadt "+lines[1]+" nicht gefunden.");
				return;
			}
			Techtree techtree = city.getTechtree();
			AddonType type = techtree!=null ? techtree.findAddonType(lines[2]).orElse(null) : null;
			if(type==null){
				SwissSMPler.get(player).sendActionBar(ChatColor.RED+"Addon "+lines[2]+" nicht gefunden.");
				return;
			}

			addon = city.createAddon(type);
		}
		if(!city.isCitizen(player.getUniqueId()) && !player.hasPermission("addonabnahme.admin")){
			SwissSMPler.get(player).sendActionBar(ChatColor.RED+"Du bist kein Bürger von "+city.getName()+".");
			event.setCancelled(true);
			return;
		}

		AddonGuide oldGuide = AddonGuides.findGuide(addon).orElse(null);
		addon.setOrigin(event.getBlock());
		Techtree techtree = city.getTechtree();
		techtree.updateAddonState(addon);
		Addon finalAddon = addon;
		addon.save((success)->{
			if(oldGuide!=null) oldGuide.remove();
			CitySystem.createAddonGuide(player, sign, finalAddon);
		});
	}
	
	@EventHandler(ignoreCancelled=true)
	private void onNPCInteract(PlayerInteractNPCEvent event){
		if(event.getHand()!=EquipmentSlot.HAND) return;
		NPCInstance npc = event.getNPC();
		// Check if it is an AddonGuide
		AddonGuide guide = AddonGuide.get(npc).orElse(null);
		if(guide==null) return;
		// Check if there is an ongoing conversation between the player and the npc
		NPCConversation runningConversation = NPCConversation.get(event.getPlayer());
		if(runningConversation!=null && runningConversation.getNPC().getNPCId().equals(event.getNPC().getNPCId())) return;
		// Cancel if the ongoing conversation is not between the player and the npc
		if(runningConversation!=null) runningConversation.cancel();
		// Check if the villager is already trading to prevent two players from unlocking the same addon at the same time
		Villager villager = (event.getNPC().getEntity() instanceof Villager ? (Villager) event.getNPC().getEntity() : null);
		if(villager!=null && villager.isTrading()) return;
		Addon addon = guide.getAddon();
		City city = addon.getCity();
		if(event.getPlayer().isSneaking() && city.isCitizen(event.getPlayer().getUniqueId())){
			guide.openGuideView(event.getPlayer());
			return;
		}
		guide.startConversation(event.getPlayer());
	}
	
	@EventHandler(ignoreCancelled=true,priority=EventPriority.MONITOR)
	private void onChunkLoad(ChunkLoadEvent event){
		AddonGuides.updateAll(event.getChunk());
	}
}
