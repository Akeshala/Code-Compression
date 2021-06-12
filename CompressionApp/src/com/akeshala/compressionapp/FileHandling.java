package com.akeshala.compressionapp;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileHandling {

    public List<String> getFile(String fileName, Boolean preprocess){
        List<String> lines = new ArrayList<String>();
        try{
            lines = Files.readAllLines(Paths.get("resources/"+fileName), StandardCharsets.UTF_8);
        }
        catch (IOException e) {
            System.out.println("File does not exist");
            e.printStackTrace();
        }
        if (preprocess){
            return filePreprocess(lines);
        }
        return lines;
    }

    public void writeToFile(String fileName, List<String> text) {
        try{
            File file = new File("resources/"+fileName);
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getName());
            } else {
                System.out.println("File already exists.");
            }
            FileOutputStream fos = new FileOutputStream(file);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

            for (String line:text){
                bw.write(line);
                bw.newLine();
            }
            bw.close();
            System.out.println("Code has been written successfully to the file.");

        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    // If same line  appears multiple times in a code. Eg. six consecutive 'A's -> A_6
    private List<String> filePreprocess(List<String> list){
        int count = 0;
        String last = list.get(0);
        List<String> preprocessedList = new ArrayList<String>();
        for (int i=1;i<list.size();i++){
            if (list.get(i).equals(last)){
                count=count+1;
            }
            else{
                if (count>0){
                    preprocessedList.add(list.get(i-1)+"_"+Integer.toString(count+1));
                }
                else{
                    preprocessedList.add(list.get(i-1));
                }
                count=0;
            }
            if (i==list.size()-1 && count>0){
                preprocessedList.add(list.get(i)+"_"+Integer.toString(count+1));
            }else if (i==list.size()-1 && count==0){
                preprocessedList.add(list.get(i));
            }
            last=list.get(i);
        }
        return preprocessedList;
    }
}
