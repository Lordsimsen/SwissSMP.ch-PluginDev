package ch.swisssmp.netherportals.configuration.slots;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.ButtonSlot;
import ch.swisssmp.netherportals.WorldConfiguration;
import ch.swisssmp.utils.SwissSMPler;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Orientable;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

import java.util.List;

public class TrySlot extends ButtonSlot {

    private final WorldConfiguration configuration;

    public TrySlot(CustomEditorView view, int slot, WorldConfiguration configuration) {
        super(view, slot);
        this.configuration = configuration;
    }

    @Override
    protected void triggerOnClick(ClickType clickType) {
        this.getView().closeLater(this::tryConfiguration);
    }

    private void tryConfiguration(){
        Player player = getView().getPlayer();
        if(!configuration.isSetupComplete()){
            SwissSMPler.get(player).sendActionBar(ChatColor.RED+"Einstellungen unvollständig.");
            return;
        }
        Location location = configuration.createToLocation(player.getLocation()).add(0.5,0.1,0.5);
        if(location==null){
            SwissSMPler.get(player).sendActionBar(ChatColor.RED+"Einstellungen ungültig.");
            return;
        }

        Block targetBlock = location.getBlock();
        BlockData data = targetBlock.getBlockData();
        if(data instanceof Orientable){
            Axis axis = ((Orientable) data).getAxis();
            location = location.add(axis==Axis.X ? BlockFace.NORTH.getDirection() : BlockFace.EAST.getDirection());
        }
        player.teleport(location, PlayerTeleportEvent.TeleportCause.NETHER_PORTAL);
    }

    @Override
    protected boolean isComplete() {
        return configuration.isSetupComplete();
    }

    @Override
    public String getName() {
        return ChatColor.AQUA+"Testen";
    }

    @Override
    protected List<String> getNormalDescription() {
        return null;
    }

    @Override
    protected CustomItemBuilder createSlotBase() {
        return new CustomItemBuilder(Material.FLINT_AND_STEEL);
    }
}
