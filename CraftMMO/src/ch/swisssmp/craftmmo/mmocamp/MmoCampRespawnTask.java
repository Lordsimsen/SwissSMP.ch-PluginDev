package ch.swisssmp.craftmmo.mmocamp;

import ch.swisssmp.craftmmo.Main;

public class MmoCampRespawnTask implements Runnable{
	
	public final MmoCamp camp;
	public final String respawnHandling;
	public final int delay;
	public boolean active = true;
	
	public MmoCampRespawnTask(MmoCamp camp, String respawnHandling, int delay){
		this.camp = camp;
		this.respawnHandling = respawnHandling;
		this.delay = delay;
		Main.info("Das Camp mit der ID "+camp.mmo_camp_id+" wird sich in "+delay+" Sekunden regenerieren!");
	}
	
	@Override
	public void run() {
		camp.respawnTask = null;
		if(!active){
			return;
		}
		boolean doSpawning = camp.isActive() && camp.isSpawning();
		camp.remaining_cooldown = Math.max(0, camp.remaining_cooldown-this.delay);
		int mob_count = 0;
		switch(camp.respawnHandling){
		case "INSTANT":
			for(MmoCampSpawnpoint spawnpoint : camp.spawnpoints.values()){
				for(MmoCampMob mob : spawnpoint.mobs){
					int spawn_amount = mob.max_count-mob.live_entities.size()-mob.prepared_count;
					mob.prepared_count+=spawn_amount;
					mob_count+=spawn_amount;
				}
			}
			break;
		case "REGENERATE":
			MmoCampMob regenerateMob = camp.getIncomplete();
			if(regenerateMob!=null){
				if(regenerateMob.live_entities.size()<regenerateMob.max_count-regenerateMob.prepared_count){
					regenerateMob.prepared_count++;
					mob_count+=1;
					break;
				}
			}
			mob_count+=1;
			break;
		case "MULTIGENERATE":
			for(MmoCampSpawnpoint spawnpoint : camp.spawnpoints.values()){
				for(MmoCampMob multigenerateMob : spawnpoint.mobs){
					if(multigenerateMob.live_entities.size()<multigenerateMob.max_count-multigenerateMob.prepared_count){
						multigenerateMob.prepared_count++;
						mob_count+=1;
						break;
					}
				}
			}
			break;
		default:
			break;
		}
		if(doSpawning){
			camp.spawnAllPrepared();
		}
		if(!doSpawning){
			Main.info("Das Camp mit der ID "+camp.mmo_camp_id+" hat "+mob_count+" Mobs im Hintergrund regeneriert.");
		}
		else{
			Main.info("Das Camp mit der ID "+camp.mmo_camp_id+" hat "+mob_count+" Mobs regeneriert.");
		}
		if(camp.canRespawn()){
			camp.attemptSpawning();
		}
		else{
			camp.remaining_cooldown = 0;
		}
	}
	public void setActive(boolean active){
		this.active = active;
	}
}
