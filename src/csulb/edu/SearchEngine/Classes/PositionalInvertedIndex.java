package csulb.edu.SearchEngine.Classes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class PositionalInvertedIndex {

    public static LinkedHashMap < String, LinkedHashMap < Integer, List < Integer >>> mIndex = new LinkedHashMap < String, LinkedHashMap < Integer, List < Integer >>> ();
    PositionalInvertedIndex() {}
	
    public static void addTerm(String term, int documentID, int Position) {
        term = PorterStemmer.processToken(term);
        if (mIndex.containsKey(term)) {
            LinkedHashMap < Integer, List < Integer >> lnkHmptDp = mIndex.get(term);
            if (lnkHmptDp.containsKey(documentID)) {
                lnkHmptDp.get(documentID).add(Position);
            } else {
                List < Integer > lst = new ArrayList < Integer > ();
                lst.add(Position);
                mIndex.get(term).put(documentID, lst);
            }
        } else {
            List < Integer > lst = new ArrayList < Integer > ();
            lst.add(Position);
            LinkedHashMap < Integer, List < Integer >> lnk = new LinkedHashMap < Integer, List < Integer >> ();
            lnk.put(documentID, lst);
            mIndex.put(term, lnk);
        }
    }

    public String[] getDictionary() {
        // TO-DO: fill an array of Strings with all the keys from the hashtable.
        // Sort the array and return it.

        String[] str = mIndex.keySet().toArray(new String[0]);
        Arrays.sort(str);
        return str;
    }

    public LinkedHashMap < Integer, List < Integer >> getPostings(String term) {
        // TO-DO: return the postings list for the given term from the index
        // map.
        if (mIndex.containsKey(term)) {
            return mIndex.get(term);
        } else {
            return null;
        }
    }
}
