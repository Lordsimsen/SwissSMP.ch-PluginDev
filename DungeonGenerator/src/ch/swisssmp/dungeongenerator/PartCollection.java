package ch.swisssmp.dungeongenerator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PartCollection implements Iterable<ProxyGeneratorPart>{
	
	private final DungeonGenerator dungeonGenerator;
	private List<ProxyGeneratorPart> parts;
	
	private PartCollection(DungeonGenerator dungeonGenerator){
		this.dungeonGenerator = dungeonGenerator;
	}
	
	protected List<ProxyGeneratorPart> getParts(){
		return this.parts;
	}

	@Override
	public Iterator<ProxyGeneratorPart> iterator() {
		return parts.iterator();
	}
	
	public ProxyGeneratorPart get(int index){
		return parts.get(index);
	}
	
	private void collectParts(){
		this.parts = this.generateRotatedTemplateVersions(this.dungeonGenerator.getTemplateParts());
		
	}
	
	/*
	 * Generates rotated proxy versions for all GeneratorParts
	 */
	private List<ProxyGeneratorPart> generateRotatedTemplateVersions(List<GeneratorPart> templateParts){
		List<ProxyGeneratorPart> result = new ArrayList<ProxyGeneratorPart>();
		int[] rotations = new int[]{0,90,180,270};
		List<Integer> validRotations;
		for(GeneratorPart part : templateParts){
			for(int i = 0; i < rotations.length; i++){
				validRotations = part.getRotations();
				if(validRotations!=null && !validRotations.contains(rotations[i])){
					System.out.println("Skipping rotation "+rotations[i]+" for part "+part.getInfoString());
					continue;
				}
				result.add(new ProxyGeneratorPart(part, i));
			}
		}
		return result;
	}
	
	protected static PartCollection get(DungeonGenerator dungeonGenerator){
		PartCollection result = new PartCollection(dungeonGenerator);
		result.collectParts();
		return result;
	}
}
