package ch.swisssmp.flyday;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class FlyDayCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		boolean currentlyFlyDay = Main.isFlyDay();
		Main.setFlyDay(!currentlyFlyDay);
		if(Main.isFlyDay()){
			arg0.sendMessage("Der Fly-Day ist nun aktiviert.");
		}
		else{
			arg0.sendMessage("Der Fly-Day ist nun beendet.");
		}
		return false;
	}

}
