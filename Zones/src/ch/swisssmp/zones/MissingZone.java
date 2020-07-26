package ch.swisssmp.zones;

import com.google.gson.JsonObject;
import org.bukkit.util.BlockVector;

import java.util.UUID;

public class MissingZone extends Zone {

    protected MissingZone(UUID uid) {
        super(null, uid, null, MissingZoneType.getInstance());
    }

    @Override
    public BlockVector getMin() {
        return null;
    }

    @Override
    public BlockVector getMax() {
        return null;
    }

    @Override
    public boolean isSetupComplete() {
        return true;
    }

    @Override
    protected boolean tryLoadWorldGuardRegion() {
        return true;
    }

    @Override
    protected void updateWorldGuardRegion() {

    }

    @Override
    protected JsonObject saveData() {
        return null;
    }

    @Override
    protected void loadData(JsonObject json) {

    }
}
