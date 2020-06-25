package ch.swisssmp.world.transfer;

import ch.swisssmp.utils.nbt.legacy.NBTBase;
import ch.swisssmp.utils.nbt.legacy.NBTTagCompound;
import ch.swisssmp.utils.nbt.NBTUtil;
import ch.swisssmp.world.WorldManager;
import org.bukkit.Bukkit;

import java.io.File;

public class WorldDataPatcher {
    protected static void changeLevelName(File worldDirectory, String newName){
        File levelFile = new File(worldDirectory, "level.dat");
        NBTBase nbt = NBTUtil.parse(levelFile);
        if(!(nbt instanceof NBTTagCompound)){
            Bukkit.getLogger().warning(WorldManager.getPrefix()+" Konnte den LevelName im level.dat unter "+worldDirectory+" nicht anpassen, da die Daten nicht gelesen werden konnten!");
            return;
        }
        NBTTagCompound dataCompound = (NBTTagCompound) nbt;
        dataCompound.setString("LevelName", newName);
        NBTUtil.save(levelFile, dataCompound);
    }
}
