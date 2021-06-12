package com.akeshala.compressionapp;

import java.util.List;

public class Main {

    public static void main(String[] args) {

            if (args[0].equals("1") && args.length == 1){

                System.out.println("Input 1 - Compression");

                // Reading uncompressed file
                FileHandling fileHandling = new FileHandling();
                List<String> file = fileHandling.getFile("original.txt",false);

                // Finding the dictionary from the uncompressed file.
                Dictionary dictionary = new Dictionary(file);
                dictionary.setDictionary();

                // Preprocessing the file before compression
                List<String> filePreprocessed = fileHandling.getFile("original.txt",true);

                // Compressing file
                Compression compression = new Compression(filePreprocessed,dictionary);
                compression.compressText();

                // Write compressed file to cout.txt
                fileHandling.writeToFile("cout.txt",compression.getCompressedText());
            }
            else if (args[0].equals("2") && args.length == 1){
                System.out.println("Input 2 - Decompression");

                // Reading compressed file
                FileHandling fileHandling = new FileHandling();
                List<String> file = fileHandling.getFile("compressed.txt",false);

                // Decompression
                Decompression decompression = new Decompression(file);
                decompression.setSeparatedLines(); // Separation of lines
                decompression.setDecompressedList(); //Decompression line by line

                // Write decompressed code to a file.
                fileHandling.writeToFile("dout.txt",decompression.getDecompressedList());

            }
            else {
                System.out.println("Invalid Input !");
            }
    }
}
