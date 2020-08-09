package ch.swisssmp.weaver;

import ch.swisssmp.city.SigilRingInfo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.banner.Pattern;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;

public class EventListener implements Listener {

    @EventHandler
    private void onBannerPut(InventoryClickEvent event){
        Inventory inventory = event.getClickedInventory();
        if(!(inventory instanceof PlayerInventory)) return;
        if(!event.getSlotType().equals(InventoryType.SlotType.ARMOR)) return;

        Player player = (Player) event.getWhoClicked();
        if(!player.hasPermission("weaver.use")) return;
        ItemStack banner = event.getCursor();
        if(!CityBanners.isBanner(banner, player)) return;
        event.setCancelled(true);

        ItemStack helmet = ((PlayerInventory) inventory).getHelmet();
        ((PlayerInventory) inventory).setHelmet(banner);
        event.setCursor(helmet);
    }

    @EventHandler
    private void onBannerRightclickAir(PlayerInteractEvent event){
        if(event.getAction() != Action.RIGHT_CLICK_AIR) return;
        ItemStack banner = event.getItem();
        Player player = event.getPlayer();
        if(!player.hasPermission("weaver.use")) return;
        PlayerInventory inventory = player.getInventory();
        ItemStack helmet = inventory.getHelmet();
        if(helmet == null || helmet.getType().equals(Material.AIR)) {
            if (!CityBanners.isBanner(banner, player)) {
                Bukkit.getLogger().info("Not a citybanner");
                return;
            }
            inventory.remove(banner); //TODO removes all stacks equal to this... must be changed
            inventory.setHelmet(banner);
        }
        event.setCancelled(true);
    }

    @EventHandler
    private void onBannerRegister(PlayerInteractEvent event){
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        ItemStack ring = event.getItem();
        SigilRingInfo ringInfo = SigilRingInfo.get(ring);
        if(ringInfo == null) {
            return;
        }
        Player player = event.getPlayer();
        if(!ringInfo.getOwner().getUniqueId().equals(player.getUniqueId()) ||
                (!ringInfo.getCity().getMayor().equals(player.getUniqueId()) && !player.isOp())) {
            return;
        }
        if(!player.hasPermission("weaver.setbanner")) return;

        if(!(event.getClickedBlock().getState() instanceof Banner)) {
            return;
        }
        event.setCancelled(true);
        Bukkit.getLogger().info("PlayerInteract cancelled by bannerregister");
        Banner banner = (Banner) event.getClickedBlock().getState();
        List<Pattern> patterns = banner.getPatterns();

        CityBanners.registerBanner(patterns, ringInfo.getCity(), (success)->{
            if(success){
                CityBanners.addCityBanner(ringInfo.getCity().getUniqueId(), patterns);
                player.sendMessage(WeaverPlugin.getPrefix() + ChatColor.GREEN + " Banner als Stadtbanner registriert!");
            } else{
                player.sendMessage(WeaverPlugin.getPrefix() + ChatColor.RED + " Etwas ist schief gelaufen. Bitte kontaktiere die Spielleitung bei wiederholtem scheitern.");
            }
        });
    }
}
