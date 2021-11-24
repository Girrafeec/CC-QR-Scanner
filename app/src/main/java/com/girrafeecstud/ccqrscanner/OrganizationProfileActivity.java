package com.girrafeecstud.ccqrscanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class OrganizationProfileActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization_profile);

        initUiElements();

        // Set mainActivity selected in bottom nav bar
        bottomNavigationView.setSelectedItemId(R.id.organizationProfileActivity);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.organizationProfileActivity:
                        return true;
                    case R.id.quickResponseCodeHistoryActivity:
                        OrganizationProfileActivity.this.startActivity(new Intent(OrganizationProfileActivity.this, QuickResponseCodeHistoryActivity.class));
                        overridePendingTransition(0,0);
                        break;
                    case R.id.mainActivity:
                        OrganizationProfileActivity.this.startActivity(new Intent(OrganizationProfileActivity.this, MainActivity.class));
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