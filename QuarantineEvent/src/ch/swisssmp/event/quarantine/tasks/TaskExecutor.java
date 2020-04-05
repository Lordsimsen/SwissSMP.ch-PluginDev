package ch.swisssmp.event.quarantine.tasks;

public class TaskExecutor implements Runnable {

	private Task activeTask;
	private Task task;
	
	@Override
	public void run() {
		if(task==null || task!=activeTask) {
			stopActiveTask();
		}
		if(task!=null) {
			activeTask = task;
			if(!task.isInitialized()) task.initialize();
			if(!task.isFinished()) task.run();
		}
	}

	public void setTask(Task task) {
		stopActiveTask();
		this.task = task;
		task.initialize();
	}
	
	public void stopActiveTask() {
		if(activeTask==null || activeTask.isFinished()) return;
		activeTask.cancel();
		activeTask = null;
	}
}
