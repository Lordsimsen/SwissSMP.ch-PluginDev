package ch.swisssmp.editor.slot;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.CustomEditorAPI;
import ch.swisssmp.editor.CustomEditorView;

public abstract class EditorSlot {
	
	private final CustomEditorView view;
	private final int slot;
	
	public EditorSlot(CustomEditorView view, int slot){
		this.view = view;
		this.slot = slot;
	}
	
	public CustomEditorView getView(){
		return view;
	}
	
	public int getSlot(){
		return slot;
	}
	
	protected void setItem(ItemStack itemStack){
		this.view.getTopInventory().setItem(slot, itemStack);
	}
	
	protected void setItemLater(ItemStack itemStack){
		Bukkit.getScheduler().runTaskLater(CustomEditorAPI.getInstance(), ()->{
			this.view.getTopInventory().setItem(slot, itemStack);
		}, 1L);
	}
	
	protected ChatColor getDescriptionColor(){
		return ChatColor.GRAY;
	}
	
	protected ChatColor getSuggestActionColor(){
		return ChatColor.YELLOW;
	}
	
	protected ChatColor getValueColor(){
		return ChatColor.GREEN;
	}
	
	public abstract void createItem();
	
	/**
	 * Trigger a click on this slot
	 * @param clickType - The click type for this event
	 * @return <code>true</code> if default behaviour should be cancelled;
	 * 		   otherwise <code>false</code>
	 */
	public abstract boolean onClick(ClickType clickType);

	protected abstract boolean isComplete();
	
	public abstract String getName();
	
	protected List<String> getDescription(){
		List<String> description = new ArrayList<String>();
		List<String> valueDisplay = this.getValueDisplay();
		if(valueDisplay!=null){
			for(String line : valueDisplay){
				description.add(this.getValueColor()+line);
			}
		}
		if(!this.isComplete()){
			List<String> incompleteDescription = this.getIncompleteDescription();
			if(incompleteDescription!=null){
				for(String line : incompleteDescription){
					description.add(getSuggestActionColor()+line);
				}
			}
		}
		else{
			List<String> normalDescription = this.getNormalDescription();
			if(normalDescription!=null){
				for(String line : this.getNormalDescription()){
					description.add(getDescriptionColor()+line);
				}
			}
		}
		return description;
	}
	
	/**
	 * @return A list of lines displayed to the user representing the current value of this slot;
	 * 		   return <code>null</code> or empty to skip this
	 */
	protected List<String> getValueDisplay(){
		return null;
	}
	
	/**
	 * 
	 * @return A <code>List</code> of Strings describing what this slot is for, not containing the main color code
	 */
	protected abstract List<String> getNormalDescription();
	
	/**
	 * 
	 * @return A <code>List</code> of Strings describing what this slot is for when it is not fully assigned, not containing the main color code
	 */
	protected List<String> getIncompleteDescription() {
		return this.getNormalDescription();
	}
	
	protected ItemStack createSlot(){
		CustomItemBuilder itemBuilder = this.createSlotBase();
		if(itemBuilder==null){
			itemBuilder = new CustomItemBuilder();
			itemBuilder.setMaterial(Material.BARRIER);
		}
		itemBuilder.setAmount(1);
		itemBuilder.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		itemBuilder.setDisplayName(this.getName());
		itemBuilder.setLore(getDescription());
		ItemStack result = itemBuilder.build();
		return result;
	}
	
	protected abstract CustomItemBuilder createSlotBase();
}
