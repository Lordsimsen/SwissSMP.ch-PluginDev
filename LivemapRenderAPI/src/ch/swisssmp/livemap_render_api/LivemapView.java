package ch.swisssmp.livemap_render_api;

import ch.swisssmp.utils.Mathf;
import org.bukkit.Bukkit;
import org.bukkit.util.BlockVector;

public class LivemapView {
	
	private final LivemapInfo livemapInfo;
	
	/**
	 * Amount of map items on x axis
	 */
	private int mapSizeX;
	
	/**
	 * Amount of map items on y axis
	 */
	private int mapSizeY;
	
	public LivemapView(int mapSizeX, int mapSizeY, LivemapInfo info) {
		this.livemapInfo = info;
		this.mapSizeX = mapSizeX;
		this.mapSizeY = mapSizeY;
	}
	
	public String[][] getUrls(BlockVector min, BlockVector max){
		
		IsoBounds isoBounds = IsoBounds.calculate(min, max, livemapInfo.isoDirection, livemapInfo.isoAngle, livemapInfo.chunkSize);

		double isoCenterX = isoBounds.fromX+(isoBounds.toX-isoBounds.fromX)/2f;
		double isoCenterY = isoBounds.fromY+(isoBounds.toY-isoBounds.fromY)/2f;

		int isoSizeX = (isoBounds.toX - isoBounds.fromX);
		int isoSizeY = (isoBounds.toY - isoBounds.fromY);
		
		int zoomX = (int) Math.floor(Math.log(isoSizeX/(double)mapSizeX) / Math.log(2) + 1e-10);
		int zoomY = (int) Math.floor(Math.log(isoSizeY/(double)mapSizeY) / Math.log(2) + 1e-10);
		
		int zoom = Math.min(livemapInfo.maxZoomLevel, Math.max(zoomX, zoomY));
		int tileSize = Math.max(1, (int)(Math.pow(2, zoom)));

		int centerTileX = Mathf.floorToInt(isoCenterX/tileSize+(mapSizeX%2!=0?-0.5f:0));
		int centerTileY = Mathf.floorToInt(isoCenterY/tileSize+(mapSizeY%2!=0?-0.5f:0));

		int leftX = (centerTileX-Mathf.floorToInt((mapSizeX)/2f))*tileSize;
		int lowerY = (centerTileY-Mathf.floorToInt((mapSizeY)/2f)+1)*tileSize;

		Bukkit.getLogger().info(
				"Bounds: "+min.getBlockX()+","+min.getBlockY()+","+min.getBlockZ()+" to "+max.getBlockX()+","+max.getBlockY()+","+max.getBlockZ()+"\n"
				+ "IsoBounds: "+isoBounds.fromX+","+isoBounds.fromY+" to "+isoBounds.toX+","+isoBounds.toY+"\n"
				+ "Size: "+isoSizeX+","+isoSizeY+"\n"
				+ "Zoom: "+zoom+"\n"
				+ "TileSize: "+tileSize+"\n"
				+ "Calculated: "+leftX+","+lowerY+" to "+(leftX+(mapSizeX*tileSize))+","+(lowerY+(mapSizeY*tileSize)));

		
		String zoomPrefix = getZoomPrefix(zoom);
		String[][] result = new String[mapSizeY][mapSizeX];
		for(int y = 0; y < mapSizeY; y++) {
			for(int x = 0; x < mapSizeX; x++) {
				int tileX = leftX + x * tileSize;
				int tileY = lowerY + y * tileSize;
				String url = livemapInfo.url+zoomPrefix+tileX+"_"+tileY+".jpg";
				result[y][x] = url;
			}
		}
		return result;
	}
	
	private String getZoomPrefix(int zoom) {
		StringBuilder result = new StringBuilder();
		if(zoom==0) return result.toString();
		for(int i = 0; i < zoom; i++) {
			result.append("z");
		}
		result.append("_");
		return result.toString();
	}
}
