package ch.swisssmp.particles.modules;

import org.bukkit.util.Vector;

import com.adamratana.rotationvectorcompass.math.Quaternion;

import ch.swisssmp.particles.data.ParticleSystemShapeMultiModeValue;
import ch.swisssmp.particles.data.ParticleSystemShapeType;

public class ShapeModule extends Module {
	public boolean alignToDirection = false;
	public float angle = 25;
	public float arc = 360;
	public ParticleSystemShapeMultiModeValue arcMode = ParticleSystemShapeMultiModeValue.Random;
	public float arcSpeed = 1;
	public float arcSpeedMultiplier = 1;
	public float arcSpread = 1;
	public Vector boxThickness = new Vector();
	public float donutRadius = 1f;
	public float length = 1f;
	public Vector position = new Vector();
	public float radius = 1f;
	public ParticleSystemShapeMultiModeValue radiusMode = ParticleSystemShapeMultiModeValue.Random;
	public float radiusSpeed = 1f;
	public float radiusSpeedMultiplier = 1f;
	public float radiusSpread = 1f;
	public float radiusThickness = 1f;
	public float randomDirectionAmount = 0f;
	public float randomPositionAmount = 0f;
	public Quaternion rotation = Quaternion.identity;
	public Vector scale = new Vector(1,1,1);
	public ParticleSystemShapeType shapeType = ParticleSystemShapeType.Cone;
	public float sphericalDirectionAmount = 0;
}
