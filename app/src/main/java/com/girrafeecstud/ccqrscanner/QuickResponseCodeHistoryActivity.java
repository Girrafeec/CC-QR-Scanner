package com.girrafeecstud.ccqrscanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class QuickResponseCodeHistoryActivity extends AppCompatActivity /*implements QuickResponseCodeHistoryRecViewAdapter.OnQrHistoryItemDropListener*/{

    private long backPressedTime;

    private BottomNavigationView bottomNavigationView;

    private RecyclerView qrHistory;

    private androidx.appcompat.widget.Toolbar toolbar;

    private Toast backToast;

    private HistoryFileInputOutput historyFileInputOutput = new HistoryFileInputOutput(this);
    private HistoryFileParser historyFileParser = new HistoryFileParser();
    private QuickResponseCodeHistoryRecViewAdapter adapter;
    /*
    @Override
    public void onQrHistoryItemDropClick(int position) {

    }
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_responce_code_history);

        editActionBar();
        initUiElements();

        addScannedHistory();

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

        backToast = Toast.makeText(this, "Нажмите ещё раз для выхода из приложения", Toast.LENGTH_SHORT);
        backToast.show();

        backPressedTime = System.currentTimeMillis();
    }

    private void initUiElements(){
        qrHistory = findViewById(R.id.qrHistoryRecView);
        bottomNavigationView = findViewById(R.id.mainNavigationMenu);
        //toolbar = findViewById(R.id.qrHIstoryItemToolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.qr_history_action_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.qrHistorySettings:
                break;
            case R.id.deleteQrHistory:
                clearQrHistory();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void editActionBar(){
        getSupportActionBar().setTitle("История сканирования");
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#0065b1\">" + "История сканирования" + "</font>"));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.gos_white)));
        //getSupportActionBar().men
    }

    private void addScannedHistory(){

        String history = historyFileInputOutput.readFile();

        if (!history.isEmpty()) {
            QuickResponseCodeHistoryRecViewAdapter adapter = new QuickResponseCodeHistoryRecViewAdapter(this/*, this*/);
            historyFileParser.convertHistoryToArrayList(history);
            historyFileParser.sortArrayByTime();
            adapter.setQuickResponseCodeHistoryItemArrayList(historyFileParser.getQuickResponseCodeHistoryItemArrayList());
            qrHistory.setAdapter(adapter);
            qrHistory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        }
    }

    // procedure to clear qr history from file
    private void clearQrHistory(){
        //TODO вылетело приложение
        if (adapter.getItemCount() != 0){
            //historyFileInputOutput.clearFile();
            //historyFileParser.getQuickResponseCodeHistoryItemArrayList().clear();
            //adapter.clear();
        }
    }
}