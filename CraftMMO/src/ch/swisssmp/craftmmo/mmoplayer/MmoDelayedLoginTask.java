package ch.swisssmp.craftmmo.mmoplayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

import org.bukkit.entity.Player;

import ch.swisssmp.craftmmo.Main;
import ch.swisssmp.craftmmo.mmoentity.MmoEntity;
import ch.swisssmp.craftmmo.util.MmoResourceManager;

public class MmoDelayedLoginTask implements Runnable{

	public final Player player;
	
	public MmoDelayedLoginTask(Player player){
		this.player = player;
	}
	@Override
	public void run() {
		if(player != null){
			MmoPlayer.neutralizeAttackSpeed(player, player.getInventory().getItemInMainHand());
			MmoPlayer.updateInventory(player);
			MmoQuestbook.get(player.getUniqueId()).relinkInstance();
			MmoEntity mmoEntity = new MmoEntity(player);
			double maxHealth = 20+mmoEntity.getMaxHealth();
			player.setMaxHealth(maxHealth);
			Main.info("Health: "+maxHealth);
			MmoPlayerParty party = MmoPlayerParty.get(player.getUniqueId());
			if(party!=null)party.setupPlayer(player);
			
			String urlString;
			try {
				urlString = MmoResourceManager.rootURL+"resourcepack.php?"
						+ "player="+URLEncoder.encode(player.getUniqueId().toString(), "UTF-8")
						+ "&token="+MmoResourceManager.pluginToken;
				URL url = new URL(urlString);
				BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
				String strTemp = "";
				String result = "";
				while(null!=(strTemp = br.readLine())){
					result+= strTemp;
				}
				if(!result.isEmpty()){
					player.setResourcePack(result);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
