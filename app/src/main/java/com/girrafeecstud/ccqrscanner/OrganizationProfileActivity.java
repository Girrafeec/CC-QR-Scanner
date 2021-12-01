package com.girrafeecstud.ccqrscanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class OrganizationProfileActivity extends AppCompatActivity {

    private long backPressedTime;

    private BottomNavigationView bottomNavigationView;

    private Toast backToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization_profile);

        editActionBar();
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
        bottomNavigationView = findViewById(R.id.mainNavigationMenu);
    }

    private void editActionBar(){
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#0065b1\">" + "Профиль" + "</font>"));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.gos_white)));
    }
}