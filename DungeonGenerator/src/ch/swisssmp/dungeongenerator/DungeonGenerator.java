package ch.swisssmp.dungeongenerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.session.SessionKey;
import com.sk89q.worldedit.session.SessionOwner;
import com.sk89q.worldedit.util.auth.AuthorizationException;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.YamlConfiguration;

public class DungeonGenerator implements SessionOwner{
	private final GeneratorManager manager;
	private final World world;
	
	private final int generator_id;
	private String generator_name;
	
	private final Material boundingBoxMaterial = Material.GOLD_BLOCK;
	private final Material generationBoxMaterial = Material.DIAMOND_BLOCK;

	private ArrayList<GeneratorPart> templateParts = new ArrayList<GeneratorPart>();
	private BlockVector templatePosition = null;
	private BlockVector generationPosition = null;
	private int defaultSize;
	private int partSizeXZ;
	private int partSizeY;
	
	private final SessionKey sessionKey;
	
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
		this.defaultSize = (dataSection.contains("default_size") ? dataSection.getInt("default_size") : 100);
		this.partSizeXZ = dataSection.getInt("part_size_xz");
		this.partSizeY = dataSection.getInt("part_size_y");
		
		
		this.sessionKey = new WorldEditSessionKey();
		
		if(this.templatePosition!=null){
			this.loadGeneratorParts(templatePosition);
			this.updateSignatures();
		}
	}
	
	@Override
	public void checkPermission(String arg0) throws AuthorizationException {
		return;
	}
	
	public int generate(CommandSender sender, long seed, int size){
		this.update();
		List<GenerationPart> parts = PartGenerator.generateData(sender, this, this.generationPosition, seed, size);
		if(parts==null) return -1;
		int resultSize = 0;
		for(GenerationPart part : parts){
			if(part instanceof ObstructedGenerationPart) continue;
			resultSize++;
		}
		Bukkit.getLogger().info("[DungeonGenerator] Data generated, placing blocks...");
		GenerationRoutine.run(parts, world, this.generationPosition, this.partSizeXZ, this.partSizeY);
		return resultSize;
	}
	
	public int generate(CommandSender sender, long seed){
		return this.generate(sender, seed, this.defaultSize);
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
	
	public void setGenerationPosition(BlockVector position){
		this.generationPosition = position;
		for(Block block : BlockUtil.getBox(position.toLocation(this.world).getBlock(), partSizeXZ, partSizeY)){
			block.setType(this.generationBoxMaterial);
		}
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
	
	public void setDefaultSize(int size){
		this.defaultSize = size;
		this.manager.saveAll();
	}
	
	public int getDefaultSize(){
		return this.defaultSize;
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
				itemStack.setDurability(tokenStack.getDurability());
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
	
	private GeneratorPart getTemplatePart(Block block){
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
		return new GeneratorPart(this,block.getWorld().getBlockAt(boxOrigin.getX()+1,boxOrigin.getY()+1,boxOrigin.getZ()+1),partConfiguration);
	}
	
	protected void save(ConfigurationSection dataSection){
		dataSection.set("generator_id", this.generator_id);
		dataSection.set("generator_name", this.generator_name);
		dataSection.set("part_size_xz", this.partSizeXZ);
		dataSection.set("part_size_y", this.partSizeY);
		if(this.templatePosition!=null) dataSection.set("template_position", new Vector(this.templatePosition.getBlockX(),this.templatePosition.getY(),this.templatePosition.getZ()));
		if(this.generationPosition!=null) dataSection.set("generation_position", new Vector(this.generationPosition.getBlockX(),this.generationPosition.getBlockY(),this.generationPosition.getBlockZ()));
		dataSection.set("default_size", this.defaultSize);
		this.updateTokens();
	}

	protected void setName(String name){
		this.generator_name = name;
		this.manager.saveAll();
	}

	protected void update(){
		this.loadGeneratorParts(this.templatePosition);
		this.updateSignatures();
		this.manager.saveAll();
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
		while(remainingCheckTolerance>0){
			current = world.getBlockAt(x,y,z);
			if(current.getType()==this.boundingBoxMaterial){
				//attempt loading a GeneratorPart
				part = this.getTemplatePart(world.getBlockAt(current.getX(),current.getY(),current.getZ()));
				//GeneratorPart found
				if(part!=null){
					markerLocation = part.getMaxBlock().toLocation(world);
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

	private void updateSignatures(){
		for(GeneratorPart part : this.templateParts){
			part.updateSignatures();
		}
	}
}
