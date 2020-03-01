package ch.swisssmp.livemap.markers;

import org.bukkit.Location;
import org.bukkit.World;

import ch.swisssmp.livemap.Livemap;

public class CircleMarker extends Marker{

	private Location center;
	private double radiusX;
	private double radiusZ;

	private boolean boostFlag = false;
	
	private int fillColor = 1;
	private double fillOpacity = 0.2;
	
	private int lineColor = 1;
	private double lineOpacity = 1;
	private int lineWeight = 2;
	public CircleMarker(World world, String group_id, String marker_id, Location center, double radiusX, double radiusZ) {
		super(world, group_id, marker_id);
		this.center = center;
		this.radiusX = radiusX;
		this.radiusZ = radiusZ;
	}

	@Override
	public void save() {
		Livemap.saveMarker(this);
	}

	public void setCenter(Location location) {
		this.center = location;
	}
	
	public void setRadius(double radiusX, double radiusZ){
		this.radiusX = radiusX;
		this.radiusZ = radiusZ;
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

	public Location getCenter() {
		return center;
	}

	public double getRadiusX() {
		return radiusX;
	}

	public double getRadiusZ() {
		return radiusZ;
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
