package ch.swisssmp.particles.modules;

import ch.swisssmp.particles.data.MinMaxCurve;
import ch.swisssmp.particles.data.MinMaxGradient;
import ch.swisssmp.particles.data.ParticleSystemEmitterVelocityMode;
import ch.swisssmp.particles.data.ParticleSystemSimulationSpace;

public class MainModule extends Module {
	public float duration = 5f; //The duration of the particle system in seconds.
	public ParticleSystemEmitterVelocityMode emitterVelocityMode; //Control how the Particle System calculates its velocity, when moving in the world.
	public float gravityModifier = 0;
	public float gravityModifierMultiplier = 1f;
	public boolean loop = true;
	public int maxParticles = 1000;
	public boolean playOnAwake = false;
	public boolean prewarm = false;
	public ParticleSystemSimulationSpace simulationSpace = ParticleSystemSimulationSpace.Local;
	public float simulationSpeed = 1f;
	public MinMaxGradient startColor = new MinMaxGradient();
	public float startDelay = 0;
	public float startDelayMultiplier = 1f;
	public MinMaxCurve startLifetime = new MinMaxCurve();
	public float startLifetimeMultiplier = 1f;
	public MinMaxCurve startRotation  = new MinMaxCurve();
	public float startRotationMultiplier = 1f;
	public MinMaxCurve startSize = new MinMaxCurve();
	public float startSizeMultiplier = 1f;
	public MinMaxCurve startSpeed = new MinMaxCurve();
	public float startSpeedMultiplier = 1f;
}
