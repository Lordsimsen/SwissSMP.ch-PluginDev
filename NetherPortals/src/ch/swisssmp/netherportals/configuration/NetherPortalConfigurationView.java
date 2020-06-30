package ch.swisssmp.netherportals.configuration;

import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.EditorSlot;
import ch.swisssmp.netherportals.WorldConfiguration;
import ch.swisssmp.netherportals.configuration.slots.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;

public class NetherPortalConfigurationView extends CustomEditorView {

    private final WorldConfiguration configuration;

    protected NetherPortalConfigurationView(Player player, WorldConfiguration configuration) {
        super(player);
        this.configuration = configuration;
    }

    @Override
    protected int getInventorySize() {
        return 9;
    }

    @Override
    protected Collection<EditorSlot> initializeEditor() {
        Collection<EditorSlot> result = new ArrayList<>();
        result.add(new TargetWorldSlot(this, 0, configuration));
        result.add(new TargetCoordinateFactorSlot(this, 1, configuration));
        result.add(new OperationSlot(this, 2, configuration));
        result.add(new PortalCreationSlot(this, 3, configuration));
        result.add(new DefaultsSlot(this, 6, configuration));
        result.add(new TrySlot(this, 7, configuration));
        result.add(new EnabledSlot(this, 8, configuration));
        return result;
    }

    @Override
    public String getTitle() {
        return configuration.getBukkitWorld().getName();
    }

    public static NetherPortalConfigurationView open(Player player, WorldConfiguration configuration){
        NetherPortalConfigurationView view = new NetherPortalConfigurationView(player, configuration);
        view.open();
        return view;
    }
}
