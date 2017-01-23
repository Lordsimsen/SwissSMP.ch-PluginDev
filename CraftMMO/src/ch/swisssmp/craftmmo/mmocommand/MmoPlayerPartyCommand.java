package ch.swisssmp.craftmmo.mmocommand;

import ch.swisssmp.craftmmo.mmoplayer.MmoPlayerParty;
import ch.swisssmp.craftmmo.util.MmoResourceManager;
import net.md_5.bungee.api.ChatColor;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MmoPlayerPartyCommand implements CommandExecutor{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
    	if(args.length==0){
    		displayHelp(sender);
    		return true;
    	}
		MmoPlayerParty party = null;
		Player player = null;
		if(sender instanceof Player){
			player = (Player) sender;
			party = MmoPlayerParty.get(player.getUniqueId());
		}
    	switch(args[0]){
	    	case "help":
	    		displayHelp(sender);
	    		break;
	    	case "list":{
	    		for(MmoPlayerParty playerParty : MmoPlayerParty.parties.values()){
	    			sender.sendMessage("["+playerParty.tag+"] - "+playerParty.name+" ("+playerParty.members.size()+" Mitglieder)");
	    		}
	    		if(MmoPlayerParty.parties.size()<1){
	    			sender.sendMessage("Es gibt momentan keine Gruppen.");
	    		}
	    		break;
	    	}
	    	case "inv":
	    	case "invite":
	    		if(args.length<2){
	    			sender.sendMessage("Keinen Spieler definiert.");
	    		}
	    		else{
	    			if(party==null){
	    				sender.sendMessage("Erstelle zuerst eine Gruppe mit '/party create [name] [tag]'.");
	    				return true;
	    			}
		    		String playerName = args[1];
		    		Player invited = Bukkit.getPlayer(playerName);
		    		if(invited==null){
		    			sender.sendMessage(ChatColor.RED+"Spieler "+playerName+" nicht gefunden.");
		    			return true;
		    		}
	    			if(party.leader.equals(player.getUniqueId())){
	    				party.invite(invited);
	    			}
	    			else{
	    				sender.sendMessage(ChatColor.RED+"Nur der Gruppen-Leiter kann Mitglieder einladen.");
	    			}
	    		}
	    		break;
	    	case "kick":
	    		if(args.length<2){
	    			sender.sendMessage("Keinen Spieler definiert.");
	    		}
	    		else{
		    		String playerName = args[1];
		    		Player kicked = Bukkit.getPlayer(playerName);
		    		if(party!=null && player!=null){
		    			if(party.leader.equals(player.getUniqueId())){
				    		if(kicked==null){
				    			party.kick(playerName);
				    		}
				    		else{
				    			party.kick(kicked.getUniqueId());
				    		}
		    			}
		    			else{
		    				player.sendMessage(ChatColor.RED+"Nur der Gruppen-Leiter kann Mitglieder ausschliessen.");
		    			}
		    		}
	    		}
				break;
	    	case "leave":
	    		if(party==null){
	    			sender.sendMessage(ChatColor.GRAY+"Du bist in keiner Gruppe.");
	    		}
	    		else{
	    			party.leave(player.getUniqueId());
	    		}
	    		break;
	    	case "join":
	    		if(args.length<2){
	    			sender.sendMessage("Keinen Spieler definiert.");
	    		}
	    		else{
		    		if(!StringUtils.isNumeric(args[1])){
		    			return true;
		    		}
		    		int mmo_party_id = Integer.parseInt(args[1]);
		    		if(player==null && args.length>2){
		    			player = Bukkit.getPlayer(args[2]);
		    		}
		    		MmoPlayerParty joinParty = MmoPlayerParty.get(mmo_party_id);
		    		if(joinParty==null){
		    			sender.sendMessage(ChatColor.RED+"Gruppe nicht gefunden. Möglicherweise wurde sie bereits aufgelöst.");
		    			return true;
		    		}
		    		else if(player!=null){
		    			joinParty.join(player);
		    		}
	    		}
	    		break;
	    	case "leader":
	    		if(party==null){
	    			player.sendMessage(ChatColor.GRAY+"Du bist in keiner Gruppe.");
	    			return true;
	    		}
	    		if(args.length<2){
	    			player.sendMessage("Keinen Spieler definiert.");
	    			return true;
	    		}
	    		else if(player!=null){
		    		String playerName = args[1];
		    		Player partyLeader = Bukkit.getPlayer(playerName);
		    		if(partyLeader==null){
		    			sender.sendMessage(ChatColor.RED+"Spieler "+playerName+" nicht gefunden.");
		    			return true;
		    		}
		    		party.setLeader(partyLeader);
	    		}
	    		break;
	    	case "info":{
	    		if(party!=null){
	    			player.sendMessage("Gruppenname: "+party.name);
	    			player.sendMessage("Gruppen-Tag: ["+party.tag+"]");
	    			player.sendMessage("Gruppen-Leiter: "+party.getPlayerName(party.leader));
	    		}
	    	}
	    	case "members":
	    		if(party!=null){
	    			String output = "Mitglieder: ";
	    			for(UUID member : party.members){
	    				String name = party.getPlayerName(member);
	    				boolean online = (Bukkit.getPlayer(member)!=null);
	    				if(name!=null){
	    					if(online)output+=ChatColor.WHITE;
	    					else output += ChatColor.GRAY;
		    				output+=name+", ";
	    				}
	    			}
	    			output = output.substring(0, output.length()-2);
	    			sender.sendMessage(ChatColor.GRAY+output);
	    		}
	    		else{
	    			sender.sendMessage(ChatColor.RED+"Du bist in keiner Gruppe. Du kannst jederzeit mit '/party create' eine Gruppe erstellen.");
	    		}
	    		break;
	    	case "refuse":
	    		if(args.length<2){
	    			sender.sendMessage("Keine Gruppe definiert.");
	    		}
	    		else if(player!=null){
		    		if(!StringUtils.isNumeric(args[1])){
		    			return true;
		    		}
		    		int mmo_party_id = Integer.parseInt(args[1]);
		    		MmoPlayerParty joinParty = MmoPlayerParty.get(mmo_party_id);
		    		if(joinParty==null)return true;
		    		joinParty.refuse(player.getUniqueId());
	    		}
	    		break;
	    	case "create":
	    		if(party!=null){
	    			sender.sendMessage(ChatColor.RED+"Du bist bereits in der Gruppe '"+party.name+"'.");
	    			return true;
	    		}
	    		else if(player!=null){
		    		String teamName = player.getName();
		    		String teamTag = teamName;
		    		if(args.length>1){
		    			teamName = args[1];
		    			if(args.length>2){
		    				teamTag = args[2];
		    			}
		    		}
	    			MmoResourceManager.processYamlResponse(player.getUniqueId(), "party/create.php", new String[]{
    					"leader="+player.getUniqueId().toString(),
    					"leader_name="+player.getName(),
    					"name="+trim(teamName, 16),
    					"tag="+teamTag
	    			});
	    		}
	    		break;
	    	case "name":
	    		if(party==null){
	    			sender.sendMessage(ChatColor.RED+"Du bist in keiner Gruppe. Du kannst mit '/party create' jederzeit eine Gruppe erstellen.");
	    			return true;
	    		}
	    		else{
		    		String teamName = player.getName();
		    		if(args.length>1){
		    			teamName = args[1];
		    		}
	    			party.setName(teamName);
	    		}
	    		break;
	    	case "tag":
	    		if(party==null){
	    			sender.sendMessage(ChatColor.RED+"Du bist in keiner Gruppe. Du kannst mit '/party create' jederzeit eine Gruppe erstellen.");
	    			return true;
	    		}
	    		else{
		    		String teamTag = player.getName();
		    		if(args.length>1){
		    			teamTag = args[1];
		    		}
	    			party.setTag(teamTag);
	    		}
	    		break;
		}
		return true;
	}
    public void displayHelp(CommandSender sender){
    	sender.sendMessage("Hilfe zu Gruppen-Befehlen");
    	sender.sendMessage("-----");
    	sender.sendMessage("/party help"+ChatColor.GRAY+" - Zeigt diese Hilfe an");
    	sender.sendMessage("/party members"+ChatColor.GRAY+" - Listet alle Gruppen-Mitglieder auf");
    	sender.sendMessage("/party leave"+ChatColor.GRAY+" - Verlasse deine Gruppe");
    	sender.sendMessage("/party invite [Spielername]"+ChatColor.GRAY+" - Sende eine Einladung an einen Spieler, invite=inv");
    	sender.sendMessage("/party kick [Spielername]"+ChatColor.GRAY+" - Schliesse einen Spieler aus deiner Gruppe aus");
    	sender.sendMessage("/party leader [Spielername]"+ChatColor.GRAY+" - Übergib deine Anführer-Rechte an anderen Spieler");
    	sender.sendMessage("/party name [Spielername]"+ChatColor.GRAY+" - Ändere den Namen deiner Gruppe");
    	sender.sendMessage("/party tag [Spielername]"+ChatColor.GRAY+" - Ändere den Kürzel deiner Gruppe");
    }
    private String trim(String s, int length){
    	if(s==null)return "";
    	else if(s.isEmpty() || length<1)return "";
    	return s.substring(0, Math.min(s.length(), length));
    }
}
