package ch.swisssmp.mapimageloader;

import java.util.Optional;

import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapView;

public class MapImageInstanceRenderer extends MapInstanceRenderer {

	private final MapImageInstance instance;
	
	protected MapImageInstanceRenderer(MapImageInstance instance) {
		this.instance = instance;
	}
	
	public MapImageInstance getInstance() {
		return this.instance;
	}
	
	@Override
	public void render(MapView map, MapCanvas canvas) {
		Optional<MapImage> image = MapImage.get(instance.getImageUid());
		if(!image.isPresent()) return;
		byte[][] pixels = image.get().getPixels();
		if(pixels==null) return;
		for(int y = 0; y < pixels.length; y++) {
			byte[] row = pixels[y];
			for(int x = 0; x < row.length; x++) {
				byte color = row[x];
				canvas.setPixel(x, y, color);
			}
		}
	}
}
