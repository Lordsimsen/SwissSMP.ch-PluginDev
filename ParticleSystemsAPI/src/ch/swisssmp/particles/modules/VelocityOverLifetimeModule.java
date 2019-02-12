package ch.swisssmp.particles.modules;

import ch.swisssmp.particles.data.MinMaxCurve;
import ch.swisssmp.particles.data.ParticleSystemSimulationSpace;

public class VelocityOverLifetimeModule extends Module {
	public MinMaxCurve orbitalOffsetX = new MinMaxCurve();
	public float orbitalOffsetXMultiplier = 1f;
	public MinMaxCurve orbitalOffsetY = new MinMaxCurve();
	public float orbitalOffsetYMultiplier = 1f;
	public MinMaxCurve orbitalOffsetZ = new MinMaxCurve();
	public float orbitalOffsetZMultiplier = 1f;
	public MinMaxCurve orbitalX = new MinMaxCurve();
	public float orbitalXMultiplier = 1f;
	public MinMaxCurve radial = new MinMaxCurve();
	public float radialMultiplier = 1f;
	public ParticleSystemSimulationSpace space = ParticleSystemSimulationSpace.Local;
	public MinMaxCurve speedModifier = new MinMaxCurve();
	public float speedModifierMultiplier = 1f;
	public MinMaxCurve x = new MinMaxCurve();
	public float xMultiplier = 1f;
	public MinMaxCurve y = new MinMaxCurve();
	public float yMultiplier = 1f;
	public MinMaxCurve z = new MinMaxCurve();
	public float zMultiplier = 1f;
}
