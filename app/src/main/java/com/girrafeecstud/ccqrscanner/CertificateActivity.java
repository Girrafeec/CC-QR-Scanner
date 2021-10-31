package com.girrafeecstud.ccqrscanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class CertificateActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout error;
    private Button tryToConnectNetworkAgain;

    private CertificateActivityBinding binding;

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

        tryToConnectNetworkAgain.setOnClickListener(this);

        checkInternetConnection();
    }

    private void initUiElements(){

        error = findViewById(R.id.certificateActivityErrorLinLay);
        tryToConnectNetworkAgain = findViewById(R.id.certActivityTryConnectToNetworkAgainBtn);

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

}