package ch.swisssmp.zones;

import org.bukkit.block.Block;

public interface BlockFilter {
    boolean check(Block block);
}
