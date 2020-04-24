package ch.swisssmp.dungeongenerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.ItemFrame;
import org.bukkit.util.BlockVector;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.EditSession.ReorderMode;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldguard.WorldGuard;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.EntityUtil;
import ch.swisssmp.utils.WorldUtil;

public class GeneratorPart{
	private final DungeonGenerator generator;

	private final int part_id;
	private final List<PartType> partTypes = new ArrayList<PartType>();
	
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
	private final List<Integer> rotations;
	
	private final String imageData;
	
	protected GeneratorPart(DungeonGenerator generator, int part_id, Block position, ConfigurationSection dataSection){
		this.generator = generator;
		this.part_id = part_id;
		this.template_x = position.getX();
		this.template_y = position.getY();
		this.template_z = position.getZ();
		this.name = dataSection.contains("name") ? dataSection.getString("name") : "Unnamed Part";
		this.weight = dataSection.contains("weight") ? (float)dataSection.getDouble("weight") : -1;
		this.limit = dataSection.contains("limit") ? dataSection.getInt("limit") : -1;
		this.min_distance = dataSection.contains("min_distance") ? dataSection.getInt("min_distance") : -1;
		this.max_distance = dataSection.contains("max_distance") ? dataSection.getInt("max_distance") : -1;
		this.layers = dataSection.contains("layers") ? dataSection.getIntegerList("layers") : null;
		this.imageData = dataSection.getString("image");
		this.rotations = dataSection.contains("rotations") ? dataSection.getIntegerList("rotations") : null;
		if(dataSection.contains("types")){
			for(String partTypeString : dataSection.getStringList("types")){
				this.partTypes.add(PartType.get(partTypeString));
			}
		}
	}
	
	public int getId(){
		return this.part_id;
	}
	
	public String getInfoString(){
		return this.name+(this.weight>=0 ? " ["+this.weight+"]" : "")+(this.limit>=0 ? " (max. "+this.limit+")":"");
	}
	
	public String getImage(){
		return this.imageData;
	}
	
	public JsonObject getJsonData(){
		JsonObject result = new JsonObject();
		result.addProperty("id", this.part_id);
		result.addProperty("name", this.getName());
		result.addProperty("x", this.template_x);
		result.addProperty("y", this.template_y);
		result.addProperty("z", this.template_z);
		JsonArray typesArray = new JsonArray();
		for(PartType partType : this.partTypes){
			typesArray.add(partType.toString());
		}
		result.add("type", typesArray);
		String signature;
		JsonObject signaturesData = new JsonObject();
		JsonObject directionData;
		for(Direction direction : Direction.values()){
			directionData = new JsonObject();
			for(int i = 0; i < 4; i++){
			signature = this.getSignature(direction, i);
			if(signature.isEmpty()) continue;
				directionData.addProperty(String.valueOf(i), signature);
			}
			signaturesData.add(direction.toString(), directionData);
		}
		result.add("signatures", signaturesData);
		if(this.weight>=0) result.addProperty("weight", this.getWeight());
		if(this.limit>=0) result.addProperty("limit", this.getLimit());
		if(this.min_distance>=0) result.addProperty("min_distance", this.getMinDistance());
		if(this.max_distance>=0) result.addProperty("max_distance", this.getMaxDistance());
		if(this.layers!=null){
			JsonArray layersArray = new JsonArray();
			for(int layer : this.layers){
				layersArray.add(layer);
			}
			result.add("layers", layersArray);
		}
		if(this.rotations!=null){
			JsonArray rotationsArray = new JsonArray();
			for(int rotation : this.rotations){
				rotationsArray.add(rotation);
			}
			result.add("rotations", rotationsArray);
		}
		return result;
	}
	
	public void generate(World world, BlockVector position, int rotation){
		try{
			position = this.adjustPastePosition(position, rotation);
			LocalSession session = WorldEdit.getInstance().getSessionManager().get(this.generator);
			com.sk89q.worldedit.world.World worldeditWorld = BukkitAdapter.adapt(world);
			EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(worldeditWorld, -1);
			this.copyToClipboard(session, editSession);
			this.rotateClipboard(session, rotation*90);
			this.pasteClipboard(session, editSession, position);
			this.copyEntities(world, position, rotation);
			
		}
		catch(WorldEditException e){
			e.printStackTrace();
			return;
		}
	}
	
