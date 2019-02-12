package ch.swisssmp.npc.modules;

import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;

import ch.swisssmp.npc.NPCInstance;

public class AgeableModule extends FactoryModule {

	private boolean adult = true;
	private int age = -1;
	private boolean ageLock;
	private boolean breed;
	
	public void setAdult(boolean adult){
		this.adult = adult;
	}
	
	public boolean isAdult(){
		return adult;
	}
	
	public void setAge(int age){
		this.age = age;
	}
	
	public int getAge(){
		return this.age;
	}
	
	public void setAgeLock(boolean ageLock){
		this.ageLock = ageLock;
	}
	
	public boolean hasAgeLock(){
		return this.ageLock;
	}
	
	public void setBreed(boolean breed){
		this.breed = breed;
	}
	
	public boolean doesBreed(){
		return this.breed;
	}
	
	@Override
	public void applyData(NPCInstance npc) {
		Entity visible = npc.getEntity();
		if(!(visible instanceof Ageable)) return;
		Ageable ageable = (Ageable) visible;
		if(adult) ageable.setAdult();
		else ageable.setBaby();
		if(this.age>=0) ageable.setAge(age);
		ageable.setAgeLock(ageLock);
		ageable.setBreed(breed);
	}

}
