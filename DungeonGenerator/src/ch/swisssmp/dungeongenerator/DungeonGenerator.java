package ch.swisssmp.dungeongenerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.util.BlockVector;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.session.SessionKey;
import com.sk89q.worldedit.session.SessionOwner;
import com.sk89q.worldedit.util.auth.AuthorizationException;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class DungeonGenerator implements Listener, SessionOwner{
	private static HashMap<String,DungeonGenerator> generators = new HashMap<String,DungeonGenerator>();
	
	private final int generator_id;
	private final World world;
	private final SessionKey sessionKey;
	private final Material boundingBoxMaterial = Material.GOLD_BLOCK;

	private String generator_name;
	private int partSizeXZ;
	private int partSizeY;
	private Block templateOrigin = null;
	
	private ArrayList<GeneratorPart> templateParts = new ArrayList<GeneratorPart>();
	
	private DungeonGenerator(World world, ConfigurationSection dataSection){
		this.generator_id = dataSection.getInt("generator_id");
		this.generator_name = dataSection.getString("generator_name");
		this.world = world;
		this.partSizeXZ = dataSection.getInt("part_size_xz");
		this.partSizeY = dataSection.getInt("part_size_y");
		if(dataSection.contains("template_origin")){
			Location templateOriginLocation = dataSection.getLocation("template_origin");
			if(templateOriginLocation!=null){
				this.templateOrigin = templateOriginLocation.getBlock();
			}
		}
		this.sessionKey = new WorldEditSessionKey();
		Bukkit.getPluginManager().registerEvents(this, DungeonGeneratorPlugin.plugin);
		generators.put(this.generator_name.toLowerCase(), this);
		if(this.templateOrigin!=null){
			this.loadGeneratorParts(templateOrigin);
			this.updateSignatures();
		}
	}
	
	public World getWorld(){
		return this.world;
	}
	
	protected Collection<String> getInfo(){
		this.update();
		Collection<String> result = new ArrayList<String>();
		result.add("Bezeichnung: "+generator_name+" (ID: "+generator_id+")");
		result.add("Vorlage: "+this.world.getName()+" ("+this.templateOrigin.getX()+", "+this.templateOrigin.getY()+", "+this.templateOrigin.getZ()+")");
		result.add("Teil-Grösse: "+this.partSizeXZ+" x "+this.partSizeY);
		result.add("Anzahl Teile: "+this.templateParts.size());
		result.add("Teile: "+this.getPartsString());
		return result;
	}
	
	private String getPartsString(){
		List<String> stringParts = new ArrayList<String>();
		for(GeneratorPart part : this.templateParts){
			stringParts.add(part.getInfoString());
		}
		return String.join(", ", stringParts);
	}
	
	public void setName(String name){
		this.generator_name = name;
		this.save();
	}
	
	public void setPartSizeXZ(int partSizeXZ){
		this.partSizeXZ = partSizeXZ;
		this.save();
	}
	
	public int getPartSizeXZ(){
		return this.partSizeXZ;
	}
	
	public void setPartSizeY(int partSizeY){
		this.partSizeY = partSizeY;
		this.save();
	}
	
	public int getPartSizeY(){
		return this.partSizeY;
	}
	
	public Block getTemplateOrigin(){
		return this.templateOrigin;
	}
	
	public void setTemplateOrigin(Block position){
		this.templateOrigin = position;
		for(Block block : BlockUtil.getBoundingBox(position, partSizeXZ, partSizeY)){
			block.setType(this.boundingBoxMaterial);
		}
		for(int x = 0; x < this.partSizeXZ; x++){
			this.world.getBlockAt(this.templateOrigin.getX()-5-x, this.templateOrigin.getY() -1, this.templateOrigin.getZ() -1).setType(Material.REDSTONE_BLOCK);
		}
		for(int z = 0; z < this.partSizeXZ; z++){
			this.world.getBlockAt(this.templateOrigin.getX() -1, this.templateOrigin.getY() -1, this.templateOrigin.getZ()-5-z).setType(Material.LAPIS_BLOCK);
		}
		this.update();
	}
	
	protected void update(){
		this.loadGeneratorParts(this.templateOrigin);
		this.updateSignatures();
		this.save();
	}
	
	public boolean generate(BlockVector position, Long seed, int size){
		this.update();
		List<GenerationPart> parts = PartGenerator.generateData(this, position, seed, size);
		if(parts==null) return false;
		Bukkit.getLogger().info("[DungeonGenerator] Data generated, placing blocks...");
		GenerationRoutine.run(parts, position);
		return true;
	}
	
	protected ArrayList<GeneratorPart> getTemplateParts(){
		return this.templateParts;
	}
	
	protected Material getBoundingBoxMaterial(){
		return this.boundingBoxMaterial;
	}
	
	private void updateSignatures(){
		for(GeneratorPart part : this.templateParts){
			part.updateSignatures();
		}
	}
	
	/*
	 * Loads Generators based on Arrangement in the World
	 * It expects the parts to be arranged in rows aligned on the x axis (all rows share same z start position and stretch to z+)
	 */
	private void loadGeneratorParts(Block start){
		if(start==null)return;
		this.templateParts.clear();
		ArrayList<GeneratorPart> result = new ArrayList<GeneratorPart>();
		World world = start.getWorld();
		int x = start.getX()-1;//We're checking the bounding boxes, those are extruded by 1
		int y = start.getY()-1;
		int z = start.getZ()-1;
		
		int edge_length = this.partSizeXZ+2;
		int initial_z = z;
		boolean moveZ = true;
		int remainingCheckTolerance = edge_length;
		Block current;
		GeneratorPart part;
		Location markerLocation;
		while(remainingCheckTolerance>0){
			current = world.getBlockAt(x,y,z);
			if(current.getType()==this.boundingBoxMaterial){
				//attempt loading a GeneratorPart
				part = GeneratorPart.get(this, world.getBlockAt(current.getX()+1,current.getY()+1,current.getZ()+1));
				//GeneratorPart found
				if(part!=null){
					markerLocation = part.getMaxPoint().toLocation(world);
					markerLocation.add(1-this.partSizeXZ/2f,3f,1-this.partSizeXZ/2f);
					GeneratorPartMarker.show(markerLocation, Color.GREEN, 60L);
					//Bukkit.getLogger().info("[DungeonGenerator] Found Part "+part.getName());
					result.add(part);
					//reset tolerance
					remainingCheckTolerance = edge_length;
					//new row found, move along z axis
					if(!moveZ){
						moveZ = true;
					}
					//jump to position after the newly registered part
					z+=edge_length+1;
				}
				//no part found, reduce check tolerance
				else if(part==null) remainingCheckTolerance--;
			}
			//no part found, reduce check tolerance
			else{
				remainingCheckTolerance--;
			}
			//move along current axis
			if(remainingCheckTolerance>0){
				if(moveZ) z+=1;
				else x+=1;
			}
			//jump to new row
			else if(moveZ){
				z = initial_z;
				x+= edge_length+1;
				remainingCheckTolerance = edge_length;
				moveZ = false;
			}
		}
		this.templateParts = result;
	}
	
	private void save(){
		List<String> arguments = new ArrayList<String>();
		if(this.templateOrigin!=null){
			arguments.add("template_origin[world]="+this.templateOrigin.getWorld().getName());
			arguments.add("template_origin[x]="+this.templateOrigin.getX());
			arguments.add("template_origin[y]="+this.templateOrigin.getY());
			arguments.add("template_origin[z]="+this.templateOrigin.getZ());
		}
		for(int i = 0; i < this.templateParts.size(); i++){
			this.templateParts.get(i).savePart(arguments, i);
		}
		DataSource.getResponse("dungeons/save_generator.php", new String[]{
				"id="+this.generator_id,
				String.join("&", arguments)
		});
	}
	
	@EventHandler
	private void onWorldUnload(WorldUnloadEvent event){
		if(event.getWorld()!=this.world)return;
		generators.remove(this.generator_name.toLowerCase());
		WorldEdit.getInstance().getSessionManager().remove(this);
		HandlerList.unregisterAll(this);
	}
	
	private static DungeonGenerator load(World world, String name){
		if(generators.containsKey(name.toLowerCase())){
			return generators.get(name.toLowerCase());
		}
		YamlConfiguration yamlConfiguration;
		yamlConfiguration = DataSource.getYamlResponse("dungeons/get_generator.php", new String[]{
			"name="+URLEncoder.encode(name)
		});
		if(yamlConfiguration==null || !yamlConfiguration.contains("generator")) return null;
		return new DungeonGenerator(world,yamlConfiguration.getConfigurationSection("generator"));
	}
	
	public static DungeonGenerator create(World world, String name, int partSizeXZ, int partSizeY){
		DungeonGenerator existing = DungeonGenerator.get(world, name.toLowerCase());
		if(existing!=null) return existing;
		YamlConfiguration yamlConfiguration;
		yamlConfiguration = DataSource.getYamlResponse("dungeons/create_generator.php", new String[]{
				"world="+world.getName(),
				"name="+URLEncoder.encode(name),
				"part_size[xz]="+partSizeXZ,
				"part_size[y]="+partSizeY
		});
		if(yamlConfiguration==null || !yamlConfiguration.contains("generator")) return null;
		return new DungeonGenerator(world,yamlConfiguration.getConfigurationSection("generator"));
	}
	
	public static DungeonGenerator get(World world, String name){
		if(!generators.containsKey(name.toLowerCase())){
			return DungeonGenerator.load(world, name.toLowerCase());
		}
		else{
			return generators.get(name.toLowerCase());
		}
	}
	
	public static Collection<DungeonGenerator> getAll(){
		return generators.values();
	}

	@Override
	public void checkPermission(String arg0) throws AuthorizationException {
		return;
	}

	@Override
	public String[] getGroups() {
		return new String[]{};
	}

	@Override
	public boolean hasPermission(String arg0) {
		return true;
	}

	@Override
	public SessionKey getSessionKey() {
		return this.sessionKey;
	}
}
