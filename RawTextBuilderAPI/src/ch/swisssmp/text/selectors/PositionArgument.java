package ch.swisssmp.text.selectors;

import org.bukkit.util.Vector;

public abstract class PositionArgument implements ISelectorArgument {
	private Vector position;
	
	protected PositionArgument(PositionArgument template){
		this.position = template.position;
	}
	
	public PositionArgument(Vector position) {
		this.position = position;
	}
	
	@Override
	public String getValue() {
		return "x="+position.getX()+",y="+position.getY()+",z="+position.getZ()+","+getAdditionalValue();
	}
	
	protected abstract String getAdditionalValue();
}
