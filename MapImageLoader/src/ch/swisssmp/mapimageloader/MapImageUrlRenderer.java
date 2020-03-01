package ch.swisssmp.mapimageloader;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class MapImageUrlRenderer extends MapRenderer {

	private final String url;
	private BufferedImage image;
	private boolean rendered = false;
	
	protected MapImageUrlRenderer(String url){
		this.url = url;
	}
	
	public void load(){
		try {
			image = ImageIO.read(new URL(url)); //should be initialized outside the render method
		Bukkit.getLogger().info(MapImageLoaderPlugin.getPrefix()+" Das Bild ist "+image.getWidth()+", "+image.getHeight()+" Pixel gross.");
		} catch (IOException e) {
			Bukkit.getLogger().info(ChatColor.RED + "[ImageLoader] Das Bild konnte nicht geladen werden. Bitte überprüfe den Pfad: "+url);
		}
	}
	
	@Override
	public void render(MapView view, MapCanvas canvas, Player player) {
		if(rendered) return;
		rendered = true;
		byte[][] pixels = new byte[image.getHeight()][image.getWidth()];
		MapImages.getDitherer().ditherOnto(image, pixels);
		for(int y = 0; y < pixels.length && y < 128; y++) {
			byte[] row = pixels[y];
			for(int x = 0; x < row.length && x < 128 ; x++) {
				short c = row[x];
				if(c==0) continue;
				canvas.setPixel(x,y,(byte) c);
			}
		}
	}
}
