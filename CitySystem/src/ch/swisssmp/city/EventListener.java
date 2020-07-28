package ch.swisssmp.city;

import java.util.List;
import java.util.UUID;

import ch.swisssmp.city.ceremony.promotion.CityPromotionCeremony;
import ch.swisssmp.city.ceremony.promotion.HayPile;
import ch.swisssmp.city.ceremony.promotion.PromotionCeremonyData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import ch.swisssmp.city.ceremony.founding.CityFoundingCeremony;
import ch.swisssmp.resourcepack.PlayerResourcePackUpdateEvent;
import ch.swisssmp.utils.BlockUtil;
import ch.swisssmp.utils.PlayerInfo;
import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.HTTPRequest;
import org.bukkit.scheduler.BukkitTask;

class EventListener implements Listener {
	private static final Material INITIATOR_MATERIAL = Material.BLAZE_POWDER;
	private static boolean ceremonyAnnounced = false;
	
	@EventHandler
	private void onResourcepackUpdate(PlayerResourcePackUpdateEvent event){
		event.addComponent("citysystem");
	}
	
	@EventHandler
	private void onPlayerJoin(PlayerJoinEvent event){
		ItemManager.updateItems(event.getPlayer().getInventory());
	}
	
	@EventHandler
	private void onOpenInventory(InventoryOpenEvent event){
		ItemManager.updateItems(event.getInventory());
	}
	
	@EventHandler
	private void onResourcepackUpdated(PlayerResourcePackStatusEvent event){
		if(event.getStatus()!=Status.SUCCESSFULLY_LOADED) return;
		Player player = event.getPlayer();
		player.getWorld().playSound(player.getLocation(), "founding_ceremony_drums", SoundCategory.RECORDS, 0.01f, 1);
		Bukkit.getScheduler().runTaskLater(CitySystemPlugin.getInstance(), ()->{
			player.stopSound("founding_ceremony_drums", SoundCategory.RECORDS);
		}, 20);
	}

	@EventHandler
	private void onTributeAnnounce(PlayerInteractEvent event){
		if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		Player player = event.getPlayer();
		if(!player.isOp()) return; //Todo remove when done
		SigilRingInfo info = SigilRingInfo.get(event.getItem());
//        if(info == null) return;
//        if(!info.getOwner().getUniqueId().equals(player.getUniqueId())) {
//            SwissSMPler.get(player).sendActionBar(ChatColor.RED + "Du musst deinen eigenen Siegelring benutzen");
//            return;
//        }
//        if(!info.getCitizenRrank().equals(CitizenRank.MAYOR)) {
//            SwissSMPler.get(player).sendActionBar(ChatColor.RED + "Nur der Bürgermeister kann eine Zeremonie initieren!");
//            return;
//        }
		Block block = event.getClickedBlock();
		if(!(block.getState() instanceof org.bukkit.block.Chest)) {
			return;
		}

		City city = info.getCity();
		if(city == null){
			Bukkit.getLogger().info(CitySystemPlugin.getPrefix() + " Couldn't load city from SigilRingInfo from " + info.getOwner());
			return;
		}
		PromotionCeremonyData data = PromotionCeremonyData.load(city);
		if(data == null){
			Bukkit.getLogger().info(CitySystemPlugin.getPrefix() + " Couldn't load Promotion ceremony data for city: " + city.getName());
			return;
		}

		/**
		 * Checks whether the haypile below the Chest is of adequate size.
		 */
		if(!HayPile.checkSize(block, CityPromotionCeremony.baseMaterial, data.getPromotionHaybalecount())) {
			return;
		}

		/**
		 * Checks whether at least the required tribute is present in the tributechest.
		 */
		Inventory inventory = ((Chest) block.getState()).getBlockInventory();
		for(ItemStack required : data.getTribute()){
			int proposedAmount = 0;
			for(ItemStack proposed : inventory.getContents()){
				if(required == null || required.getType() == Material.AIR) continue;
				if(proposed.getType() != required.getType()) continue;
				proposedAmount += proposed.getAmount();
			}
			if(proposedAmount < required.getAmount()){
				SwissSMPler.get(player).sendActionBar(ChatColor.RED + "Du wagst es mit ungenügenden Opfergaben zu versuchen?");
				player.getWorld().strikeLightningEffect(player.getLocation());
				return;
			}
		}

		event.setCancelled(true);

		long time = player.getWorld().getTime();
		if(time > 12000){
			SwissSMPler.get(player).sendActionBar(ChatColor.YELLOW + "Heute kannst du keine Zeremonie mehr starten.");
			return;
		}
		if(!ceremonyAnnounced) {
			SwissSMPler.get(player).sendActionBar(ChatColor.GREEN + "Versammle deine Bürger vor Sonnenuntergang am Festplatz!");
			SwissSMPler.get(player).sendMessage(CitySystemPlugin.getPrefix() + ChatColor.GREEN + "Die Zeremonie beginnt bei Sonnenuntergang!");

			Bukkit.getScheduler().runTaskLater(CitySystemPlugin.getInstance(), () -> {
				CityPromotionCeremony ceremony = CityPromotionCeremony.start(block, player, city, data);
				Bukkit.getPluginManager().registerEvents(ceremony, CitySystemPlugin.getInstance());
			}, (100)); //Todo replace with (12000-time)
			ceremonyAnnounced = true;
		} else{
			SwissSMPler.get(player).sendActionBar(ChatColor.GREEN + "Die Zeremonie beginnt bei Sonnenuntergang!");
		}
	}

	
	@EventHandler
	private void onItemDrop(PlayerDropItemEvent event){
		if(event.getItemDrop().getItemStack().getType()==INITIATOR_MATERIAL){
			onDropInitiatorMaterial(event);
			return;
		}
		CitizenBillInfo billInfo = CitizenBillInfo.get(event.getItemDrop().getItemStack());
		if(billInfo!=null && billInfo.getCitizen()!=null){
			onDropCitizenBill(event, billInfo);
		}
	}
	
