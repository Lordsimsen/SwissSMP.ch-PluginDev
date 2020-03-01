package ch.swisssmp.lift;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Debug {
	protected static boolean active = false;
	protected static void Log(Object o){
		if(!active) return;
		Exception e = new Exception();
		System.out.println(o+"\n"+String.join("\n", Arrays.stream(e.getStackTrace()).skip(1).limit(2).map(s->s.toString()).collect(Collectors.toList())));
	}
}
