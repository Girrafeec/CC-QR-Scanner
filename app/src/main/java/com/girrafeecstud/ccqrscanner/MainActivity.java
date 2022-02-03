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
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.zxing.Result;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int CAMERA_REQUEST_CODE = 101;

    private static final String SCAN_RESULT_NOT_URL = "SCAN_RESULT_NOT_URL";
    private static final String SCAN_RESULT_INVALID_URL = "SCAN_RESULT_INVALID_URL";
    private static final String SCAN_RESULT_VALID_URL = "SCAN_RESULT_VALID_URL";

    public static final String EXTRA_SCAN_RESULT_VALID_URL = "com.girrafeecstud.ccqrscanner.EXTRA_SCAN_RESULT_VALID_URL";

    private long backPressedTime;

    private Dialog notSuccessScanResultDialog, helpDialog;

    private ImageButton helpButton;

    private Toast backToast;

    private QuickResponseCodeURL quickResponseCodeURL = new QuickResponseCodeURL();
    private HistoryFileInputOutput historyFileInputOutput = new HistoryFileInputOutput(MainActivity.this);
    private CodeScanner codeScanner;
    private CodeScannerView codeScannerView;

    private BottomNavigationView bottomNavigationView;

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.helpBtn:
                showHelpDialog();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        historyFileInputOutput.createHistoryFile();

        askCameraPermission();

        editActionBar();
        initUiElements();
        initCodeScanner();

        codeScannerProc();

        helpButton.setOnClickListener(this);

        // Set mainActivity selected in bottom nav bar
        bottomNavigationView.setSelectedItemId(R.id.mainActivity);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.organizationProfileActivity:
                        MainActivity.this.startActivity(new Intent(MainActivity.this, OrganizationProfileActivity.class));
                        overridePendingTransition(0,0);
                        break;
                    case R.id.quickResponseCodeHistoryActivity:
                        MainActivity.this.startActivity(new Intent(MainActivity.this, QuickResponseCodeHistoryActivity.class));
                        overridePendingTransition(0,0);
                        break;
                    case R.id.mainActivity:
                        return true;
                    default:
                        break;
                }
                return false;
            }
        });

    }

    @Override
    public void onBackPressed() {

        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            backToast.cancel();
            finishAffinity();
            return;
        }

        backToast = Toast.makeText(this, "Для выхода нажмите назад ещё раз", Toast.LENGTH_SHORT);
        backToast.show();

        backPressedTime = System.currentTimeMillis();
    }

    private void codeScannerProc(){
        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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
        bottomNavigationView = findViewById(R.id.mainNavigationMenu);
        helpButton = findViewById(R.id.helpBtn);
        notSuccessScanResultDialog = new Dialog(this);
        helpDialog = new Dialog(this);
    }

    private void editActionBar(){
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#0065b1\">" + "Сканер" + "</font>"));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.gos_white)));
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

        Date currentTime = Calendar.getInstance().getTime();
        String scanTime = String.valueOf(currentTime);
        scanTime = scanTime.replace(" ", "\\");

        if (!quickResponseCodeURL.isURL(str)) {
            historyFileInputOutput.writeInvalidQrToFile(1, str, scanTime);
            showNotSuccessScanResultAlertDialog(SCAN_RESULT_NOT_URL);
            return;
        }

        if (!quickResponseCodeURL.isValidURL(str)) {
            historyFileInputOutput.writeInvalidQrToFile(2, str, scanTime);
            showNotSuccessScanResultAlertDialog(SCAN_RESULT_INVALID_URL);
            return;
        }

        str = quickResponseCodeURL.replaceSpaces(str);

        startCertificateActivity(str);

    }

    private void startCertificateActivity(String str){
        Intent intent = new Intent(MainActivity.this, CertificateActivity.class);
        intent.putExtra(EXTRA_SCAN_RESULT_VALID_URL, str);
        MainActivity.this.startActivity(intent);

        /*
        Intent intent = new Intent(MainActivity.this, WebPageCertificateActivity.class);
        intent.putExtra(EXTRA_SCAN_RESULT_VALID_URL, str);
        MainActivity.this.startActivity(intent);
        */
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
                scanStatusLongMessage.setText("Отсканированный QR-код содержит данные, не являющиеся ссылкой.");
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
        handler.postDelayed(runnable, 3000);

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

    private void showHelpDialog(){
        helpDialog.setContentView(R.layout.help_info_dialog);
        helpDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        helpDialog.show();

        helpDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                onResume();
            }
        });
    }
}
