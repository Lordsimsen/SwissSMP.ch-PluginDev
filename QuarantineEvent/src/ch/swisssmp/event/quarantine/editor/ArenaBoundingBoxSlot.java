package ch.swisssmp.event.quarantine.editor;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.selector.CuboidRegionSelector;
import com.sk89q.worldedit.world.World;

import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.BoundingBoxSlot;
import ch.swisssmp.event.quarantine.QuarantineArena;
import ch.swisssmp.utils.Position;

public class ArenaBoundingBoxSlot extends BoundingBoxSlot {

	private final QuarantineArena arena;
	private final WorldEditPlugin worldEdit;
	
	public ArenaBoundingBoxSlot(CustomEditorView view, int slot, QuarantineArena arena) {
		super(view, slot);
		this.arena = arena;
		this.worldEdit = this.getWorldEdit();
	}

	@Override
	public String getName() {
		return ChatColor.AQUA+"Areal";
	}

	@Override
	protected void applyBoundingBox(Position min, Position max) {
		arena.setBoundingBoxMin(min);
		arena.setBoundingBoxMax(max);
		arena.getContainer().save();
	}

	@Override
	protected Position getNewBoundingBoxMax() {
		Player player = (Player) this.getView().getPlayer();
		LocalSession session = worldEdit.getSession(player);
		if(session==null) return null;
		try {
			World world = session.getSelectionWorld();
			if(world==null) return null;
			Region region = session.getSelection(world);
			BlockVector3 vector3 = region.getMaximumPoint();
			return new Position(vector3.getX(),vector3.getY(),vector3.getZ());
		} catch (IncompleteRegionException e) {
			return null;
		}
	}

	@Override
	protected Position getNewBoundingBoxMin() {
		Player player = (Player) this.getView().getPlayer();
		LocalSession session = worldEdit.getSession(player);
		if(session==null) return null;
		try {
			World world = session.getSelectionWorld();
			if(world==null) return null;
			Region region = session.getSelection(world);
			BlockVector3 vector3 = region.getMinimumPoint();
			return new Position(vector3.getX(),vector3.getY(),vector3.getZ());
		} catch (IncompleteRegionException e) {
			return null;
		}
	}

	@Override
	protected List<String> getNormalDescription() {
		return Arrays.asList("Linksklick: Box anpassen","Rechtsklick: Box auswählen",ChatColor.ITALIC+"Auswahl mit WorldEdit");
	}

	@Override
	protected List<String> getIncompleteDescription() {
		return Arrays.asList("Klicken, um WorldEdit","Auswahl zu verwenden");
	}

	@Override
	protected boolean isComplete() {
		return arena.getBoundingBoxMin()!=null && arena.getBoundingBoxMax()!=null;
	}
	
	protected WorldEditPlugin getWorldEdit(){
		Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldEdit");
		if(!(plugin instanceof WorldEditPlugin)) return null;
		return (WorldEditPlugin) plugin;
	}

	@Override
	protected Position getBoundingBoxMax() {
		return arena.getBoundingBoxMax();
	}

	@Override
	protected Position getBoundingBoxMin() {
		return arena.getBoundingBoxMin();
	}

	@Override
	protected void selectBoundingBox(Position min, Position max) {
		Player player = (Player) this.getView().getPlayer();
		LocalSession session = worldEdit.getSession(player);
		BlockVector3 from = BlockVector3.at(min.getBlockX(),min.getBlockY(),min.getBlockZ());
		BlockVector3 to = BlockVector3.at(max.getBlockX(),max.getBlockY(),max.getBlockZ());
		CuboidRegionSelector selector = new CuboidRegionSelector(null, from, to);
		session.setRegionSelector(session.getSelectionWorld(), selector);
	}
}
