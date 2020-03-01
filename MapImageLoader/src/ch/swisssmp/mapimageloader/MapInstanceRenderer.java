package ch.swisssmp.mapimageloader;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public abstract class MapInstanceRenderer extends MapRenderer {
	private boolean dirty = true;
	
	public void setDirty() {
		dirty = true;
	}
	
	@Override
	public void render(MapView map, MapCanvas canvas, Player player) {
		if(!dirty) return;
		dirty = false;
		render(map,canvas);
	}
	
	protected abstract void render(MapView map, MapCanvas canvas);
}
