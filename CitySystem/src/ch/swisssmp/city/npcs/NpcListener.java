package ch.swisssmp.city.npcs;

import ch.swisssmp.city.*;
import ch.swisssmp.city.npcs.guides.AddonGuide;
import ch.swisssmp.utils.SwissSMPler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class NpcListener implements Listener {

    @EventHandler
    private void onNPCAttack(EntityDamageByEntityEvent event){
        PersistentDataContainer container = event.getEntity().getPersistentDataContainer();
        NamespacedKey typeKey = CitySystem.getNpcTypeKey();
        if(!container.has(typeKey, PersistentDataType.STRING)) return;
        String typeIdentifier = container.get(typeKey, PersistentDataType.STRING);
        NpcType type = NpcType.getByIdentifier(typeIdentifier);
        if(type==null){
            Bukkit.getLogger().warning(CitySystemPlugin.getPrefix()+" Unbekannter Npc-Typ '"+typeIdentifier+"'!");
            return;
        }

        event.setCancelled(true); // behaviour is always overwritten by this listener

        switch (type){
            case ADDON_GUIDE:{
                if(!(event.getDamager() instanceof Player)){
                    return;
                }

                AddonGuide guide = AddonGuide.get(event.getEntity()).orElse(null);
                if(guide==null){
                    Bukkit.getLogger().warning(CitySystemPlugin.getPrefix()+" AddonGuide konnte nicht richtig erkannt werden.");
                    return;
                }

                this.onAddonGuideAttack(event, guide);
                return;
            }
        }
    }

    private void onAddonGuideAttack(EntityDamageByEntityEvent event, AddonGuide guide){
        Addon addon = guide.getAddon();
        if(addon==null){
            guide.remove();
            return;
        }
        City city = addon.getCity();
        if(city==null){
            return;
        }

        Player player = (Player) event.getDamager();
        if(!city.isCitizen(player) && !player.hasPermission(CitySystemPermission.ADMIN)){
            SwissSMPler.get(player).sendActionBar(ChatColor.RED+"Nur Bürger von "+city.getName()+" können diesen Guide entfernen!");
            return;
        }
        guide.remove();
        SwissSMPler.get(player).sendActionBar(ChatColor.GREEN+"Guide entfernt.");
    }
}
