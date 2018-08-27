package com.harounian.shai.callgate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;

import static android.content.Context.MODE_PRIVATE;


public class SMSReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = "SMSReceiver";

    private static final String PREFS_NAME = "prefs";
    private static final String PREFS_KEY_KEYWORD = "prefsKeyword";

    Context context;
    String senderNumber;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        SmsMessage[] smsMessage = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        String messageBody = smsMessage[0].getMessageBody();

        // Retrieves a map of extended data from the intent.
        final Bundle extras = intent.getExtras();
        if (extras != null) {
            final Object[] pdusObj = (Object[]) extras.get("pdus");
            for (int i = 0; i < pdusObj.length; i++) {

                // read the sms:
                SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);

                //get the sender's address messageBody:
                senderNumber = currentMessage.getDisplayOriginatingAddress();

                //get the message's body
                messageBody = currentMessage.getDisplayMessageBody();
            }
        }

        if (messageBody.equals(getKeywordFromPrefs())) {
//        if ((ContextCompat.checkSelfPermission(context,
//                Manifest.permission.SEND_SMS) !=
//                PackageManager.PERMISSION_GRANTED) ||
//                (ContextCompat.checkSelfPermission(context,
//                        Manifest.permission.RECEIVE_SMS) !=
//                        PackageManager.PERMISSION_GRANTED)){
//            ActivityCompat.requestPermissions(MainActivity.this,
//                    new String[] {
//                    }, 1);
//                            Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS
            startCallActivity();
        }

    }

    public void startCallActivity() {
        Intent callIntent = new Intent(context, CallActivity.class);
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        callIntent.putExtra("senderNumber", senderNumber);
        context.startActivity(callIntent);
    }

    public String getKeywordFromPrefs() {
        return context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .getString(PREFS_KEY_KEYWORD, "");//"No code defined" is the default value.

    }

}
