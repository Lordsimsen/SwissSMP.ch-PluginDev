package ch.swisssmp.weaver;

import ch.swisssmp.city.City;
import ch.swisssmp.utils.ItemUtil;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;
import net.minecraft.server.v1_16_R1.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import java.util.List;

public class CityBanner {

    public static boolean isBanner(ItemStack banner, Player player){
        //TODO check, probably via WebCore, whether given item is a registered banner of one of the player's cities
        //TODO must get the list of patterns, or the sample citybanner containng that meta.

        HTTPRequest request = DataSource.getResponse(WeaverPlugin.getInstance(), "/get_citybanner.php");
        request.onFinish(()->{
            //same old bullshit. How to return true for isBanner ? Or to tell the function that the banners match or not?
        });

        if(!banner.getType().equals(Material.BANNER)) return false;
        BannerMeta bannerMeta = (BannerMeta) banner.getItemMeta();
        List<Pattern> bannerPatterns = bannerMeta.getPatterns();

        // if bannerPatterns and the stored one's patterns are equal, return true

        return true;
    }

    protected static void registerBanner(ItemStack banner, City city){
        String serializedBanner = ItemUtil.serialize(banner);

        HTTPRequest request = DataSource.getResponse(WeaverPlugin.getInstance(), "/register_banner.php", new String[]{
                "city=" + city.getUniqueId(),
                "banner=" + serializedBanner
        });
    }
}
