package ch.swisssmp.livemap.markers;

import org.bukkit.World;

import ch.swisssmp.livemap.Livemap;

public class AreaMarker extends Marker {

	private double[] x;
	private double[] z;
	private double bottomY = 50;
	private double topY = 256;
	private boolean boostFlag = false;
	
	private int fillColor = 1;
	private double fillOpacity = 0.2;
	
	private int lineColor = 1;
	private double lineOpacity = 1;
	private int lineWeight = 2;
	
	public AreaMarker(World world, String group_id, String marker_id, double[] x, double[] z) {
		super(world, group_id, marker_id);
		this.x = x;
		this.z = z;
	}

	@Override
	public void save() {
		Livemap.saveMarker(this);
	}

	public void setCorners(double[] x, double[] z) {
		this.x = x;
		this.z = z;
	}

	public void setRangeY(double bottomY, double topY) {
		this.bottomY = bottomY;
		this.topY = topY;
	}

	public void setBoostFlag(boolean boostFlag) {
		this.boostFlag = boostFlag;
	}

	public void setFillColor(int fillColor) {
		this.fillColor = fillColor;
	}

	public void setFillOpacity(double fillOpacity) {
		this.fillOpacity = fillOpacity;
	}

	public void setLineColor(int lineColor) {
		this.lineColor = lineColor;
	}

	public void setLineOpacity(double lineOpacity) {
		this.lineOpacity = lineOpacity;
	}

	public void setLineWeight(int lineWeight) {
		this.lineWeight = lineWeight;
	}

	public double[] getX() {
		return x;
	}

	public double[] getZ() {
		return z;
	}

	public double getBottomY() {
		return bottomY;
	}

	public double getTopY() {
		return topY;
	}

	public boolean getBoostFlag() {
		return boostFlag;
	}

	public int getFillColor() {
		return fillColor;
	}

	public double getFillOpacity() {
		return fillOpacity;
	}

	public int getLineWeight() {
		return lineWeight;
	}

	public double getLineOpacity() {
		return lineOpacity;
	}

	public int getLineColor() {
		return lineColor;
	}
}
