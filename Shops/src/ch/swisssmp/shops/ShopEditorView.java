package ch.swisssmp.shops;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.EditorSlot;
import ch.swisssmp.shops.editor.NPCSlot;

public class ShopEditorView extends CustomEditorView{
	
	private final Shop shop;
	
	private ShopEditorView(Shop shop, Player player){
		super(player);
		this.shop = shop;
	}
	
	private void updateShopTrades(ItemStack[] newContents){
		List<MerchantRecipe> newRecipes = new ArrayList<MerchantRecipe>();
		MerchantRecipe recipe;
		ItemStack ingredient;
		for(int i = 0; i < 8; i++){
			ItemStack result = newContents[i];
			if(result==null || result.getType()==Material.AIR){
				continue;
			}
			else if(newContents[i+9]==null && newContents[i+18]==null){
				continue;
			}
			recipe = new MerchantRecipe(result, Integer.MAX_VALUE);
			ingredient = newContents[i+9];
			if(ingredient!=null && ingredient.getType()!=Material.AIR){
				recipe.addIngredient(ingredient);
			}
			ingredient = newContents[i+18];
			if(ingredient!=null && ingredient.getType()!=Material.AIR){
				recipe.addIngredient(ingredient);
			}
			if(recipe.getIngredients().size()>0){
				newRecipes.add(recipe);
			}
		}
		this.shop.setRecipes(newRecipes);
	}
	
	@Override
	protected boolean allowEmptySlotInteraction(){
		return true;
	}

	@Override
	protected void onInventoryClosed(InventoryCloseEvent event) {
		if(this.getTopInventory()==null) return;
		this.updateShopTrades(this.getTopInventory().getContents());
	}
	
	public static ShopEditorView open(Player player, Shop shop){
		if(player==null || shop==null){
			return null;
		}
		ShopEditorView shopEditor = new ShopEditorView(shop,player);
		shopEditor.open();
		return shopEditor;
	}

	@Override
	protected int getInventorySize() {
		return 27;
	}

	@Override
	protected Collection<EditorSlot> initializeEditor() {
		Collection<EditorSlot> result = new ArrayList<EditorSlot>();
		result.add(new NPCSlot(this,8,this.shop.getNPC()));
		return result;
	}
	
	@Override
	protected void createItems() {
		super.createItems();

		Inventory inventory = this.getTopInventory();
		int column = 0;
		for(MerchantRecipe recipe : this.shop.getVillager().getRecipes()){
			inventory.setItem(column, recipe.getResult());
			inventory.setItem(column+9, recipe.getIngredients().get(0));
			if(recipe.getIngredients().size()>1){
				inventory.setItem(column+18, recipe.getIngredients().get(1));
			}
			column++;
		}
	}

	@Override
	public String getTitle() {
		return this.shop.getName()!=null && !shop.getName().isEmpty() ? this.shop.getName() : "Unbenannter Shop";
	}
}
