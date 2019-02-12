package ch.swisssmp.stalker;

import java.util.ArrayList;
import java.util.List;

import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class DataLink extends Thread{
	
	private List<LogEntry> queue = new ArrayList<LogEntry>();
	
	private boolean alive = true;
	
	@Override
	public void run(){
		while(alive){
			try {
				Thread.sleep(1000);
				try{
					upload();
				}
				catch(Exception e){
					e.printStackTrace();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	protected void push(LogEntry logEntry){
		synchronized(queue){
			queue.add(logEntry);
		}
	}
	
	private void upload(){
		LogEntry[] queue = this.getCurrentQueue();
		if(queue.length==0) return;
		List<Integer> mainIds = this.uploadMainEntries(queue);
		this.uploadExtraEntries(queue, mainIds);
	}
	
	private LogEntry[] getCurrentQueue(){
		LogEntry[] queue;
		synchronized(this.queue){
			queue = new LogEntry[this.queue.size()];
			this.queue.toArray(queue);
			this.queue.clear();
		}
		return queue;
	}
	
	private List<Integer> uploadMainEntries(LogEntry[] entries){
		List<String> argumentsList = new ArrayList<String>();
		for(int i = 0; i < entries.length; i++){
			String[] logEntryData = entries[i].getData("data["+i+"]");
			for(String logEntryLine : logEntryData){
				argumentsList.add(logEntryLine);
			}
		}
		String[] args = new String[argumentsList.size()];
		argumentsList.toArray(args);
		YamlConfiguration result = DataSource.getYamlResponse(Stalker.getInstance(), "log.php", args);
		return result.getIntegerList("main_ids");
	}
	
	private void uploadExtraEntries(LogEntry[] entries, List<Integer> mainIds){
		List<String> argumentsList = new ArrayList<String>();
		for(int i = 0; i < mainIds.size() && i < entries.length; i++){
			int mainId = mainIds.get(i);
			if(mainId<=0 || !entries[i].hasExtraData()) continue;
			String[] logEntryData = entries[i].getExtraData("data["+i+"]", mainId);
			for(String logEntryLine : logEntryData){
				argumentsList.add(logEntryLine);
			}
		}
		String[] args = new String[argumentsList.size()];
		argumentsList.toArray(args);
		DataSource.getResponse(Stalker.getInstance(), "log_extra.php", args);
	}
}
