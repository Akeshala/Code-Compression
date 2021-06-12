package com.akeshala.compressionapp;

import java.util.ArrayList;
import java.util.List;

public class Compression {

    private Dictionary dictionary;
    private List<String> lines;
    private List<String> compressedText;
    private List<String> compressedList; //only for testing

    public Compression(List<String> lines, Dictionary dictionary){
        this.lines = lines;
        this.dictionary = dictionary;
    }

    public void compressText(){
        this.compressedList = compress();
        this.compressedText = combineElements();
        this.compressedText.add("xxxx");
        this.compressedText.addAll(dictionary.getDictionaryList());
    }

    public List<String> getCompressedText(){
        return compressedText;
    }

    public List<String> combineElements(){
         List<String> list32 = new ArrayList<String>();
         String line32 = "";
         for (String line:compressedList){
             line32 = line32 + line;
             if (line32.length()>=32){
                 list32.add(line32.substring(0,32));
                 line32 =line32.substring(32,line32.length());
                 if(line32.length()>=32){
                     list32.add(line32.substring(0,32));
                     line32 =line32.substring(32,line32.length());
                 }
             }
         }
         if (line32.length()>0){
             Integer minusLength = 32 - line32.length();
             String addZero = new String(new char[minusLength]).replace("\0", "0");
             line32 = line32 + addZero;
             list32.add(line32);
         }
         return list32;
    }

    private List<String> compress(){
        List<String> compressedList = new ArrayList<String>();
        for (String row :lines){

            if (isRLE(row)){
                List<String> rleList = rle(row);
                compressedList.addAll(rleList);

            }else if (isDirectMap(row)){
                compressedList.add(directMap(row));

            }else if (isOneMismatch(row)){
                compressedList.add(oneMismatch(row));

            }else if (isTwoMismatch(row)){

                compressedList.add(twoMismatch(row));

            }else if (isFourMismatch(row)) {
                compressedList.add(fourMismatch(row));

            }else if (isBitMask(row)){
                compressedList.add(bitMask(row));

            }else if (isTwoAnyMismatch(row)){
                compressedList.add(twoAnyMismatch(row));

            }else{
                compressedList.add("000"+row);
                // Original binary
            }
        }
        return compressedList;
    }

    private boolean isRLE(String row){
        return (row.length()>32);
    }

    private List<String> rle(String row){
        List<String> rleList = new ArrayList<String>();
        String line = row.substring(0,32);
        int repeat = Integer.parseInt(row.substring(33,row.length()));

        if (isDirectMap(line)){
            //System.out.println("Direct");
            rleList.add(directMap(line));
        }else if (isOneMismatch(line)){
            //System.out.println("One");
            rleList.add(oneMismatch(line));
        }else if (isTwoMismatch(line)){
            //System.out.println("Two");
            rleList.add(twoMismatch(line));
        }else if (isFourMismatch(line)) {
            //System.out.println("Four");
            rleList.add(fourMismatch(line));
        }else if (isBitMask(line)){
            //System.out.println("Mask");
            rleList.add(bitMask(line));
        }else if (isTwoAnyMismatch(line)){
            //System.out.println("TwoAny");
            rleList.add(twoAnyMismatch(line));
        }else{
            rleList.add("000"+line);
        }

        int noOfNines = repeat/9;
        int remainder = repeat%9;

        for (int i=0; i<noOfNines ;i++){
            Integer minusLength = 3 - Integer.toBinaryString(7).length();
            String addZero = new String(new char[minusLength]).replace("\0", "0");
            rleList.add("001"+addZero+Integer.toBinaryString(7));
            if (noOfNines-i-1>0 | remainder>0){
                rleList.add(rleList.get(0));
            }
        }

        if (remainder>1) {
            Integer minusLength = 3 - Integer.toBinaryString(remainder - 1).length(); //check -1
            String addZero = new String(new char[minusLength]).replace("\0", "0");
            rleList.add("001" + addZero + Integer.toBinaryString(remainder - 2));
        }
        return rleList;
    }

