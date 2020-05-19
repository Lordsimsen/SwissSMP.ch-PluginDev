package ch.swisssmp.laposte;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.SkullCreator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

public class CraftingRecipes {

//    protected static final String mailboxTextureBlue = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjZhNDllZmFhYWI1MzI1NTlmZmY5YWY3NWRhNmFjNGRkNzlkMTk5ZGNmMmZkNDk3Yzg1NDM4MDM4NTY0In19fQ==";
//    protected static final String mailboxIdBlue = "48614330-6c44-47be-85ec-33ed037cf48c";
//    protected static final String mailboxTextureWhite = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTM5ZTE5NzFjYmMzYzZmZWFhYjlkMWY4NWZjOWQ5YmYwODY3NjgzZjQxMjk1NWI5NjExMTdmZTY2ZTIifX19";
//    protected static final String mailboxIdWhite = "480bff09-ed89-4214-a2bd-dab19fa5177d";
//    protected static final String mailboxTextureRed = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGZhODljZTg1OTMyYmVjMWExYzNmMzFjYjdjMDg1YTViZmIyYWM3ZTQwNDA5NDIwOGMzYWQxMjM4NzlkYTZkYSJ9fX0=";
//    protected static final String mailboxIdRed = "6a71ad04-2422-41f3-a501-6ea5707aaef3";
//    protected static final String mailboxTextureGreen = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzJiY2NiNTI0MDg4NWNhNjRlNDI0YTBjMTY4YTc4YzY3NmI4Yzg0N2QxODdmNmZiZjYwMjdhMWZlODZlZSJ9fX0=";
//    protected static final String mailboxIdGreen = "60621c0e-cb3e-471b-a237-4dec155f4889";


    protected static void registerCraftingRecipes() {
//        CustomItemBuilder mailboxBuilder = CustomItems.getCustomItemBuilder("LA_POSTE_MAILBOX");
//        if (mailboxBuilder == null) {
//            Bukkit.getLogger().info(LaPostePlugin.getPrefix() + " Mailboxrezept konnte nicht geladen werden.");
//            return;
//        }
//        mailboxBuilder.setAmount(1);
//        https://mcadmin.swisssmp.ch/test/player_head.png
//        ItemStack mailbox = mailboxBuilder.build();
        ItemStack mailbox = SkullCreator.itemWithUrl(new ItemStack(Material.PLAYER_HEAD), "https://mcadmin.swisssmp.ch/test/player_head.png");
        ItemUtil.setBoolean(mailbox, "la_poste_mailbox", true);
        ShapedRecipe mailboxRecipe = new ShapedRecipe(new NamespacedKey(LaPostePlugin.getInstance(), "la_poste_mailbox"), mailbox);
        mailboxRecipe.shape(
                "  w",
                "iii",
                "ici"
        );
        mailboxRecipe.setIngredient('w', new RecipeChoice.MaterialChoice(Material.GREEN_WOOL, Material.BLUE_WOOL, Material.RED_WOOL, Material.WHITE_WOOL));
        mailboxRecipe.setIngredient('i', Material.IRON_INGOT);
        mailboxRecipe.setIngredient('c', Material.ENDER_CHEST);
        Bukkit.getServer().addRecipe(mailboxRecipe);


        CustomItemBuilder packageBuilder = CustomItems.getCustomItemBuilder("LA_POSTE_PACKAGE");
        if (packageBuilder == null) {
            Bukkit.getLogger().info(LaPostePlugin.getPrefix() + " Paketrezept konnte nicht geladen werden");
            return;
        }
        packageBuilder.setAmount(1);
        ItemStack paket = packageBuilder.build();
        ItemUtil.setBoolean(paket, "la_poste_package", true);
        ItemUtil.setInt(paket, "weight", 0);
        ShapedRecipe packageRecipe = new ShapedRecipe(new NamespacedKey(LaPostePlugin.getInstance(), "la_post_package"), paket);
        packageRecipe.shape(
                " s ",
                "ppp",
                "pcp"
        );
        packageRecipe.setIngredient('s', Material.STRING);
        packageRecipe.setIngredient('p', Material.PAPER);
        packageRecipe.setIngredient('c', Material.CHEST);
        Bukkit.getServer().addRecipe(packageRecipe);


        CustomItemBuilder letterBuilder = CustomItems.getCustomItemBuilder("LA_POSTE_LETTER");
        if(letterBuilder == null){
            Bukkit.getLogger().info(LaPostePlugin.getPrefix() + " Briefrezept konnte nicht geladen werden");
            return;
        }
        letterBuilder.setAmount(1);
        ItemStack letter = letterBuilder.build();
        ItemUtil.setBoolean(letter, "la_poste_letter", true);
        ShapelessRecipe letterRecipe = new ShapelessRecipe(new NamespacedKey(LaPostePlugin.getInstance(), "la_poste_letter"), letter);
        letterRecipe.addIngredient(Material.INK_SAC);
        letterRecipe.addIngredient(Material.FEATHER);
        letterRecipe.addIngredient(Material.PAPER);
        Bukkit.getServer().addRecipe(letterRecipe);
    }

}
