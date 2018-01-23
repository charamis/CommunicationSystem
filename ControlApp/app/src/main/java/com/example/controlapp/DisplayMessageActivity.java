package com.example.controlapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/*  H klash auth, afora thn emfanish twn entolwn pou lamvanontai apo ton Broker,
* kathws kai twn diaforwn koumpiwn-settings pou apaitountai. */

public class DisplayMessageActivity extends AppCompatActivity {

    private boolean doubleBackToExitPressedOnce = false; //gia na ylopoithei to double back button gia epistrofh
    private TextView receivedMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        Toolbar returnToolbar = (Toolbar) findViewById(R.id.return_toolbar);
        setSupportActionBar(returnToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        receivedMessages = findViewById(R.id.receivedMessages);
        receivedMessages.setText("Waiting Command");

        registerReceiver(CommandReceiver,new IntentFilter("GETDATA"));
    }

    private BroadcastReceiver CommandReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("DATA");
            receivedMessages.setText(message);
        }
    };

    public void stopNotifications(View view) {
        sendSetting("STOP");
        TextView warnings = findViewById(R.id.receivedMessages);
        warnings.setText("Commands Stopped!");
    }

    public void Logout(View view) {
        sendSetting("LOGOUT");
        Toast.makeText(getApplicationContext(),"Disconnected!",Toast.LENGTH_SHORT).show();
        finish(); //Epistrofh sto mainActivity
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            sendSetting("LOGOUT");
            Toast.makeText(this, "Disconnected!", Toast.LENGTH_SHORT).show();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press Back again to return", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                sendSetting("LOGOUT");
                Toast.makeText(getApplicationContext(),"Disconnected!", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(CommandReceiver);
    }

    /* Voithitikes Synarthseis */

    private void sendSetting(String msg) {
        //apostolh rythmisewn sto MessagingService
        Intent message = new Intent("SETSETTINGS");
        message.putExtra("SETTINGS",msg);
        sendBroadcast(message);
    }
}