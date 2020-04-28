package ch.swisssmp.monuments;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.ArrayList;
import java.util.List;

public class EventListener implements Listener {

    @EventHandler(priority= EventPriority.HIGHEST)
    private void onEntityExplode(EntityExplodeEvent event){
        List<Block> protectedBlocks = new ArrayList<>();

        for(Block block : event.blockList())
        {
            if(MonumentEntries.blockInAnyMonumentArea(block)){
                protectedBlocks.add(block);
            }
        }
        event.blockList().removeAll(protectedBlocks);
    }
}
