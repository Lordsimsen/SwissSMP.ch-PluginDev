package ch.swisssmp.npc.editor;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Zombie;

import ch.swisssmp.npc.NPCEditorView;
import ch.swisssmp.npc.NPCInstance;

public class Editors {
	public static List<AbstractEditor> get(NPCEditorView view){
		List<AbstractEditor> editors = new ArrayList<AbstractEditor>();
		NPCInstance npc = view.getNPC();
		Entity visible = npc.getEntity();
		
		//the name
		editors.add(new BaseConfiguration(view, view.getNPC()));
		
		if(visible instanceof Villager){
			//the profession, the career
			editors.add(new VillagerEditor(view, (Villager) visible));
		}
		
		if(visible instanceof Ageable){
			//the age
			editors.add(new AgeableEditor(view, (Ageable) visible));
		}
		
		if(visible instanceof Zombie || visible instanceof Skeleton){
			//the equipment
		}
		
		if(visible instanceof Sheep){
			//the color
		}
		
		return editors;
	}
}
