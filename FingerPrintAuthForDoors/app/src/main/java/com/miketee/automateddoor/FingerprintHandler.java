package com.miketee.automateddoor;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;

import android.os.Build;
import android.os.CancellationSignal;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Timer;
import java.util.TimerTask;


@TargetApi(Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

    private int status = 0;
    private Context context;
    private boolean door_state;
    DatabaseReference rootRef,servoOpen;

    public FingerprintHandler(Context context){

        this.context = context;

    }


    public void startAuth(FingerprintManager fingerprintManager, FingerprintManager.CryptoObject cryptoObject){

        CancellationSignal cancellationSignal = new CancellationSignal();
        fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, this, null);

    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {

        this.update("There was an Auth Error. " + errString, false);

    }

    /**
     * I Have Automated the text messaging
     * A message will be sent any time the the fingerprint auth fails.
     */
    @Override
    public void onAuthenticationFailed() {
//        String phone = "0501542707";
//        String msg = "Alert text";
//        try{
//            SmsManager smgr = SmsManager.getDefault();
//            smgr.sendTextMessage(phone,null,msg,null,null);
//            Toast.makeText(context, "SMS Sent Successfully", Toast.LENGTH_SHORT).show();
            this.update("Auth Failed. ", false);
//        }
//        catch (Exception e){
//           Toast.makeText(context, "SMS Failed to Send, Please try again", Toast.LENGTH_SHORT).show();
//        }

    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {

        this.update("Error: " + helpString, false);

    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        door_state = true;
        if(door_state){
            updateFirebase();
        }
        this.update("The door is opened now.", true);
    }

    private void updateFirebase() {
        status = 1;
        rootRef = FirebaseDatabase.getInstance().getReference();
        servoOpen = rootRef.child("servo_state");
        servoOpen.setValue(status);

        Timer delay = new Timer();
        delay.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                status = 0;
                servoOpen.setValue(status);
            }
        }, 20000, 20000);
    }

    private void update(String s, boolean b) {

        TextView paraLabel = (TextView) ((Activity)context).findViewById(R.id.paraLabel);
        ImageView imageView = (ImageView) ((Activity)context).findViewById(R.id.fingerprintImage);

        paraLabel.setText(s);

        if(b == false){

            paraLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));

        } else {
            paraLabel.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            imageView.setImageResource(R.mipmap.action_door_open);

        }

    }
}