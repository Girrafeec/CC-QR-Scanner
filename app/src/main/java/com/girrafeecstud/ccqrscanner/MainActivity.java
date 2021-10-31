package com.girrafeecstud.ccqrscanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

import java.net.URL;

public class MainActivity extends AppCompatActivity {

    //TODO увеличение/уменьшение зума камеры
    //TODO кнопка вспышки на смартфоне
    //TODO автофокус камеры - кнопка
    //TODO - кнопка вопросика с краткой инфой о том, что надо сделать
    //TODO добавить повторный запрос разрешений на камеру (с помощью диалога)
    //TODO если человек нажал "больше не спрашивать", то отправить его в настройки самостоятельно включить доступ к камере" - диалог

    private static final int CAMERA_REQUEST_CODE = 101;

    private TextView url;

    private QuickResponseCodeURL quickResponseCodeURL;
    private CodeScanner codeScanner;
    private CodeScannerView codeScannerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        quickResponseCodeURL = new QuickResponseCodeURL();

        askCameraPermission();

        initUiElements();
        initCodeScanner();

       codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, result.getText(), Toast.LENGTH_SHORT).show();
                        //url.setText(result.getText());
                        checkContent(result.getText());
                    }
                });
            }
        });
        codeScannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codeScanner.startPreview();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkCameraPermission())
            codeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        codeScanner.releaseResources();
        super.onPause();
    }

    private void initCodeScanner(){
        codeScannerView = findViewById(R.id.scannerView);
        codeScanner = new CodeScanner(MainActivity.this, codeScannerView);
    }

    private void initUiElements(){
        url = findViewById(R.id.urlTxt);
    }

    // procedure to check is it necessary to request camera permission
    private void askCameraPermission(){

        if (!checkCameraPermission())
            requestCameraPermission();

    }

    // function check for camera permission
    private boolean checkCameraPermission(){

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED)
            return false;
        else
            return true;

    }

    // procedure to request for camera permission
    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
    }

    private void checkContent(String str){

        if (!quickResponseCodeURL.isURL(str)) {
            Toast.makeText(this, "QR does not contain URL", Toast.LENGTH_SHORT).show();
            return;
        }
        else
            Toast.makeText(this, "QR contains URL", Toast.LENGTH_SHORT).show();

        String a = quickResponseCodeURL.isValidURL(str);
        Toast.makeText(this, a, Toast.LENGTH_SHORT).show();

    }
}