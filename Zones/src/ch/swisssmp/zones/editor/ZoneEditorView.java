package ch.swisssmp.zones.editor;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.EditorSlot;
import ch.swisssmp.zones.MemberRole;
import ch.swisssmp.zones.ZoneType;
import ch.swisssmp.zones.editor.slots.ConfirmZoneEditorSlot;
import ch.swisssmp.zones.editor.slots.LaunchZoneEditorSlot;
import ch.swisssmp.zones.editor.slots.RemoveZoneSlot;
import ch.swisssmp.zones.zoneinfos.ZoneInfo;
import ch.swisssmp.zones.zoneinfos.ZoneInfoState;

public class ZoneEditorView extends CustomEditorView {

	private final ItemStack itemStack;
	private final ZoneInfo zoneInfo;
	
	protected ZoneEditorView(Player player, ItemStack itemStack, ZoneInfo zoneInfo) {
		super(player);
		this.itemStack = itemStack;
		this.zoneInfo = zoneInfo;
	}

	@Override
	protected int getInventorySize() {
		return 27;
	}

	@Override
	protected Collection<EditorSlot> initializeEditor() {
		ZoneEditor editor = ZoneEditor.get((Player) this.getPlayer());
		Collection<EditorSlot> slots = new ArrayList<EditorSlot>();
		if(editor==null){
			if(zoneInfo.getRegion()==null){
				slots.add(new LaunchZoneEditorSlot(this, 8, itemStack, zoneInfo));
			}
			else{
				slots.add(new LaunchZoneEditorSlot(this, 7, itemStack,zoneInfo));
				slots.add(new RemoveZoneSlot(this,8,zoneInfo));
			}
			if(zoneInfo.getZoneType()!=ZoneType.GENERIC && zoneInfo.getState()==ZoneInfoState.ACTIVE && 
			  (zoneInfo.getMembers(MemberRole.OWNER).contains(getPlayer().getUniqueId()) ||
		       getPlayer().hasPermission("zones.admin"))){
				slots.add(new RemoveZoneSlot(this, 26, zoneInfo));
			}
		}
		else{
			slots.add(new ConfirmZoneEditorSlot(this,8,zoneInfo, editor));
		}
		return slots;
	}
	
	public static ZoneEditorView open(Player player, ItemStack itemStack, ZoneInfo zoneInfo){
		ZoneEditorView result = new ZoneEditorView(player, itemStack, zoneInfo);
		result.open();
		return result;
	}

	@Override
	public String getTitle() {
		return zoneInfo.getDisplayName();
	}
}
