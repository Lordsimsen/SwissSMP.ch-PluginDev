package ch.swisssmp.text.selectors;

import org.bukkit.util.Vector;

/**
 * Filter target selection based on their Euclidean distances from some point. 
 * If the positional arguments are left undefined, radius/i is calculated relative to the postion of the command's execution. Only unsigned values are allowed.
 *
 */
public class DistanceArgument extends PositionArgument {

	private DistanceType type;
	private double distance;
	private double maxDistance;
	
	private DistanceArgument(DistanceArgument template) {
		super(template);
		this.type = template.type;
		this.distance = template.distance;
		this.maxDistance = template.maxDistance;
	}
	
	public DistanceArgument(Vector position, double distance, DistanceType type) {
		super(position);
		this.type = type;
		this.distance = distance;
		this.maxDistance = distance;
	}
	
	public DistanceArgument(Vector position, double min, double max) {
		super(position);
		this.distance = min;
		this.maxDistance = max;
	}

	@Override
	protected String getAdditionalValue() {
		return "distance="+getDistanceString();
	}
	
	private String getDistanceString() {
		switch(type) {
		case EXACT: return String.valueOf(distance);
		case BETWEEN: return distance+".."+maxDistance;
		case MIN: return distance+"..";
		case MAX: return ".."+maxDistance;
		default: return String.valueOf(distance);
		}
	}

	public enum DistanceType{
		/**
		 * Target all entities exactly x blocks away.
		 */
		EXACT,
		/**
		 * Target all entities more than x blocks but less than y blocks away (inclusive).
		 */
		BETWEEN,
		/**
		 * Target all entities at least x blocks away.
		 */
		MIN,
		/**
		 * Target all entities at most x blocks away.
		 */
		MAX
	}

	@Override
	public ISelectorArgument duplicate() {
		return new DistanceArgument(this);
	}
}
