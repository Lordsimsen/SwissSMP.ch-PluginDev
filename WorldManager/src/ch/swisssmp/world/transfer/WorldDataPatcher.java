package ch.swisssmp.world.transfer;

import ch.swisssmp.utils.nbt.NBTUtil;
import ch.swisssmp.world.WorldManagerPlugin;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.Tag;
import org.bukkit.Bukkit;

import java.io.File;

public class WorldDataPatcher {
    protected static void changeLevelName(File worldDirectory, String newName){
        File levelFile = new File(worldDirectory, "level.dat");
        NamedTag namedTag = NBTUtil.parse(levelFile);
        if(namedTag==null){
            Bukkit.getLogger().warning(WorldManagerPlugin.getPrefix()+" Konnte den LevelName im level.dat unter "+worldDirectory+" nicht anpassen, da die Daten nicht gelesen werden konnten!");
            return;
        }
        Tag<?> nbt = namedTag.getTag();
        if(!(nbt instanceof CompoundTag)){
            Bukkit.getLogger().warning(WorldManagerPlugin.getPrefix()+" Konnte den LevelName im level.dat unter "+worldDirectory+" nicht anpassen, da die Daten ein unerwartes Format haben!");
            return;
        }
        CompoundTag dataCompound = (CompoundTag) nbt;
        try{
            dataCompound.getCompoundTag("Data").putString("LevelName", newName);
        }
        catch(Exception e){
            Bukkit.getLogger().warning(WorldManagerPlugin.getPrefix()+" Konnte den LevelName im level.dat unter "+worldDirectory+" nicht anpassen, da die Daten ein unerwartes Format haben!");
            e.printStackTrace();
            return;
        }
        NBTUtil.save(levelFile, new NamedTag(namedTag.getName(), dataCompound));
    }
}
