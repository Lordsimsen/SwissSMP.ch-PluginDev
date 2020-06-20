package ch.swisssmp.knightstournament;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.entity.Player;

public class PlayerReleaseUseListener {

    protected static void register(){
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(KnightsTournamentPlugin.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Client.BLOCK_DIG) {
            @Override public void onPacketReceiving(PacketEvent event){
                PacketContainer packetContainer = event.getPacket();
                boolean isArrowShoot = packetContainer.getPlayerDigTypes().getValues().contains(EnumWrappers.PlayerDigType.RELEASE_USE_ITEM);
                if(!isArrowShoot) return;
                Player player = event.getPlayer();
                LanceCharge charge = LanceCharge.get(player.getUniqueId()).orElse(null);
                if(charge==null) return;
                charge.complete();
            }
        });
    }
}
