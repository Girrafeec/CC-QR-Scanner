package com.girrafeecstud.ccqrscanner;

import android.util.Log;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;

public class HistoryFileParser {

    private static ArrayList<QuickResponseCodeHistoryItem> quickResponseCodeHistoryItemArrayList = new ArrayList<>();

    public ArrayList<QuickResponseCodeHistoryItem> getQuickResponseCodeHistoryItemArrayList() {
        return quickResponseCodeHistoryItemArrayList;
    }

    public void setQuickResponseCodeHistoryItemArrayList(ArrayList<QuickResponseCodeHistoryItem> quickResponseCodeHistoryItemArrayList) {
        this.quickResponseCodeHistoryItemArrayList = quickResponseCodeHistoryItemArrayList;
    }

    // procedure to parse string with scan history and convert it to array list with objects
    public void convertHistoryToArrayList(String history){

        ArrayList<String> historyStrings = new ArrayList<>(Arrays.asList(history.split("\n\n")));

        //Log.i("ar size", String.valueOf(historyStrings.size()));
        for (int i=0; i<historyStrings.size();i++)
          System.out.println(i + "  " + historyStrings.get(i));

        quickResponseCodeHistoryItemArrayList.clear();

        for (int i=0; i < historyStrings.size(); i++){
            historyStrings.set(i, historyStrings.get(i).replaceAll("\\[", ""));
            historyStrings.set(i, historyStrings.get(i).replaceAll("\\]", ""));
        }

        for (int i = 0; i < historyStrings.size(); i++) {
               if (historyStrings.get(i).charAt(0) == '1' || historyStrings.get(i).charAt(0) == '2')
                   addInvalidContentToObject(historyStrings.get(i));
               else
                   addValidContentToObject(historyStrings.get(i));
        }

    }

    // sorting array list by scanned time
    public void sortArrayByTime(){

        if (!quickResponseCodeHistoryItemArrayList.isEmpty()) {
            quickResponseCodeHistoryItemArrayList.sort(new Comparator<QuickResponseCodeHistoryItem>() {
                @Override
                public int compare(QuickResponseCodeHistoryItem t1, QuickResponseCodeHistoryItem t2) {
                    return t2.getTime().compareTo(t1.getTime());
                }
            });
        }

    }

    // add invalid content or invalid url to history item constructor
    private void addInvalidContentToObject(String str){

        ArrayList<String> parsedQrScanResult = new ArrayList<>(Arrays.asList(str.split("\t")));

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("E\\MMM\\d\\HH:mm:ss\\z\\yyyy", Locale.US);
        LocalDateTime date = LocalDateTime.parse(parsedQrScanResult.get(2), dtf);

        quickResponseCodeHistoryItemArrayList.add(new QuickResponseCodeHistoryItem(Integer.valueOf(parsedQrScanResult.get(0)),
                parsedQrScanResult.get(1), date));

    }

    // add invalid content or valid url to history item constructor
    private void addValidContentToObject(String str){

        ArrayList<String> parsedQrScanResult = new ArrayList<>(Arrays.asList(str.split("\t")));

        quickResponseCodeHistoryItemArrayList.add(new QuickResponseCodeHistoryItem(Integer.valueOf(parsedQrScanResult.get(0)),
                Boolean.valueOf(parsedQrScanResult.get(1)), parsedQrScanResult.get(2), parsedQrScanResult.get(3), parsedQrScanResult.get(4),
                parsedQrScanResult.get(5), parsedQrScanResult.get(6), parsedQrScanResult.get(7), parsedQrScanResult.get(8), parsedQrScanResult.get(9),
                parsedQrScanResult.get(10), parsedQrScanResult.get(11), parsedQrScanResult.get(12)));

    }

}
