package com.girrafeecstud.ccqrscanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.sql.Date;
import java.util.Comparator;

public class QuickResponseCodeHistoryActivity extends AppCompatActivity implements QuickResponseCodeHistoryRecViewAdapter.OnQrHistoryItemDropListener{

    private long backPressedTime;

    private BottomNavigationView bottomNavigationView;

    private RecyclerView qrHistory;

    private Toast backToast;

    private HistoryFileInputOutput historyFileInputOutput = new HistoryFileInputOutput(this);
    private HistoryFileParser historyFileParser = new HistoryFileParser();

    @Override
    public void onQrHistoryItemDropClick(int position) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_responce_code_history);

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
    }

    private void addScannedHistory(){

        String history = historyFileInputOutput.readFile();

        if (!history.isEmpty()) {
            QuickResponseCodeHistoryRecViewAdapter adapter = new QuickResponseCodeHistoryRecViewAdapter(this, this);
            historyFileParser.convertHistoryToArrayList(history);
            historyFileParser.sortArrayByTime();
            adapter.setQuickResponseCodeHistoryItemArrayList(historyFileParser.getQuickResponseCodeHistoryItemArrayList());
            qrHistory.setAdapter(adapter);
            qrHistory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        }
    }

}