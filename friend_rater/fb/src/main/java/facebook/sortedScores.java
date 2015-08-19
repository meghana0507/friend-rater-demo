package facebook;

/**
 * Created by Meghana on 5/1/2015.
 */

import java.util.*;

public class sortedScores {

    HashMap<String, Long> hmap = new HashMap<String, Long>();

    public sortedScores(HashMap<String, Long> map){
        this.hmap = map;
    }

    public List sortByValues(HashMap map) {
        List list = new LinkedList(map.entrySet());
        // Defined Custom Comparator here
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o2)).getValue())
                        .compareTo(((Map.Entry) (o1)).getValue());
            }
        });

        return list.subList(0,5);
    }
}