    private boolean isDirectMap(String row){
        return dictionary.getDictionaryList().contains(row);
    }
    private String directMap(String row){
        return "111" + dictionary.getDictionaryIndex(row);
    }

    private boolean isOneMismatch(String row){

        for (String l: dictionary.getDictionaryList()){

            Long dicInteger = Long.parseLong(l,2);
            Long rowInteger = Long.parseLong(row,2);
            String changes = Long.toBinaryString(dicInteger ^ rowInteger);
            if (numOfOnes(changes)==1){
                return true;
            }
        }
        return false;
    }
    private String oneMismatch(String row){

        for (int i=0; i<dictionary.getDictionaryList().size();i++){
            Long dicInteger = Long.parseLong(dictionary.getDictionaryList().get(i),2);
            Long rowInteger = Long.parseLong(row,2);
            String changes = Long.toBinaryString(dicInteger ^ rowInteger);

            if (numOfOnes(changes)==1) {
                int changeIndex = 32 - changes.length();
                String location = Integer.toBinaryString(changeIndex);
                Integer minusLength = 5 - location.length();
                String addZero = new String(new char[minusLength]).replace("\0", "0");
                return "011" + addZero + location + dictionary.getIndexList().get(i);
            }
        }return "";
    }


    private boolean isTwoMismatch(String row){
        for (String l: dictionary.getDictionaryList()){
            Long dicInteger = Long.parseLong(l,2);
            Long rowInteger = Long.parseLong(row,2);
            //System.out.println(dicInteger);System.out.println(rowInteger);
            String changes = Long.toBinaryString(dicInteger ^ rowInteger);
            //System.out.println(changes);
            if (numOfOnes(changes)==2){
                if (placeOfChange(changes).get(1)-placeOfChange(changes).get(0)==1){
                    return true;
                }
            }
        }
        return false;
    }
    private String twoMismatch(String row){
        System.out.println("Two");
        System.out.println(row);
        for (int i=0; i<dictionary.getDictionaryList().size();i++){
            Long dicInteger = Long.parseLong(dictionary.getDictionaryList().get(i),2);
            Long rowInteger = Long.parseLong(row,2);
            String changes = Long.toBinaryString(dicInteger ^ rowInteger);
            System.out.println(changes);
            if (numOfOnes(changes)==2) {
                if (placeOfChange(changes).get(1)-placeOfChange(changes).get(0)==1) {
                    int changeIndex = 32 - changes.length();
                    String location = Integer.toBinaryString(changeIndex);
                    Integer minusLength = 5 - location.length();
                    String addZero = new String(new char[minusLength]).replace("\0", "0");
                    return "100" + addZero + location + dictionary.getIndexList().get(i);
                }
            }
        }return "";
    }

    private boolean isFourMismatch(String row){
        for (String l: dictionary.getDictionaryList()){
            Long dicInteger = Long.parseLong(l,2);
            Long rowInteger = Long.parseLong(row,2);
            String changes = Long.toBinaryString(dicInteger ^ rowInteger);
            if (numOfOnes(changes)==4){
                if (placeOfChange(changes).get(3)-placeOfChange(changes).get(0)==3){
                    return true;
                }
            }
        }
        return false;
    }
    private String fourMismatch(String row){
        for (int i=0; i<dictionary.getDictionaryList().size();i++){
            Long dicInteger = Long.parseLong(dictionary.getDictionaryList().get(i),2);
            Long rowInteger = Long.parseLong(row,2);
            String changes = Long.toBinaryString(dicInteger ^ rowInteger);

            if (numOfOnes(changes)==4) {
                if (placeOfChange(changes).get(3)-placeOfChange(changes).get(0)==3) {
                    int changeIndex = 32 - changes.length();
                    String location = Integer.toBinaryString(changeIndex);
                    Integer minusLength = 5 - location.length();
                    String addZero = new String(new char[minusLength]).replace("\0", "0");
                    return "101" + addZero + location + dictionary.getIndexList().get(i);
                }
            }
        }return "";
    }

