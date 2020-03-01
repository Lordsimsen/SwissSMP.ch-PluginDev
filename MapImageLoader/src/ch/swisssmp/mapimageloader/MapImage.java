package ch.swisssmp.mapimageloader;

//import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
//import java.awt.image.BufferedImageOp;
//import java.awt.image.ColorConvertOp;
import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

public class MapImage {
	private final UUID uid;
	private String url;

	private final boolean keepLocalCopy;
	
	private BufferedImage image;
	private byte[][] pixels;
	
	private MapImage(UUID uid, String url, boolean keepLocalCopy) {
		this.uid = uid;
		this.url = url;
		
		this.keepLocalCopy = keepLocalCopy;
	}
	
	public UUID getUid() {
		return uid;
	}
	
	public String getUrl() {
		return this.url;
	}

	public void reloadImage() {
		reloadImage(false);
	}
	
	private boolean load() {
		if(this.url==null) return true;
		reloadImage(true);
		return image!=null;
	}
	
	public byte[][] getPixels(){
		return pixels;
	}
	
	public void replace(String url) {
		this.url = url;
		this.reloadImage(true);
	}
	
	public void reloadImage(boolean forceReloadFromUrl) {
		if(this.url==null) return;
		try {
			File localFile = this.getLocalFile();
			boolean download = forceReloadFromUrl || !keepLocalCopy || !localFile.exists();
			BufferedImage loadedImage = download ? ImageIO.read(new URL(url)) : ImageIO.read(localFile);
			if(loadedImage==null) throw new NullPointerException();
			if(download && keepLocalCopy) {
				try {
					if(!localFile.getParentFile().exists()) localFile.getParentFile().mkdirs();
					ImageIO.write(loadedImage, "png", localFile);
				}
				catch(Exception e){
					Bukkit.getLogger().info(MapImageLoaderPlugin.getPrefix()+" Beim speichern des Bilds "+url+" ist ein Fehler aufgetreten: "+e.toString());
					return;
				}
			}
			
			//// Stelle sicher dass das Bild den richtigen Farbraum für Karten hat
			//BufferedImageOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_sRGB), null);
			image = loadedImage;//op.filter(loadedImage, null);
			this.recalculatePixels();
			for(MapViewComposition c : MapViewCompositions.get(this)) {
				c.setDirty();
			}
			
		} catch (Exception e) {
			Bukkit.getLogger().info(ChatColor.RED + MapImageLoaderPlugin.getPrefix()+" Das Bild konnte nicht geladen werden. Bitte überprüfe den Pfad: "+url);
		}
	}
	
	private void recalculatePixels() {
		byte[][] pixels = new byte[image.getHeight()][image.getWidth()];
		MapImages.getDitherer().ditherOnto(image, pixels);
		this.pixels = pixels;
	}
	
	protected void unload() {
		if(image!=null) image.flush();
	}
	
	public void remove() {
		unload();
		File localFile = this.getLocalFile();
		if(localFile.exists()) localFile.delete();
		MapImages.remove(this);
	}
	
	private File getLocalFile() {
		File dataFolder = MapImageLoaderPlugin.getInstance().getDataFolder();
		return new File(dataFolder, "local_copies/"+uid+".png");
	}
	
	protected void save(ConfigurationSection dataSection) {
		dataSection.set("url", this.url);
		dataSection.set("keep_local_copy", this.keepLocalCopy);
	}
	
	public static Optional<MapImage> get(UUID uid) {
		return MapImages.get(uid);
	}
	
	public static MapImage create(String url, boolean keepLocalCopy) {
		MapImage result = new MapImage(UUID.randomUUID(), url, keepLocalCopy);
		if(!result.load()) {
			return null;
		}
		MapImages.add(result);
		return result;
	}
	
	protected static MapImage load(UUID uid, ConfigurationSection dataSection) {
		String url = dataSection.getString("url");
		boolean keepLocalCopy = dataSection.getBoolean("keep_local_copy", true);
		MapImage result = new MapImage(uid, url, keepLocalCopy);
		result.load();
		MapImages.add(result);
		return result;
	}
}
