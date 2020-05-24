package ch.swisssmp.laposte;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.utils.SkullCreator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

public class CraftingRecipes {

    protected static final String mailboxTextureBlue = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjZhNDllZmFhYWI1MzI1NTlmZmY5YWY3NWRhNmFjNGRkNzlkMTk5ZGNmMmZkNDk3Yzg1NDM4MDM4NTY0In19fQ==";
    protected static final String mailboxTextureWhite = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTM5ZTE5NzFjYmMzYzZmZWFhYjlkMWY4NWZjOWQ5YmYwODY3NjgzZjQxMjk1NWI5NjExMTdmZTY2ZTIifX19";
    protected static final String mailboxTextureRed = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGZhODljZTg1OTMyYmVjMWExYzNmMzFjYjdjMDg1YTViZmIyYWM3ZTQwNDA5NDIwOGMzYWQxMjM4NzlkYTZkYSJ9fX0=";
    protected static final String mailboxTextureGreen = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzJiY2NiNTI0MDg4NWNhNjRlNDI0YTBjMTY4YTc4YzY3NmI4Yzg0N2QxODdmNmZiZjYwMjdhMWZlODZlZSJ9fX0=";

    protected static Recipe paketRezept;
    protected static Recipe briefRezept;
    protected static Recipe blueBoxRecipe;
    protected static Recipe whiteBoxRecipe;
    protected static Recipe greenBoxRecipe;
    protected static Recipe redBoxRecipe;

