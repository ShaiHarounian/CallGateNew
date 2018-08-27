package com.harounian.shai.callgate;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class CallActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "prefs";
    private static final String PREFS_KEY_PHONE_NUMBER = "prefsPhoneNumber";

    private String senderNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_call);

        if (getNumberFromPrefs().isEmpty() ||
                getNumberFromPrefs() == null ||
                getNumberFromPrefs().equals("No number defined")) {
        } else {
            call(getNumberFromPrefs());
        }
    }


    public void call(String phoneNumber) {

        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }


    public String getNumberFromPrefs() {
        return getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getString(
                PREFS_KEY_PHONE_NUMBER,
                "No number defined");//"No number defined" is the default value.
    }


}