package ch.swisssmp.livemap_render_api;

import org.bukkit.block.BlockFace;

public class LivemapInfo {
	public final String url;
	public final int isoAngle;
	public final BlockFace isoDirection;
	public final int maxZoomLevel;
	public final int chunkSize; // usually 16

	public LivemapInfo(String url, int isoAngle, BlockFace isoDirection, int maxZoomLevel, int chunkSize) {
		this.url = url;
		this.isoAngle = isoAngle;
		this.isoDirection = isoDirection;
		this.maxZoomLevel = maxZoomLevel;
		this.chunkSize = chunkSize;
	}
}
