package ch.swisssmp.shops;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.MerchantRecipe;

import ch.swisssmp.npc.NPCInstance;

public class Shop {
	
	private static String SHOP_IDENTIFIER = "admin_shop";
	
	private final NPCInstance npc;
	private final Villager villager;
	
	private Shop(NPCInstance npc, Villager villager){
		this.npc = npc;
		this.villager = villager;
	}
	
	protected String getName(){
		return this.npc.getName();
	}
	
	public NPCInstance getNPC(){
		return this.npc;
	}
	
	public Villager getVillager(){
		return this.villager;
	}
	
	protected boolean hasTrades(){
		return this.villager.getRecipeCount() > 0;
	}
	
	protected void openEditor(Player player){
		ShopEditorView.open(player, this);
	}
	
	protected void setRecipes(List<MerchantRecipe> recipes){
		this.villager.setVillagerExperience(Integer.MAX_VALUE);
		this.villager.setVillagerLevel(5);
		this.villager.setRecipes(recipes);
	}
	
	public static Shop create(Location location, Player owner){
		String shopName = "§a"+owner.getName()+"s Shop";
		NPCInstance npcInstance = NPCInstance.create(EntityType.VILLAGER, location);
		npcInstance.setName(shopName);
		npcInstance.setIdentifier(SHOP_IDENTIFIER);
		((Villager) npcInstance.getEntity()).setRecipes(new ArrayList<MerchantRecipe>());
		return new Shop(npcInstance, (Villager)npcInstance.getEntity());
	}
	
	protected static Shop get(Entity entity){
		NPCInstance npc = NPCInstance.get(entity);
		if(npc==null) return null;
		return get(npc);
	}
	
	protected static Shop get(NPCInstance npc){
		if(!(npc.getEntity() instanceof Villager) || npc.getIdentifier()==null || !npc.getIdentifier().equals(SHOP_IDENTIFIER)) return null;
		Villager villager = (Villager) npc.getEntity();
		return new Shop(npc, villager);
	}
}
