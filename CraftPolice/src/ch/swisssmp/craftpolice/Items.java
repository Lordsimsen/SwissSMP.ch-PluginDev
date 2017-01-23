package ch.swisssmp.craftpolice;

import java.util.Arrays;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.material.MaterialData;

public class Items {
	public static ItemMeta badgemeta;
	public static ItemStack badge;
	public static ItemMeta equipmentmeta;
	public static ItemStack equipment;
	public static ItemMeta helmetmeta;
	public static ItemStack helmet;
	public static LeatherArmorMeta chestplatemeta;
	public static ItemStack chestplate;
	public static LeatherArmorMeta legginsmeta;
	public static ItemStack leggins;
	public static LeatherArmorMeta bootsmeta;
	public static ItemStack boots;
	public static ItemMeta batmeta;
	public static ItemStack bat;
	
	public static ItemStack createContract(Player player){
		ItemStack contract = new ItemStack(Material.PAPER);
		ItemMeta contractMeta = contract.getItemMeta();
		contractMeta.setDisplayName("Arbeitsvertrag");
		contractMeta.setLore(Arrays.asList(player.getName(), "Polizist", "Rechtsklicke, um diesen", "Vertrag aufzulösen."));
		contract.setItemMeta(contractMeta);
		return contract;
	}

	public static void createBadgeRecipe(){
		MaterialData mat_data = new MaterialData(Material.NAME_TAG);
		badge = mat_data.toItemStack(1);
		ItemMeta meta = badge.getItemMeta();
		meta.setDisplayName("Polizeimarke");
		meta.setLore(Arrays.asList("Diese Marke zeichnet einen echten", "Polizisten im Dienst aus."));
		badgemeta = meta;
		badge.setItemMeta(meta);
		ShapedRecipe recipe = new ShapedRecipe(badge);
		recipe.shape("gdg","dnd","gdg");
		recipe.setIngredient('g', Material.GOLD_INGOT);
		recipe.setIngredient('d', Material.DIAMOND);
		recipe.setIngredient('n', Material.NAME_TAG);
		Main.server.addRecipe(recipe);
	}
	
	public static void createEquipmentRecipe(){
		MaterialData mat_data = new MaterialData(Material.CHEST);
		equipment = mat_data.toItemStack(1);
		ItemMeta meta = equipment.getItemMeta();
		meta.setDisplayName("Polizei-Rüstung");
		meta.setLore(Arrays.asList("Rechtsklicke, um deine", "Ausrüstung auszupacken."));
		equipmentmeta = meta;
		equipment.setItemMeta(meta);
		ShapedRecipe recipe = new ShapedRecipe(equipment);
		recipe.shape("igi","ioi","iii");
		recipe.setIngredient('i', Material.IRON_INGOT);
		recipe.setIngredient('g', Material.GOLD_INGOT);
		recipe.setIngredient('o', Material.OBSIDIAN);
		Main.server.addRecipe(recipe);
	}
	
	public static void createEquipmentItems(){
		helmet = new ItemStack(Material.CHAINMAIL_HELMET);
		helmetmeta = helmet.getItemMeta();
		helmetmeta.setDisplayName("Polizei Helm");
		helmetmeta.setLore(Arrays.asList("Dieser Helm wird nur von", "echten Polizisten getragen."));
		helmet.setItemMeta(helmetmeta);
		chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
		chestplatemeta = (LeatherArmorMeta)chestplate.getItemMeta();
		chestplatemeta.setDisplayName("Schutzweste");
		chestplatemeta.setLore(Arrays.asList("Erhöhter Schutz vor Projektilen", "für wahre Polizisten."));
		chestplatemeta.setColor(Color.fromRGB(25, 25, 25));
		chestplate.setItemMeta(chestplatemeta);
		leggins = new ItemStack(Material.LEATHER_LEGGINGS);
		legginsmeta = (LeatherArmorMeta)leggins.getItemMeta();
		legginsmeta.setDisplayName("Polizei Hose");
		legginsmeta.setLore(Arrays.asList("Verstärkte Hosen mit", "erhöhter Haltbarkeit."));
		legginsmeta.setColor(Color.fromRGB(25, 25, 200));
		leggins.setItemMeta(legginsmeta);
		boots = new ItemStack(Material.LEATHER_BOOTS);
		bootsmeta = (LeatherArmorMeta)boots.getItemMeta();
		bootsmeta.setDisplayName("Polizei Schuhe");
		bootsmeta.setLore(Arrays.asList("Gelände- und wetterfest, ideal", "für wilde Verfolgungsjagden!"));
		bootsmeta.setColor(Color.fromRGB(25, 25, 25));
		boots.setItemMeta(bootsmeta);
		bat = new ItemStack(Material.STICK);
		batmeta = bat.getItemMeta();
		batmeta.setDisplayName("Schlagstock");
		batmeta.setLore(Arrays.asList("Die Gauner werden ihn lieben!"));
		batmeta.addEnchant(Enchantment.DAMAGE_ALL, 2, false);
		bat.setItemMeta(batmeta);
	}
}
