package ch.swisssmp.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class ChatRequest {
	private static String[] colors = new String[]{
			"green",
			"red",
			"blue",
			"gold",
			"light_purple",
			"dark_aqua",
			"dark_red",
			"aqua",
			"dark_purple",
			"dark_blue"
	};
	private int indexer = 0;
	private final static HashMap<Integer, ChatRequest> pendingRequests = new HashMap<Integer, ChatRequest>();
	private final int index;
	private final String question;
	private final LinkedHashMap<String, String> options = new LinkedHashMap<String, String>();
	private UUID recipient = null;
	private BukkitTask task = null;
	
	public ChatRequest(String question){
		this.index = indexer;
		this.question = question;
		pendingRequests.put(this.index, this);
		indexer++;
	}
	public void addOption(String label, String command){
		options.put(label, command);
	}
	public void send(UUID player_uuid){
	    this.task = Bukkit.getScheduler().runTaskLater(SwissSMPUtils.plugin, new Runnable(){
			@Override
			public void run() {
				pendingRequests.remove(index);
			}
	    	
	    }, 1200L);
		this.recipient = player_uuid;
		SwissSMPler swisssmpler = SwissSMPler.get(player_uuid);
		if(swisssmpler==null) return;
		ArrayList<String> choices = new ArrayList<String>();
		int line_index = 0;
		for(Entry<String, String> entry : options.entrySet()){
			choices.add("{'text':'["+entry.getKey()+"]','color':'"+colors[line_index]+"','clickEvent':{'action':'run_command','value':'/choose "+String.valueOf(this.index)+" "+entry.getKey()+"'}}");
			line_index++;
			if(line_index>=colors.length)
				line_index = 0;
		}
	    String json_request = ("{'text':'','extra':["+String.join(",", choices)+"]}").replace("'", "\"");
	    if(question!=null) swisssmpler.sendMessage(question);
	    swisssmpler.sendRawMessage(json_request);
	}
	public void choose(Player player, String key){
		if(player==null || !this.recipient.equals(player.getUniqueId())) {
			return;
		}
		Bukkit.dispatchCommand(player, options.get(key));
		task.cancel();
		pendingRequests.remove(this.index);
	}
	public static ChatRequest get(String stringIndex){
		Integer index = Integer.valueOf(stringIndex);
		if(index==null) return null;
		else return get(index);
	}
	public static ChatRequest get(int index){
		return pendingRequests.get(index);
	}
}
