package ch.swisssmp.craftmmo.mmoentity;

import org.bukkit.Bukkit;

import ch.swisssmp.craftmmo.Main;
import ch.swisssmp.craftmmo.mmocamp.MmoCampMob;
import ch.swisssmp.craftmmo.mmocamp.MmoCampSpawnpoint;
import net.minecraft.server.v1_12_R1.EntityInsentient;
import net.minecraft.server.v1_12_R1.NBTTagCompound;

public class MmoEntitySaveData {
	public int mmo_mob_id = -1;
	public int mmo_camp_spawnpoint_id = -1;
	public int mmo_camp_mob_id = -1;
	public int[] spawnpoint = null;
	public MmoEntitySaveData(int mmo_mob_id){
		this.mmo_mob_id = mmo_mob_id;
	}
	private MmoEntitySaveData(NBTTagCompound nbttagcompound){
		try{
			if(nbttagcompound.hasKey("mmo_mob_id")){
				this.mmo_mob_id = nbttagcompound.getInt("mmo_mob_id");
			}
			if(nbttagcompound.hasKey("mmo_camp_mob_id") && nbttagcompound.hasKey("mmo_camp_spawnpoint_id")){
				this.mmo_camp_mob_id = nbttagcompound.getInt("mmo_camp_mob_id");
				this.mmo_camp_spawnpoint_id = nbttagcompound.getInt("mmo_camp_spawnpoint_id");
			}
			if(nbttagcompound.hasKey("home_x")&&nbttagcompound.hasKey("home_y")&&nbttagcompound.hasKey("home_z")){
				this.spawnpoint = new int[3];
				this.spawnpoint[0] = nbttagcompound.getInt("home_x");
				this.spawnpoint[1] = nbttagcompound.getInt("home_y");
				this.spawnpoint[2] = nbttagcompound.getInt("home_z");
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public static MmoEntitySaveData load(EntityInsentient entity, NBTTagCompound nbttagcompound){
		MmoEntitySaveData data = new MmoEntitySaveData(nbttagcompound);
		try{
			if(data.mmo_camp_spawnpoint_id>=0 && data.mmo_camp_mob_id>=0){
				MmoCampSpawnpoint spawnpoint = MmoCampSpawnpoint.get(data.mmo_camp_spawnpoint_id);
				if(spawnpoint==null){
					MmoCampMob.pendingAssignments.add((IControllable)entity);
				}
				else{
					for(MmoCampMob mob : spawnpoint.mobs){
						if(mob.mmo_camp_mob_id==data.mmo_camp_mob_id){
							mob.live_entities.add(entity.getBukkitEntity());
							MmoCampMob.instances.put(entity.getUniqueID(), mob);
							break;
						}
					}
				}
			}
			MmoMob mmoMob = MmoMob.get(data.mmo_mob_id);
			if(mmoMob!=null){
				DelayedAIAssignmentTask task = new DelayedAIAssignmentTask(mmoMob.ai, entity);
				Bukkit.getScheduler().runTaskLater(Main.plugin, task, 5L);
			}
			else if(entity instanceof IControllable){
				MmoMob.pending_AI_assignments.put((IControllable)entity, data.mmo_mob_id);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return data;
	}
	public void saveTo(NBTTagCompound nbttagcompound){
		try{
			nbttagcompound.setInt("mmo_mob_id", mmo_mob_id);
			if(mmo_camp_mob_id>=0 && mmo_camp_spawnpoint_id>=0){
				nbttagcompound.setInt("mmo_camp_mob_id", mmo_camp_mob_id);
				nbttagcompound.setInt("mmo_camp_spawnpoint_id", mmo_camp_spawnpoint_id);
			}
			if(spawnpoint!=null){
				nbttagcompound.setInt("home_x", spawnpoint[0]);
				nbttagcompound.setInt("home_y", spawnpoint[1]);
				nbttagcompound.setInt("home_z", spawnpoint[2]);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
