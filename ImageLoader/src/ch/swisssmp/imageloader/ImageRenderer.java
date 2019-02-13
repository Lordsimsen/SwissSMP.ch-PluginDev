package ch.swisssmp.imageloader;

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

public class ImageRenderer extends MapRenderer {

	private final String url;
	private BufferedImage image;
	
	protected ImageRenderer(String url){
		this.url = url;
	}
	
	public void load(){
		try {
		image = ImageIO.read(new URL(url)); //should be initialized outside the render method
		Bukkit.getLogger().info("[ImageLoader] Das Bild ist "+image.getWidth()+", "+image.getHeight()+" Pixel gross.");
		} catch (IOException e) {
			Bukkit.getLogger().info(ChatColor.RED + "[ImageLoader] Das Bild konnte nicht geladen werden. Bitte überprüfe den Pfad: "+url);
		}
	}
	
	@Override
	public void render(MapView view, MapCanvas canvas, Player player) {
		try {
			canvas.drawImage(0, 0, image);
			player.getInventory().clear();
		} catch (Exception e) {
			player.sendMessage(ChatColor.RED + "[ImageLoader] Das Bild konnte nicht gezeichnet werden. Bitte überprüfe den Pfad: "+url);
			return;
		}
	}

}
