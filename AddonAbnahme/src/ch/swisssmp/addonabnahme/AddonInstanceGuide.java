package ch.swisssmp.addonabnahme;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import ch.swisssmp.city.City;
import ch.swisssmp.npc.NPCInstance;
import ch.swisssmp.npc.conversations.NPCConversation;
import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;

public class AddonInstanceGuide {
	
	public static NPCInstance create(Location location, AddonInstanceInfo instance){
		NPCInstance npc = NPCInstance.create(EntityType.VILLAGER, location);
		instance.apply(npc);
		report(instance, true);
		return npc;
	}
	
	public static void remove(NPCInstance npc, AddonInstanceInfo instance){
		npc.remove();
		report(instance, false);
	}
	
	public static void report(AddonInstanceInfo instance, boolean active){
		instance.setGuideActive(active);
		DataSource.getResponse(AddonAbnahme.getInstance(), "report_guide.php", new String[]{
				"addon="+URLEncoder.encode(instance.getAddonInfo().getAddonId()),
				"city="+instance.getCity().getId(),
				"active="+(active ? 1 : 0)
		});
	}
	
	public static void openGuideView(Player player, NPCInstance npc){
		AddonInstanceInfo npcInfo = AddonInstanceInfo.get(npc);
		if(npcInfo==null){
			npc.remove();
			return;
		}
		HTTPRequest request = AddonManager.downloadAddonInstanceInfo(npcInfo.getCity().getId(), npcInfo.getCity().getTechtreeId(), npcInfo.getAddonInfo().getAddonId());
		if(request==null){
			npc.remove();
			return;
		}
		request.onFinish(()->{
			YamlConfiguration yamlConfiguration = request.getYamlResponse();
			if(yamlConfiguration==null || !yamlConfiguration.contains("addon")) return;
			ConfigurationSection addonSection = yamlConfiguration.getConfigurationSection("addon");
			AddonInstanceInfo instance = AddonInstanceInfo.get(addonSection);
			if(instance==null){
				return;
			}
			AddonInstanceGuideView.open(player, npc, instance);
		});
	}
	
	public static void startConversation(Player player, NPCInstance npc){
		AddonInstanceInfo info = AddonInstanceInfo.get(npc);
		if(info==null) return;
		int city_id = info.getCity().getId();
		String addon_id = info.getAddonInfo().getAddonId();
		HTTPRequest request = AddonManager.downloadAddonInstanceInfo(city_id, info.getCity().getTechtreeId(), addon_id);
		request.onFinish(()->{
			YamlConfiguration yamlConfiguration = request.getYamlResponse();
			if(yamlConfiguration==null || !yamlConfiguration.contains("addon")){
				return;
			}
			ConfigurationSection addonSection = yamlConfiguration.getConfigurationSection("addon");
			AddonInstanceInfo instanceInfo = AddonInstanceInfo.get(addonSection);
			if(instanceInfo==null){
				return;
			}
			instanceInfo.apply(npc);
			startConversation(player, npc, instanceInfo);
		});
	}
	
	public static void startConversation(Player player, NPCInstance npc, AddonInstanceInfo addonInfo){
		if(addonInfo.getCity().isCitizen(player.getUniqueId())){
			startCitizenConversation(player, npc, addonInfo);
		}
		else{
			startVisitorConversation(player, npc, addonInfo);
		}
	}
	
	private static void startCitizenConversation(Player player, NPCInstance npc, AddonInstanceInfo instance){
		NPCConversation conversation = NPCConversation.start(npc, player,200);
		AddonState state = instance.getState();
		String reason = instance.getAddonStateReason();
		switch(state){
		case Accepted:{
			conversation.addLine("Dieses Addon ist "+state.getColor()+"einsatzbereit"+ChatColor.RESET+".");
			if(reason==null){
				conversation.addLine("Kontaktiere ein Mitglied vom "+ChatColor.GREEN+"Staff MC"+ChatColor.RESET+"...");
				conversation.addLine("... um es "+AddonState.Activated.getColor()+"aktivieren"+ChatColor.RESET+" zu lassen.");
			}
			else{
				for(String line : reason.split("<br>")){
					conversation.addLine(line);
				}
			}
			break;
		}
		case Activated:{
			conversation.addLine("Dieses Addon ist "+state.getColor()+"aktiviert"+ChatColor.RESET+".");
			conversation.addLine("Möchtest du eine Änderung vornehmen?");
			conversation.onComplete(()->{
				AddonInstanceGuideView.open(player, npc, instance);
			});
			break;
		}
		case Available:{
			conversation.addLine("Dieses Addon ist "+state.getColor()+"verfügbar"+ChatColor.RESET+".");
			if(instance.getUnlockTrades().size()>0){
				conversation.addLine("Möchtest du es "+AddonState.Activated.getColor()+"freischalten"+ChatColor.RESET+"?");
				conversation.onFinish(()->{
					AddonUnlockView.open(player, npc, instance);
				});
			}
			else{
				conversation.addLine("Leider hab ich grad keine Papiere für die Aktivierung da.");
				conversation.addLine("Komm doch später nochmals vorbei.");
			}
			break;
		}
		case Blocked:{
			conversation.addLine("Dieses Addon ist momentan "+state.getColor()+"blockiert"+ChatColor.RESET+".");
			if(reason==null){
				conversation.addLine("Mir ist zwar nicht bekannt weshalb,...");
				conversation.addLine("...aber komm doch später nochmals vorbei.");
			}
			else{
				for(String line : reason.split("<br>")){
					conversation.addLine(line);
				}
			}
			break;
		}
		case Examination:{
			conversation.addLine("Momentan ist scheinbar eine "+state.getColor()+"Inspektion"+ChatColor.RESET+" im Gang...");
			break;
		}
		case Unavailable:{
			conversation.addLine("Dieses Addon ist momentan "+state.getColor()+"nicht verfügbar"+ChatColor.RESET+".");
			if(reason!=null){
				for(String line : reason.split("<br>")){
					conversation.addLine(line);
				}
				break;
			}
			break;
		}
		default:{
			conversation.addLine("Dieses Addon ist momentan "+state.getColor()+state.getDisplayName()+ChatColor.RESET+".");
			if(reason!=null){
				for(String line : reason.split("<br>")){
					conversation.addLine(line);
				}
			}
			break;
		}
		}
	}
	
	private static void startVisitorConversation(Player player, NPCInstance npc, AddonInstanceInfo addonInfo){
		NPCConversation conversation = NPCConversation.start(npc, player,200);
		if(addonInfo.getState()==AddonState.Accepted || addonInfo.getState()==AddonState.Activated){
			City city = addonInfo.getCity();
			conversation.addLine("Dieses Bauwerk gehört zu "+city.getName()+".");
		}
		else{
			conversation.addLine("Dieses Addon ist noch nicht aktiviert.");
			conversation.addLine("Wie wärs wenn du später wieder auf Besuch kommst?");
			conversation.addLine("Schönen Tag noch!");
		}
	}
}
