package ch.swisssmp.zones.editor;

import ch.swisssmp.utils.SwissSMPler;
import ch.swisssmp.zones.ZonesPlugin;
import ch.swisssmp.zones.editor.selection.PointSelector;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class InstructionDisplay extends BukkitRunnable {

    private final Player player;
    private final PointSelector selector;

    private InstructionDisplay(Player player, PointSelector selector){
        this.player = player;
        this.selector = selector;
    }

    private void initialize(){
        this.runTaskTimer(ZonesPlugin.getInstance(), 0, 20);
    }

    @Override
    public void run() {
        SwissSMPler.get(player).sendActionBar(selector.getInstructions());
    }

    protected static InstructionDisplay run(Player player, PointSelector selector){
        InstructionDisplay result = new InstructionDisplay(player, selector);
        result.initialize();
        return result;
    }
}
