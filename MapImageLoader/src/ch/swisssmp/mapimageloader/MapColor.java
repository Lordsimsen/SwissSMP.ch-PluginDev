package ch.swisssmp.mapimageloader;

public class MapColor{
	
	private final short id;
	private final short r;
	private final short g;
	private final short b;

	public MapColor(int id, double r, double g, double b) {
		this(id,(int)Math.floor(r),(int)Math.floor(g),(int)Math.floor(b));
	}
	public MapColor(int id, int r, int g, int b) {
		this((short)id,(short)r,(short)g,(short)b);
	}
	public MapColor(short id, short r, short g, short b) {
		this.id = id;
		this.r = r;
		this.g = g;
		this.b = b;
	}
	public short getId() {
		return this.id;
	}
	public short getRed() {
		return this.r;
	}
	public short getGreen() {
		return this.g;
	}
	public short getBlue() {
		return this.b;
	}
	
	public double getDistanceSquared(short r, short g, short b) {
		return Math.pow((r-this.r)*0.299, 2)+Math.pow((g-this.g)*0.587, 2)+Math.pow((b-this.b)*0.114, 2);
	}
}
