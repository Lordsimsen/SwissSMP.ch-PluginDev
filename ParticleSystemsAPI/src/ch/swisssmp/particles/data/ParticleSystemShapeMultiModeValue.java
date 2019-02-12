package ch.swisssmp.particles.data;

public enum ParticleSystemShapeMultiModeValue {
	Random, //Generate points randomly. (Default)
	Loop, //Animate the emission point around the shape.
	PingPong, //Animate the emission point around the shape, alternating between clockwise and counter-clockwise directions.
	BurstSpread //Distribute new particles around the shape evenly.
}
