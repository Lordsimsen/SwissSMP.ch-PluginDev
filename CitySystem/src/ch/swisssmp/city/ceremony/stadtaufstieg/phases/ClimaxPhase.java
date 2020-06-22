package ch.swisssmp.city.ceremony.stadtaufstieg.phases;

import ch.swisssmp.ceremonies.Phase;
import ch.swisssmp.city.ceremony.stadtaufstieg.CityRankCeremony;
import org.bukkit.block.Block;

public class ClimaxPhase extends Phase {

    private final CityRankCeremony ceremony;
    private final Block banner;

    public ClimaxPhase(CityRankCeremony ceremony){
        this.ceremony = ceremony;
        this.banner = ceremony.getBanner();
    }

    @Override
    public void run() {

    }
}
