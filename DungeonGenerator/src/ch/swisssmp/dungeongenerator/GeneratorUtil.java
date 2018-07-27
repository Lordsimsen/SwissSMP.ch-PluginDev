package ch.swisssmp.dungeongenerator;

import java.util.ArrayList;
import java.util.Collections;

public class GeneratorUtil {
	public static int getDistanceToStart(GenerationPart... neighbours){
		ArrayList<Integer> distances = new ArrayList<Integer>();
		for(GenerationPart neighbour : neighbours){
			if(neighbour==null || neighbour.getDistanceToStart()<0) continue;
			distances.add(neighbour.getDistanceToStart());
		}
		return Collections.min(distances)+1;
	}
}
