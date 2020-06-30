package ch.swisssmp.warps;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.ButtonSlot;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.Arrays;
import java.util.List;


public class WarpSlot extends ButtonSlot {

    private final CustomEditorView view;
    private final Player player;
    private final WarpPoint warp;
    private final World warpWorld;

    public WarpSlot(CustomEditorView view, WarpPoint warp, int slot) {
        super(view, slot);
        this.view = view;
        this.player = view.getPlayer();
        this.warp = warp;
        this.warpWorld = warp.getWorld();
    }

    @Override
    protected void triggerOnClick(ClickType clickType) {
        if(clickType != ClickType.LEFT) return;
        player.teleport(warp.getWarpLocation());
    }

    @Override
    protected boolean isComplete() {
        return true;
    }

    @Override
    public String getName() {
        return ChatColor.AQUA + warp.getName() + ChatColor.DARK_AQUA + " (" + warpWorld.getName() + ")";
    }

    @Override
    protected List<String> getNormalDescription() {
        return Arrays.asList("Klicke um zu warpen!");
    }


    @Override
    protected CustomItemBuilder createSlotBase() {
        CustomItemBuilder builder;
        World world = player.getWorld();
        if(warpWorld.equals(world)){
            builder = CustomItems.getCustomItemBuilder("MARKER_RED");
        } else{
            builder = CustomItems.getCustomItemBuilder("WORLD_OVERWORLD");
        }
        builder.setDisplayName(ChatColor.AQUA + warp.getName() + ChatColor.DARK_AQUA + " (" + warpWorld.getName() + ")");

        return builder;
    }
}
