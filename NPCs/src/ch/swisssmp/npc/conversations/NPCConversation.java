package ch.swisssmp.npc.conversations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import ch.swisssmp.npc.NPCInstance;
import ch.swisssmp.npc.NPCs;
import ch.swisssmp.npc.event.PlayerInteractNPCEvent;
import ch.swisssmp.npc.event.PlayerNPCConversationEvent;
import ch.swisssmp.utils.Mathf;
import ch.swisssmp.utils.SwissSMPler;

public class NPCConversation implements Runnable, Listener {
	
	private static HashMap<UUID,NPCConversation> conversations = new HashMap<UUID,NPCConversation>();

	private final NPCInstance npc;
	private final Player player;
	private final SwissSMPler swissSMPler;
	
	private final List<String> lines = new ArrayList<String>();
	
	private BukkitTask task;
	private final long maxTimeout;
	private long timeout = 0;
	private long sendTimeout = 0;
	private long maxSendTimeout = 15;
	private long minLineTime = 5;
	private long currentLineTime = 0;
	private long currentTotalLineTime = 0;
	private long maxCharacterTime = 2;
	private long maxLineTime = 2;
	
	private long buildupTime = 20;
	private float buildupProgress = 0;
	
	private double maxDistanceSquared = 100;

	private List<Runnable> completeListeners = new ArrayList<Runnable>();
	private List<Runnable> finishListeners = new ArrayList<Runnable>();
	
	private NPCConversation(NPCInstance npc, Player player, long maxTimeout){
		this.npc = npc;
		this.player = player;
		this.swissSMPler = SwissSMPler.get(player);
		this.maxTimeout = maxTimeout;
	}
	
	public NPCInstance getNPC(){
		return this.npc;
	}
	
	public Player getPlayer(){
		return this.player;
	}
	
	public void setLines(Collection<String> lines){
		this.lines.clear();
		this.lines.addAll(lines);
		this.resetTimes();
	}
	
	public void addLine(String line){
		if(line==null) return;
		this.lines.add(line);
		if(this.lines.size()>1) return;
		this.resetTimes();
	}
	
	public void removeLine(int index){
		if(index<0 || index>=lines.size()) return;
		this.lines.remove(index);
		if(this.lines.size()==0){
			this.complete();
			return;
		}
		this.resetTimes();
	}
	
	private void resetTimes(){
		if(this.lines.size()==0) return;
		this.buildupProgress = 0;
		this.currentLineTime = 0;
		this.currentTotalLineTime = 0;
		this.sendTimeout = this.maxSendTimeout;
		if(this.lines.size()>0){
			String line = this.lines.get(0);
			if(line==null){
				NullPointerException e = new NullPointerException("Current line in conversation is null!");
				e.printStackTrace();
				return;
			}
			this.maxLineTime = Mathf.roundToInt((20+line.length()/3*2)*maxCharacterTime);
		}
		else{
			this.maxLineTime = 40;
		}
	}
	
	public List<String> getLines(){
		return new ArrayList<String>(this.lines);
	}
	
	public String getLine(int index){
		if(index<0 || index>=lines.size()) return null;
		return this.lines.get(index);
	}
	
	public void setMaxDistance(double maxDistance){
		this.maxDistanceSquared = Math.pow(maxDistance, 2);
	}
	
	public void setMaxCharacterTime(long maxCharacterTime){
		this.maxCharacterTime = maxCharacterTime;
	}
	
	public void setBuildupTime(long buildupTime){
		this.buildupTime = buildupTime;
	}
	
	private void playSound(){
		World world = this.npc.getEntity().getWorld();
		world.playSound(this.npc.getEntity().getLocation(), Sound.ENTITY_VILLAGER_YES, SoundCategory.NEUTRAL, 1,1);
	}
	
	@EventHandler
	public void onPlayerInteractNPC(PlayerInteractNPCEvent event){
		if(event.getPlayer()!=this.player || !event.getNPC().getNPCId().equals(npc.getNPCId())) return;
		event.setCancelled(true);
		if(this.buildupProgress<1){
			this.buildupProgress = 1;
			this.sendLine();
			return;
		}
		if(this.currentLineTime<this.minLineTime) return;
		PlayerNPCConversationEvent conversationEvent = new PlayerNPCConversationEvent(player, npc, this);
		Bukkit.getPluginManager().callEvent(conversationEvent);
		if(conversationEvent.isCancelled()){
			return;
		}
		this.removeLine(0);
		this.playSound();
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event){
		if(event.getPlayer()!=this.player) return;
		this.cancel();
	}
	
	@Override
	public void run() {
		if(this.lines.size()==0){
			this.cancel();
			return;
		}
		Location npcLocation = this.npc.getEntity().getLocation();
		if(npcLocation.getWorld()!=player.getWorld()){
			this.cancel();
			return;
		}
		double distanceSquared = npcLocation.distanceSquared(player.getLocation());
		if(distanceSquared > maxDistanceSquared){
			this.cancel();
			return;
		}
		if(this.buildupProgress<1 && this.buildupTime>0){
			this.buildupProgress+=1f/this.buildupTime;
			String line = this.lines.get(0);
			int length = Mathf.ceilToInt(line.length()*buildupProgress);
			int maxLength = line.length();
			String output = line.substring(0, Math.min(length,maxLength));
			for(int i = 0; i < maxLength-length; i++){
				output+=" ";
			}
			swissSMPler.sendActionBar(output);
		}
		else{
			this.sendTimeout++; //to take a break between signals
			if(this.sendTimeout>=this.maxSendTimeout && this.currentTotalLineTime<this.maxLineTime){
				this.sendTimeout = 0;
				this.sendLine();
			}
			this.timeout++; //to ensure conversation stops automatically after no user action is taken
			if(this.timeout>=this.maxTimeout){
				this.finish();
			}
		}
		this.currentLineTime++; //to ensure you cant accidentally skip through
		this.currentTotalLineTime++; //to make line fade out after appropriate reading time
	}
	
	private void sendLine(){
		if(this.lines.size()==0) return;
		swissSMPler.sendActionBar(this.lines.get(0));
	}
	
	public void cancel(){
		this.finish();
	}
	
	private void complete(){
		this.triggerListeners(completeListeners);
		this.finish();
	}
	
	private void finish(){
		HandlerList.unregisterAll(this);
		if(this.task!=null) this.task.cancel();
		this.triggerListeners(finishListeners);
		conversations.remove(player.getUniqueId());
	}
	
	private void triggerListeners(Collection<Runnable> runnables){
		for(Runnable runnable : runnables){
			try{
				runnable.run();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public void onComplete(Runnable runnable){
		this.completeListeners.add(runnable);
	}
	
	public void onFinish(Runnable runnable){
		this.finishListeners.add(runnable);
	}

	public static NPCConversation start(NPCInstance npc, Player player, long maxTimeout){
		NPCConversation conversation = new NPCConversation(npc, player, maxTimeout);
		conversation.task = Bukkit.getScheduler().runTaskTimer(NPCs.getInstance(), conversation, 0, 1);
		Bukkit.getPluginManager().registerEvents(conversation, NPCs.getInstance());
		conversations.put(player.getUniqueId(), conversation);
		conversation.playSound();
		return conversation;
	}
	
	public static NPCConversation get(Player player){
		if(player==null) return null;
		return conversations.get(player.getUniqueId());
	}
}
