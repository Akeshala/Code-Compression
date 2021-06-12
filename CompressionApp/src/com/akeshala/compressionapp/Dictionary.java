package com.akeshala.compressionapp;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Dictionary {

    private List<String> dictionaryList; //output
    private List<String> indexList; //output
    public List<String> list; //input

    //default constructor sets input value for the object.
    public Dictionary(List<String> list){
        this.list = list;
    }

    public void setDictionary(){
        this.dictionaryList = findDictionary();
        this.indexList = createIndexList(dictionaryList.size());
    }

    //Getters
    public List<String> getDictionaryList(){
        return dictionaryList;
    }

    public List<String> getIndexList(){
        return indexList;
    }

    public String getDictionaryEntry(String index){
        return dictionaryList.get(indexList.indexOf(index));
    }

    public String getDictionaryIndex(String entry){
        return indexList.get(dictionaryList.indexOf(entry));
    }

    private List<String> findDictionary(){
        List<String> dicList = new ArrayList<String>();
        List<String> distinctList = new ArrayList<String>();
        List<Integer> distinctCount = new ArrayList<Integer>();

        /* Find frequencies */
        Set<String> distinct = new HashSet<>(list);
        for (String s: distinct) {
            distinctCount.add(Collections.frequency(list, s));
            distinctList.add(s);
        }

        /* Sort Frequencies */
        distinctList = sortFromValue(distinctCount,distinctList,1);
        distinctCount = sortIntegers(distinctCount,1);

        /* Find priorities of same frequencies */
        for (int i=0; i<distinctList.size()-1;){
            List<String> sameCountList = new ArrayList<String>(); // Same frequency List
            sameCountList.add(distinctList.get(i));
            while (distinctCount.get(i).equals(distinctCount.get(i + 1))){
                i=i+1;
                sameCountList.add(distinctList.get(i));
                if (distinctList.size()-1==i) break;
            }
            if (sameCountList.size()>1){
                List<String> sortedSameCountList = new ArrayList<String>();
                sortedSameCountList = sortSameCount(list, sameCountList);
                dicList.addAll(sortedSameCountList);
                i=i+1;
            } else{
                dicList.add(distinctList.get(i));
                i=i+1;
            }
            if (i==distinctList.size()-1){
                dicList.add(distinctList.get(i));
            }
        }
        return adjustList(dicList);
    }

    /* Sort same frequencies using first appearance  */
    private List<String> sortSameCount(List<String> codeList, List<String> sameList){
        List<Integer> indexList = new ArrayList<Integer>();
        for (String same:sameList){
            for (int j=0;j<codeList.size();j++){
                if (codeList.get(j).equals(same)){
                    indexList.add(j);
                    break;
                }
            }
        }
        return sortFromValue(indexList,sameList, 0);
    }

    // Sort a String list using a integer list.
    private List<String> sortFromValue(List<Integer> indexList, List<String> valueList, Integer reverse){
        boolean sorted = false;
        int temp;
        String tempSame;
        while(!sorted) {
            sorted = true;
            for (int i = 0; i < indexList.size() - 1; i++) {
                if (indexList.get(i) > indexList.get(i+1)) {
                    temp = indexList.get(i);
                    tempSame = valueList.get(i);
                    indexList.set(i, indexList.get(i+1));
                    valueList.set(i, valueList.get(i+1));
                    indexList.set(i+1, temp);
                    valueList.set(i+1, tempSame);
                    sorted = false;
                }
            }
        }
        if (reverse != 0) {
            Collections.reverse(valueList);
        }
        return valueList;
    }

    // Sort a integer list
    private List<Integer> sortIntegers(List<Integer> indexList, Integer reverse){
        boolean sorted = false;
        int temp;
        String tempSame;
        while(!sorted) {
            sorted = true;
            for (int i = 0; i < indexList.size() - 1; i++) {
                if (indexList.get(i) > indexList.get(i+1)) {
                    temp = indexList.get(i);
                    indexList.set(i, indexList.get(i+1));
                    indexList.set(i+1, temp);
                    sorted = false;
                }
            }
        }
        if (reverse != 0) {
            Collections.reverse(indexList);
        }
        return indexList;
    }

    /* Set Length of the dictionary to 16 */
    private List<String> adjustList(List<String> dicList){
        if (dicList.size()>16){
            return dicList.subList(0,16);
        }
        return dicList;
    }

    // Create dictionary index list
    private List<String> createIndexList(int length){
        List<Integer> range = IntStream.rangeClosed(0, length-1).boxed().collect(Collectors.toList());
        List<String> binaryIndex = new ArrayList<>();
        for (int i :range){
            Integer minusLength = 4 - Integer.toBinaryString(i).length();
            String addZero = new String(new char[minusLength]).replace("\0", "0");
            binaryIndex.add(addZero+Integer.toBinaryString(i));
        }
        return binaryIndex;
    }
}
