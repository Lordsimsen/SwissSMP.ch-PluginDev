package ch.swisssmp.livemap_render_api;

import org.bukkit.block.BlockFace;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

public class IsoBounds {
	
	public int fromX;
	public int fromY;
	public int toX;
	public int toY;
	
	public IsoBounds(int fromX, int fromY, int toX, int toY) {
		this.fromX = fromX;
		this.fromY = fromY;
		this.toX = toX;
		this.toY = toY;
	}
	
	private static double getAzimuth(BlockFace direction) {
		switch(direction) {
		case EAST: return 90;
		case SOUTH_EAST: return 135;
		case SOUTH: return 180;
		case SOUTH_WEST: return 225;
		case WEST: return 270;
		case NORTH_WEST: return 315;
		case NORTH: return 0;
		case NORTH_EAST: return 45;
		default: return 0;
		}
	}
	
	public static IsoBounds calculate(BlockVector min, BlockVector max, BlockFace direction, int inclination, int chunkSize) {

        double azimuth = 90.0 + getAzimuth(direction);    /* Get azimuth (default to classic kzed POV) */
        if(azimuth >= 360.0) {
            azimuth = azimuth - 360.0;
        }
        
        /* Generate transform matrix for world-to-tile coordinate mapping */
        /* First, need to fix basic coordinate mismatches before rotation - we want zero azimuth to have north to top
         * (world -X -> tile +Y) and east to right (world -Z to tile +X), with height being up (world +Y -> tile +Z)
         */
        Matrix3D transform = new Matrix3D(0.0, 0.0, -1.0, -1.0, 0.0, 0.0, 0.0, 1.0, 0.0);
        /* Next, rotate world counterclockwise around Z axis by azumuth angle */
        transform.rotateXY(180-azimuth);
        /* Next, rotate world by (90-inclination) degrees clockwise around +X axis */
        transform.rotateYZ(90.0-inclination);
        /* Finally, shear along Z axis to normalize Z to be height above map plane */
        transform.shearZ(0, Math.tan(Math.toRadians(90.0-inclination)));
        /* And scale Z to be same scale as world coordinates, and scale X and Y based on setting */
        transform.scale(chunkSize, chunkSize, Math.sin(Math.toRadians(inclination)));
		
		BlockVector[] corners = new BlockVector[] {
				// bottom
				min,
				new BlockVector(min.getBlockX(), min.getBlockY(), max.getBlockZ()),
				new BlockVector(max.getBlockX(), min.getBlockY(), min.getBlockZ()),
				new BlockVector(max.getBlockX(), min.getBlockY(), max.getBlockZ()),
				// top
				new BlockVector(min.getBlockX(), max.getBlockY(), min.getBlockZ()),
				new BlockVector(min.getBlockX(), max.getBlockY(), max.getBlockZ()),
				new BlockVector(max.getBlockX(), max.getBlockY(), min.getBlockZ()),
				max
		};
		BlockVector[] isoVectors = new BlockVector[corners.length];
		for(int i = 0; i < corners.length; i++) {
			BlockVector c = corners[i];
			int x = c.getBlockX();
			int y = c.getBlockY();
			int z = c.getBlockZ();
			Vector3D v = new Vector3D(x,y,z);
			transform.transform(v);
			BlockVector isoVector = new BlockVector(v.x / 128, v.y / 128, 0);
			isoVectors[i] = isoVector;
			// Bukkit.getLogger().info(c.getBlockX()+","+c.getBlockY()+","+c.getBlockZ()+" turns into "+isoVector.getBlockX()+","+isoVector.getBlockY());
		}
		
		Vector first = isoVectors[0];
		double isoMinX = first.getX();
		double isoMinY = first.getY();
		double isoMaxX = isoMinX;
		double isoMaxY = isoMinY;
		
		for(int i = 0; i < isoVectors.length; i++) {
			Vector v = isoVectors[i];
			isoMinX = Math.min(isoMinX, v.getX());
			isoMinY = Math.min(isoMinY, v.getY());
			isoMaxX = Math.max(isoMaxX, v.getX());
			isoMaxY = Math.max(isoMaxY, v.getY());
		}
		return new IsoBounds(
				(int) Math.floor(Math.min(isoMinX, isoMaxX)),
				(int) Math.floor(Math.min(isoMinY, isoMaxY)),
				(int) Math.ceil(Math.max(isoMinX, isoMaxX)),
				(int) Math.ceil(Math.max(isoMinY, isoMaxY))
				);
	}
}
