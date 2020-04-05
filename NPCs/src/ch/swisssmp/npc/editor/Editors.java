package ch.swisssmp.npc.editor;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Ageable;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Zombie;

import ch.swisssmp.npc.NPCEditorView;
import ch.swisssmp.npc.NPCInstance;
import ch.swisssmp.npc.editor.cat.CatEditor;
import ch.swisssmp.npc.editor.parrot.ParrotEditor;
import ch.swisssmp.npc.editor.villager.VillagerEditor;

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
		
		if(visible instanceof Cat) {
			editors.add(new CatEditor(view, (Cat) visible));
		}
		
		if(visible instanceof Parrot) {
			editors.add(new ParrotEditor(view, (Parrot) visible));
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
