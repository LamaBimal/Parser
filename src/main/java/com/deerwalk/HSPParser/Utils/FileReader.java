package com.deerwalk.HSPParser.Utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

/**
 * Created by btamang on 5/22/18.
 */
public class FileReader {

    public Scanner scanner;
    public FileInputStream inputStream;
    public String filePath;

    public FileReader(String filePath){
        setFilePath(filePath);
    }

    public String getFilePath(){
        return this.filePath;
    }

    public void setFilePath(String filePath){
        this.filePath = filePath;
    }

    public static void readFile(String filePath) throws IOException{
        FileInputStream inputStream = null;
        Scanner sc = null;
        try {
            inputStream = new FileInputStream(filePath);
            sc = new Scanner(inputStream, "UTF-8");
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                System.out.println(line);
            }
            // note that Scanner suppresses exceptions
            if (sc.ioException() != null) {
                throw sc.ioException();
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (sc != null) {
                sc.close();
            }
        }
    }

    public  Scanner getScanner() {

        try {
             inputStream = new FileInputStream(getFilePath());
             scanner = new Scanner(inputStream,"UTF-8");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return scanner;
    }

    public void close() throws IOException{

        try {
            if (inputStream != null) {
                inputStream.close();
            }
            if (scanner != null) {
                scanner.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

}
