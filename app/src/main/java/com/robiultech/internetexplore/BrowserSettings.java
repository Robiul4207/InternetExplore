package com.robiultech.internetexplore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class BrowserSettings extends AppCompatActivity {
    ImageView btnBackSetting;
    TextView Setting;
    // WebView webView;
    SwitchMaterial btnSwitchNotification,btnSwitchDayNightMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser_settings);
        btnBackSetting = findViewById(R.id.btnCancelSetting);
        btnSwitchDayNightMode = findViewById(R.id.btnSwitchDayNightMode);
        Setting =findViewById(R.id.Setting);
        //webView = findViewById(R.id.webView);
        btnSwitchNotification = findViewById(R.id.btnSwitchNotification);


        btnBackSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BrowserSettings.this.finish();

            }
        });
        btnSwitchDayNightMode.setChecked(false);
        btnSwitchDayNightMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Setting.setTextColor(getResources().getColor(R.color.white));
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    Setting.setTextColor(getResources().getColor(R.color.black));
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            }
        });
    }
}
