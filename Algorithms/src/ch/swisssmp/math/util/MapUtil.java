package ch.swisssmp.math.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MapUtil {
	
	/**
	 * Sortiert eine Map<S,T> nach ihren Werten
	 * @param unsortMap - Eine zu sortierende Map<S,T>
	 * @param order - <code>true</code> für absteigend; <code>false</code> für aufsteigend
	 * @return Eine neue Map<S,T>, in der die Einträge nach ihren Werten sortiert sind
	 */
    public static <S, T extends Comparable<T>> Map<S, T> sortByValue(Map<S, T> unsortMap, final boolean order)
    {

        List<Entry<S, T>> list = new LinkedList<Entry<S, T>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Entry<S, T>>()
        {
            public int compare(Entry<S, T> o1,
                    Entry<S, T> o2)
            {
                if (order)
                {
                    return o1.getValue().compareTo(o2.getValue());
                }
                else
                {
                    return o2.getValue().compareTo(o1.getValue());

                }
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<S, T> sortedMap = new LinkedHashMap<S, T>();
        for (Entry<S, T> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
}
