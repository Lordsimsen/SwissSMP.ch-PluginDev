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
import ch.swisssmp.webcore.HTTPRequest;

class PlayerInteractListener implements Listener {

	@EventHandler
	private void onPlayerInteract(PlayerInteractEvent event){
		if(event.getAction()!=Action.RIGHT_CLICK_AIR && event.getAction()!=Action.RIGHT_CLICK_BLOCK) return;
		ItemStack itemStack = event.getItem();
		if(itemStack==null) return;
		if(itemStack.getType()==Material.DIAMOND_SWORD){
			City city = ItemManager.getCity(event.getPlayer(), itemStack);
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
			CitizenBillInfo billInfo = CitizenBillInfo.get(itemStack);
			if(billInfo==null) return;
			if(billInfo.getCitizen()==null || billInfo.getParent()==null) return;
			Player player = event.getPlayer();
			Player otherPlayer;
			if(!billInfo.isSignedByCitizen() && billInfo.getCitizen().getUniqueId().equals(player.getUniqueId())){
				billInfo.setSignedByCitizen();
				otherPlayer = null;
			}
			else if(!billInfo.isSignedByCitizen() && billInfo.getParent().getUniqueId().equals(player.getUniqueId())){
				SwissSMPler.get(player).sendActionBar(ChatColor.YELLOW+billInfo.getCitizen().getName()+" muss zuerst unterschreiben.");
				return;
			}
			else if(billInfo.isSignedByCitizen() && !billInfo.isSignedByParent() && 
					(billInfo.getParent().getUniqueId().equals(player.getUniqueId()) || player.hasPermission("citysystem.admin"))){
				otherPlayer = Bukkit.getPlayer(billInfo.getCitizen().getUniqueId());
				if(otherPlayer==null){
					SwissSMPler.get(player).sendActionBar(ChatColor.RED+billInfo.getCitizen().getName()+" muss anwesend sein.");
					return;
				}
				billInfo.setSignedByParent();
			}
			else{
				return;
			}
			ItemStack ink = inv.getItem(inkSlot);
			if(billInfo.isSignedByCitizen() && billInfo.isSignedByParent() && otherPlayer!=null){
				HTTPRequest request = billInfo.getCity().addCitizen(otherPlayer, player, billInfo.getCitizenRole());
				if(request==null){
					return;
				}
				request.onFinish(()->{
					if(billInfo.getCity().isCitizen(billInfo.getCitizen().getUniqueId())){
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
