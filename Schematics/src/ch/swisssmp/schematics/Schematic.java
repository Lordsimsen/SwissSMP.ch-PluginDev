package ch.swisssmp.schematics;

import net.querz.nbt.tag.ByteArrayTag;
import net.querz.nbt.tag.CompoundTag;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.util.BlockVector;

public class Schematic {

    private BlockVector size;
    private BlockState[][][] blocks;

    public CompoundTag toNBT(){
        CompoundTag result = new CompoundTag();
        if(size!=null){
            result.putInt("Width", size.getBlockX());
            result.putInt("Height", size.getBlockY());
            result.putInt("Length", size.getBlockZ());
        }
        if(blocks!=null){
            ByteArrayTag blocksArray = new ByteArrayTag();
            //blocksArray.setValue();
        }
        return result;
    }

    private static byte[] getBlockBytes(BlockVector size, BlockState[][][] blocks){
        byte[] result = new byte[size.getBlockX() * size.getBlockY() * size.getBlockZ()];
        for(int y = 0; y < size.getBlockY(); y++){
            for(int z = 0; z < size.getBlockZ(); z++){
                for(int x = 0; x < size.getBlockX(); x++){
                    int index = (y * size.getBlockZ() + z) * size.getBlockX() + x;
                    BlockState state = blocks[y][z][x];
                    // result[index] = state.getType().getKey().get
                }
            }
        }
        return result;
    }
}
