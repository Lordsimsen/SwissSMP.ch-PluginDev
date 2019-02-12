package ch.swisssmp.shops.editor;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.ButtonSlot;
import ch.swisssmp.shops.Shop;
import ch.swisssmp.shops.ShopEditorView;
import ch.swisssmp.shops.ShopsPlugin;

public class ShopSlot extends ButtonSlot {

	private final Shop shop;
	
	public ShopSlot(CustomEditorView view, int slot, Shop shop) {
		super(view, slot);
		this.shop = shop;
	}

	@Override
	protected void triggerOnClick(ClickType arg0) {
		this.getView().closeLater();
		Bukkit.getScheduler().runTaskLater(ShopsPlugin.getInstance(), ()->{
			ShopEditorView.open((Player) this.getView().getPlayer(), shop);
		}, 2L);
	}

	@Override
	protected CustomItemBuilder createSlotBase() {
		CustomItemBuilder result = new CustomItemBuilder();
		result.setMaterial(Material.EMERALD);
		return result;
	}

	@Override
	public String getName() {
		return ChatColor.AQUA+"Angebote bearbeiten";
	}

	@Override
	protected List<String> getNormalDescription() {
		return null;
	}

	@Override
	protected boolean isComplete() {
		return true;
	}
}
