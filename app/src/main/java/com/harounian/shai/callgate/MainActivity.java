package com.harounian.shai.callgate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PHONE_CALL_PERMISSION = 1111;
    private static final int REQUEST_SMS_READ_PERMISSION = 1111;

    private static final int REQUEST_PICK_CONTACT_FOR_GATE_NUMBER = 2111;
    private final String LOG_TAG = this.getClass().getSimpleName();

    private static final String PREFS_NAME = "prefs";
    private static final String PREFS_KEY_PHONE_NUMBER = "prefsPhoneNumber";
    private static final String PREFS_KEY_KEYWORD = "prefsKeyword";

    TextInputEditText etGatePhoneNumber, etKeyword;
    Button btnCallGate, btnSendKeywordMessage;
    ImageButton btnPickGatePhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {


        btnCallGate = findViewById(R.id.btn_open_gate);
        btnCallGate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CallManual();
            }
        });

        etGatePhoneNumber = findViewById(R.id.et_gate_phone_number);
        etGatePhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i(LOG_TAG, "Gate phone number text changed");
                saveGateNumberToPrefs(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etKeyword = findViewById(R.id.et_open_keyword);
        etKeyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i(LOG_TAG, "Gate keyword text changed");
                saveKeywordToPrefs(s.toString());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnPickGatePhoneNumber = findViewById(R.id.btn_pick_gate_phone_number);
        btnPickGatePhoneNumber.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickContactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                pickContactIntent.setType(Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
                startActivityForResult(pickContactIntent, REQUEST_PICK_CONTACT_FOR_GATE_NUMBER);
            }
        });

        btnSendKeywordMessage = findViewById(R.id.btn_send_keyword_message);
        btnSendKeywordMessage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
//                shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "SUBJECT");

                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                        getString(R.string.share_keyword_message_1) + " " + etKeyword.getText() + "\n" + getString(R.string.share_keyword_message_2));

                Intent chooserIntent = Intent.createChooser(shareIntent, "Share with");
                chooserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(chooserIntent);

            }
        });


        setGateNumberFromPrefs();
        setKeywordFromPrefs();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        // Check which request we're responding to
        if (requestCode == REQUEST_PICK_CONTACT_FOR_GATE_NUMBER) {
            setPickedGateNumberResult(resultCode, data);

        }
    }

    private void setPickedGateNumberResult(int resultCode, Intent data) {
        // Make sure the request was successful
        if (resultCode == RESULT_OK) {
            // The user picked a contact.
            // Get the URI that points to the selected contact
            Uri contactUri = data.getData();
            // We only need the NUMBER column, because there will be only one row in the result
            String[] projection = {Phone.NUMBER, Phone.DISPLAY_NAME};

// Perform the query on the contact to get the NUMBER column
            // We don't need a selection or sort order (there's only one result for the given URI)
            // CAUTION: The query() method should be called from a separate thread to avoid blocking
            // your app's UI thread. (For simplicity of the sample, this code doesn't do that.)
            // Consider using CursorLoader to perform the query.
            Cursor cursor = getContentResolver()
                    .query(contactUri, projection, null, null, null);
            cursor.moveToFirst();

// Retrieve the contact's name from from the Display Name column
            int columnName = cursor.getColumnIndex(Phone.DISPLAY_NAME);
            String name = cursor.getString(columnName);

            // Retrieve the phone number from the NUMBER column
            int columnNumber = cursor.getColumnIndex(Phone.NUMBER);
            String number = cursor.getString(columnNumber);

            cursor.close();

            // Do something with the phone number...
            etGatePhoneNumber.setText(number);


        }
    }

    public void CallManual() {
        Log.i(LOG_TAG, "calling...");
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + etGatePhoneNumber.getText()));
        startActivity(intent);

    }

    public void saveGateNumberToPrefs(String phoneNumber) {
        Log.i(LOG_TAG, "Saving gate number to prefs");

        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString(PREFS_KEY_PHONE_NUMBER, phoneNumber);
        editor.apply();

    }

    public void setGateNumberFromPrefs() {
        Log.i(LOG_TAG, "setting gate number from prefs");
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String phoneNumber = prefs.getString(PREFS_KEY_PHONE_NUMBER, "");//"No number defined" is the default value.
        etGatePhoneNumber.setText(phoneNumber);
    }

    public void saveKeywordToPrefs(String Keyword) {
        Log.i(LOG_TAG, "Saving Gate keyword text to prefs");
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString(PREFS_KEY_KEYWORD, Keyword);
        editor.apply();

    }

    public void setKeywordFromPrefs() {
        Log.i(LOG_TAG, "Setting gate keyword from prefs");
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String phoneNumber = prefs.getString(PREFS_KEY_KEYWORD, "");//"No code defined" is the default value.
        etKeyword.setText(phoneNumber);
    }

}

