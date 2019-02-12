package ch.swisssmp.city.ceremony.founding;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.Particle.DustOptions;
import org.bukkit.util.Vector;

import ch.swisssmp.utils.Mathf;

public class FoundingCeremonyCircleEffect implements Runnable {

	private final int ringCount = 10;
	private final float ringStep = (2 * (float) Math.PI) / ringCount;
	private final Vector up = new Vector(0,1,0);
	
	private final World world;
	private final Vector ringCenter;
	private float radius = 1;
	private float targetRadius = 1;
	
	private long t = 0;
	
	private RingEffectType ringEffect = RingEffectType.Wisp;
	
	private Color[] ringColors = new Color[]{
			Color.fromRGB(255,150,20),
			Color.fromRGB(255,180,20),
			Color.fromRGB(255,120,20),
			Color.fromRGB(255,150,20),
			Color.fromRGB(255,200,20)
	};
	
	protected FoundingCeremonyCircleEffect(Location center){
		this.world = center.getWorld();
		this.ringCenter = center.toVector();
	}
	
	public void setRingEffectType(RingEffectType ringEffectType){
		this.ringEffect = ringEffectType;
	}
	
	@Override
	public void run() {
		final float rotationSpeed = 0.005f;
		if(radius<targetRadius){
			radius+=0.3;
		}
		for(int i = 0; i < ringCount; i++){
			double x = getCircleX(ringStep * i + t * rotationSpeed) * radius;
			double z = getCircleZ(ringStep * i + t * rotationSpeed) * radius;
			playEffectAt(new Vector(x,radius*0.3,z), ringColors[Mathf.wrap(i, ringColors.length)]);
		}
		t++;
	}
	
	public void setRadius(float radius){
		this.targetRadius = radius;
	}
	
	public void setColor(int index, Color color){
		if(index>=ringColors.length || index<0) return;
		this.ringColors[index] = color;
	}
	
	private void playEffectAt(Vector position, Color color){
		Vector forward = position.clone().multiply(-1).setY(0).normalize();
		Vector side = forward.crossProduct(up);
		Vector center = position.add(ringCenter);
		switch(this.ringEffect){
		case Wisp: playWisp(center,side,color); break;
		case WhirlingBlade: playWhirlingBlade(center,side,color); break;
		case RotatingRing: playRotatingRing(center,side,color);break;
		}
	}
	
	private void playWisp(Vector center, Vector side,Color color){
		DustOptions dustOptions = new DustOptions(color,1);
		world.spawnParticle(Particle.REDSTONE, new Location(world,center.getX(),center.getY(),center.getZ()), 1, dustOptions);
	}
	
	private void playWhirlingBlade(Vector center, Vector side,Color color){
		DustOptions dustOptions = new DustOptions(color,1);
		final int bladeDetail = 4;
		float rotationSpeed = 0.12f;
		final int subParticleCount = 2;
		final float subCircleStep = (2 * (float) Math.PI) / subParticleCount;
		for(int i = 0; i < subParticleCount; i++){
			double localX = getCircleX(subCircleStep * i + t * rotationSpeed) * radius / 6;
			double localY = getCircleZ(subCircleStep * i + t * rotationSpeed) * radius / 6;
			for(int j = 0; j < bladeDetail; j++){
				float factor = 1f/bladeDetail*j;
				Vector local = side.clone().multiply(localX * factor).add(new Vector(0,localY * factor,0));
				Vector subPosition = local.add(center);
				world.spawnParticle(Particle.REDSTONE, new Location(world,subPosition.getX(),subPosition.getY(),subPosition.getZ()), 1, dustOptions);
			}
		}
	}
	
	private void playRotatingRing(Vector center, Vector side, Color color){
		DustOptions dustOptions = new DustOptions(color,1);
		float rotationSpeed = 0.12f;
		final int subParticleCount = 6;
		final float subCircleStep = (2 * (float) Math.PI) / subParticleCount;
		double size = (Math.sin(t * rotationSpeed * 0.2)*0.5+1)*radius / 6;
		for(int i = 0; i < subParticleCount; i++){
			double localX = getCircleX(subCircleStep * i + t * rotationSpeed) * size;
			double localY = getCircleZ(subCircleStep * i + t * rotationSpeed) * size;
			Vector local = side.clone().multiply(localX).add(new Vector(0,localY,0));
			Vector subPosition = local.add(center);
			world.spawnParticle(Particle.REDSTONE, new Location(world,subPosition.getX(),subPosition.getY(),subPosition.getZ()), 1, dustOptions);
		}
	}

	private double getCircleX(float radians){
		return Math.cos(radians);
	}

	private double getCircleZ(float radians){
		return Math.sin(radians);
	}
	
	public enum RingEffectType{
		Wisp,
		WhirlingBlade,
		RotatingRing
		;
		public RingEffectType next(){
			switch(this){
			case Wisp: return Wisp;
			case WhirlingBlade: return RotatingRing;
			case RotatingRing: return WhirlingBlade;
			default: return Wisp;
			}
		}
	}
}
