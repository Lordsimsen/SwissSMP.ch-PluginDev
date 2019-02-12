package ch.swisssmp.npc.modules;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Career;
import org.bukkit.entity.Villager.Profession;

import ch.swisssmp.npc.NPCInstance;

public class VillagerModule extends AgeableModule {

	private Profession profession;
	private Career career;
	
	public void setProfession(Profession profession){
		this.profession = profession;
	}
	
	public Profession getProfession(){
		return profession;
	}
	
	public void setCareer(Career career){
		this.career = career;
	}
	
	public Career getCareer(){
		return career;
	}
	
	@Override
	public void applyData(NPCInstance npc) {
		super.applyData(npc);
		Entity visible = npc.getEntity();
		if(!(visible instanceof Villager)) return;
		Villager villager = (Villager) visible;
		if(profession!=null) villager.setProfession(profession);
		if(career!=null) villager.setCareer(career);
	}

}
