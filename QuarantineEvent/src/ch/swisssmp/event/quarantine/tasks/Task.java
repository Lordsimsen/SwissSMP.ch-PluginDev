package ch.swisssmp.event.quarantine.tasks;

public abstract class Task implements Runnable {
	
	private boolean initialized = false;
	private boolean completed = false;
	private boolean cancelled = false;
	private boolean finished = false;
	
	protected final void initialize() {
		if(initialized) return;
		initialized = true;
		this.onInitialize();
	}
	
	protected final void complete() {
		if(finished) return;
		completed = true;
		finished = true;
		onComplete();
		finish();
	}
	
	protected final void cancel() {
		if(finished) return;
		cancelled = true;
		finished = true;
		onCancel();
		finish();
	}
	
	private final void finish() {
		onFinish();
	}
	
	
	protected void onInitialize() {
		
	}
	
	
	protected void onComplete() {
		
	}
	
	
	protected void onCancel() {
		
	}
	
	
	protected void onFinish() {
		
	}
	
	
	public boolean isInitialized() {
		return initialized;
	}

	
	public boolean isCompleted() {
		return completed;
	}
	
	public boolean isCancelled() {
		return cancelled;
	}
	
	public boolean isFinished() {
		return finished;
	}
}
