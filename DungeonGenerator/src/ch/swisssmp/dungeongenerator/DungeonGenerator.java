package ch.swisssmp.dungeongenerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.session.SessionKey;
import com.sk89q.worldedit.session.SessionOwner;
import com.sk89q.worldedit.util.auth.AuthorizationException;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.ObservableCompoundRoutine;
import ch.swisssmp.utils.ObservableRoutine;
import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class DungeonGenerator implements SessionOwner{
	private final Material boundingBoxMaterial = Material.GOLD_BLOCK;
	private int branchDensity;
	
	private int chamberCount;
	private int corridorLength;
	
	private ObservableRoutine currentWorkingRoutine = null;
	private int defaultSize;

	private final Material generationBoxMaterial = Material.DIAMOND_BLOCK;
	private BlockVector generationPosition = null;
	private final int generator_id;
	private String generator_name;
	private List<GenerationPart> lastGenerationData;

	private final GeneratorManager manager;
	private int partSizeXZ;
	private int partSizeY;
	private final SessionKey sessionKey;
	
	private ArrayList<GeneratorPart> templateParts = new ArrayList<GeneratorPart>();
	
	private BlockVector templatePosition = null;
	
	private final World world;
	
	protected DungeonGenerator(GeneratorManager manager, ConfigurationSection dataSection){
		this.manager = manager;
		this.world = manager.getWorld();
		
		this.generator_id = dataSection.getInt("generator_id");
		this.generator_name = dataSection.getString("generator_name");
		
		Vector templateOriginVector = dataSection.getVector("template_position");
		if(templateOriginVector!=null){
			this.templatePosition = new BlockVector(templateOriginVector);
		}
		Vector defaultPosition = dataSection.getVector("generation_position");
		if(defaultPosition!=null){
			this.generationPosition = new BlockVector(defaultPosition.getBlockX(),defaultPosition.getBlockY(),defaultPosition.getBlockZ());
		}
		this.partSizeXZ = dataSection.getInt("part_size_xz");
		this.partSizeY = dataSection.getInt("part_size_y");

		this.defaultSize = (dataSection.contains("default_size") ? dataSection.getInt("default_size") : 100);
		this.corridorLength = (dataSection.contains("corridor_length") ? dataSection.getInt("corridor_length") : 10);
		this.chamberCount = (dataSection.contains("chamber_count") ? dataSection.getInt("chamber_count") : 5);
		this.branchDensity = (dataSection.contains("branch_density") ? dataSection.getInt("branch_density") : 3);
		
		this.sessionKey = new WorldEditSessionKey();
		
		if(this.templatePosition!=null){
			this.loadGeneratorParts(templatePosition);
		}
	}

	@Override
	public void checkPermission(String arg0) throws AuthorizationException {
		return;
	}
	
	public ObservableRoutine generate(CommandSender sender, long seed){
		return this.generate(sender, seed, this.defaultSize);
	}
	
	public ObservableRoutine generate(CommandSender sender, long seed, int size){
		if(this.currentWorkingRoutine!=null) return currentWorkingRoutine;
		List<ObservableRoutine> routineParts = new ArrayList<ObservableRoutine>();
		if(this.lastGenerationData!=null){
			ResetRoutine resetRoutine = new ResetRoutine(lastGenerationData, world, generationPosition, partSizeXZ, partSizeY);
			routineParts.add(resetRoutine);
		}
		
		PartGenerationRoutine generationRoutine = new PartGenerationRoutine(this,sender,seed,size);
		this.lastGenerationData = generationRoutine.getResult();
		PartPlacementRoutine placementRoutine = new PartPlacementRoutine(generationRoutine.getResult(), world, this.generationPosition, this.partSizeXZ, this.partSizeY);
		routineParts.add(generationRoutine);
		routineParts.add(placementRoutine);
		ObservableCompoundRoutine compoundRoutine = new ObservableCompoundRoutine(routineParts);
		compoundRoutine.start(DungeonGeneratorPlugin.getInstance(), 0, 1);
		return compoundRoutine;
	}
	
	public int getDefaultSize(){
		return this.defaultSize;
	}
	
	public int getCorridorLength(){
		return this.corridorLength;
	}
	
	public int getChamberCount(){
		return this.chamberCount;
	}
	
	public int getBranchDensity(){
		return this.branchDensity;
	}
	
	public Material getGenerationBoxMaterial(){
		return this.generationBoxMaterial;
	}
	
	public BlockVector getGenerationPosition(){
		return this.generationPosition;
	}
	
	@Override
	public String[] getGroups() {
		return new String[]{};
	}
	
	public String getName(){
		return this.generator_name;
	}
	
	public int getPartSizeXZ(){
		return this.partSizeXZ;
	}
	
	public int getPartSizeY(){
		return this.partSizeY;
	}
	
	@Override
	public SessionKey getSessionKey() {
		return this.sessionKey;
	}
	
	public BlockVector getTemplatePosition(){
		return this.templatePosition;
	}
	
	public World getWorld(){
		return this.world;
	}
	
	@Override
	public boolean hasPermission(String arg0) {
		return true;
	}
	
	public ObservableRoutine reset(CommandSender sender){
		if(this.currentWorkingRoutine!=null) return currentWorkingRoutine;
		if(lastGenerationData==null || lastGenerationData.size()==0){
			this.markGenerationPosition();
			return null;
		}
		ResetRoutine result = new ResetRoutine(lastGenerationData,world,generationPosition,partSizeXZ,partSizeY);
		if(sender instanceof Player) result.addObserver((Player) sender);
		result.addOnFinishListener(()->{
			this.markGenerationPosition();
			this.lastGenerationData = null;
		});
		result.start(DungeonGeneratorPlugin.getInstance(), 0, 1);
		return result;
	}
	
	public void setDefaultSize(int size){
		this.defaultSize = size;
		this.manager.saveAll();
	}
	
	public void setGenerationPosition(BlockVector position){
		this.generationPosition = position;
		this.markGenerationPosition();
		this.update();
	}
	
	public void setPartSizeXZ(int partSizeXZ){
		this.partSizeXZ = partSizeXZ;
		this.manager.saveAll();
	}
	
	public void setPartSizeY(int partSizeY){
		this.partSizeY = partSizeY;
		this.manager.saveAll();
	}
	
	public void setTemplatePosition(BlockVector position){
		this.templatePosition = position;
		for(Block block : BlockUtil.getBox(position.toLocation(this.world).getBlock(), partSizeXZ, partSizeY, 1)){
			block.setType(this.boundingBoxMaterial);
		}
		for(int x = 0; x < this.partSizeXZ; x++){
			this.world.getBlockAt(this.templatePosition.getBlockX()-5-x, this.templatePosition.getBlockY() -1, this.templatePosition.getBlockZ() -1).setType(Material.REDSTONE_BLOCK);
		}
		for(int z = 0; z < this.partSizeXZ; z++){
			this.world.getBlockAt(this.templatePosition.getBlockX() -1, this.templatePosition.getBlockY() -1, this.templatePosition.getBlockZ()-5-z).setType(Material.LAPIS_BLOCK);
		}
		this.update();
	}
	
	public void unload(){
		WorldEdit.getInstance().getSessionManager().remove(this);
	}
	
	public void updateTokens(){
		ItemStack tokenStack = ItemManager.getInventoryToken(this, 1);
		for(Player player : Bukkit.getOnlinePlayers()){
			for(ItemStack itemStack : player.getInventory()){
				if(itemStack==null || !itemStack.hasItemMeta()) continue;
				if(ItemUtil.getInt(itemStack, "generator_id")!=this.generator_id) continue;
				itemStack.setItemMeta(tokenStack.getItemMeta());
			}
		}
	}
	
	protected Material getBoundingBoxMaterial(){
		return this.boundingBoxMaterial;
	}
	
	protected int getId(){
		return this.generator_id;
	}
	
	protected List<String> getInfo(){
		List<String> result = new ArrayList<String>();
		if(this.generationPosition!=null) result.add(ChatColor.GRAY+"Startpunkt: "+this.generationPosition.getBlockX()+", "+this.generationPosition.getBlockY()+", "+this.generationPosition.getBlockZ());
		else result.add(ChatColor.GRAY+"Startpunkt: "+ChatColor.RED+"Bitte setzen");
		result.add(ChatColor.GRAY+"Standardgrösse: "+this.defaultSize+(this.defaultSize==1 ? " Teil" : " Teile"));
		result.add(ChatColor.GRAY+"Teil-Grösse: "+this.partSizeXZ+" x "+this.partSizeY);
		if(this.templatePosition!=null) result.add(ChatColor.GRAY+"Vorlage: "+this.templatePosition.getX()+", "+this.templatePosition.getY()+", "+this.templatePosition.getZ());
		else result.add(ChatColor.GRAY+"Vorlage: "+ChatColor.RED+"Bitte setzen");
		result.add(ChatColor.GRAY+"Vorlagengrösse: "+this.templateParts.size()+(this.templateParts.size()==1 ? " Teil" : " Teile"));
		return result;
	}
	
	protected String getPartsString(){
		List<String> stringParts = new ArrayList<String>();
		for(GeneratorPart part : this.templateParts){
			stringParts.add(part.getInfoString());
		}
		return String.join(", ", stringParts);
	}
	
	protected ArrayList<GeneratorPart> getTemplateParts(){
		return this.templateParts;
	}
	
	protected void inspectInBrowser(Player player){
		this.manager.addBrowserInspection(player, this);
		this.update();
	}
	
	protected void save(ConfigurationSection dataSection){
		dataSection.set("generator_id", this.generator_id);
		dataSection.set("generator_name", this.generator_name);
		dataSection.set("part_size_xz", this.partSizeXZ);
		dataSection.set("part_size_y", this.partSizeY);
		if(this.templatePosition!=null) dataSection.set("template_position", new Vector(this.templatePosition.getBlockX(),this.templatePosition.getY(),this.templatePosition.getZ()));
		if(this.generationPosition!=null) dataSection.set("generation_position", new Vector(this.generationPosition.getBlockX(),this.generationPosition.getBlockY(),this.generationPosition.getBlockZ()));
		dataSection.set("default_size", this.defaultSize);
		dataSection.set("corridor_length", this.corridorLength);
		dataSection.set("chamber_count", this.chamberCount);
		dataSection.set("branch_density", this.branchDensity);
		this.updateTokens();
	}
	
	protected void saveSettings(){
		this.manager.saveAll();
	}
	
	protected void setName(String name){
		this.generator_name = name;
		this.manager.saveAll();
	}
	
	protected void setCorridorLength(int corridorLength){
		this.corridorLength = corridorLength;
	}
	
	protected void setChamberCount(int chamberCount){
		this.chamberCount = chamberCount;
	}
	
	protected void setBranchDensity(int branchDensity){
		this.branchDensity = branchDensity;
	}
	
	protected void update(){
		this.loadGeneratorParts(this.templatePosition);
		this.manager.saveAll();
		this.updateBrowserInspections();
	}

	private GeneratorPart getTemplatePart(int part_id, Block block){
		if(block.getType()!=this.boundingBoxMaterial){
			System.out.println("Block gehört nicht zu einer Bounding Box.");
			return null;
		}
		Block boxOrigin = BlockUtil.getBoxOrigin(block);
		Block partOrigin = boxOrigin.getWorld().getBlockAt(boxOrigin.getX()+1, boxOrigin.getY()+1, boxOrigin.getZ()+1);
		Collection<Block> boundingBox = BlockUtil.getBox(partOrigin, this.getPartSizeXZ(), this.getPartSizeY(), 1);
		//check if bounding box is correct and complete
		for(Block boundingBoxPart : boundingBox){
			if(boundingBoxPart.getType()!=this.getBoundingBoxMaterial()){
				System.out.println("Ungültigen Block gefunden.");
				return null;
			}
		}
		YamlConfiguration partConfiguration = PartConfigurationUtil.getPartConfiguration(boundingBox);
		return new GeneratorPart(this, part_id, partOrigin,partConfiguration);
	}
	
	/*
	 * Loads Generators based on Arrangement in the World
	 * It expects the parts to be arranged in rows aligned on the x axis (all rows share same z start position and stretch to z+)
	 */
	private void loadGeneratorParts(BlockVector start){
		if(start==null)return;
		this.templateParts.clear();
		ArrayList<GeneratorPart> result = new ArrayList<GeneratorPart>();
		int x = start.getBlockX()-1;//We're checking the bounding boxes, those are extruded by 1
		int y = start.getBlockY()-1;
		int z = start.getBlockZ()-1;
		
		if(this.world.getBlockAt(x, y, z).getType()!=this.boundingBoxMaterial){
			System.out.println("Vorlage nicht gefunden.");
			return;
		}
		
		int edge_length = this.partSizeXZ+2;
		int initial_z = z;
		boolean moveZ = true;
		int remainingCheckTolerance = edge_length;
		Block current;
		GeneratorPart part;
		Location markerLocation;
		int part_id = 0;
		while(remainingCheckTolerance>0){
			current = world.getBlockAt(x,y,z);
			if(current.getType()==this.boundingBoxMaterial){
				//attempt loading a GeneratorPart
				part = this.getTemplatePart(part_id, world.getBlockAt(current.getX(),current.getY(),current.getZ()));
				//GeneratorPart found
				if(part!=null){
					//Bukkit.getLogger().info("[DungeonGenerator] Found Part "+part.getName());
					if(!GeneratorUtil.isVolumeEmpty(current.getRelative(BlockFace.SOUTH_EAST).getRelative(BlockFace.UP), this.partSizeXZ, this.partSizeY)){
						result.add(part); //skip empty boxes but add valid parts with contents
						markerLocation = part.getMaxBlock().toLocation(world);
						markerLocation.add(1-this.partSizeXZ/2f,3f,1-this.partSizeXZ/2f);
						PartMarker.show(markerLocation, Color.GREEN, 60L);
						part_id++;
					}
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
				else if(part==null){
					System.out.println("Kein Teil gefunden ("+x+", "+y+", "+z+").");
					remainingCheckTolerance--;
				}
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
	
	private void markGenerationPosition(){
		for(Block block : BlockUtil.getBox(this.generationPosition.toLocation(this.world).getBlock(), partSizeXZ, partSizeY)){
			block.setType(this.generationBoxMaterial);
		}
	}

	private void updateBrowserInspections(){
		Collection<String> inspectors = this.manager.getInspectors(this);
		List<String> arguments = new ArrayList<String>();
		for(String inspector : inspectors){
			arguments.add("player[]="+URLEncoder.encode(inspector));
		}
		arguments.add("id="+this.generator_id);
		arguments.add("generator_name="+URLEncoder.encode(this.generator_name));
		arguments.add("mc_world="+URLEncoder.encode(this.world.getName()));
		JsonObject jsonData = new JsonObject();
		jsonData.addProperty("part_size_xz", this.partSizeXZ);
		jsonData.addProperty("part_size_y", this.partSizeY);
		jsonData.addProperty("default_size", this.defaultSize);
		if(this.templatePosition!=null){
			JsonObject templatePositionData = new JsonObject();
			templatePositionData.addProperty("x", this.templatePosition.getX());
			templatePositionData.addProperty("y", this.templatePosition.getY());
			templatePositionData.addProperty("z", this.templatePosition.getZ());
			jsonData.add("template_position", templatePositionData);
		}
		if(this.generationPosition!=null){
			JsonObject generationPositionData = new JsonObject();
			generationPositionData.addProperty("x", this.templatePosition.getX());
			generationPositionData.addProperty("y", this.templatePosition.getY());
			generationPositionData.addProperty("z", this.templatePosition.getZ());
			jsonData.add("generation_position", generationPositionData);
		}
		JsonArray partsData = new JsonArray();
		for(GeneratorPart part : this.templateParts){
			partsData.add(part.getJsonData());
		}
		jsonData.add("parts", partsData);
		arguments.add("data="+Base64.encodeBase64URLSafeString(jsonData.toString().getBytes()));
		String[] argumentsArray = new String[arguments.size()];
		arguments.toArray(argumentsArray);
		DataSource.getResponse(DungeonGeneratorPlugin.getInstance(), "inspect_generator.php", argumentsArray);
	}
}