	public void reset(World world, BlockVector position){
		for(int y = this.generator.getPartSizeY()-1; y >= 0; y--){
			for(int x = 0; x < this.generator.getPartSizeXZ(); x++){
				for(int z = 0; z < this.generator.getPartSizeXZ(); z++){
					world.getBlockAt(position.getBlockX()+x, position.getBlockY()+y, position.getBlockZ()+z).setType(Material.AIR);
				}
			}
		}
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
	
	public List<Integer> getRotations(){
		return this.rotations;
	}
	
	public Collection<PartType> getPartTypes(){
		return Collections.unmodifiableCollection(this.partTypes);
	}
	
	public boolean typeMatch(PartType...partTypes){
		for(PartType partType : partTypes){
			if(this.partTypes.contains(partType)) return true;
		}
		return false;
	}
	
	public BlockVector getMinBlock(){
		return new BlockVector(this.template_x,this.template_y,this.template_z);
	}
	
	public BlockVector getMaxBlock(){
		return new BlockVector(this.template_x+this.generator.getPartSizeXZ()-1,this.template_y+this.generator.getPartSizeY()-1,this.template_z+this.generator.getPartSizeXZ()-1);
	}
	
	public org.bukkit.util.Vector getMinVector(){
		return new org.bukkit.util.Vector(this.template_x,this.template_y,this.template_z);
	}
	
	public org.bukkit.util.Vector getMaxVector(){
		return new org.bukkit.util.Vector(this.template_x+this.generator.getPartSizeXZ(),this.template_y+this.generator.getPartSizeY(),this.template_z+this.generator.getPartSizeXZ());
	}
	
	private String getTopSignature(int rotation){
		BlockVector from = new BlockVector(this.template_x,this.template_y+this.generator.getPartSizeY(),this.template_z);
		BlockVector to = new BlockVector(from.getBlockX()+this.generator.getPartSizeXZ()-1,from.getBlockY(),from.getBlockZ()+this.generator.getPartSizeXZ()-1);
		return BlockUtil.getSignature(this.generator.getWorld(), from, to, rotation);
	}
	
	private String getBottomSignature(int rotation){
		BlockVector from = new BlockVector(this.template_x,this.template_y-1,this.template_z);
		BlockVector to = new BlockVector(from.getBlockX()+this.generator.getPartSizeXZ()-1,from.getBlockY(),from.getBlockZ()+this.generator.getPartSizeXZ()-1);
		return BlockUtil.getSignature(this.generator.getWorld(), from, to, rotation);
	}
	
	private String getNorthSignature(int rotation){
		BlockVector from = new BlockVector(this.template_x,this.template_y,this.template_z-1);
		BlockVector to = new BlockVector(from.getBlockX()+this.generator.getPartSizeXZ()-1,from.getBlockY()+this.generator.getPartSizeY()-1,from.getBlockZ());
		return BlockUtil.getSignature(this.generator.getWorld(), from, to, rotation);
	}
	
	private String getEastSignature(int rotation){
		BlockVector from = new BlockVector(this.template_x+this.generator.getPartSizeXZ(),this.template_y,this.template_z);
		BlockVector to = new BlockVector(from.getBlockX(),from.getBlockY()+this.generator.getPartSizeY()-1,from.getBlockZ()+this.generator.getPartSizeXZ()-1);
		return BlockUtil.getSignature(this.generator.getWorld(), from, to, rotation);
	}
	
	private String getSouthSignature(int rotation){
		BlockVector from = new BlockVector(this.template_x,this.template_y,this.template_z+this.generator.getPartSizeXZ());
		BlockVector to = new BlockVector(from.getBlockX()+this.generator.getPartSizeXZ()-1,from.getBlockY()+this.generator.getPartSizeY()-1,from.getBlockZ());
		return BlockUtil.getSignature(this.generator.getWorld(), from, to, rotation);
	}
	
	private String getWestSignature(int rotation){
		BlockVector from = new BlockVector(this.template_x-1,this.template_y,this.template_z);
		BlockVector to = new BlockVector(from.getBlockX(),from.getBlockY()+this.generator.getPartSizeY()-1,from.getBlockZ()+this.generator.getPartSizeXZ()-1);
		return BlockUtil.getSignature(this.generator.getWorld(), from, to, rotation);
	}
	
	public String getSignature(Direction direction, int rotation){
		switch(direction){
		case UP: return this.getTopSignature( rotation);
		case DOWN: return this.getBottomSignature( rotation);
		case NORTH: return this.getNorthSignature( rotation);
		case EAST: return this.getEastSignature( rotation);
		case SOUTH: return this.getSouthSignature( rotation);
		case WEST: return this.getWestSignature( rotation);
		default: return null;
		}
	}
	
	public DungeonGenerator getGenerator(){
		return this.generator;
	}
	
	public BlockVector getPosition(){
		return new BlockVector(this.template_x,this.template_y,this.template_z);
	}
	
	public String getPositionString(){
		return this.template_x+", "+this.template_y+", "+this.template_z;
	}
	
	public String getName(){
		return this.name;
	}
	
	private BlockVector adjustPastePosition(BlockVector position, int rotation){
		switch(rotation){
		case 1:{
			return new BlockVector(position.getBlockX()+this.generator.getPartSizeXZ()-1,position.getBlockY(), position.getBlockZ());
		}
		case 2:{
			return new BlockVector(position.getBlockX()+this.generator.getPartSizeXZ()-1,position.getBlockY(), position.getBlockZ()+this.generator.getPartSizeXZ()-1);
		}
		case 3:{
			return new BlockVector(position.getBlockX(),position.getBlockY(), position.getBlockZ()+this.generator.getPartSizeXZ()-1);
		}
		default: return position;
		}
	}
	
	private void copyToClipboard(LocalSession session, EditSession editSession) throws WorldEditException{
		BlockVector3 from = BlockVector3.at(this.template_x, this.template_y, this.template_z);
		BlockVector3 to = BlockVector3.at(this.template_x+this.generator.getPartSizeXZ()-1, this.template_y+this.generator.getPartSizeY()-1, this.template_z+this.generator.getPartSizeXZ()-1);
		CuboidRegion selection = new CuboidRegion(from,to);
		BlockArrayClipboard clipboard = new BlockArrayClipboard(selection);
		clipboard.setOrigin(from);
		ForwardExtentCopy copy = new ForwardExtentCopy(editSession, selection, clipboard, BlockVector3.at(this.template_x,this.template_y,this.template_z));
		copy.setCopyingEntities(false);
		Operations.complete(copy);
		ClipboardHolder holder = new ClipboardHolder(clipboard);
		session.setClipboard(holder);
	}
	
	/*
	 * Rotates Clipboard and returns new paste position to keep the part in place
	 */
	private void rotateClipboard(LocalSession session, int rotation) throws WorldEditException{
		ClipboardHolder holder;
		holder = session.getClipboard();
		AffineTransform transform = new AffineTransform();
		transform = transform.rotateY(-rotation);
		holder.setTransform(holder.getTransform().combine(transform));
	}
	
	private void pasteClipboard(LocalSession session, EditSession editSession, BlockVector position) throws WorldEditException{
		editSession.setReorderMode(ReorderMode.MULTI_STAGE);
		ClipboardHolder holder = session.getClipboard();
		BlockVector3 to = BlockVector3.at(position.getBlockX(),position.getBlockY(),position.getBlockZ());
        Operation operation = holder
                .createPaste(editSession)
                .to(to)
                .ignoreAirBlocks(true)
                .build();
        Operations.completeLegacy(operation);
		editSession.flushSession();
	}
	
	private void copyEntities(World world, BlockVector toPosition, int rotation){
		//TODO fix item frame positioning
		List<Entity> entities = WorldUtil.getEntitiesWithinBoundingBox(this.generator.getWorld(), this.getMinVector(), this.getMaxVector());
		if(entities.size()>100){
			Bukkit.getLogger().info("[WARNUNG] [DungeonGenerator] Teil '"+this.name+"' versucht, "+entities.size()+" Entities zu kopieren! (Limit: 100)");
			return;
		}
		Entity entity;
		Entity clone;
		for(int i = 0; i < entities.size(); i++){
			entity = entities.get(i);
			if(entity.isInsideVehicle()){
				Entity vehicle = this.getRecursiveVehicle(entity);
				if(!entities.contains(vehicle)) entities.add(vehicle);
				continue;
			}
			clone = this.copyEntity(entity, world, toPosition, rotation);
			if(clone==null) continue;
			this.performDuplicationSafeties(clone);
		}
	}
	
	private void performDuplicationSafeties(Entity entity){
		//this unlinks custom shops from their original template
		if(entity.getCustomName()!=null && entity.getCustomName().startsWith("§rShop_")) entity.setCustomName("");
	}
	
	private Entity copyEntity(Entity template, World world, BlockVector pivot, int rotation){
		Location location = GeneratorUtil.getTargetLocation(template, world, new BlockVector(this.template_x,this.template_y,this.template_z), pivot, rotation);
		Entity result = EntityUtil.clone(template, location);
		if(result==null) return null;
		if(template instanceof Hanging){
			GeneratorUtil.cloneRotatedHangingSettings((Hanging)template, (Hanging)result, rotation);
		}
		if(template instanceof ItemFrame){
			GeneratorUtil.cloneItemFrameSettings((ItemFrame)template, (ItemFrame)result); //have to do that again after calling setFacingDirection for Hanging
		}
		return result;
	}
	
	private Entity getRecursiveVehicle(Entity entity){
		if(!entity.isInsideVehicle()) return entity;
		return this.getRecursiveVehicle(entity.getVehicle());
	}
	
	public static void createBoundingBox(DungeonGenerator generator, BlockVector position){
		Collection<Block> boundingBox = BlockUtil.getBox(generator.getWorld().getBlockAt(position.getBlockX(), position.getBlockY(), position.getBlockZ()), generator.getPartSizeXZ(), generator.getPartSizeY(), 1);
		for(Block block : boundingBox){
			block.setType(generator.getBoundingBoxMaterial());
		}
	}
}
