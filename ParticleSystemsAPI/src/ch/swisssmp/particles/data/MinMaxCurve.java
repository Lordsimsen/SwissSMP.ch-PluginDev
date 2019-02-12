package ch.swisssmp.particles.data;

public class MinMaxCurve {
	public int constant = 0;
	public int constantMax = 0;
	public int constantMin = 0;
	public AnimationCurve curve;
	public AnimationCurve curveMax;
	public AnimationCurve curveMin;
	public ParticleSystemCurveMode mode = ParticleSystemCurveMode.Constant;
}