    private boolean isTwoAnyMismatch(String row){
        for (String l: dictionary.getDictionaryList()){
            Long dicInteger = Long.parseLong(l,2);
            Long rowInteger = Long.parseLong(row,2);
            String changes = Long.toBinaryString(dicInteger ^ rowInteger);
            if (numOfOnes(changes)==2){
                return true;
            }
        }
        return false;
    }
    private String twoAnyMismatch(String row){
        for (int i=0; i<dictionary.getDictionaryList().size();i++){
            Long dicInteger = Long.parseLong(dictionary.getDictionaryList().get(i),2);
            Long rowInteger = Long.parseLong(row,2);
            String changes = Long.toBinaryString(dicInteger ^ rowInteger);

            if (numOfOnes(changes)==2) {
                int changeIndex1 = 32 - changes.length();
                String location1 = Integer.toBinaryString(changeIndex1);
                Integer minusLength1 = 5 - location1.length();
                String addZero1 = new String(new char[minusLength1]).replace("\0", "0");
                int changeIndex2 = 32 - (changes.length() - placeOfChange(changes).get(1));
                String location2 = Integer.toBinaryString(changeIndex2);
                Integer minusLength2 = 5 - location2.length();
                String addZero2 = new String(new char[minusLength2]).replace("\0", "0");
                return "110" + addZero1 + location1 + addZero2 + location2 + dictionary.getIndexList().get(i);
            }
        }return "";
    }

    private boolean isBitMask(String row){
        for (String l: dictionary.getDictionaryList()){
            Long dicInteger = Long.parseLong(l,2);
            Long rowInteger = Long.parseLong(row,2);
            String changes = Long.toBinaryString(dicInteger ^ rowInteger);
            if (numOfOnes(changes)==3){
                if (placeOfChange(changes).get(2)-placeOfChange(changes).get(0)==3 | placeOfChange(changes).get(2)-placeOfChange(changes).get(0)==2) {
                    return true;
                }
            }else if(numOfOnes(changes)==2){
                if (placeOfChange(changes).get(1)-placeOfChange(changes).get(0)==3 | placeOfChange(changes).get(1)-placeOfChange(changes).get(0)==2){
                    return true;
                }
            }
        }
        return false;
    }
    private String bitMask(String row){
        for (int i=0; i<dictionary.getDictionaryList().size();i++){
            Long dicInteger = Long.parseLong(dictionary.getDictionaryList().get(i),2);
            Long rowInteger = Long.parseLong(row,2);
            String changes = Long.toBinaryString(dicInteger ^ rowInteger);

            if (numOfOnes(changes)==3) {
                if (placeOfChange(changes).get(2)-placeOfChange(changes).get(0)==3 | placeOfChange(changes).get(2)-placeOfChange(changes).get(0)==2) {
                    int changeIndex = 32 - changes.length();
                    String location = Integer.toBinaryString(changeIndex);
                    Integer minusLength = 5 - location.length();
                    String addZero = new String(new char[minusLength]).replace("\0", "0");

                    return "010" + addZero + location + changes.substring(0,4) + dictionary.getIndexList().get(i);
                }
            }
            else if (numOfOnes(changes)==2){
                if (placeOfChange(changes).get(1)-placeOfChange(changes).get(0)==3 | placeOfChange(changes).get(1)-placeOfChange(changes).get(0)==2){
                    int changeIndex = 32 - changes.length();
                    String location = Integer.toBinaryString(changeIndex);
                    Integer minusLength = 5 - location.length();
                    String addZero = new String(new char[minusLength]).replace("\0", "0");

                    return "010" + addZero + location + changes.substring(0,4) + dictionary.getIndexList().get(i);
                }
            }
        }return "";
    }

    private int numOfOnes(String binary){
        char[] binaryArray = binary.toCharArray();
        int count =0;
        for (char ch:binaryArray){
            if (String.valueOf(ch).equals("1")){
                ++count;
            }
        }
        return count;
    }

    private List<Integer> placeOfChange(String binary){
        char[] binaryArray = binary.toCharArray();
        List<Integer> places = new ArrayList<Integer>();
        for (int i=0;i<binaryArray.length;i++){
            if (String.valueOf(binaryArray[i]).equals("1")){
                places.add(i);
            }
        }
        return places;
    }
}
