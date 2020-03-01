package ch.swisssmp.antiguest.preventions.blocks;

import org.bukkit.Material;

public class EndPortalFrame extends BlockInteractPrevention {

	@Override
	protected Material GetType() {
		return Material.END_PORTAL_FRAME;
	}

	@Override
	protected String GetSubPermission() {
		return "end_portal_frame";
	}
}
