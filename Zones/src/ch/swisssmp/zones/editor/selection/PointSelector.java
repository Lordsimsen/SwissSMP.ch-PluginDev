package ch.swisssmp.zones.editor.selection;

import ch.swisssmp.zones.util.Edge;
import org.bukkit.block.Block;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

public interface PointSelector {

    void initialize();

    /**
     * Interact with a block and return whether the interaction changed anything
     * @param block - The interacted block
     * @param click - The type of the click
     * @return Whether the selection changed
     */
    boolean click(Block block, ClickType click);

    /**
     * Try to apply the selection and return whether the action was successful
     * @return Whether the selection was applied
     */
    boolean apply();

    /**
     * Get the current selection
     */
    List<Edge> getEdges();

    /**
     * The current state of the selection as evaluated by the selector
     */
    PointSelectionState getState();

    String getInstructions();
}
