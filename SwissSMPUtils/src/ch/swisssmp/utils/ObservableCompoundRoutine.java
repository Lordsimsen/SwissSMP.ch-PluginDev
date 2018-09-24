package ch.swisssmp.utils;

import java.util.List;


public class ObservableCompoundRoutine extends ObservableRoutine {

	private final List<ObservableRoutine> routines;
	private int currentIndex = 0;
	
	public ObservableCompoundRoutine(List<ObservableRoutine> runnables){
		this.routines = runnables;
	}
	
	@Override
	public float getProgress() {
		return currentIndex/(float)routines.size()+(currentIndex<routines.size()?routines.get(currentIndex).getProgress():0);
	}

	@Override
	public String getProgressLabel() {
		return (currentIndex<routines.size()?routines.get(currentIndex).getProgressLabel():"Abgeschlossen!");
	}

	@Override
	public void run() {
		if(currentIndex<routines.size()){
			try{
				routines.get(currentIndex).run();
				if(routines.get(currentIndex).getProgress()<1) return;
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		else{
			this.finish();
		}
		currentIndex++;
	}
}
