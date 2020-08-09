package ch.swisssmp.city;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.SwissSMPler;

class PlayerInteractListener implements Listener {

	@EventHandler
	private void onPlayerInteract(PlayerInteractEvent event) {
		Bukkit.getScheduler().runTaskLater(CitySystemPlugin.getInstance(), ()-> {
			listen(event);
		}, 1);
	}

	private void listen(PlayerInteractEvent event){
		if(event.getAction()!=Action.RIGHT_CLICK_AIR && event.getAction()!=Action.RIGHT_CLICK_BLOCK) return;
		try {
			if (event.getClickedBlock().getType().isInteractable()) return;
		} catch (NullPointerException ignored){}
		ItemStack itemStack = event.getItem();
		if(itemStack==null) return;
		if(itemStack.getType()==Material.DIAMOND_SWORD){
			City city = ItemUtility.getCity(event.getPlayer(), itemStack);
			if(city==null) return;
			String city_tool = ItemUtil.getString(itemStack, "city_tool");
			switch(city_tool){
				case "sigil_ring":
					CityView.open(event.getPlayer(),city);
					break;
			}
		}
		else if(itemStack.getType()==Material.WOODEN_SWORD){
			PlayerInventory inv = event.getPlayer().getInventory();
			ItemStack other = event.getHand()==EquipmentSlot.HAND ? inv.getItemInOffHand() : inv.getItemInMainHand();
			if(other==null||other.getType()!=Material.FEATHER) return;
			int inkSlot = inv.first(Material.INK_SAC);
			if(inkSlot<0) return;
			CitizenBill billInfo = CitizenBill.get(itemStack);
			if(billInfo==null) return;
			if(billInfo.getPlayerData()==null || billInfo.getParent()==null) return;
			City city = billInfo.getCity();
			Player player = event.getPlayer();
			Player otherPlayer;
			if(!billInfo.isSignedByCitizen() && billInfo.getPlayerData().getUniqueId().equals(player.getUniqueId())){
				billInfo.setSignedByCitizen();
				otherPlayer = null;
			}
			else if(!billInfo.isSignedByCitizen() && billInfo.getParent().getUniqueId().equals(player.getUniqueId())){
				SwissSMPler.get(player).sendActionBar(ChatColor.YELLOW+billInfo.getPlayerData().getName()+" muss zuerst unterschreiben.");
				return;
			}
			else if(billInfo.isSignedByCitizen() && !billInfo.isSignedByParent() &&
					(billInfo.getParent().getUniqueId().equals(player.getUniqueId()) || player.hasPermission(CitySystemPermission.ADMIN))){
				otherPlayer = Bukkit.getPlayer(billInfo.getPlayerData().getUniqueId());
				if(otherPlayer==null){
					SwissSMPler.get(player).sendActionBar(ChatColor.RED+billInfo.getPlayerData().getName()+" muss anwesend sein.");
					return;
				}
				billInfo.setSignedByParent();
			}
			else{
				return;
			}
			ItemStack ink = inv.getItem(inkSlot);
			if(billInfo.isSignedByCitizen() && billInfo.isSignedByParent() && otherPlayer!=null){
				city.addCitizen(otherPlayer, player, billInfo.getRole(), (success)->{
					if(success){
						itemStack.setAmount(2);
						ink.setAmount(ink.getAmount()-1);
						billInfo.apply(itemStack);
						SwissSMPler.get(player).sendActionBar(ChatColor.GREEN+"Bürgerschein unterschrieben!");
					}
					else{
						SwissSMPler.get(player).sendActionBar(ChatColor.RED+"Konnte den Vorgang nicht abschliessen.");
					}
				});
			}
			else{
				ink.setAmount(ink.getAmount()-1);
				billInfo.apply(itemStack);
				SwissSMPler.get(player).sendActionBar(ChatColor.GREEN+"Bürgerschein unterschrieben!");
			}
		}
	}
}
