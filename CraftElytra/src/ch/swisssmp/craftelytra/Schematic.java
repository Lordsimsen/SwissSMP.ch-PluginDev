package ch.swisssmp.craftelytra;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.Vector;

import net.md_5.bungee.api.ChatColor;

public class Schematic {
	private final Vector _pos1;
	private final Vector _pos2;
	private final String _name;
	private final YamlConfiguration _yamlConfiguration;
	public Schematic(String name) throws NullPointerException{
		this._name = name;
		this._yamlConfiguration = YamlConfiguration.loadConfiguration(this.getFile());
		if(_yamlConfiguration==null) throw new NullPointerException("Schematic "+name+" could not be loaded!");
		this._pos1 = new Vector(0, 0, 0);
		ConfigurationSection metaSection = this._yamlConfiguration.getConfigurationSection("meta");
		if(metaSection!=null){
			int x = metaSection.getInt("x");
			int y = metaSection.getInt("y");
			int z = metaSection.getInt("z");
			this._pos2 = new Vector(x, y, z);
		}
		else{
			this._pos2 = new Vector(1, 1, 1);
		}
	}
	private File getFile(){
		return new File(Main.dataFolder, "schematics/"+_name+".yml");
	}
	public Schematic(Vector pos1, Vector pos2, String name){
		_pos1 = new Vector(Math.min(pos1.getX(), pos2.getX()), Math.min(pos1.getY(), pos2.getY()), Math.min(pos1.getZ(), pos2.getZ()));
		_pos2 = new Vector(Math.max(pos1.getX(), pos2.getX()), Math.max(pos1.getY(), pos2.getY()), Math.max(pos1.getZ(), pos2.getZ()));
		this._name = name;
		this._yamlConfiguration = new YamlConfiguration();
	}
	@SuppressWarnings("deprecation")
	public boolean save(World world){
		if(_pos1==null || _pos2==null || _name==null || world==null) return false;
		int size_x = Math.abs((int)_pos2.getX()-(int)_pos1.getX())+1;
		int size_y = Math.abs((int)_pos2.getY()-(int)_pos1.getY())+1;
		int size_z = Math.abs((int)_pos2.getZ()-(int)_pos1.getZ())+1;
		ConfigurationSection layersSection = _yamlConfiguration.createSection("layers");
		for(int y = 0; y < size_y; y++){
			ConfigurationSection layerSection = layersSection.createSection("layer_"+y);
			for(int z = 0; z < size_z; z++){
				ArrayList<String> blocks = new ArrayList<String>();
				for(int x = 0; x < size_x; x++){
					Block block = new Location(world, _pos1.getX()+x, _pos1.getY()+y, _pos1.getZ()+z).getBlock();
					blocks.add(block.getType().toString()+"."+block.getState().getData().getData());
				}
				layerSection.set("row_"+z, blocks);
			}
		}
		ConfigurationSection metaSection = this._yamlConfiguration.createSection("meta");
		metaSection.set("x", size_x);
		metaSection.set("y", size_y);
		metaSection.set("z", size_z);
		try {
			_yamlConfiguration.save(this.getFile());
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	public static boolean compare(Location northWest, int topLimit, String schematicName){
		return compare(northWest, topLimit, schematicName, null);
	}
	@SuppressWarnings("deprecation")
	public static boolean compare(Location northWest, int topLimit, String schematicName, Player player){
		if(northWest==null) return false;
		World world = northWest.getWorld();
		Schematic schematic = new Schematic(schematicName);
		ConfigurationSection layersSection = schematic._yamlConfiguration.getConfigurationSection("layers");
		int compareCount = 0;
		int maxY = layersSection.getKeys(false).size();
		if(topLimit>-1) maxY = Math.min(maxY, topLimit);
		for(int y = 0; y < maxY; y++){
			ConfigurationSection layerSection = layersSection.getConfigurationSection("layer_"+y);
			for(int z = 0; z < layerSection.getKeys(false).size(); z++){
				List<String> blocks = layerSection.getStringList("row_"+z);
				for(int x = 0; x < blocks.size(); x++){
					compareCount++;
					String[] materialStrings = blocks.get(x).split(Pattern.quote("."));
					Material targetMaterial = Material.valueOf(materialStrings[0]);
					if((x<2 || x>6) || (z<2 || z>6) || y<3){
						if(targetMaterial==Material.AIR) continue;
						if(targetMaterial==Material.SNOW) continue;
						if(targetMaterial==Material.WATER) continue;
					}
					byte b = Byte.valueOf(materialStrings[1]);
					Location location = new Location(world, northWest.getX()+x, northWest.getY()+y, northWest.getZ()+z);
					Block block = location.getBlock();
					Material actualMaterial = block.getType();
					if(actualMaterial==Material.REDSTONE_LAMP_ON) actualMaterial = Material.REDSTONE_LAMP_OFF;
					if(actualMaterial!=targetMaterial){
						if(player!=null){
							player.sendMessage(ChatColor.RED+"Block "+northWest.getX()+x+","+northWest.getY()+y+","+northWest.getZ()+z+" ist "+block.getType().toString()+", sollte aber "+targetMaterial.toString()+" sein.");
						}
						return false;
					}
					if(targetMaterial==Material.LEVER) continue;
					if(targetMaterial==Material.SIGN) continue;
					if(block.getState().getData().getData()!=b){
						if(player!=null){
							player.sendMessage(ChatColor.RED+"Block "+northWest.getX()+x+","+northWest.getY()+y+","+northWest.getZ()+z+" ist "+block.getType().toString()+":"+block.getState().getData().getData()+", sollte aber "+targetMaterial.toString()+":"+b+" sein.");
						}
						return false;
					}
				}
			}
		}
		Bukkit.getLogger().info("Compared "+compareCount+" blocks.");
		return true;
	}
}
