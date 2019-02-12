package ch.swisssmp.warehouse;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.BlockVector;

import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.nbt.NBTTagCompound;

public class StockLedgerInfo {
	
	private UUID warehouse_id;
	private Master master;
	
	public StockLedgerInfo(){
		
	}
	
	public StockLedgerInfo(Master master){
		this.warehouse_id = master.getId();
		this.master = master;
	}
	
	public void apply(ItemStack itemStack){
		NBTTagCompound data = ItemUtil.getData(itemStack);
		boolean isComplete = warehouse_id!=null && master!=null && master.getChests().size()>0;
		if(data==null) data = new NBTTagCompound();
		if(!isComplete){
			data.remove("warehouse_id");
		}
		else{
			data.setString("warehouse_id", warehouse_id.toString());
		}
		ItemUtil.setData(itemStack, data);
		ItemMeta itemMeta = itemStack.getItemMeta();
		if(!isComplete){
			itemMeta.setDisplayName("Leeres Lagerbuch");
			itemMeta.setLore(new ArrayList<String>());
		}
		else{
			if(!itemMeta.hasDisplayName() || itemMeta.getDisplayName().equals("Leeres Lagerbuch")){
				itemMeta.setDisplayName(ChatColor.AQUA+"Lagerbuch");
			}
			BlockVector vector = master.getChests().stream().findFirst().orElse(new BlockVector());
			List<String> description = new ArrayList<String>();
			description.add(ChatColor.GRAY+"Verteiler: "+vector.getX()+", "+vector.getY()+", "+vector.getZ());
			description.add(ChatColor.GRAY+"Empfänger: "+master.getSlaves().size()+" Truhen");
			itemMeta.setLore(description);
		}
		itemStack.setItemMeta(itemMeta);
	}
	
	public UUID getId(){
		return warehouse_id;
	}
	
	public void setId(UUID warehouse_id){
		this.warehouse_id = warehouse_id;
	}
	
	public void setMaster(Master master){
		this.warehouse_id = master.getId();
		this.master = master;
	}
	
	public Master getMaster(){
		return master;
	}
	
	public static StockLedgerInfo get(ItemStack itemStack){
		if(itemStack==null || itemStack.getType()!=Material.DIAMOND_SWORD) return null;
		NBTTagCompound data = ItemUtil.getData(itemStack);
		if(data==null || !data.hasKey("customEnum") || !data.getString("customEnum").toLowerCase().equals("stock_ledger")) return null;
		StockLedgerInfo result = new StockLedgerInfo();
		if(data.hasKey("warehouse_id")){
			UUID warehouse_id = UUID.fromString(data.getString("warehouse_id"));
			result.warehouse_id = warehouse_id;
			Master master = Master.get(warehouse_id);
			if(master!=null){
				result.master = master;
			}
		}
		return result;
	}
	
	public static StockLedgerInfo get(Block block){
		Master master = Master.get(block);
		if(master==null) return null;
		StockLedgerInfo result = new StockLedgerInfo(master);
		return result;
	}
}
