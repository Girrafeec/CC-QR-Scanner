package com.girrafeecstud.ccqrscanner;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class HistoryFileInputOutput {

    private static Context context;

    private static final String fileName = "scannedHistory";

    public HistoryFileInputOutput(Context context){
        this.context = context;
    }

    public static void createHistoryFile(){

        if (!fileExists()) {

            FileOutputStream fos = null;

            try {
                fos = context.openFileOutput(fileName, MODE_PRIVATE);
                //Toast.makeText(context,
                  //      "Created file  " + context.getFilesDir() + "/" + fileName, Toast.LENGTH_LONG).show();
                //Log.i("here", context.getFilesDir() + "/" + fileName);
            } catch (FileNotFoundException exception) {
                exception.printStackTrace();
            } catch (IOException exception) {
                exception.printStackTrace();
            } finally {
                try {
                    fos.close();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    public void writeInvalidQrToFile(int qrCodeType, String content, String currentTime){

        final File file = context.getFileStreamPath(fileName);

        String history = readFile();

        String str = "[" + qrCodeType + "]" + "\t" + "[" + content + "]" + "\t" + "[" + currentTime + "]";

        System.out.println("invalid str" + str);

        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
        }
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));

        try {
            bufferedWriter.write(history + str + "\r\n\n");
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        try {
            bufferedWriter.flush();
            fileOutputStream.flush();
            bufferedWriter.close();
            fileOutputStream.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }

    public void writeValidQrToFile(int qrCodeType, boolean certificateReuse,String type, String title, String status,
                                   String certificateId, String expiredAt, String validFrom, String isBeforeValidFrom, String fio, String enFio, String recoveryDate,
                                   String passport, String enPassport, String birthDate, String currentTime){

        final File file = context.getFileStreamPath(fileName);

        String history = readFile();

        String str = "[" + qrCodeType + "]" + "\t" + "[" + certificateReuse + "]" + "\t" + "[" + type + "]" + "\t" + "[" + title + "]" + "\t"
                + "[" + status + "]" + "\t" + "[" + certificateId + "]" + "\t" + "[" + expiredAt + "]" + "\t" + "[" + validFrom + "]" + "\t"
                + "[" + isBeforeValidFrom + "]" + "\t" + "[" + fio + "]" + "\t" + "[" + enFio + "]" + "\t" + "[" + recoveryDate + "]" + "\t"
                + "[" + passport + "]" + "\t" + "[" + enPassport + "]" + "\t" + "[" + birthDate + "]" + "\t" + "[" + currentTime + "]";

        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
        }
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));

        try {
            bufferedWriter.write(history + str + "\r\n\n");
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        try {
            bufferedWriter.flush();
            fileOutputStream.flush();
            bufferedWriter.close();
            fileOutputStream.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }

    // function to read current strings from file
    public String readFile(){

        FileInputStream fis = null;

        String history = "";

        try{
            fis = context.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;

            while ((text = br.readLine()) != null){
                sb.append(text).append("\n");
            }

            //Toast.makeText(context, sb.toString(), Toast.LENGTH_SHORT).show();
            //Log.i("history", history);

            history = sb.toString();

        }catch (FileNotFoundException exception){
            exception.printStackTrace();
        } catch (IOException exception) {
            exception.printStackTrace();
        }finally {
            if (fis != null){
                try {
                    fis.close();
                }catch (IOException exception){
                    exception.printStackTrace();
                }
            }
        }

        return history;
    }

    // procedure clears file with qr history
    public void clearFile(){
        final File file = context.getFileStreamPath(fileName);

        String history = readFile();

        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
        }
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));

        try {
            bufferedWriter.write("");
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        try {
            bufferedWriter.flush();
            fileOutputStream.flush();
            bufferedWriter.close();
            fileOutputStream.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    // function to check if the file exists
    private static boolean fileExists(){
        File file = context.getFileStreamPath(fileName);
        if (file == null || !file.exists()){
            return false;
        }
        return true;
    }

}
