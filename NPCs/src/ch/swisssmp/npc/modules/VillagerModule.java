package ch.swisssmp.npc.modules;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.entity.Villager.Type;

import ch.swisssmp.npc.NPCInstance;

public class VillagerModule extends AgeableModule {

	private Profession profession;
	private Type type;
	
	public void setProfession(Profession profession){
		this.profession = profession;
	}
	
	public Profession getProfession(){
		return profession;
	}
	
	public void setVillagerType(Type type){
		this.type = type;
	}
	
	public Type getVillagerType(){
		return type;
	}
	
	@Override
	public void applyData(NPCInstance npc) {
		super.applyData(npc);
		Entity visible = npc.getEntity();
		if(!(visible instanceof Villager)) return;
		Villager villager = (Villager) visible;
		if(profession!=null) villager.setProfession(profession);
		if(type!=null) villager.setVillagerType(type);
	}

}
