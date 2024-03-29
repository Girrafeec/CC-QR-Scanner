package com.girrafeecstud.ccqrscanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class QuickResponseCodeHistoryActivity extends AppCompatActivity implements View.OnClickListener /*implements QuickResponseCodeHistoryRecViewAdapter.OnQrHistoryItemDropListener*/{

    private long backPressedTime;

    private BottomNavigationView bottomNavigationView;

    private RecyclerView qrHistory;

    private LinearLayout emptyHistory;

    private Button startScan;

    private androidx.appcompat.widget.Toolbar toolbar;

    private Toast backToast;

    private HistoryFileInputOutput historyFileInputOutput = new HistoryFileInputOutput(this);
    private HistoryFileParser historyFileParser = new HistoryFileParser();
    private QuickResponseCodeHistoryRecViewAdapter adapter = new QuickResponseCodeHistoryRecViewAdapter(this/*, this*/);
    /*
    @Override
    public void onQrHistoryItemDropClick(int position) {

    }
     */

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.startScanFromHistoryBtn:
                QuickResponseCodeHistoryActivity.this.startActivity(new Intent(QuickResponseCodeHistoryActivity.this, MainActivity.class));
                overridePendingTransition(0,0);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_responce_code_history);

        editActionBar();
        initUiElements();

        startScan.setOnClickListener(this);

        addScannedHistory();

        showEmptyHistoryBanner();

        // Set mainActivity selected in bottom nav bar
        bottomNavigationView.setSelectedItemId(R.id.quickResponseCodeHistoryActivity);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.organizationProfileActivity:
                        QuickResponseCodeHistoryActivity.this.startActivity(new Intent(QuickResponseCodeHistoryActivity.this, OrganizationProfileActivity.class));
                        overridePendingTransition(0,0);
                        break;
                    case R.id.quickResponseCodeHistoryActivity:
                        return true;
                    case R.id.mainActivity:
                        QuickResponseCodeHistoryActivity.this.startActivity(new Intent(QuickResponseCodeHistoryActivity.this, MainActivity.class));
                        overridePendingTransition(0,0);
                        break;
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

    private void initUiElements(){
        qrHistory = findViewById(R.id.qrHistoryRecView);
        bottomNavigationView = findViewById(R.id.mainNavigationMenu);
        emptyHistory = findViewById(R.id.emptyHistoryBannerLinLay);
        startScan = findViewById(R.id.startScanFromHistoryBtn);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.qr_history_action_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.deleteQrHistory:
                if (adapter.getItemCount() != 0) {
                    clearQrHistory();
                    break;
                }
                Toast.makeText(this, "История сканирования пуста", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void editActionBar(){
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#0065b1\">" + "История" + "</font>"));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.gos_white)));
    }

    // procedure shows empty history banner
    private void showEmptyHistoryBanner(){

        if (adapter.getItemCount() == 0) {
            qrHistory.setVisibility(View.GONE);
            emptyHistory.setVisibility(View.VISIBLE);
            return;
        }
        emptyHistory.setVisibility(View.GONE);
        qrHistory.setVisibility(View.VISIBLE);

    }

    private void addScannedHistory(){

        String history = historyFileInputOutput.readFile();

        if (!history.isEmpty()) {
            //QuickResponseCodeHistoryRecViewAdapter adapter = new QuickResponseCodeHistoryRecViewAdapter(this/*, this*/);
            historyFileParser.convertHistoryToArrayList(history);
            historyFileParser.sortArrayByTime();
            adapter.setQuickResponseCodeHistoryItemArrayList(historyFileParser.getQuickResponseCodeHistoryItemArrayList());
            qrHistory.setAdapter(adapter);
            qrHistory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        }
    }

    // procedure to clear qr history from file
    private void clearQrHistory(){

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Очистить историю")
                .setMessage("Вы действительно хотите очистить историю сканирования?")
                .setPositiveButton("Очистить", null)
                .setNegativeButton("Отмена", null)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        showEmptyHistoryBanner();
                    }
                }).show();


        Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adapter.getItemCount() != 0){
                    historyFileInputOutput.clearFile();
                    historyFileParser.getQuickResponseCodeHistoryItemArrayList().clear();
                    adapter.clear();
                    adapter.notifyDataSetChanged();
                    addScannedHistory();
                }
                Toast.makeText(QuickResponseCodeHistoryActivity.this,
                        "История сканирования очищена", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

    }

}