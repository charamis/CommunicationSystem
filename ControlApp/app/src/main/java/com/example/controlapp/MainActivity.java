package com.example.controlapp;

import android.content.*;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.view.Menu;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/* H klash auth afora thn ylopoihsh ths kyrias "othonis", dhladh
* ths formas eisodou kai sundeshs ston MQTT Broker, kathws kai
* twn diaforwn epilogwn (settings) pou sunodeuoun th diadikasia. */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);

        registerReceiver(ConnectionReceiver,new IntentFilter("GETCONNECTION"));
    }

    /** Called when the user taps the LOGIN button */
    public void sendCredentials(View view) {

        RadioGroup QOSGroup; RadioButton QOSButton;
        Intent intent = new Intent(this, MessagingService.class);
        //edw ekkinw to service kai tou stelnw ta credentials
        Bundle credentials = new Bundle();

        EditText editText = (EditText) findViewById(R.id.Server);
        credentials.putString("SERVER", editText.getText().toString());

        editText = findViewById(R.id.Port);
        credentials.putString("PORT", editText.getText().toString());

        editText = findViewById(R.id.username);
        credentials.putString("USERNAME", editText.getText().toString());

        editText = findViewById(R.id.password);
        credentials.putString("PASSWORD", editText.getText().toString());

        editText = findViewById(R.id.topic);
        credentials.putString("TOPIC", editText.getText().toString());

        QOSGroup = (RadioGroup) findViewById(R.id.radioGroup);
        QOSButton = (RadioButton) findViewById(QOSGroup.getCheckedRadioButtonId());
        credentials.putString("QOS", QOSButton.getText().toString());

        intent.putExtras(credentials);
        startService(intent);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options,menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Do you want to Exit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //if user pressed "yes", then he is allowed to exit from application
                dialog.dismiss();
                onFinish();
            }
        });
        builder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //if user select "No", just cancel this dialog and continue with app
                dialog.cancel();
            }
        });
        AlertDialog alert=builder.create();
        alert.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.option_exit:
                onFinish();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onFinish() {
        unregisterReceiver(ConnectionReceiver);
        stopService(new Intent(getBaseContext(),MessagingService.class));
        finish();
    }

    private BroadcastReceiver ConnectionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("CONNECTION"); //to string message periexei to status sundeshs
            if (message.equals("CONNECTED"))
            {
                //an einai true, phgaine me sto displaymessageactivity
                Intent display = new Intent(getApplicationContext(),DisplayMessageActivity.class);
                startActivity(display);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        sendSetting("CHECKCONNECTION"); //send to service
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        sendSetting("CHECKCONNECTION"); //send to service
    }

    /* Voithitikes Synarthseis */

    private void sendSetting(String msg) {
        //apostolh rythmisewn sto MessagingService
        Intent message = new Intent("SETSETTINGS");
        message.putExtra("SETTINGS",msg);
        sendBroadcast(message);
    }
}