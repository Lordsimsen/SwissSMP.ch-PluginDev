package ch.swisssmp.shops;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

public class ShopUtil {
	protected static int countTradeBuyerSide(MerchantRecipe recipe, ItemStack[] payment){
		ItemStack price_1 = recipe.getIngredients().get(0);
		ItemStack price_2 = null;
		if(recipe.getIngredients().size()>1){
			price_2 = recipe.getIngredients().get(1);
		}
		int price_1_availability = 0;
		int price_2_availability = 0;
		for(int i = 0; i < 2; i++){
			if(payment[i]!=null){
				if(price_1!=null && price_1.isSimilar(payment[i])){
					price_1_availability+=payment[i].getAmount();
				}
				if(price_2!=null && price_2.isSimilar(payment[i])){
					price_2_availability+=payment[i].getAmount();
				}
			}
		}
		int price_1Count = (int)Math.floor(price_1_availability/(double)price_1.getAmount());
		int price_2Count = price_1Count;
		if(price_2!=null && price_2.getType()!=Material.AIR) price_2Count = (int)Math.floor(price_2_availability/(double)price_2.getAmount());
		return Math.min(price_1Count, price_2Count);
	}
    protected static <K, V extends Comparable<? super V>> Map<K, V> 
        sortByValue( Map<K, V> map )
    {
        List<Map.Entry<K, V>> list =
            new LinkedList<Map.Entry<K, V>>( map.entrySet() );
        Collections.sort( list, new Comparator<Map.Entry<K, V>>()
        {
            public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )
            {
                return (o1.getValue()).compareTo( o2.getValue() );
            }
        } );

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list)
        {
            result.put( entry.getKey(), entry.getValue() );
        }
        return result;
    }
}
