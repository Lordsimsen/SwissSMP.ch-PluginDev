package ch.swisssmp.particles.modules;

import ch.swisssmp.particles.data.Burst;

public class EmissionModule extends Module {
	public int burstCount = 0;
	public int rateOverDistance = 0;
	public float rateOverDistanceMultiplier = 1f;
	public int rateOverTime = 30;
	public float rateOverTimeMultiplier;
	
	private Burst[] bursts;
	
	public Burst getBurst(int index){
		return bursts.length>index && index>=0 ? bursts[index] : null;
	}
	
	public Burst[] getBursts(){
		return bursts;
	}

	public void setBurst(int index, Burst burst){
		if(bursts.length<=index || index<0) return;
		bursts[index] = burst;
	}
	
	public void setBursts(Burst[] bursts){
		this.bursts = bursts;
	}
}
