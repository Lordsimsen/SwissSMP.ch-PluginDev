package ch.swisssmp.craftmmo.mmoitem;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import ch.swisssmp.craftmmo.util.MmoResourceManager;

public class MmoItemManager {
	public static MmoItem getItem(String itemStringID) throws Exception{
		Integer mmo_item_id = Integer.parseInt(itemStringID);
		if(MmoItem.templates.containsKey(mmo_item_id)){
			return MmoItem.templates.get(mmo_item_id);
		}
		else{
			return null;
		}
	}
	public static ItemStack getItemStack(String itemName) throws Exception{
		MmoItem mmoitem = getItem(itemName);
		if(mmoitem==null)
			return null;
		return mmoitem.toItemStack();
	}
	public static void updateInventory(Inventory inventory){
		ItemStack[] itemStacks = inventory.getContents();
		for(ItemStack itemStack : itemStacks){
			if(itemStack==null){
				continue;
			}
			MmoItem.update(itemStack);
		}
	}
	@SuppressWarnings("deprecation")
	public static MaterialData getMaterialData(String materialString){
		if(materialString==null) return null;
		else if(materialString.isEmpty()) return null;
		if(materialString.contains(":")){
			String[] parts = materialString.split(":");
			try{
				return new MaterialData(Material.valueOf(parts[0]), Byte.parseByte(parts[1]));
			}
			catch(Exception e){
				return null;
			}
		}
		else{
			return new MaterialData(Material.valueOf(materialString));
		}
	}
	public static String getMaterialString(ItemStack itemStack){
		return MmoItemManager.getMaterialString(itemStack, false);
	}
	@SuppressWarnings("deprecation")
	public static String getMaterialString(ItemStack itemStack, boolean matchData){
		if(itemStack==null)
			return "";
		return MmoItemManager.getMaterialString(itemStack.getType(), itemStack.getData().getData(), matchData);
	}
	public static String getMaterialString(Material material){
		return MmoItemManager.getMaterialString(material, (byte) 0, false);
	}
	public static String getMaterialString(Material material, byte b){
		return MmoItemManager.getMaterialString(material, (byte) b, true);
	}
	public static String getMaterialString(Material material, byte b, boolean matchData){
		if(material==null)
			return "";
		String result = material.toString();
		if(matchData){
			return result+":"+b;
		}
		else return result;
	}
	public static ItemStack getItemFromHybridID(ConfigurationSection dataSection){
		ItemStack itemStack;
		if(dataSection.contains("mmo_item_id")){
			int mmo_item_id = dataSection.getInt("mmo_item_id");
			MmoItem mmoItem = MmoItem.get(mmo_item_id);
			if(mmoItem==null){
				return null;
			}
			itemStack = mmoItem.toItemStack();
		}
		else if(dataSection.contains("mc_id")){
			try{
				String enumData = dataSection.getString("mc_id");
				String[] enumSplit = enumData.split(":");
				String enumString = enumSplit[0];
				Material material = Material.valueOf(enumString);
				if(enumSplit.length>1){
					itemStack = new ItemStack(material, 1, (short) Short.parseShort(enumSplit[1]));
				}
				else{
					itemStack = new ItemStack(material);
				}
				if(dataSection.contains("name")){
					ItemMeta itemMeta = itemStack.getItemMeta();
					itemMeta.setDisplayName(dataSection.getString("name"));
					itemStack.setItemMeta(itemMeta);
				}
			}
			catch(Exception e){
				e.printStackTrace();
				return null;
			}
		}
		else{
			return null;
		}
		if(itemStack==null)
			return null;
		int amount = 1;
		if(dataSection.contains("amount")){
			amount = dataSection.getInt("amount");
		}
		if(dataSection.contains("max") && amount<1){
			int max = dataSection.getInt("max");
			if(max<1){
				return null;
			}
			int min = 1;
			if(dataSection.contains("min")){
				min = dataSection.getInt("min");
			}
			Random random = new Random();
			amount = random.nextInt(max-min+1)+min;
		}
		itemStack.setAmount(amount);
		if(itemStack.getAmount()<1)
			return null;
		return itemStack;
	}
	@SuppressWarnings("deprecation")
	public static void loadCraftingRecipes() throws Exception{
		Bukkit.resetRecipes();
		YamlConfiguration mmoCraftingConfiguration = MmoResourceManager.getYamlResponse("crafting.php");
		for(String itemIDstring : mmoCraftingConfiguration.getKeys(false)){
			ConfigurationSection recipesSection = mmoCraftingConfiguration.getConfigurationSection(itemIDstring);
			for(String recipe_id : recipesSection.getKeys(false)){
				ConfigurationSection dataSection = recipesSection.getConfigurationSection(recipe_id);
				if(dataSection.contains("mmo_item_id")){
					MmoItem mmoItem = MmoItem.get(dataSection.getInt("mmo_item_id"));
					if(mmoItem==null){
						continue;
					}
					mmoItem.recipes = new HashMap<String, Recipe>();
					ConfigurationSection ingredientsSection = dataSection.getConfigurationSection("ingredients");
					if(dataSection.getInt("shapeless")==1){
						ShapelessRecipe recipe = new ShapelessRecipe(mmoItem.toItemStack());
						for(String row_x : ingredientsSection.getKeys(false)){
							ConfigurationSection rowSection = ingredientsSection.getConfigurationSection(row_x);
							for(String row_y : rowSection.getKeys(false)){
								ConfigurationSection ingredientSection = rowSection.getConfigurationSection(row_y);
								MaterialData ingredient;
								if(ingredientSection.contains("mmo_item_id")){
									MmoItem ingredientItem = MmoItem.get(ingredientSection.getInt("mmo_item_id"));
									if(ingredientItem==null){
										ingredient = new MaterialData(Material.AIR);
									}
									else{
										ItemStack itemStack = ingredientItem.toItemStack();
										ingredient = itemStack.getData();
									}
								}
								else{
									String materialString = ingredientSection.getString("mc_id");
									Material material = Material.valueOf(materialString.split(":")[0]);
									String sub_id = materialString.split(":")[1];
									if(sub_id!="0"){
										ingredient = new MaterialData(material, Byte.parseByte(sub_id));
									}
									else{
										ingredient = new MaterialData(material);
									}
								}
								recipe.addIngredient(ingredient);
							}
						}
						mmoItem.recipes.put(dataSection.getName(), recipe);
					}
					else{
						ShapedRecipe recipe = new ShapedRecipe(mmoItem.toItemStack());
						HashMap<MaterialData, Character> ingredients = new HashMap<MaterialData, Character>();
						String shape = "";
						for(String row_x : ingredientsSection.getKeys(false)){
							ConfigurationSection rowSection = ingredientsSection.getConfigurationSection(row_x);
							for(String row_y : rowSection.getKeys(false)){
								ConfigurationSection ingredientSection = rowSection.getConfigurationSection(row_y);
								MaterialData ingredient;
								if(ingredientSection.contains("mmo_item_id")){
									MmoItem ingredientItem = MmoItem.get(ingredientSection.getInt("mmo_item_id"));
									if(ingredientItem==null){
										ingredient = new MaterialData(Material.AIR);
									}
									else{
										ItemStack itemStack = ingredientItem.toItemStack();
										ingredient = itemStack.getData();
									}
								}
								else{
									String materialString = ingredientSection.getString("mc_id");
									Material material;
									try{
										material = Material.valueOf(materialString.split(":")[0]);
										String sub_id = materialString.split(":")[1];
										if(sub_id!="0"){
											ingredient = new MaterialData(material, Byte.parseByte(sub_id));
										}
										else{
											ingredient = new MaterialData(material);
										}
									}
									catch(Exception e){
										ingredient = new MaterialData(Material.AIR);
									}
								}
								if(ingredient.getItemType()==Material.AIR){
									shape+=" ";
									continue;
								}
								if(ingredients.containsKey(ingredient)){
									shape+=ingredients.get(ingredient);
								}
								else{
									Character key = (char)ingredients.size();
									ingredients.put(ingredient, key);
									shape+=key;
								}
								
							}
						}
						recipe.shape(shape.substring(0, 3), shape.substring(3, 6), shape.substring(6, 9));
						for(Entry<MaterialData, Character> entry : ingredients.entrySet()){
							recipe.setIngredient(entry.getValue(), entry.getKey());
						}
						mmoItem.recipes.put(dataSection.getName(), recipe);
					}
					mmoItem.registerRecipes();
				}
			}
		}
	}
}