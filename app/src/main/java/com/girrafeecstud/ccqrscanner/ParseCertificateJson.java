package com.girrafeecstud.ccqrscanner;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ParseCertificateJson {

    private String type = "";
    private String title = "";
    private String status = "";
    private String certificateId = "";
    private String expiredAt = "";
    private String fio = "";
    private String enFio = "";
    private String recoveryDate = "";
    private String passport = "";
    private String enPassport = "";
    private String birthDate = "";
    private String validFrom = "";
    private String isBeforeValidFrom = "";

    // в json, где нет items нет типа сертификата, но в stuff записан тип вакцины
    private String stuff = "";

    private JSONObject jsonObject;

    public ParseCertificateJson(JSONObject jsonObject){
        this.jsonObject = jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getStatus() {
        return status;
    }

    public String getCertificateId() {
        return certificateId;
    }

    public String getExpiredAt() {
        return expiredAt;
    }

    public String getFio() {
        return fio;
    }

    public String getEnFio() {
        return enFio;
    }

    public String getRecoveryDate() {
        return recoveryDate;
    }

    public String getPassport() {
        return passport;
    }

    public String getEnPassport() {
        return enPassport;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getValidFrom(){
        return validFrom;
    }

    public String getStuff() {
        return stuff;
    }

    public String getIsBeforeValidFrom(){
        return isBeforeValidFrom;
    }

    public void parseJson(){

        if (jsonObject.has("items"))
            parseJsonWithItems();
        else if (!jsonObject.has("items"))
            parseJsonWithoutItems();

        Log.i("type", type);
        Log.i("title", title);
        Log.i("status", status);
        Log.i("certificate id", certificateId);
        Log.i("expiredAt", expiredAt);
        Log.i("fio", fio);
        Log.i("enFio", enFio);
        Log.i("passport", passport);
        Log.i("enPassport", enPassport);
        Log.i("recoveryDate", recoveryDate);
        Log.i("birthDate", birthDate);
        Log.i("stuff", stuff);

    }

    private void parseJsonWithoutItems(){

        try {
            certificateId = jsonObject.getString("unrz");
            fio = jsonObject.getString("fio");
            enFio = jsonObject.getString("enFio");
            birthDate = jsonObject.getString("birthdate");
            passport = jsonObject.getString("doc");
            enPassport = jsonObject.getString("enDoc");
            status = jsonObject.getString("status");
            expiredAt = jsonObject.getString("expiredAt");
            stuff = jsonObject.getString("stuff");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void parseJsonWithItems(){

        try {
            JSONArray jsonArray = jsonObject.getJSONArray("items");
            JSONObject jsonObjectType = null;
            JSONArray extraAttrs = new JSONArray();

            for (int i=0; i < jsonArray.length(); i++){

                jsonObjectType = jsonArray.getJSONObject(i);
                type = jsonObjectType.getString("type");
                title = jsonObjectType.getString("title");
                status = jsonObjectType.getString("status");
                certificateId = jsonObjectType.getString("unrzFull");
                expiredAt = jsonObjectType.getString("expiredAt");
                //validFrom = jsonObjectType.getString("validFrom");

                extraAttrs = jsonObjectType.getJSONArray("attrs");
                Log.i("extra", extraAttrs.toString());
            }

            ArrayList<JSONObject> extraAttrsArrayList = new ArrayList();

            for (int i=0; i < extraAttrs.length(); i++){

                Log.i("i val", String.valueOf(i));
                extraAttrsArrayList.add(new JSONObject(String.valueOf(extraAttrs.getJSONObject(i))));
            }

            for (int i = 0; i < extraAttrsArrayList.size(); i++){

                String extraDataType = extraAttrsArrayList.get(i).getString("type");
                String extraDataTitle = extraAttrsArrayList.get(i).getString("title");

                parseExtraData(extraDataType, extraDataTitle, extraAttrsArrayList.get(i));

            }

            validFrom = jsonObjectType.getString("validFrom");
            isBeforeValidFrom = jsonObjectType.getString("isBeforeValidFrom");

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    // procedure parse items json for extra info
    private void parseExtraData(String dataType, String dataTitle, JSONObject extraJsonObject){

        try {

            if (dataType.equals("date") && dataTitle.equals("Дата выздоровления")) {
                recoveryDate = extraJsonObject.getString("value");
                return;
            }

            if (dataType.equals("fio")) {
                fio = extraJsonObject.getString("value");
                enFio = extraJsonObject.getString("envalue");
                return;
            }

            if (dataType.equals("birthDate")) {
                birthDate = extraJsonObject.getString("value");
                return;
            }

            if (dataType.equals("passport")) {
                passport = extraJsonObject.getString("value");
                return;
            }

            if (dataType.equals("enPassport")) {
                enPassport = extraJsonObject.getString("value");
                return;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
