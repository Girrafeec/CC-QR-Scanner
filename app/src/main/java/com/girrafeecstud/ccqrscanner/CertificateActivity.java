package com.girrafeecstud.ccqrscanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javax.net.ssl.HttpsURLConnection;

public class CertificateActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout error, certificateBackground, recoveryDateLinLay, validFromLinLay;
    private Button tryToConnectNetworkAgain;

    private TextView jsonStatusTxt;

    private TextView titleTxt, statusTxt, certificateIdTxt, recoveryDateTxt, expiredAtTxt
            , fioTxt, pasportTxt, enPasportTxt, birthDateTxt, validFromTxt;

    private ProgressBar progressBar;

    private ScrollView scrollView;

    private String certificateUrl = "", jsonString = "";
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
    private String stuff = "";
    private String validFrom = "";

    private static boolean jsonSucceeed = false;
    private boolean certificateReuse = false;

    private JSONObject jsonObject;

    private ParseCertificateJson parseCertificateJson;

    private HistoryFileInputOutput historyFileInputOutput = new HistoryFileInputOutput(this);

    private HistoryFileParser historyFileParser = new HistoryFileParser();

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.certActivityTryConnectToNetworkAgainBtn:
                error.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                checkInternetConnection();
                break;
            default:
                break;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate);

        initUiElements();

        getDataFromMainActivity();

        tryToConnectNetworkAgain.setOnClickListener(this);

        checkInternetConnection();

        // continue code only when json object is received
        jsonStatusTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Log.i("Text", "changed");
            }

            @Override
            public void afterTextChanged(Editable editable) {
                progressBar.setVisibility(View.GONE);
                getJsonData();
            }
        });
    }

    private void getJsonData(){

        Log.i("before", "getting json");
        parseCertificateJson = new ParseCertificateJson(jsonObject);
        parseCertificateJson.parseJson();
        setCertificateDataValues();
        setUiValues();
        checkPotentialCertificateReuse();
        saveCertificateToMemory();

    }

    private void getDataFromMainActivity(){
        Intent intent = getIntent();
        certificateUrl = intent.getStringExtra(MainActivity.EXTRA_SCAN_RESULT_VALID_URL);
    }

    private void initUiElements(){

        progressBar = findViewById(R.id.getJsonProgressBar);

        error = findViewById(R.id.certificateActivityErrorLinLay);
        certificateBackground = findViewById(R.id.certificateCardLinLay);
        recoveryDateLinLay = findViewById(R.id.certificateRecoveryDateLinlay);
        validFromLinLay = findViewById(R.id.validFromLinLay);

        tryToConnectNetworkAgain = findViewById(R.id.certActivityTryConnectToNetworkAgainBtn);

        scrollView = findViewById(R.id.certificateActivityScrollBar);

        jsonStatusTxt = findViewById(R.id.jsonSucceedTxt);
        birthDateTxt = findViewById(R.id.certificateBirthDateTxt);
        certificateIdTxt = findViewById(R.id.certificateIdTxt);
        enPasportTxt = findViewById(R.id.certificateEnPassportTxt);
        pasportTxt = findViewById(R.id.certificatePassportTxt);
        fioTxt = findViewById(R.id.certificateFioTxt);
        statusTxt = findViewById(R.id.certificateStatusTxt);
        titleTxt = findViewById(R.id.certificateTitleTxt);
        expiredAtTxt = findViewById(R.id.certificateExpiredDateDateTxt);
        recoveryDateTxt = findViewById(R.id.certificateRecoveryDateTxt);
        validFromTxt = findViewById(R.id.certificateValidFromDateTxt);

    }

    private void checkInternetConnection(){

        if (!isConnectedToInternet()) {
            progressBar.setVisibility(View.GONE);
            error.setVisibility(View.VISIBLE);
        }
        else {
            error.setVisibility(View.VISIBLE);
            error.setVisibility(View.INVISIBLE);
            getJsonFromUrl(certificateUrl);
        }

    }

    // function returns boolean result of internet connection
    private boolean isConnectedToInternet(){

        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(CertificateActivity.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();

    }

    private void getJsonFromUrl(String url){
        FetchJsonData fetchJsonData = new FetchJsonData(url);
        fetchJsonData.execute();
    }

    // set json data to string values
    private void setCertificateDataValues(){

        type = parseCertificateJson.getType();
        title= parseCertificateJson.getTitle();
        status= parseCertificateJson.getStatus();
        certificateId= parseCertificateJson.getCertificateId();
        expiredAt= parseCertificateJson.getExpiredAt();
        fio= parseCertificateJson.getFio();
        enFio= parseCertificateJson.getEnFio();
        recoveryDate= parseCertificateJson.getRecoveryDate();
        passport= parseCertificateJson.getPassport();
        enPassport= parseCertificateJson.getEnPassport();
        birthDate= parseCertificateJson.getBirthDate();
        stuff = parseCertificateJson.getStuff();
        validFrom  =parseCertificateJson.getValidFrom();

        if (status.equals("1"))
            status = "Действителен";
        else
            status = "Не действителен";

        if (title.isEmpty() && type.isEmpty() && !stuff.isEmpty()) {
            title = "Сертификат вакцинации COVID-19";
            type = "VACCINE_CERT";
        }

        if (type.equals("VACCINE_CERT")) {
            type = "Сертификат вакцинации";
            return;
        }
        if (type.equals("TEMPORARY_CERT")) {
            type = "Временный сертификат вакцинации";
            return;
        }
        if (type.equals("COVID_TEST")) {
            type = "ПЦР-тест";
            return;
        }
        if (type.equals("ILLNESS_FACT")) {
            type = "Сведения о перенесённом заболевании";
            return;
        }

    }

    // set json data to ui elements
    private void setUiValues(){

        scrollView.setVisibility(View.VISIBLE);

        birthDateTxt.setText(birthDateTxt.getText() + birthDate);
        certificateIdTxt.setText(certificateIdTxt.getText() + certificateId);
        pasportTxt.setText(pasportTxt.getText() + passport);
        fioTxt.setText(fio);
        titleTxt.setText(title);
        expiredAtTxt.setText(expiredAtTxt.getText() + expiredAt);

        if (!enPassport.isEmpty()) {
            enPasportTxt.setVisibility(View.VISIBLE);
            enPasportTxt.setText(enPasportTxt.getText() + enPassport);
        }

        if (!recoveryDate.isEmpty()) {
            recoveryDateLinLay.setVisibility(View.VISIBLE);
            recoveryDateTxt.setText(recoveryDateTxt.getText() + recoveryDate);
        }

        if (!validFrom.isEmpty()) {
            validFromLinLay.setVisibility(View.VISIBLE);
            validFromTxt.setText(validFromTxt.getText() + validFrom);
        }

        statusTxt.setText(status);
        if (status.equals("Не действителен"))
            certificateBackground.setBackground(ContextCompat.getDrawable(this, R.drawable.red_rounded_recktangle));
        else
            certificateBackground.setBackground(ContextCompat.getDrawable(this, R.drawable.green_rounded_recktangle));

    }

    // procedure to check if current certificate eas used earlier
    private void checkPotentialCertificateReuse(){

        String history = historyFileInputOutput.readFile();

        if (!history.isEmpty()) {
            historyFileParser.convertHistoryToArrayList(history);

            for (int i = 0; i < historyFileParser.getQuickResponseCodeHistoryItemArrayList().size(); i++) {

                if (historyFileParser.getQuickResponseCodeHistoryItemArrayList().get(i).getQrCodeType() == 3 &&
                        historyFileParser.getQuickResponseCodeHistoryItemArrayList().get(i).getCertificateId().equals(certificateId)) {
                    certificateReuse = true;
                    System.out.println("potential reuse!");
                    break;
                }
                //TODO доделать возможный поиск переиспользовани

            }
        }

    }

    // save certificate data to local memory file
    private void saveCertificateToMemory() {

        Date currentTime = Calendar.getInstance().getTime();
        String scanTime = String.valueOf(currentTime);
        scanTime = scanTime.replace(" ", "\\");

        if (recoveryDate.isEmpty())
            recoveryDate = "0";
        if (enPassport.isEmpty())
            enPassport = "0";
        if (validFrom.isEmpty())
            validFrom = "0";

        historyFileInputOutput.writeValidQrToFile(3, certificateReuse, type, title, status, certificateId, expiredAt, validFrom,
                fio, enFio, recoveryDate, passport, enPassport, birthDate, scanTime);

        //String str = historyFileInputOutput.readFile();
        //Toast.makeText(this, str, Toast.LENGTH_LONG).show();

    }

    // class that makes connection to the url and get json object
    private class FetchJsonData extends AsyncTask{

        String websiteUrl;
        String data = "";

        public FetchJsonData(String websiteUrl){
            this.websiteUrl = websiteUrl;
        }

        protected Object doInBackground(Object[] objects) {
            fetch();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            jsonStatusTxt.setText(String.valueOf(jsonSucceeed));
        }

        public void fetch(){

            // 1 тип
            //https://www.gosuslugi.ru/covid-cert/verify/9780000018577364?lang=ru&ck=8184f31948a5353cf9a0c28b7326d1c6 - url
            //https://www.gosuslugi.ru/api/covid-cert/v3/cert/check/9780000018577364?lang=ru&ck=8184f31948a5353cf9a0c28b7326d1c6 - json of url

            //https://www.gosuslugi.ru/covid-cert/verify/8471082132567136?lang=ru&ck=feded94bb56d7c2580a314a6f4b472a4 - url
            //https://www.gosuslugi.ru/api/covid-cert/v3/cert/check/8471082132567136?lang=ru&ck=feded94bb56d7c2580a314a6f4b472a4 - json of ilness

            // 2 тип
            //https://www.gosuslugi.ru/vaccine/cert/verify/0fcfc8a8-945d-4b2e-a6ab-691c3d6fd67d - url
            //https://www.gosuslugi.ru/api/vaccine/v1/cert/verify/0fcfc8a8-945d-4b2e-a6ab-691c3d6fd67d - json of vacc from paper

            // 3 тип
            //https://www.gosuslugi.ru/covid-cert/status/e4d9657a-48fe-477b-9777-c8053a2bdfc3?lang=ru - url
            //https://www.gosuslugi.ru/api/covid-cert/v2/cert/status/e4d9657a-48fe-477b-9777-c8053a2bdfc3?lang=ru - json

            //TODO проверить ссылку с status

            String[] urlElementsArray = websiteUrl.split("/");

            ArrayList<String> ar = new ArrayList<>(Arrays.asList(urlElementsArray));
            ar.remove("");

            String jsonUrl = "";

            if (websiteUrl.contains("vaccine")) {
                jsonUrl = ar.get(0) + "//" + ar.get(1) + "/api/" + ar.get(2) + "/v1/" + ar.get(3) + "/" + ar.get(4) + "/" + ar.get(5);
            }else if (websiteUrl.contains("covid-cert") && !websiteUrl.contains("status")) {
                jsonUrl = ar.get(0) + "//" + ar.get(1) + "/api/" + ar.get(2) + "/v3/cert/check/" + ar.get(4);
            }else if (websiteUrl.contains("covid-cert") && websiteUrl.contains("status")){
                jsonUrl = ar.get(0) + "//" + ar.get(1) + "/api/" + ar.get(2) + "/v2/cert/status/" + ar.get(4);
            }

            Log.i("json url: ", jsonUrl);

            ///////////////////////connect to original url to get html code///////////////
            try {

                URL url = new URL(jsonUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                Log.i("connection json started", " ");

                InputStream inputStream = httpURLConnection.getInputStream();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line = "";

                while((line = bufferedReader.readLine()) != null){
                    data = data + line;
                }

                if (!data.isEmpty()){

                    jsonObject = new JSONObject(data);
                    jsonString = jsonObject.toString();
                    Log.i("json: ", jsonString);
                    jsonSucceeed = true;
                    // parseCertificateJson = new ParseCertificateJson(jsonObject);
                    //  parseCertificateJson.parseJson();

                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}