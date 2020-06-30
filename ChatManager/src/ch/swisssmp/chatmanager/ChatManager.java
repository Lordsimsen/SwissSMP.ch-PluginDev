package ch.swisssmp.chatmanager;

import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;
import org.bukkit.event.Listener;

import java.util.UUID;

public class ChatManager implements Listener{

	public static HTTPRequest log(UUID sender, String senderName, String worldName, String message){
		return log(sender, senderName, null, worldName, message);
	}

	public static HTTPRequest log(UUID sender, String senderName, String recipientName, String worldName, String message){
		return DataSource.getResponse(ChatManagerPlugin.getInstance(), "chat.php", new String[]{
				"player_uuid="+ URLEncoder.encode(sender!=null ? sender.toString() : senderName),
				"name="+URLEncoder.encode(senderName),
				"world="+(worldName!=null ? URLEncoder.encode(worldName) : ""),
				"message="+URLEncoder.encode(message),
				"recipient="+(recipientName!=null ? URLEncoder.encode(recipientName) : "")
		});
	}

	public static String extractMessage(String command){
	    String[] messageParts = command.split(" ");
	    int offset = messageParts[0].length()+messageParts[1].length()+1;
	    if(command.length()<=offset) return "";
	    return command.substring(offset);
	}
}
