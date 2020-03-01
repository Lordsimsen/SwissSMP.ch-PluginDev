package ch.swisssmp.livemap_render_api;

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
		int sizeX = (isoBounds.toX - isoBounds.fromX);
		int sizeY = (isoBounds.toY - isoBounds.fromY);
		
		int zoomX = (int) Math.ceil(Math.log(sizeX/(double)mapSizeX) / Math.log(2) + 1e-10);
		int zoomY = (int) Math.ceil(Math.log(sizeY/(double)mapSizeY) / Math.log(2) + 1e-10);
		
		int zoom = Math.min(livemapInfo.maxZoomLevel, Math.max(zoomX, zoomY));
		int tileSize = Math.max(1, (int)(Math.pow(2, zoom)));
		//int calculatedWidth = tileSize * mapSizeX;
		//int calculatedHeight = tileSize * mapSizeY;
		
		double aspectRatio = mapSizeY / (double) mapSizeX;

		double isoHalfWidthDiff = sizeX*aspectRatio<sizeY ? (sizeY/aspectRatio - sizeX) / 2.0 : 0;
		double isoHalfHeightDiff = sizeY<sizeX*aspectRatio ? (sizeX*aspectRatio - sizeY) / 2.0 : 0;
		
		double leftIsoX = isoBounds.fromX - isoHalfWidthDiff;
		double lowerIsoY = isoBounds.fromY - isoHalfHeightDiff;
		
		int leftX = (int)Math.floor(leftIsoX / tileSize) * tileSize;
		int lowerY = (int)(Math.floor(lowerIsoY / tileSize)+1) * tileSize;
		
		/*
		Bukkit.getLogger().info(
				"Bounds: "+min.getBlockX()+","+min.getBlockY()+","+min.getBlockZ()+" to "+max.getBlockX()+","+max.getBlockY()+","+max.getBlockZ()+"\n"
				+ "IsoBounds: "+isoBounds.fromX+","+isoBounds.fromY+" to "+isoBounds.toX+","+isoBounds.toY+"\n"
				+ "Size: "+sizeX+","+sizeY+"\n"
				+ "Zoom: "+zoom+"\n"
				+ "TileSize: "+tileSize+"\n"
				+ "Calculated: "+leftX+","+lowerY+" to "+(leftX+(mapSizeX*tileSize))+","+(lowerY+(mapSizeY*tileSize)));
				*/
		
		String zoomPrefix = getZoomPrefix(zoom);
		String[][] result = new String[mapSizeY][mapSizeX];
		for(int y = 0; y < mapSizeY; y++) {
			for(int x = 0; x < mapSizeX; x++) {
				int tileX = leftX + x * tileSize;
				int tileY = lowerY + y * tileSize;
				result[y][x] = livemapInfo.url+zoomPrefix+tileX+"_"+tileY+".jpg";
			}
		}
		return result;
	}
	
	private String getZoomPrefix(int zoom) {
		String result = "";
		if(zoom==0) return result;
		for(int i = 0; i < zoom; i++) {
			result+="z";
		}
		result+="_";
		return result;
	}
}
