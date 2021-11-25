package com.girrafeecstud.ccqrscanner;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

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

        ArrayList<String> historyStrings = new ArrayList<>(Arrays.asList(history.split("\n")));

        //Log.i("ar size", String.valueOf(historyStrings.size()));
        //for (int i=0; i<historyStrings.size();i++)
          //System.out.println(i + "  " + historyStrings.get(i));

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

    // add invalid content or invalid url to history item constructor
    private void addInvalidContentToObject(String str){

        ArrayList<String> parsedQrScanResult = new ArrayList<>(Arrays.asList(str.split("\t")));

        quickResponseCodeHistoryItemArrayList.add(new QuickResponseCodeHistoryItem(Integer.valueOf(parsedQrScanResult.get(0)), parsedQrScanResult.get(1)));

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