	private void onDropInitiatorMaterial(PlayerDropItemEvent event){
		if(!event.getPlayer().hasPermission("citysystem.found")) return;
		if(event.getPlayer().getWorld()!=Bukkit.getWorlds().get(0)) return;
		event.getItemDrop().setMetadata("player", new FixedMetadataValue(CitySystemPlugin.getInstance(),event.getPlayer().getUniqueId()));
	}
	
	private void onDropCitizenBill(PlayerDropItemEvent event, CitizenBillInfo billInfo){
		City city = billInfo.getCity();
		if(!city.isCitizen(event.getPlayer().getUniqueId())) return;
		boolean isFounder = city.isFounder(event.getPlayer().getUniqueId());
		boolean isMayor = city.isMayor(event.getPlayer().getUniqueId());
		boolean isOwner = billInfo.getCitizen().getUniqueId().equals(event.getPlayer().getUniqueId());
		if(!isFounder && !isMayor && !isOwner) return;
		event.getItemDrop().setMetadata("player", new FixedMetadataValue(CitySystemPlugin.getInstance(),event.getPlayer().getUniqueId()));
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	private void onItemDestroy(EntityDamageEvent event){
		if(event.getEntityType()!=EntityType.DROPPED_ITEM) return;
		Player responsible = getThrower((Item) event.getEntity());
		if(responsible==null) return;
		Item item = (Item)event.getEntity();
		ItemStack itemStack = item.getItemStack();
		if(itemStack.getType()==INITIATOR_MATERIAL){
			Block block = BlockUtil.getClosest(event.getEntity().getLocation(), 2, (current)->current.getType()==Material.FIRE);
			CityFoundingCeremony.start(block, responsible);
			item.remove();
			return;
		}
		CitizenBillInfo billInfo = CitizenBillInfo.get(itemStack);
		if(billInfo!=null && billInfo.getCitizen()!=null && billInfo.isSignedByCitizen() && billInfo.isSignedByParent()){
			handleCitizenBillDestruction(responsible, billInfo);
			item.remove();
		}
	}
	
	private Player getThrower(Item item){
		if(!item.hasMetadata("player")) return null;
		List<MetadataValue> values = item.getMetadata("player");
		UUID player_uuid = null;
		for(MetadataValue value : values){
			if(value.getOwningPlugin()!=CitySystemPlugin.getInstance()) continue;
			try{
				player_uuid = UUID.fromString(value.asString());
			}
			catch(Exception e){
				e.printStackTrace();
				continue;
			}
		}
		return Bukkit.getPlayer(player_uuid);
	}
	
	private void handleCitizenBillDestruction(Player responsible, CitizenBillInfo billInfo){
		City city = billInfo.getCity();
		if(city==null) return;
		PlayerInfo citizen = billInfo.getCitizen();
		UUID citizen_uuid = citizen.getUniqueId();
		if(city.isMayor(citizen_uuid)||city.isFounder(citizen_uuid)){
			if(!responsible.getUniqueId().equals(citizen_uuid) && !city.isMayor(responsible.getUniqueId()) && !responsible.hasPermission("citysystem.admin")){
				SwissSMPler.get(responsible).sendActionBar(ChatColor.RED+"Du kannst "+citizen.getDisplayName()+" nicht aus der Stadt entfernen.");
				return;
			}
			else if(city.isMayor(citizen_uuid) && city.getCitizens().size()>1){
				responsible.sendMessage(CitySystemPlugin.getPrefix()+ChatColor.RED+"Trete dein Amt als Bürgermeister ab, bevor du "+city.getName()+" verlässt.");
				return;
			}
		}
		HTTPRequest request = billInfo.getCity().removeCitizen(responsible, billInfo.getCitizen().getUniqueId());
		if(request!=null) request.onFinish(()->{
			sendRemoveCitizenResponse(billInfo, responsible, request.getYamlResponse());
		});
	}
	
	private void sendRemoveCitizenResponse(CitizenBillInfo billInfo, Player player, YamlConfiguration response){
		boolean isOwner = player.getUniqueId().equals(billInfo.getCitizen().getUniqueId());
		String name = billInfo.getCitizen().getName();
		if(response!=null && response.contains("result") && response.getString("result").equals("success")){
			if(isOwner){
				player.sendMessage(CitySystemPlugin.getPrefix()+ChatColor.GRAY+billInfo.getCity().getName()+" verlassen.");
			}
			else{
				player.sendMessage(CitySystemPlugin.getPrefix()+ChatColor.GRAY+"Du hast "+name+" aus der Bürgerliste von "+billInfo.getCity().getName()+" entfernt.");
			}
		}
		else{
			String result = response!=null && response.contains("result") ? response.getString("result") : "error";
			if(result.equals("not_citizen")) return;
			if(isOwner){
				player.sendMessage(CitySystemPlugin.getPrefix()+ChatColor.RED+"Austritt fehlgeschlagen.");
			}
			else{
				player.sendMessage(CitySystemPlugin.getPrefix()+ChatColor.RED+"Konnte "+name+" nicht entfernen.");
			}
		}
	}
}
