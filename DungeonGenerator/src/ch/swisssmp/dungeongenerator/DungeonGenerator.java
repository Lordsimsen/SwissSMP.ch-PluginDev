package ch.swisssmp.dungeongenerator;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.BlockVector;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.session.SessionKey;
import com.sk89q.worldedit.session.SessionOwner;
import com.sk89q.worldedit.util.auth.AuthorizationException;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.webcore.DataSource;
import net.minecraft.server.v1_12_R1.NBTTagCompound;

public class DungeonGenerator implements SessionOwner{
	private final int generator_id;
	private final World world;
	private final SessionKey sessionKey;
	private final Material boundingBoxMaterial = Material.GOLD_BLOCK;

	private String generator_name;
	private int partSizeXZ;
	private int partSizeY;
	private Block templateOrigin = null;
	private BlockVector defaultPosition = new BlockVector(0,100,0);
	private int defaultSize;
	
	private ArrayList<GeneratorPart> templateParts = new ArrayList<GeneratorPart>();
	
	protected DungeonGenerator(World world, ConfigurationSection dataSection){
		this.generator_id = dataSection.getInt("generator_id");
		this.generator_name = dataSection.getString("generator_name");
		this.world = world;
		this.partSizeXZ = dataSection.getInt("part_size_xz");
		this.partSizeY = dataSection.getInt("part_size_y");
		if(dataSection.contains("template_origin")){
			Location templateOriginLocation = dataSection.getLocation("template_origin", world);
			if(templateOriginLocation!=null){
				this.templateOrigin = templateOriginLocation.getBlock();
			}
		}
		if(dataSection.contains("default_position")){
			Location defaultPosition = dataSection.getLocation("default_position", world);
			if(defaultPosition!=null){
				this.defaultPosition = new BlockVector(defaultPosition.getBlockX(),defaultPosition.getBlockY(),defaultPosition.getBlockZ());
			}
		}
		this.defaultSize = (dataSection.contains("default_size") ? dataSection.getInt("default_size") : 100);
		this.sessionKey = new WorldEditSessionKey();
		if(this.templateOrigin!=null){
			this.loadGeneratorParts(templateOrigin);
			this.updateSignatures();
		}
	}
	
	protected int getId(){
		return this.generator_id;
	}
	
	public ItemStack getInventoryToken(int amount){
		String displayName = ChatColor.LIGHT_PURPLE+this.generator_name;
		CustomItemBuilder customItemBuilder = CustomItems.getCustomItemBuilder("DUNGEON_GENERATOR");
		customItemBuilder.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		customItemBuilder.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		customItemBuilder.setUnbreakable(true);
		customItemBuilder.setAmount(amount);
		customItemBuilder.setDisplayName(displayName);
		customItemBuilder.setLore(this.getInfo());
		ItemStack result = customItemBuilder.build();
		net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(result);
		NBTTagCompound nbtTag = nmsStack.getTag();
		if(nbtTag==null) nbtTag = new NBTTagCompound();
		nbtTag.setInt("generator_id", this.generator_id);
		nmsStack.setTag(nbtTag);
		ItemMeta itemMeta = CraftItemStack.getItemMeta(nmsStack);
		result.setItemMeta(itemMeta);
		return result;
	}
	
	public World getWorld(){
		return this.world;
	}
	
	protected List<String> getInfo(){
		List<String> result = new ArrayList<String>();
		result.add(ChatColor.GRAY+"Startpunkt: "+this.world.getName()+" ("+this.defaultPosition.getBlockX()+", "+this.defaultPosition.getBlockY()+", "+this.defaultPosition.getBlockZ()+")");
		result.add(ChatColor.GRAY+"Standardgrösse: "+this.defaultSize+(this.defaultSize==1 ? " Teil" : " Teile"));
		result.add(ChatColor.GRAY+"Teil-Grösse: "+this.partSizeXZ+" x "+this.partSizeY);
		result.add(ChatColor.GRAY+"Vorlage: "+this.world.getName()+" ("+this.templateOrigin.getX()+", "+this.templateOrigin.getY()+", "+this.templateOrigin.getZ()+")");
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
	
	public boolean generate(World world, long seed){
		return this.generate(world, this.defaultPosition, seed, this.defaultSize);
	}
	
	public boolean generate(World world, BlockVector position, long seed, int size){
		this.update();
		List<GenerationPart> parts = PartGenerator.generateData(this, position, seed, size);
		if(parts==null) return false;
		Bukkit.getLogger().info("[DungeonGenerator] Data generated, placing blocks...");
		GenerationRoutine.run(parts, world, position);
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
		this.updateTokens();
	}
	
	public void updateTokens(){
		ItemStack tokenStack = this.getInventoryToken(1);
		for(Player player : Bukkit.getOnlinePlayers()){
			for(ItemStack itemStack : player.getInventory()){
				if(itemStack==null || !itemStack.hasItemMeta()) continue;
				net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
				if(!nmsStack.hasTag() || !nmsStack.getTag().hasKey("generator_id")) continue;
				if(nmsStack.getTag().getInt("generator_id")!=this.generator_id) continue;
				itemStack.setItemMeta(tokenStack.getItemMeta());
				itemStack.setDurability(tokenStack.getDurability());
			}
		}
	}
	
	public void unload(){
		WorldEdit.getInstance().getSessionManager().remove(this);
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
