package com.akeshala.compressionapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Decompression {

    List<String> file = new ArrayList<String>();
    final List<Integer> decodeList = new ArrayList<Integer>(Arrays.asList(35,6,16,12,12,12,17,7)){};
    List<String> separatedLines = new ArrayList<String>();
    List<String> dicList = new ArrayList<String>();
    List<String> decompressedList = new ArrayList<String>();

    public Decompression(List<String> file){
        this.file = file;
    }

    public void setSeparatedLines(){
        this.separatedLines = separateLines();
    }

    public void setDecompressedList(){
        this.decompressedList=decompress();
    }

    public List<String> getDecompressedList(){
        return decompressedList;
    }

    private List<String> separateLines(){

        List<String> separatedList = new ArrayList<String>();
        String next = "";
        for (int i=0;i<=file.size()-1;i++){
            if (file.get(i).equals("xxxx")) {
                this.dicList.addAll(file.subList(i+1,file.size()));
                break;
            }
            String line = next + file.get(i);
            for (int l=0;l<=line.length()-1;){

                //System.out.print(l);System.out.println("  l");
                if (l+3<line.length()){

                    int decodeInt = Integer.parseInt(line.substring(l,l+3),2);
                    int codeLength = decodeList.get(decodeInt);

                    if (decodeInt == 0) {
                        if (file.get(i+1).equals("xxxx")) break;
                    }

                    if (l+codeLength<line.length()){
                        separatedList.add(line.substring(l,l+codeLength));
                    }else{
                        next = line.substring(l,line.length());
                    }

                    l=l+codeLength;

                }else{
                    next = line.substring(l,line.length());
                    l=l+3;
                }
            }
        }
        return separatedList;
    }


    public List<String> decompress(){

        List<String> decompressedList = new ArrayList<String>();
        for (String code:separatedLines){

            String decodeBits = code.substring(0,3);
            if (decodeBits.equals("000")){
                decompressedList.add(code.substring(3,code.length()));

            }else if(decodeBits.equals("001")){
                int repeat = Integer.parseInt(code.substring(3,6),2);
                for (int i=0;i<=repeat;i++){
                    decompressedList.add(decompressedList.get(decompressedList.size()-1));
                }

            }else if(decodeBits.equals("010")){
                int location = Integer.parseInt(code.substring(3,8),2);
                int mask = Integer.parseInt(code.substring(8,12),2);
                int dicIndex = Integer.parseInt(code.substring(12,16),2);
                String k = Integer.toBinaryString(Integer.parseInt(dicList.get(dicIndex).substring(location,location+4),2)^mask);
                Integer minusLength = 4 - k.length();
                String addZero = new String(new char[minusLength]).replace("\0", "0");
                decompressedList.add(dicList.get(dicIndex).substring(0,location)+addZero+k+dicList.get(dicIndex).substring(location+4,32));

            }else if(decodeBits.equals("011")){
                int location = Integer.parseInt(code.substring(3,8),2);
                int dicIndex = Integer.parseInt(code.substring(8,12),2);
                String k = Integer.toBinaryString(~Integer.parseInt(dicList.get(dicIndex).substring(location,location+1),2)& 0b1);
                decompressedList.add(dicList.get(dicIndex).substring(0,location)+k+dicList.get(dicIndex).substring(location+1,32));

            }else if(decodeBits.equals("100")){
                int location = Integer.parseInt(code.substring(3,8),2);
                int dicIndex = Integer.parseInt(code.substring(8,12),2);
                String k = Integer.toBinaryString(~Integer.parseInt(dicList.get(dicIndex).substring(location,location+2),2)& 0b11);
                Integer minusLength = 2 - k.length();
                String addZero = new String(new char[minusLength]).replace("\0", "0");
                decompressedList.add(dicList.get(dicIndex).substring(0,location)+addZero+k+dicList.get(dicIndex).substring(location+2,32));

            }else if(decodeBits.equals("101")){
                int location = Integer.parseInt(code.substring(3,8),2);
                int dicIndex = Integer.parseInt(code.substring(8,12),2);
                String k = Integer.toBinaryString(~Integer.parseInt(dicList.get(dicIndex).substring(location,location+4),2)& 0b1111);
                Integer minusLength = 4 - k.length();
                String addZero = new String(new char[minusLength]).replace("\0", "0");
                decompressedList.add(dicList.get(dicIndex).substring(0,location)+addZero+k+dicList.get(dicIndex).substring(location+4,32));

            }else if(decodeBits.equals("110")){
                int location1 = Integer.parseInt(code.substring(3,8),2);
                int location2 = Integer.parseInt(code.substring(8,13),2);
                int dicIndex = Integer.parseInt(code.substring(13,17),2);
                String k1 = Integer.toBinaryString(~Integer.parseInt(dicList.get(dicIndex).substring(location1,location1+1),2)& 0b1);
                String k2 = Integer.toBinaryString(~Integer.parseInt(dicList.get(dicIndex).substring(location2,location2+1),2)& 0b1);
                decompressedList.add(dicList.get(dicIndex).substring(0,location1)+k1+dicList.get(dicIndex).substring(location1+1,location2)+k2+dicList.get(dicIndex).substring(location2+1,32));

            }else if(decodeBits.equals("111")){
                int dicIndex = Integer.parseInt(code.substring(3,7),2);
                decompressedList.add(dicList.get(dicIndex));

            }else{
                System.out.println("Invalid code");
            }

        }return decompressedList;
    }
}
