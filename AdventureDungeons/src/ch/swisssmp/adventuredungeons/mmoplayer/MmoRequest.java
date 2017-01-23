package ch.swisssmp.adventuredungeons.mmoplayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import ch.swisssmp.adventuredungeons.Main;

public class MmoRequest {
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
	private final static HashMap<Integer, MmoRequest> pendingRequests = new HashMap<Integer, MmoRequest>();
	private final int index;
	private final String question;
	private final LinkedHashMap<String, String> options = new LinkedHashMap<String, String>();
	private UUID recipient = null;
	private BukkitTask task = null;
	
	public MmoRequest(String question){
		this.index = indexer;
		this.question = question;
		pendingRequests.put(this.index, this);
		indexer++;
	}
	public void addOption(String label, String command){
		options.put(label, command);
	}
	public void send(UUID player_uuid){
		this.recipient = player_uuid;
		ArrayList<String> choices = new ArrayList<String>();
		int line_index = 0;
		for(Entry<String, String> entry : options.entrySet()){
			choices.add("{'text':'["+entry.getKey()+"]','color':'"+colors[line_index]+"','clickEvent':{'action':'run_command','value':'/choose "+String.valueOf(this.index)+" "+entry.getKey()+"'}}");
			line_index++;
			if(line_index>=colors.length)
				line_index = 0;
		}
	    String json_request = ("{'text':'','extra':["+String.join(",", choices)+"]}").replace("'", "\"");
	    if(question!=null) MmoPlayer.sendMessage(player_uuid, question);
	    MmoPlayer.sendRawMessage(player_uuid, json_request);
	    this.task = Bukkit.getScheduler().runTaskLater(Main.plugin, new Runnable(){
			@Override
			public void run() {
				pendingRequests.remove(index);
			}
	    	
	    }, 1200L);
	}
	public void choose(Player player, String key){
		Main.debug(player.getName()+" chose "+key);
		if(player==null || !this.recipient.equals(player.getUniqueId())) {
			return;
		}
		Main.debug("Dispatching command "+options.get(key));
		Bukkit.dispatchCommand(player, options.get(key));
		task.cancel();
		pendingRequests.remove(this.index);
	}
	public static MmoRequest get(String stringIndex){
		Integer index = Integer.valueOf(stringIndex);
		if(index==null) return null;
		else return get(index);
	}
	public static MmoRequest get(int index){
		return pendingRequests.get(index);
	}
}
