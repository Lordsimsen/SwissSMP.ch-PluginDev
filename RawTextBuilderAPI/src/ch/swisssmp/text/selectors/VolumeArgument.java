package ch.swisssmp.text.selectors;

import org.bukkit.util.Vector;

/**
 * 
 * @author Oliver
 *
 */
public class VolumeArgument extends PositionArgument {

	private boolean relative;
	private Vector volume;
	
	private VolumeArgument(VolumeArgument template) {
		super(template);
		this.relative = template.relative;
		this.volume = template.volume;
	}
	
	public VolumeArgument(Vector position, Vector volumeRadius) {
		super(position);
		this.volume = volumeRadius;
		this.relative = false;
	}
	
	public VolumeArgument(Vector volume) {
		super(new Vector());
		this.volume = volume;
		this.relative = true;
	}
	
	@Override
	public String getValue() {
		if(relative) return this.getAdditionalValue();
		else return super.getValue();
	}

	@Override
	protected String getAdditionalValue() {
		return "dx="+volume.getX()+",dy="+volume.getY()+",dz="+volume.getZ();
	}

	@Override
	public ISelectorArgument duplicate() {
		return new VolumeArgument(this);
	}

}
