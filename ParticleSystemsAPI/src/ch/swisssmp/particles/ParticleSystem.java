package ch.swisssmp.particles;

import ch.swisssmp.particles.modules.CollisionModule;
import ch.swisssmp.particles.modules.ColorBySpeedModule;
import ch.swisssmp.particles.modules.ColorOverLifetimeModule;
import ch.swisssmp.particles.modules.CustomDataModule;
import ch.swisssmp.particles.modules.EmissionModule;
import ch.swisssmp.particles.modules.ForceOverLifetimeModule;
import ch.swisssmp.particles.modules.InheritVelocityModule;
import ch.swisssmp.particles.modules.LightsModule;
import ch.swisssmp.particles.modules.LimitVelocityOverLifetimeModule;
import ch.swisssmp.particles.modules.MainModule;
import ch.swisssmp.particles.modules.NoiseModule;
import ch.swisssmp.particles.modules.RotationBySpeedModule;
import ch.swisssmp.particles.modules.RotationOverLifetimeModule;
import ch.swisssmp.particles.modules.ShapeModule;
import ch.swisssmp.particles.modules.SizeBySpeedModule;
import ch.swisssmp.particles.modules.SizeOverLifetimeModule;
import ch.swisssmp.particles.modules.SubEmittersModule;
import ch.swisssmp.particles.modules.TextureSheetAnimationModule;
import ch.swisssmp.particles.modules.TrailModule;
import ch.swisssmp.particles.modules.TriggerModule;
import ch.swisssmp.particles.modules.VelocityOverLifetimeModule;

public class ParticleSystem {

	private MainModule main = new MainModule();
	private EmissionModule emission = new EmissionModule();
	private ShapeModule shape = new ShapeModule();
	private VelocityOverLifetimeModule velocityOverLifetime = new VelocityOverLifetimeModule();
	private LimitVelocityOverLifetimeModule limitVelocityOverLifetime = new LimitVelocityOverLifetimeModule();
	private InheritVelocityModule inheritVelocity = new InheritVelocityModule();
	private ForceOverLifetimeModule forceOverLifetime = new ForceOverLifetimeModule();
	private ColorOverLifetimeModule colorOverLifetime = new ColorOverLifetimeModule();
	private ColorBySpeedModule colorBySpeed = new ColorBySpeedModule();
	private SizeOverLifetimeModule sizeOverLifetime = new SizeOverLifetimeModule();
	private SizeBySpeedModule sizeBySpeed = new SizeBySpeedModule();
	private RotationOverLifetimeModule rotationOverLifetime = new RotationOverLifetimeModule();
	private RotationBySpeedModule rotationBySpeed = new RotationBySpeedModule();
	private NoiseModule noise = new NoiseModule();
	private CollisionModule collision = new CollisionModule();
	private TriggerModule triggers = new TriggerModule();
	private SubEmittersModule subEmitters = new SubEmittersModule();
	private TextureSheetAnimationModule textureSheetAnimation = new TextureSheetAnimationModule();
	private LightsModule lights = new LightsModule();
	private TrailModule trails = new TrailModule();
	private CustomDataModule customData = new CustomDataModule();
	
	private org.bukkit.Particle particleType = org.bukkit.Particle.REDSTONE;

	
	public ParticleSystem(){
		main.enabled = true;
		emission.enabled = true;
		shape.enabled = true;
	}
	
	public void setParticleType(org.bukkit.Particle particleType){
		this.particleType = particleType;
	}
	
	public org.bukkit.Particle getParticleType(){
		return this.particleType;
	}
	
	public CollisionModule getCollision(){
		if(!collision.enabled) collision.enabled = true;
		return collision;
	}
	public ColorBySpeedModule getColorBySpeed(){
		if(!colorBySpeed.enabled) colorBySpeed.enabled = true;
		return colorBySpeed;
	}
	public ColorOverLifetimeModule getColorOverLifetime(){
		if(!colorOverLifetime.enabled) colorOverLifetime.enabled = true;
		return colorOverLifetime;
	}
	public CustomDataModule getCustomData(){
		if(!customData.enabled) customData.enabled = true;
		return customData;
	}
	public EmissionModule getEmission(){
		if(!emission.enabled) emission.enabled = true;
		return emission;
	}
	public ForceOverLifetimeModule getForceOverLifetime(){
		if(!forceOverLifetime.enabled) forceOverLifetime.enabled = true;
		return forceOverLifetime;
	}
	public InheritVelocityModule getInheritVelocity(){
		if(!inheritVelocity.enabled) inheritVelocity.enabled = true;
		return inheritVelocity;
	}
	public LightsModule getLights(){
		if(!lights.enabled) lights.enabled = true;
		return lights;
	}
	public LimitVelocityOverLifetimeModule getLimitVelocityOverLifetime(){
		if(!limitVelocityOverLifetime.enabled) limitVelocityOverLifetime.enabled = true;
		return limitVelocityOverLifetime;
	}
	public MainModule getMain(){
		if(!main.enabled) main.enabled = true;
		return main;
	}
	public NoiseModule getNoise(){
		if(!noise.enabled) noise.enabled = true;
		return noise;
	}
	public RotationBySpeedModule getRotationBySpeed(){
		if(!rotationBySpeed.enabled) rotationBySpeed.enabled = true;
		return rotationBySpeed;
	}
	public RotationOverLifetimeModule getRotationOverLifetime(){
		if(!rotationOverLifetime.enabled) rotationOverLifetime.enabled = true;
		return rotationOverLifetime;
	}
	public ShapeModule getShape(){
		if(!shape.enabled) shape.enabled = true;
		return shape;
	}
	public SizeBySpeedModule getSizeBySpeed(){
		if(!sizeBySpeed.enabled) sizeBySpeed.enabled = true;
		return sizeBySpeed;
	}
	public SizeOverLifetimeModule getSizeOverLifetime(){
		if(!sizeOverLifetime.enabled) sizeOverLifetime.enabled = true;
		return sizeOverLifetime;
	}
	public SubEmittersModule getSubEmitters(){
		if(!subEmitters.enabled) subEmitters.enabled = true;
		return subEmitters;
	}
	public TextureSheetAnimationModule getTextureSheetAnimation(){
		if(!textureSheetAnimation.enabled) textureSheetAnimation.enabled = true;
		return textureSheetAnimation;
	}
	public TrailModule getTrails(){
		if(!trails.enabled) trails.enabled = true;
		return trails;
	}
	public TriggerModule getTriggers(){
		if(!triggers.enabled) triggers.enabled = true;
		return triggers;
	}
	public VelocityOverLifetimeModule getVelocityOverLifetime(){
		if(!velocityOverLifetime.enabled) velocityOverLifetime.enabled = true;
		return velocityOverLifetime;
	}
}
