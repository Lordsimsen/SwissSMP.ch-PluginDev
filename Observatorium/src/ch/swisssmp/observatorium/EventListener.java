package ch.swisssmp.observatorium;

import ch.swisssmp.city.Cities;
import ch.swisssmp.city.City;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class EventListener implements Listener {

    @EventHandler
    private void onItemPlace(PlayerInteractEntityEvent event){
        if(!(event.getRightClicked() instanceof ItemFrame)) return;
        if(!event.getPlayer().hasPermission("observatorium.use")) return;
        Player player = event.getPlayer();
        Location location = BukkitAdapter.adapt(event.getRightClicked().getLocation());
        if(WeatherMachine.isWeatherMachine(location)){

            ItemFrame itemFrame = (ItemFrame) event.getRightClicked();
            ItemStack weatherItem = itemFrame.getItem();
//            WeatherMachine.setWeather(weatherItem, city);
        }
    }
}
