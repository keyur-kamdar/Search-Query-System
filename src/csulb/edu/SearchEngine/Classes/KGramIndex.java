package csulb.edu.SearchEngine.Classes;

import java.util.ArrayList;
import java.util.List;

public class KGramIndex {
    SearchWord searchWord;
    private final ArrayList < String > stringArray = new ArrayList < > ();
	
    public ArrayList < String > GenKGramIndex(String wildcardQuery) {

        for (int i = 0;
            (i = wildcardQuery.indexOf("*", i)) != -1;) {
            String temp = wildcardQuery.substring(0, i);
            wildcardQuery = wildcardQuery.replaceFirst(temp, "");
            stringArray.add(temp);
            wildcardQuery = wildcardQuery.substring(1);
            i = 0;
        }
        stringArray.add(wildcardQuery);
        return stringArray;
    }

    public List < Integer > searchForQuery(ArrayList < String > wildcardArray) {
        String beginningString = "";
        String endString = "";
        List < String > Words = new ArrayList < String > ();
        List < Integer > Files = new ArrayList < Integer > ();
        List < Integer > UniqueFiles = new ArrayList < Integer > ();
        String regEx;

        for (int i = 0; i < wildcardArray.size(); i++) {
            if (!(wildcardArray.get(0).isEmpty())) {
                beginningString = wildcardArray.get(0);
            }
            wildcardArray.remove(0);
            if (wildcardArray.size() == 0) {
                return UniqueFiles;
            }
            if (!(wildcardArray.get(wildcardArray.size() - 1).isEmpty())) {
                endString = wildcardArray.get(wildcardArray.size() - 1);
            }
            wildcardArray.remove(wildcardArray.size() - 1);
        }

        for (String key: PositionalInvertedIndex.mIndex.keySet()) {
            Files = new ArrayList < Integer > ();
            if (key.startsWith(beginningString) && key.endsWith(endString)) {
                if (wildcardArray.size() == 0) {
                    regEx = generateRegEx(beginningString, endString);
                    if (key.matches(regEx)) {

                        Words.add(key);
                        for (Integer doc: PositionalInvertedIndex.mIndex.get(key).keySet()) {
                            Files.add(doc);
                        }
                        UniqueFiles.removeAll(Files);
                        UniqueFiles.addAll(Files);
                    }

                } else {
                    for (String wildCard: wildcardArray) {
                        if (key.contains(wildCard)) {
                            if (!(Words.contains(key))) {
                                Words.add(key);
                                for (Integer doc: PositionalInvertedIndex.mIndex.get(key).keySet()) {
                                    Files.add(doc);
                                }
                                UniqueFiles.removeAll(Files);
                                UniqueFiles.addAll(Files);
                            }
                        } else {
                            if (Words.contains(key)) {
                                Words.remove(key);
                            }
                        }
                    }
                    regEx = generateRegex(beginningString, endString, wildcardArray);
                }
            }
        }
        return UniqueFiles;
    }

    private String generateRegex(String beginningString, String endString, List < String > words) {
        String regularEx = "";
        String word = words.get(0);
        if (beginningString == "" && !(endString == "")) {
            if (words.size() > 1) {
                regularEx = "^(.*)" + word + "(.*)" + generateInnerRegex(words) + "(.*)" + endString + "$";
            } else {
                regularEx = "^(.*)" + word + "(.*)" + endString + "$";
            }

        } else if (endString == "" && !(beginningString == "")) {
            if (words.size() > 1) {
                regularEx = "^" + beginningString + "(.*)" + word + "(.*)" + generateInnerRegex(words) + "(.*)$";
            } else {
                regularEx = "^" + beginningString + "(.*)" + word + "(.*)$";
            }

        } else if (beginningString == "" && endString == "") {
            if (words.size() > 1) {
                regularEx = "^(.*)" + word + "(.*)" + generateInnerRegex(words) + "(.*)$";
            } else {
                regularEx = "^(.*)" + word + "(.*)$";
            }
        }
        return regularEx;
    }

    private String generateInnerRegex(List < String > words) {
        String innerRegEx = "";
        String word = words.remove(0);
        innerRegEx = "(?:(\\s+)" + word + "(.*)" + generateInnerRegex(words) + ")?";
        return innerRegEx;
    }

    private String generateRegEx(String beginningString, String endString) {
        String regularEx = "";
        if (beginningString == "") {
            regularEx = "^(.*)" + endString + "$";
        }
        if (endString == "") {
            regularEx = "^" + beginningString + "(.*)$";
        }
        if (!(beginningString == "" && endString == "")) {
            regularEx = "^" + beginningString + "(.*)" + endString + "$";
        }
        return regularEx;
    }
}
