package com.girrafeecstud.ccqrscanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class QuickResponseCodeHistoryActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_responce_code_history);

        initUiElements();

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

    private void initUiElements(){
        bottomNavigationView = findViewById(R.id.mainNavigationMenu);
    }
}