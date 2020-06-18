package ch.swisssmp.customportals;

import ch.swisssmp.customportals.editor.*;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.DeleteSlot;
import ch.swisssmp.editor.slot.EditorSlot;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.ArrayList;
import java.util.Collection;

public class CustomPortalEditorView extends CustomEditorView {

    private final CustomPortal portal;

    protected CustomPortalEditorView(Player player, CustomPortal portal) {
        super(player);
        this.portal = portal;
    }

    @Override
    protected int getInventorySize() {
        return 18;
    }

    @Override
    protected Collection<EditorSlot> initializeEditor() {
        Collection<EditorSlot> slots = new ArrayList<>();
        slots.add(new TriggerRegionSlot(this, 0, portal));
        slots.add(new FromPositionSlot(this, 1, portal));
        slots.add(new ToPositionSlot(this, 2, portal));
        slots.add(new TravelPermissionSlot(this, 3, portal));
        slots.add(new GameModeSlot(this, 4, portal));
        slots.add(new UseRelativeTeleportationSlot(this, 5, portal));
        slots.add(new KeepVelocitySlot(this, 6, portal));
        slots.add(new TravelSoundSlot(this, 7, portal));
        slots.add(new PortalActiveSlot(this, 8, portal));
        slots.add(new DeleteSlot(this, 17, portal, "Portal").onRemove(()->portal.getContainer().save()));
        return slots;
    }

    @Override
    public String getTitle() {
        return portal.getName();
    }

    public static CustomPortalEditorView open(Player player, CustomPortal portal){
        CustomPortalEditorView view = new CustomPortalEditorView(player, portal);
        view.open();
        return view;
    }
}
