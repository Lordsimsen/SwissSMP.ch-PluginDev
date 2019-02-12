package ch.swisssmp.city;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.PlayerInfo;
import ch.swisssmp.webcore.HTTPRequest;

public class CraftingListener implements Listener {
	
	@EventHandler
	private void onPrepareCraftCitizenBill(PrepareItemCraftEvent event){
		InventoryView view = event.getView();
		if(!(view.getTopInventory() instanceof CraftingInventory)) return;
		CraftingInventory inventory = (CraftingInventory) view.getTopInventory();
		checkCitizenBillRecipe(view, inventory);
	}
	
	private void checkCitizenBillRecipe(InventoryView view, CraftingInventory inventory){
		ItemStack[] matrix = inventory.getMatrix();
		if(matrix.length<9) return;
		int[] emeraldSlots = new int[]{0,2,6,8};
		int[] paperSlots = new int[]{1,3,5,7};
		for(int i = 0; i < emeraldSlots.length; i++){
			int slot = emeraldSlots[i];
			if(matrix[slot]==null || matrix[slot].getType()!=Material.EMERALD) return;
		}
		for(int i = 0; i < paperSlots.length; i++){
			int slot = paperSlots[i];
			if(matrix[slot]==null || matrix[slot].getType()!=Material.PAPER) return;
		}
		ItemStack center = inventory.getMatrix()[4];
		City city = ItemManager.getCity((Player)view.getPlayer(), center);
		if(city==null){
			inventory.setResult(null);
			return;
		}
		ItemStack bill = ItemManager.createCitizenBill(new CitizenBillInfo(city));
		inventory.setResult(bill);
	}
	
	@EventHandler
	private void onPrepareAnvil(PrepareAnvilEvent event){
		ItemStack left = event.getInventory().getItem(0);
		if(left==null){
			return;
		}
		String city_tool = ItemUtil.getString(left, "city_tool");
		if(city_tool==null) return;
		if(city_tool.equals("citizen_bill")){
			onEditCitizenBill(event);
		}
	}
	
	private void onEditCitizenBill(PrepareAnvilEvent event){
		ItemStack left = event.getInventory().getItem(0);
		CitizenBillInfo billInfo = CitizenBillInfo.get(left);
		if(billInfo==null){
			event.setResult(null);
			return;
		}
		
		City city = billInfo.getCity();
		UUID player_uuid = event.getView().getPlayer().getUniqueId();
		if(!event.getView().getPlayer().hasPermission("citysystem.admin") && (!city.isCitizen(player_uuid) || (!city.isFounder(player_uuid) && !city.isMayor(player_uuid)))){
			event.setResult(null);
			return;
		}
		
		String renameText = event.getInventory().getRenameText();
		CitizenInfo citizenInfo = city.getCitizen(renameText);
		if(citizenInfo==null && billInfo.getCitizen()==null){
			//neither existing citizen nor existing assignment -> create new citizen bill
			Player player = Bukkit.getPlayer(renameText);
			if(player==null){
				event.setResult(null);
				return;
			}
			else if(!player.hasPermission("citysystem.join")){
				event.setResult(null);
				return;
			}
			billInfo.setCitizen(PlayerInfo.get(player));
			billInfo.setParent(PlayerInfo.get((Player)event.getView().getPlayer()));
		}
		else if(citizenInfo!=null && billInfo.getCitizen()==null){
			//existing citizen, no assignment -> recreate citizen bill
			billInfo.setCitizen(citizenInfo.getPlayerInfo());
			billInfo.setParent(city.getCitizen(citizenInfo.getParent()).getPlayerInfo());
			billInfo.setCitizenRole(citizenInfo.getRole());
			billInfo.setSignedByCitizen();
			billInfo.setSignedByParent();
		}
		else if(billInfo.getCitizen()!=null){
			//text does not belong to a citizen, but bill is linked to player -> assign role
			billInfo.setCitizenRole(renameText);
		}
		ItemStack result = event.getResult();
		billInfo.apply(result);
		event.setResult(result);
	}
	
	@EventHandler
	private void onCraftAnvil(InventoryClickEvent event){
		if(event.getView().getType()!=InventoryType.ANVIL) return;
		if(event.getClickedInventory()!=event.getView().getTopInventory()) return;
		AnvilInventory inventory = (AnvilInventory) event.getInventory();
		if(event.getSlotType()!=SlotType.RESULT){
			return;
		}
		ItemStack result = inventory.getItem(2);
		if(result==null){
			return;
		}
		CitizenBillInfo billInfo = CitizenBillInfo.get(result);
		if(billInfo==null){
			return;
		}
		if(billInfo.getCitizen()==null){
			event.setCancelled(true);
		}
		if(billInfo.isSignedByCitizen() && billInfo.isSignedByParent()){
			CitizenInfo citizen = billInfo.getCity().getCitizen(billInfo.getCitizen().getUniqueId());
			if(citizen==null){
				event.setCancelled(true);
				return;
			}
			HTTPRequest request = billInfo.getCity().setCitizenRole((Player) event.getView().getPlayer(), citizen.getUniqueId(), billInfo.getCitizenRole());
			if(request==null){
				event.setCancelled(true);
				return;
			}
			request.onFinish(()->{
				billInfo.setCitizenRole(citizen.getRole());
				billInfo.apply(result);
			});
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	private void onCraft(CraftItemEvent event){
		CraftingInventory inventory = event.getInventory();
		ItemStack result = inventory.getResult();
		if(result==null){
			System.out.print("Kein Resultat");
			return;
		}
		keepRingAfterCrafting(event);
	}
	
	private void keepRingAfterCrafting(CraftItemEvent event){
		CraftingInventory inventory = event.getInventory();
		ItemStack[] matrix = inventory.getMatrix();
		for(int i = 0; i < matrix.length; i++){
			ItemStack itemStack = matrix[i];
			if(itemStack==null || itemStack.getType()!=Material.DIAMOND_SWORD){
				continue;
			}
			String city_tool = ItemUtil.getString(itemStack, "city_tool");
			if(city_tool==null || !city_tool.equals("sigil_ring")){
				continue;
			}
			int index = i+1;
			ItemStack copy = itemStack.clone();
			Bukkit.getScheduler().runTaskLater(CitySystemPlugin.getInstance(), ()->{
				if(event.isCancelled()) return;
				inventory.setItem(index,copy);
			}, 1L);
		}
	}
}
