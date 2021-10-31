package com.girrafeecstud.ccqrscanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;
import com.makeramen.roundedimageview.RoundedImageView;

public class MainActivity extends AppCompatActivity {

    //TODO увеличение/уменьшение зума камеры
    //TODO кнопка вспышки на смартфоне
    //TODO автофокус камеры - кнопка
    //TODO - кнопка вопросика с краткой инфой о том, что надо сделать
    //TODO добавить повторный запрос разрешений на камеру (с помощью диалога)
    //TODO если человек нажал "больше не спрашивать", то отправить его в настройки самостоятельно включить доступ к камере" - диалог

    private static final int CAMERA_REQUEST_CODE = 101;

    private static final String SCAN_RESULT_NOT_URL = "SCAN_RESULT_NOT_URL";
    private static final String SCAN_RESULT_INVALID_URL = "SCAN_RESULT_INVALID_URL";
    private static final String SCAN_RESULT_VALID_URL = "SCAN_RESULT_VALID_URL";

    public static final String EXTRA_SCAN_RESULT_VALID_URL = "com.girrafeecstud.ccqrscanner.EXTRA_SCAN_RESULT_VALID_URL";

    private TextView url;

    private Dialog notSuccessScanResultDialog;

    private QuickResponseCodeURL quickResponseCodeURL = new QuickResponseCodeURL();
    private CodeScanner codeScanner;
    private CodeScannerView codeScannerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        askCameraPermission();

        initUiElements();
        initCodeScanner();

        codeScannerProc();

    }

    private void codeScannerProc(){
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
        notSuccessScanResultDialog = new Dialog(this);
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
            //Toast.makeText(this, "QR does not contain URL", Toast.LENGTH_SHORT).show();
            showNotSuccessScanResultAlertDialog(SCAN_RESULT_NOT_URL);
            return;
        }

        if (!quickResponseCodeURL.isValidURL(str)) {
            showNotSuccessScanResultAlertDialog(SCAN_RESULT_INVALID_URL);
            return;
        }

        startCertificateActivity(str);

    }

    private void startCertificateActivity(String str){
        Intent intent = new Intent(MainActivity.this, CertificateActivity.class);

        intent.putExtra(EXTRA_SCAN_RESULT_VALID_URL, str);
        MainActivity.this.startActivity(intent);
    }

    // procedure to show alert dialog with info about not success result
    private void showNotSuccessScanResultAlertDialog(String scanResult){

        notSuccessScanResultDialog.setContentView(R.layout.scan_result_dialog);
        notSuccessScanResultDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        RoundedImageView scanResultImage = notSuccessScanResultDialog.findViewById(R.id.scanStatusImgView);
        Button clickOk = notSuccessScanResultDialog.findViewById(R.id.scanResultAlertDialogClickOkBtn);
        TextView scanStatusShortMessage = notSuccessScanResultDialog.findViewById(R.id.scanStatusTxt);
        TextView scanStatusLongMessage = notSuccessScanResultDialog.findViewById(R.id.scanStatusDescriptionTxt);

        switch (scanResult){
            case SCAN_RESULT_NOT_URL:
                scanResultImage.setImageResource(R.drawable.ic__891023_cancel_cercle_close_delete_dismiss_icon);
                scanStatusShortMessage.setText("QR-код не содержит ссылку!");
                scanStatusLongMessage.setText("Отсканированный QR-код содержит данные, не являющиеся ссылкой на сертификат.");
                break;
            case SCAN_RESULT_INVALID_URL:
                scanResultImage.setImageResource(R.drawable.ic__891023_cancel_cercle_close_delete_dismiss_icon);
                scanStatusShortMessage.setText("QR-код содержит неправильную ссылку!");
                scanStatusLongMessage.setText("Отсканированный QR-код содержит ссылку на сторонний ресурс или фишинговый сайт.");
                break;
            default:
                break;
        }

        notSuccessScanResultDialog.show();

        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (notSuccessScanResultDialog.isShowing())
                    notSuccessScanResultDialog.dismiss();
            }
        };

        // dismiss dialog after 5 seconds (1 secons equals to 1000 seconds)
        handler.postDelayed(runnable, 5000);

        clickOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notSuccessScanResultDialog.cancel();
                onResume();
            }
        });

        notSuccessScanResultDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                onResume();
            }
        });

    }
}