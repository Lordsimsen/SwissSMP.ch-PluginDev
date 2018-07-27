package ch.swisssmp.dungeongenerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.util.BlockVector;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;

public class GeneratorPart{
	private final DungeonGenerator generator;
	private final int template_x;
	private final int template_y;
	private final int template_z;
	//custom configurations
	private final String name;
	private final float weight;
	private final int limit;
	private final int min_distance;
	private final int max_distance;
	private final List<Integer> layers;
	
	//calculated signatures
	private String topSignature;
	private String bottomSignature;
	private String northSignature;
	private String eastSignature;
	private String southSignature;
	private String westSignature;
	
	private GeneratorPart(DungeonGenerator generator, Block position, ConfigurationSection dataSection){
		this.generator = generator;
		this.template_x = position.getX();
		this.template_y = position.getY();
		this.template_z = position.getZ();
		this.name = dataSection.contains("name") ? dataSection.getString("name") : "GeneratorPart";
		this.weight = dataSection.contains("weight") ? (float)dataSection.getDouble("weight") : -1;
		this.limit = dataSection.contains("limit") ? dataSection.getInt("limit") : -1;
		this.min_distance = dataSection.contains("min_distance") ? dataSection.getInt("min_distance") : -1;
		this.max_distance = dataSection.contains("max_distance") ? dataSection.getInt("max_distance") : -1;
		this.layers = dataSection.contains("layers") ? dataSection.getIntegerList("layers") : null;
	}
	
	public String getInfoString(){
		return this.name+(this.weight>=0 ? " ["+this.weight+"]" : "")+(this.limit>=0 ? " (max. "+this.limit+")":"");
	}
	
