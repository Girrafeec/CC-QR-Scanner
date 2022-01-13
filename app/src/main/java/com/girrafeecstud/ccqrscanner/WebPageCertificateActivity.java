package com.girrafeecstud.ccqrscanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

public class WebPageCertificateActivity extends AppCompatActivity implements View.OnClickListener {

    private WebView webView;
    private TextView pageLoadTime, webPageLoadSucceedTxt;

    private Date httpStartTime, pagePrintedTime;

    private String certificateUrl = "";

    private static boolean loadSucceed = false;

    @Override
    public void onClick(View view) {

        switch (view.getId()){
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
        setContentView(R.layout.activity_web_page_certificate);

        editActionBar();
        initUiElements();

        getDataFromMainActivity();

        checkInternetConnection();

        // continue code only when web page is loaded
        webPageLoadSucceedTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Log.i("Text", "changed");
            }

            @Override
            public void afterTextChanged(Editable editable) {
                showLoadTime();
            }
        });
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
        webView = findViewById(R.id.certificateWebView);
        pageLoadTime = findViewById(R.id.pageLoadTimeTxt);
        webPageLoadSucceedTxt = findViewById(R.id.webPageLoadSucceedTxt);
    }

    private void checkInternetConnection(){

        if (isConnectedToInternet()) {
            showWebPage();
        }

    }

    // function returns boolean result of internet connection
    private boolean isConnectedToInternet(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(CertificateActivity.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    private void showWebPage(){
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new MyWebChromeClient());
        webView.setWebViewClient(new MyWebViewClient());
        webView.loadUrl(certificateUrl);
        //showLoadTime();
    }

    private void showLoadTime(){
        long diffInMilles = Math.abs(pagePrintedTime.getTime() - httpStartTime.getTime());
        pageLoadTime.setVisibility(View.VISIBLE);
        pageLoadTime.setText(String.valueOf(diffInMilles) + " ms");
    }


    private class MyWebChromeClient extends WebChromeClient{

        @Override
        public void onProgressChanged(WebView view, int progress) {
            if (progress == 100) {

            }
        }

        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            pagePrintedTime = Calendar.getInstance().getTime();
            loadSucceed = true;
            webPageLoadSucceedTxt.setText(String.valueOf(loadSucceed));
        }

    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            httpStartTime = Calendar.getInstance().getTime();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
        }
    }

}