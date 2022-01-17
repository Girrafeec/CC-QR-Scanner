package com.girrafeecstud.ccqrscanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class CertificateActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout error, certificateBackground, recoveryDateLinLay, validFromLinLay, expiredAtLinLay;
    private Button tryToConnectNetworkAgain;

    private TextView jsonStatusTxt, loadTimeTxt;

    private TextView titleTxt, statusTxt, certificateIdTxt, recoveryDateTxt, expiredAtTxt
            , fioTxt, passportTxt, enPasportTxt, birthDateTxt, validFromTxt;

    private ProgressBar progressBar;

    private ScrollView scrollView;

    private Dialog reuseDialog;

    private String certificateUrl = "";
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
    private String isBeforeValidFrom = "";

    private static boolean jsonSucceeed = false;
    private boolean certificateReuse = false;

    private JSONObject jsonObject = new JSONObject();

    private Date httpStartTime, uiPrintedTime;

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

    // this event will enable the back function to the button on press
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate);

        editActionBar();
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
        parseCertificateJson = new ParseCertificateJson(jsonObject);
        parseCertificateJson.parseJson();
        setCertificateDataValues();
        setUiValues();
        if (!jsonObject.toString().equals("{}"))
            checkPotentialCertificateReuse();
        saveCertificateToMemory();
    }

    private void getDataFromMainActivity(){
        Intent intent = getIntent();
        certificateUrl = intent.getStringExtra(MainActivity.EXTRA_SCAN_RESULT_VALID_URL);
    }

    private void editActionBar(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back);
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#0065b1\">" + "Данные сертификата" + "</font>"));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.gos_white)));
    }

    private void initUiElements(){

        progressBar = findViewById(R.id.getJsonProgressBar);

        error = findViewById(R.id.certificateActivityErrorLinLay);
        certificateBackground = findViewById(R.id.certificateCardLinLay);
        expiredAtLinLay = findViewById(R.id.expiredAtLinLay);
        recoveryDateLinLay = findViewById(R.id.certificateRecoveryDateLinlay);
        validFromLinLay = findViewById(R.id.validFromLinLay);

        tryToConnectNetworkAgain = findViewById(R.id.certActivityTryConnectToNetworkAgainBtn);

        scrollView = findViewById(R.id.certificateActivityScrollBar);

        loadTimeTxt = findViewById(R.id.loadTimeTxt);
        jsonStatusTxt = findViewById(R.id.jsonSucceedTxt);
        birthDateTxt = findViewById(R.id.certificateBirthDateTxt);
        certificateIdTxt = findViewById(R.id.certificateIdTxt);
        enPasportTxt = findViewById(R.id.certificateEnPassportTxt);
        passportTxt = findViewById(R.id.certificatePassportTxt);
        fioTxt = findViewById(R.id.certificateFioTxt);
        statusTxt = findViewById(R.id.certificateStatusTxt);
        titleTxt = findViewById(R.id.certificateTitleTxt);
        expiredAtTxt = findViewById(R.id.certificateExpiredDateDateTxt);
        recoveryDateTxt = findViewById(R.id.certificateRecoveryDateTxt);
        validFromTxt = findViewById(R.id.certificateValidFromDateTxt);

        reuseDialog = new Dialog(this);

    }

    private void checkInternetConnection(){

        if (!isConnectedToInternet()) {
            progressBar.setVisibility(View.GONE);
            error.setVisibility(View.VISIBLE);
        }
        else {
            error.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
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

        if (!jsonObject.toString().equals("{}")) {
            type = parseCertificateJson.getType();
            title = parseCertificateJson.getTitle();
            status = parseCertificateJson.getStatus();
            certificateId = parseCertificateJson.getCertificateId();
            expiredAt = parseCertificateJson.getExpiredAt();
            fio = parseCertificateJson.getFio();
            enFio = parseCertificateJson.getEnFio();
            recoveryDate = parseCertificateJson.getRecoveryDate();
            passport = parseCertificateJson.getPassport();
            enPassport = parseCertificateJson.getEnPassport();
            birthDate = parseCertificateJson.getBirthDate();
            stuff = parseCertificateJson.getStuff();
            validFrom = parseCertificateJson.getValidFrom();
            isBeforeValidFrom = parseCertificateJson.getIsBeforeValidFrom();

            if (!type.equals("COVID_TEST"))
                if (status.equals("1") || (status.equals("OK") && !isBeforeValidFrom.equals("true")))
                    status = "Действителен";
                else
                    status = "Не действителен";

            if (title.isEmpty() && type.isEmpty() && !stuff.isEmpty()) {
                title = "Сертификат вакцинации от COVID-19";
                type = "VACCINE_CERT";
            }

            if (type.equals("VACCINE_CERT")) {
                type = "Сертификат вакцинации от COVID-19";
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
        // if certificate does not exist, we set other values:
        status = "Не найден";
        title = "Сертификат COVID-19";

    }

    // set json data to ui elements
    private void setUiValues(){

        scrollView.setVisibility(View.VISIBLE);

        if (!jsonObject.toString().equals("{}")) {

            birthDateTxt.setText(birthDateTxt.getText() + birthDate);
            certificateIdTxt.setText(certificateIdTxt.getText() + certificateId);
            passportTxt.setText(passportTxt.getText() + passport);
            fioTxt.setText(fio);
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
        }
        else
        {
            certificateIdTxt.setVisibility(View.GONE);
            passportTxt.setVisibility(View.GONE);
            birthDateTxt.setVisibility(View.GONE);
            expiredAtLinLay.setVisibility(View.GONE);
        }

        titleTxt.setText(title);
        statusTxt.setText(status);

        if (status.equals("Действителен"))
            certificateBackground.setBackground(ContextCompat.getDrawable(this, R.drawable.green_rounded_recktangle));
        else
            certificateBackground.setBackground(ContextCompat.getDrawable(this, R.drawable.red_rounded_recktangle));

        //uiPrintedTime = Calendar.getInstance().getTime();
        //long diffInMilles = Math.abs(uiPrintedTime.getTime() - httpStartTime.getTime());
        //loadTimeTxt.setVisibility(View.VISIBLE);
        //loadTimeTxt.setText(String.valueOf(diffInMilles) + " ms");
    }

    // procedure to check if current certificate eas used earlier
    private void checkPotentialCertificateReuse(){

        String history = historyFileInputOutput.readFile();

        if (!history.isEmpty()) {
            historyFileParser.convertHistoryToArrayList(history);

            for (int i = 0; i < historyFileParser.getQuickResponseCodeHistoryItemArrayList().size(); i++) {

                if (historyFileParser.getQuickResponseCodeHistoryItemArrayList().get(i).getQrCodeType() == 3 &&
                        historyFileParser.getQuickResponseCodeHistoryItemArrayList().get(i).getCertificateId().equals(certificateId) &&
                        historyFileParser.getQuickResponseCodeHistoryItemArrayList().get(i).getStatus().equals("Действителен")) {
                    certificateReuse = true;
                    showReuseDialog(historyFileParser.getQuickResponseCodeHistoryItemArrayList().get(i).getCertificateId(), historyFileParser.getQuickResponseCodeHistoryItemArrayList().get(i).getTime());
                    break;
                }

            }
        }

    }

    // save certificate data to local memory file
    private void saveCertificateToMemory() {

        Date currentTime = Calendar.getInstance().getTime();
        String scanTime = String.valueOf(currentTime);
        scanTime = scanTime.replace(" ", "\\");

        if (type.isEmpty())
            type = "0";
        if (expiredAt.isEmpty())
            expiredAt = "0";
        if (fio.isEmpty())
            fio = "0";
        if (enFio.isEmpty())
            enFio = "0";
        if (certificateId.isEmpty())
            certificateId = "0";
        if (passport.isEmpty())
            passport = "0";
        if (birthDate.isEmpty())
            birthDate = "0";
        if (recoveryDate.isEmpty())
            recoveryDate = "0";
        if (enPassport.isEmpty())
            enPassport = "0";
        if (validFrom.isEmpty())
            validFrom = "0";
        if (isBeforeValidFrom.isEmpty())
            isBeforeValidFrom = "0";

        // if certificate does not exist, we save it as qrCodeType "4" - does not exist
        int qrCodeType;

        if (jsonObject.toString().equals("{}"))
            qrCodeType = 4;
        else
            qrCodeType = 3;

        historyFileInputOutput.writeValidQrToFile(qrCodeType, certificateReuse, type, title, status, certificateId, expiredAt, validFrom,
                isBeforeValidFrom, fio, enFio, recoveryDate, passport, enPassport, birthDate, scanTime);

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
            //https://www.gosuslugi.ru/covid-cert/verify/****************?lang=ru&ck=******************************** - url
            //https://www.gosuslugi.ru/api/covid-cert/v3/cert/check/****************?lang=ru&ck=******************************** - json of url

            //https://www.gosuslugi.ru/covid-cert/verify/****************?lang=ru&ck=******************************** - url
            //https://www.gosuslugi.ru/api/covid-cert/v3/cert/check/****************?lang=ru&ck=******************************** - json of ilness

            // 2 тип
            //https://www.gosuslugi.ru/vaccine/cert/verify/************************************ - url
            //https://www.gosuslugi.ru/api/vaccine/v1/cert/verify/************************************ - json of vacc from paper

            // 3 тип
            //https://www.gosuslugi.ru/covid-cert/status/************************************?lang=ru - url
            //https://www.gosuslugi.ru/api/covid-cert/v2/cert/status/************************************?lang=ru - json

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

            try {

                URL url = new URL(jsonUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                // save time value when http connection starts
                httpStartTime = Calendar.getInstance().getTime();

                InputStream inputStream = httpURLConnection.getInputStream();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line = "";

                while((line = bufferedReader.readLine()) != null){
                    data = data + line;
                }

                if (!data.isEmpty()){
                    jsonObject = new JSONObject(data);
                    jsonSucceeed = true;
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

    private void showReuseDialog(String certId, LocalDateTime time){

        reuseDialog.setContentView(R.layout.certificate_reuse_dialog);
        reuseDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Button clickOk = reuseDialog.findViewById(R.id.certReuseDialogClickOkBtn);
        TextView scanStatusLongMessage = reuseDialog.findViewById(R.id.certReuseDescriptionTxt);

        String curTime = "";
        // set time
        if (String.valueOf(time.getMinute()).length() == 1)
            curTime = time.getHour() + ":" + "0" + time.getMinute();
        else
            curTime = time.getHour() + ":" + time.getMinute();

        scanStatusLongMessage.setText("Сертификат № " + certId +  " уже сканировался сегодня в " + curTime + ".");

        reuseDialog.show();

        clickOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reuseDialog.cancel();
                onResume();
            }
        });

        reuseDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                onResume();
            }
        });
    }

}