	//@SuppressWarnings("deprecation")
	public void generate(BlockVector position, int rotation){
		//Bukkit.getLogger().info("[DungeonGenerator] Generiere Teil bei "+position.getBlockX()+","+position.getBlockY()+position.getBlockZ());
		try{
			World world = this.generator.getWorld();
			LocalSession session = WorldEdit.getInstance().getSessionManager().get(this.generator);
			@SuppressWarnings("deprecation")
			EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitUtil.getLocalWorld(world), -1);
			this.copyToClipboard(session, editSession);
			if(rotation>0){
				this.rotateClipboard(session, rotation*90);
				switch(rotation){
				case 1:{
					position = new BlockVector(position.getBlockX()+this.generator.getPartSizeXZ()-1,position.getBlockY(), position.getBlockZ());
					break;
				}
				case 2:{
					position = new BlockVector(position.getBlockX()+this.generator.getPartSizeXZ()-1,position.getBlockY(), position.getBlockZ()+this.generator.getPartSizeXZ()-1);
					break;
				}
				case 3:{
					position = new BlockVector(position.getBlockX(),position.getBlockY(), position.getBlockZ()+this.generator.getPartSizeXZ()-1);
					break;
				}
				default:break;
				}
			}
			this.pasteClipboard(session, editSession, position);
		}
		catch(WorldEditException e){
			e.printStackTrace();
			return;
		}
	}
	
	public void savePart(List<String> arguments, int part_id){
		arguments.add("parts["+part_id+"][position_x]="+this.template_x);
		arguments.add("parts["+part_id+"][position_y]="+this.template_y);
		arguments.add("parts["+part_id+"][position_z]="+this.template_z);
		arguments.add("parts["+part_id+"][signatures][top]="+this.topSignature);
		arguments.add("parts["+part_id+"][signatures][bottom]="+this.bottomSignature);
		arguments.add("parts["+part_id+"][signatures][north]="+this.northSignature);
		arguments.add("parts["+part_id+"][signatures][east]="+this.eastSignature);
		arguments.add("parts["+part_id+"][signatures][south]="+this.southSignature);
		arguments.add("parts["+part_id+"][signatures][west]="+this.westSignature);
	}
	
	public void updateSignatures(){
		this.updateTopSignature();
		this.updateBottomSignature();
		this.updateNorthSignature();
		this.updateEastSignature();
		this.updateSouthSignature();
		this.updateWestSignature();
	}
	
	public float getWeight(){
		return this.weight;
	}
	
	public int getLimit(){
		return this.limit;
	}
	
	public int getMinDistance(){
		return this.min_distance;
	}
	
	public int getMaxDistance(){
		return this.max_distance;
	}
	
	public List<Integer> getLayers(){
		return this.layers;
	}
	
	public BlockVector getMinPoint(){
		return new BlockVector(this.template_x,this.template_y,this.template_z);
	}
	
	public BlockVector getMaxPoint(){
		return new BlockVector(this.template_x+this.generator.getPartSizeXZ()-1,this.template_y+this.generator.getPartSizeY()-1,this.template_z+this.generator.getPartSizeXZ()-1);
	}
	
	private void updateTopSignature(){
		BlockVector from = new BlockVector(this.template_x,this.template_y+this.generator.getPartSizeY(),this.template_z);
		BlockVector to = new BlockVector(from.getBlockX()+this.generator.getPartSizeXZ()-1,from.getBlockY(),from.getBlockZ()+this.generator.getPartSizeXZ()-1);
		this.topSignature = BlockUtil.getSignature(this.generator.getWorld(), from, to);
	}
	
	private void updateBottomSignature(){
		BlockVector from = new BlockVector(this.template_x,this.template_y-1,this.template_z);
		BlockVector to = new BlockVector(from.getBlockX()+this.generator.getPartSizeXZ()-1,from.getBlockY(),from.getBlockZ()+this.generator.getPartSizeXZ()-1);
		this.bottomSignature = BlockUtil.getSignature(this.generator.getWorld(), from, to);
	}
	
	private void updateNorthSignature(){
		BlockVector from = new BlockVector(this.template_x,this.template_y,this.template_z-1);
		BlockVector to = new BlockVector(from.getBlockX()+this.generator.getPartSizeXZ()-1,from.getBlockY()+this.generator.getPartSizeY()-1,from.getBlockZ());
		this.northSignature = BlockUtil.getSignature(this.generator.getWorld(), from, to);
	}
	
	private void updateEastSignature(){
		BlockVector from = new BlockVector(this.template_x+this.generator.getPartSizeXZ(),this.template_y,this.template_z);
		BlockVector to = new BlockVector(from.getBlockX(),from.getBlockY()+this.generator.getPartSizeY()-1,from.getBlockZ()+this.generator.getPartSizeXZ()-1);
		this.eastSignature = BlockUtil.getSignature(this.generator.getWorld(), from, to);
	}
	
	private void updateSouthSignature(){
		BlockVector from = new BlockVector(this.template_x,this.template_y,this.template_z+this.generator.getPartSizeXZ());
		BlockVector to = new BlockVector(from.getBlockX()+this.generator.getPartSizeXZ()-1,from.getBlockY()+this.generator.getPartSizeY()-1,from.getBlockZ());
		this.southSignature = BlockUtil.getSignature(this.generator.getWorld(), from, to);
	}
	
	private void updateWestSignature(){
		BlockVector from = new BlockVector(this.template_x-1,this.template_y,this.template_z);
		BlockVector to = new BlockVector(from.getBlockX(),from.getBlockY()+this.generator.getPartSizeY()-1,from.getBlockZ()+this.generator.getPartSizeXZ()-1);
		this.westSignature = BlockUtil.getSignature(this.generator.getWorld(), from, to);
	}
	
	public String getSignature(Direction direction){
		switch(direction){
		case UP: return this.topSignature;
		case DOWN: return this.bottomSignature;
		case NORTH: return this.northSignature;
		case EAST: return this.eastSignature;
		case SOUTH: return this.southSignature;
		case WEST: return this.westSignature;
		default: return null;
		}
	}
	
	public DungeonGenerator getGenerator(){
		return this.generator;
	}
	
	public String getPositionString(){
		return this.template_x+", "+this.template_y+", "+this.template_z;
	}
	
	public String getName(){
		return this.name;
	}
	
	private void copyToClipboard(LocalSession session, EditSession editSession) throws WorldEditException{
		com.sk89q.worldedit.Vector from = new com.sk89q.worldedit.Vector(this.template_x, this.template_y, this.template_z);
		com.sk89q.worldedit.Vector to = new com.sk89q.worldedit.Vector(this.template_x+this.generator.getPartSizeXZ()-1, this.template_y+this.generator.getPartSizeY()-1, this.template_z+this.generator.getPartSizeXZ()-1);
		CuboidRegion selection = new CuboidRegion(from,to);
		BlockArrayClipboard clipboard = new BlockArrayClipboard(selection);
		clipboard.setOrigin(from);
		ForwardExtentCopy copy = new ForwardExtentCopy(editSession, selection, clipboard, new Vector(this.template_x,this.template_y,this.template_z));
		copy.setCopyingEntities(true);
		Operations.complete(copy);
		session.setClipboard(new ClipboardHolder(clipboard, editSession.getWorld().getWorldData()));
	}
	
	private void rotateClipboard(LocalSession session, int rotation) throws WorldEditException{
		ClipboardHolder holder;
		holder = session.getClipboard();
		AffineTransform transform = new AffineTransform();
		transform = transform.rotateY(-rotation);
		holder.setTransform(holder.getTransform().combine(transform));
	}
	
	private void pasteClipboard(LocalSession session, EditSession editSession, BlockVector position) throws WorldEditException{
		editSession.enableQueue();
		ClipboardHolder holder = session.getClipboard();
        Vector to = new Vector(position.getBlockX(),position.getBlockY(),position.getBlockZ());
        Operation operation = holder
                .createPaste(editSession, editSession.getWorld().getWorldData())
                .to(to)
                .ignoreAirBlocks(false)
                .build();
        Operations.completeLegacy(operation);
		editSession.flushQueue();
	}
	
	public static void createBoundingBox(DungeonGenerator generator, BlockVector position){
		Collection<Block> boundingBox = BlockUtil.getBoundingBox(generator.getWorld().getBlockAt(position.getBlockX(), position.getBlockY(), position.getBlockZ()), generator.getPartSizeXZ(), generator.getPartSizeY());
		for(Block block : boundingBox){
			block.setType(generator.getBoundingBoxMaterial());
		}
	}
	
	public static GeneratorPart get(DungeonGenerator generator, Block position){
		Collection<Block> boundingBox = BlockUtil.getBoundingBox(position, generator.getPartSizeXZ(), generator.getPartSizeY());
		//check if bounding box is correct and complete
		for(Block block : boundingBox){
			if(block.getType()!=generator.getBoundingBoxMaterial()) return null;
		}
		//read special properties from signs
		YamlConfiguration partConfiguration = new YamlConfiguration();
		Collection<Sign> attachedSigns = BlockUtil.getAttachedSigns(boundingBox);
		ArrayList<String> configurationStrings = new ArrayList<String>();
		//int totalConfigurations = 0;
		for(Sign sign : attachedSigns){
			for(String line : sign.getLines()){
				if(line.isEmpty()) continue;
				configurationStrings.add(line);
				//totalConfigurations++;
			}
		}
		PartConfigurationUtil.applyPartConfiguration(partConfiguration, configurationStrings);
		//Bukkit.getLogger().info("[DungeonGenerator] "+attachedSigns.size()+" "+(attachedSigns.size()==1?"Schild":"Schilder")+" mit total "+totalConfigurations+" "+(totalConfigurations==1?"Einstellung":"Einstellungen")+" gefunden.");
		return new GeneratorPart(generator,position,partConfiguration);
	}
}