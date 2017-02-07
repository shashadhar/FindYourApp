package com.shashadhardas.findandsearch.database;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by anutosh on 08/10/16.
 */
public class FileStorage {
    public static Context appContext;

    public static void writeToFile(String filename, String contents){
        try {
            FileOutputStream fos = appContext.openFileOutput(filename, Context.MODE_PRIVATE);
            fos.write(contents.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readFromFile(String filename){
        int ch;
        StringBuffer contentBuffer = new StringBuffer();
        try {
            FileInputStream fis = appContext.openFileInput(filename);
            while((ch = fis.read()) != -1){
                contentBuffer.append((char)ch);
            }
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contentBuffer.toString();
    }
}
