package ch.swisssmp.livemap.markers;

import org.bukkit.World;
import org.bukkit.util.Vector;

import ch.swisssmp.livemap.Livemap;

public class PolyLineMarker extends Marker {

	private Vector[] points;
	
	private int lineColor = 1;
	private double lineOpacity = 1;
	private int lineWeight = 2;
	
	public PolyLineMarker(World world, String group_id, String marker_id, Vector[] points) {
		super(world, group_id, marker_id);
		this.points = points;
	}

	public void setPoints(Vector[] points) {
		this.points = points;
	}
	
	public Vector[] getPoints(){
		return points;
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

	public int getLineWeight() {
		return lineWeight;
	}

	public double getLineOpacity() {
		return lineOpacity;
	}

	public int getLineColor() {
		return lineColor;
	}

	@Override
	public void save() {
		Livemap.saveMarker(this);
	}
}
