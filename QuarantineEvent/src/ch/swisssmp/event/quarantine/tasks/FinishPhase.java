package ch.swisssmp.event.quarantine.tasks;

import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.event.quarantine.QuarantineEventInstance;
import ch.swisssmp.event.quarantine.QuarantineMaterial;
import ch.swisssmp.event.quarantine.QuarantineEventInstance.Phase;
import ch.swisssmp.utils.SwissSMPler;

/**
 * Beende Spielpartie
 * @author Oliver
 *
 */
public class FinishPhase extends QuarantineEventInstanceTask {

	public FinishPhase(QuarantineEventInstance instance) {
		super(instance);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		for(UUID playerUid : getInstance().getPlayers()) {
			Player player = Bukkit.getPlayer(playerUid);
			if(player==null) continue;
			for(ItemStack itemStack : player.getInventory()) {
				if(itemStack==null) continue;
				QuarantineMaterial material = QuarantineMaterial.of(itemStack.getType());
				if(material==QuarantineMaterial.TROPHY) {
					getInstance().getScoreController().addScore(playerUid, itemStack.getAmount());
				}
			}
			SwissSMPler.get(playerUid).sendTitle("", ChatColor.YELLOW+"Spiel beendet!");
		}
		for(Entry<UUID,Integer> entry : getInstance().getScoreController().getScores().entrySet()) {
			Player player = Bukkit.getPlayer(entry.getKey());
			if(player==null) continue;
			for(Player recipient : Bukkit.getOnlinePlayers()) {
				recipient.sendMessage(player.getDisplayName()+ChatColor.RESET+": "+entry.getValue());
			}
		}
		complete();
	}

	
	@Override
	protected void onComplete() {
		getInstance().setPhase(Phase.Review);
	}
}