    protected static void registerCraftingRecipes() {

        /*
        Blue Mailbox
         */
        ItemStack blueMailbox = SkullCreator.itemWithBase64(new ItemStack(Material.PLAYER_HEAD), mailboxTextureBlue);
        ItemMeta blueMailboxMeta = blueMailbox.getItemMeta();
        blueMailboxMeta.setDisplayName("LaPoste Briefkasten");
        blueMailbox.setItemMeta(blueMailboxMeta);
        ItemUtil.setBoolean(blueMailbox, "la_poste_mailbox", true);
        ShapedRecipe blueMailboxRecipe = new ShapedRecipe(new NamespacedKey(LaPostePlugin.getInstance(), "la_poste_mailbox_blue"), blueMailbox);
        blueMailboxRecipe.shape(
                "  w",
                "iii",
                "ici"
        );
        blueMailboxRecipe.setIngredient('w', Material.BLUE_WOOL);
        blueMailboxRecipe.setIngredient('i', Material.IRON_INGOT);
        blueMailboxRecipe.setIngredient('c', Material.ENDER_CHEST);
        blueBoxRecipe = blueMailboxRecipe;
        Bukkit.getServer().addRecipe(blueMailboxRecipe);

        /*
        Green Mailbox
         */
        ItemStack greenMailbox = SkullCreator.itemWithBase64(new ItemStack(Material.PLAYER_HEAD), mailboxTextureGreen);
        ItemMeta greenMailboxMeta = greenMailbox.getItemMeta();
        greenMailboxMeta.setDisplayName("LaPoste Briefkasten");
        greenMailbox.setItemMeta(greenMailboxMeta);
        ItemUtil.setBoolean(greenMailbox, "la_poste_mailbox", true);
        ShapedRecipe greenMailboxRecipe = new ShapedRecipe(new NamespacedKey(LaPostePlugin.getInstance(), "la_poste_mailbox_green"), greenMailbox);
        greenMailboxRecipe.shape(
                "  w",
                "iii",
                "ici"
        );
        greenMailboxRecipe.setIngredient('w', Material.GREEN_WOOL);
        greenMailboxRecipe.setIngredient('i', Material.IRON_INGOT);
        greenMailboxRecipe.setIngredient('c', Material.ENDER_CHEST);
        greenBoxRecipe = greenMailboxRecipe;
        Bukkit.getServer().addRecipe(greenMailboxRecipe);

        /*
        White Mailbox
         */
        ItemStack whiteMailbox = SkullCreator.itemWithBase64(new ItemStack(Material.PLAYER_HEAD), mailboxTextureWhite);
        ItemMeta whiteMailboxMeta = whiteMailbox.getItemMeta();
        whiteMailboxMeta.setDisplayName("LaPoste Briefkasten");
        whiteMailbox.setItemMeta(whiteMailboxMeta);
        ItemUtil.setBoolean(whiteMailbox, "la_poste_mailbox", true);
        ShapedRecipe whiteMailboxRecipe = new ShapedRecipe(new NamespacedKey(LaPostePlugin.getInstance(), "la_poste_mailbox_white"), whiteMailbox);
        whiteMailboxRecipe.shape(
                "  w",
                "iii",
                "ici"
        );
        whiteMailboxRecipe.setIngredient('w', Material.WHITE_WOOL);
        whiteMailboxRecipe.setIngredient('i', Material.IRON_INGOT);
        whiteMailboxRecipe.setIngredient('c', Material.ENDER_CHEST);
        whiteBoxRecipe = whiteMailboxRecipe;
        Bukkit.getServer().addRecipe(whiteMailboxRecipe);

        /*
        Red Mailbox
         */
        ItemStack redMailbox = SkullCreator.itemWithBase64(new ItemStack(Material.PLAYER_HEAD), mailboxTextureRed);
        ItemMeta redMailboxMeta = redMailbox.getItemMeta();
        redMailboxMeta.setDisplayName("LaPoste Briefkasten");
        redMailbox.setItemMeta(redMailboxMeta);
        ItemUtil.setBoolean(redMailbox, "la_poste_mailbox", true);
        ShapedRecipe redMailboxRecipe = new ShapedRecipe(new NamespacedKey(LaPostePlugin.getInstance(), "la_poste_mailbox_red"), redMailbox);
        redMailboxRecipe.shape(
                "  w",
                "iii",
                "ici"
        );
        redMailboxRecipe.setIngredient('w', Material.RED_WOOL);
        redMailboxRecipe.setIngredient('i', Material.IRON_INGOT);
        redMailboxRecipe.setIngredient('c', Material.ENDER_CHEST);
        redBoxRecipe = redMailboxRecipe;
        Bukkit.getServer().addRecipe(redMailboxRecipe);


        CustomItemBuilder packageBuilder = CustomItems.getCustomItemBuilder("LA_POSTE_PACKAGE");
        if (packageBuilder == null) {
            Bukkit.getLogger().info(LaPostePlugin.getPrefix() + " Paketrezept konnte nicht geladen werden");
            return;
        }
        packageBuilder.setAmount(1);
        ItemStack paket = packageBuilder.build();
        paket.setAmount(1);
        ItemMeta paketMeta = paket.getItemMeta();
        paketMeta.setDisplayName(ChatColor.YELLOW + "LaPoste Paket");
        paket.setItemMeta(paketMeta);
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
        paketRezept = packageRecipe;
        Bukkit.getServer().addRecipe(packageRecipe);


        CustomItemBuilder letterBuilder = CustomItems.getCustomItemBuilder("LA_POSTE_LETTER");
        if(letterBuilder == null){
            Bukkit.getLogger().info(LaPostePlugin.getPrefix() + " Briefrezept konnte nicht geladen werden");
            return;
        }
        letterBuilder.setAmount(1);
        ItemStack letter = letterBuilder.build();
        letter.setAmount(1);
        ItemMeta letterMeta = letter.getItemMeta();
        letterMeta.setDisplayName(ChatColor.YELLOW + "LaPoste Brief");
        letter.setItemMeta(letterMeta);
        ItemUtil.setBoolean(letter, "la_poste_letter", true);
        ShapelessRecipe letterRecipe = new ShapelessRecipe(new NamespacedKey(LaPostePlugin.getInstance(), "la_poste_letter"), letter);
        letterRecipe.addIngredient(Material.INK_SAC);
        letterRecipe.addIngredient(Material.FEATHER);
        letterRecipe.addIngredient(Material.PAPER);
        briefRezept = letterRecipe;
        Bukkit.getServer().addRecipe(letterRecipe);
    }
}
