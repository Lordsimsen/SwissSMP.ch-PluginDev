package ch.swisssmp.mapimageloader;

import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapView;

public class MapTextInstanceRenderer extends MapInstanceRenderer {

	private final MapTextInstance instance;
	
	protected MapTextInstanceRenderer(MapTextInstance instance) {
		this.instance = instance;
	}
	
	public MapTextInstance getInstance() {
		return instance;
	}
	
	@Override
	public void render(MapView map, MapCanvas canvas) {
		canvas.drawText(instance.getX(), instance.getY(), instance.getFont(), instance.getValue());
	}

}
