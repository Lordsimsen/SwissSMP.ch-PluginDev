package ch.swisssmp.zones.drawingboard;

import java.util.Collection;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.EditorSlot;
import ch.swisssmp.zones.ZoneEditorView;
import ch.swisssmp.zones.zoneinfos.ZoneInfo;

public class DrawingBoardEditZoneView extends CustomEditorView {

	private final ZoneInfo zoneInfo;
	
	protected DrawingBoardEditZoneView(Player player, ZoneInfo zoneInfo) {
		super(player);
		this.zoneInfo = zoneInfo;
	}

	@Override
	protected int getInventorySize() {
		return 9;
	}

	@Override
	protected Collection<EditorSlot> initializeEditor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTitle() {
		return zoneInfo.getDisplayName();
	}

	public static void open(Player player, ItemStack itemStack, ZoneInfo zoneInfo){
		//TODO actually do something here
		ZoneEditorView.open(player, itemStack, zoneInfo);
		/*DrawingBoardEditZoneView result = new DrawingBoardEditZoneView(player, zoneInfo);
		result.open();
		return result;
		*/
	}
}
