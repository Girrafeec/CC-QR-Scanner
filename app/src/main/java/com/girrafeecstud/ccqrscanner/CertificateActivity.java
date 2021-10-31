package com.girrafeecstud.ccqrscanner;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

public class CertificateActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout error;
    private Button tryToConnectNetworkAgain;
    private TextView fullJson;
    ProgressDialog progressDialog;

    private String certificateUrl = "", jsonString = "";

    Handler mainHandler = new Handler();

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.certActivityTryConnectToNetworkAgainBtn:
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

        if (isConnectedToInternet())
            getJsonFromUrl(certificateUrl);
    }

    private void getDataFromMainActivity(){
        Intent intent = getIntent();
        certificateUrl = intent.getStringExtra(MainActivity.EXTRA_SCAN_RESULT_VALID_URL);
    }

    private void initUiElements(){

        error = findViewById(R.id.certificateActivityErrorLinLay);
        tryToConnectNetworkAgain = findViewById(R.id.certActivityTryConnectToNetworkAgainBtn);
        fullJson = findViewById(R.id.fullJsonTxt);

    }

    private void checkInternetConnection(){

        if (!isConnectedToInternet())
            error.setVisibility(View.VISIBLE);
        else
            error.setVisibility(View.INVISIBLE);

    }

    private boolean isConnectedToInternet(){

        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(CertificateActivity.CONNECTIVITY_SERVICE);

        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();

    }

    private void getJsonFromUrl(String url){

        FetchData fetchData = new FetchData(url);
        fetchData.start();

    }

    private class FetchData extends Thread{

        String websiteUrl;
        String data = "";

        public FetchData(String websiteUrl){
            this.websiteUrl = websiteUrl;
        }

        @Override
        public void run() {

            mainHandler.post(new Runnable() {
                @Override
                public void run() {

                    progressDialog = new ProgressDialog(CertificateActivity.this);
                    progressDialog.setMessage("Загрузка...");
                    //progressDialog.setContentView(R.layout.progress_dialog);
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    Log.i("progress status: ", String.valueOf(progressDialog.isShowing()));

                }
            });



            try {


                // 1 тип
                //https://www.gosuslugi.ru/covid-cert/verify/9780000018577364?lang=ru&ck=8184f31948a5353cf9a0c28b7326d1c6 - url
                //https://www.gosuslugi.ru/api/covid-cert/v3/cert/check/9780000018577364?lang=ru&ck=8184f31948a5353cf9a0c28b7326d1c6 - json of url

                //https://www.gosuslugi.ru/covid-cert/verify/8471082132567136?lang=ru&ck=feded94bb56d7c2580a314a6f4b472a4 - url
                //https://www.gosuslugi.ru/api/covid-cert/v3/cert/check/8471082132567136?lang=ru&ck=feded94bb56d7c2580a314a6f4b472a4 - json of ilness

                // 2 тип
                //https://www.gosuslugi.ru/vaccine/cert/verify/0fcfc8a8-945d-4b2e-a6ab-691c3d6fd67d - url
                //https://www.gosuslugi.ru/api/vaccine/v1/cert/verify/0fcfc8a8-945d-4b2e-a6ab-691c3d6fd67d - json of vacc from paper

                String[] urlElementsArray = websiteUrl.split("/");

                ArrayList<String> ar = new ArrayList<>(Arrays.asList(urlElementsArray));
                ar.remove("");

                for (int i=0; i< ar.size(); i++)
                    System.out.println(ar.get(i));

                String jsonUrl = "";

                if (websiteUrl.contains("vaccine")) {
                    jsonUrl = ar.get(0) + "//" + ar.get(1) + "/api/" + ar.get(2) + "/v1/" + ar.get(3) + "/" + ar.get(4) + "/" + ar.get(5);
                }else if (websiteUrl.contains("covid-cert")) {
                    jsonUrl = ar.get(0) + "//" + ar.get(1) + "/api/" + ar.get(2) + "/v3/cert/check/" + ar.get(4);
                }

                Log.i("json url: ", jsonUrl);

                Log.i("go to ", "thread");
                URL url = new URL(jsonUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                Log.i("connection started", " ok");

                InputStream inputStream = httpURLConnection.getInputStream();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line = "";

                while((line = bufferedReader.readLine()) != null){
                    data = data + line;
                }

                if (!data.isEmpty()){

                    Log.i("data", data);

                    JSONObject jsonObject = new JSONObject(data);
                    jsonString = jsonObject.toString();
                    Log.i("json: ", jsonString);

                    ParseCertificateJson parseCertificateJson = new ParseCertificateJson(jsonObject);
                    parseCertificateJson.parseJson();

                }

            }catch (MalformedURLException e){
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }catch (JSONException e) {
                e.printStackTrace();
            }

            mainHandler.post(new Runnable() {
                @Override
                public void run() {

                    if (progressDialog.isShowing())
                        progressDialog.dismiss();

                }
            });

        }
    }

}