package com.example.controlapp;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/* H klash Messaging Service ylopoiiei to service pou kanei connect kai subscribe sto antistoixo
* requested topic. O logos pou epilexthike service gia th leitourgia auth, einai h diathrhsh twn
* dunatothtwn ths efarmoghs enw auth vrisketai sto paraskhnio.*/

public class MessagingService extends Service {

    /* Statheres */
    final static String ClientID = "AndroidClient";
    final static int MIN_DURATION = 1, MAX_DURATION = 60, DEFAULT_DURATION = 5;
    /* Oi statheres DURATION aforoun th megisth, thn elaxisth kai thn
    proepilegmenh diarkeia optikis h hxhtikhs eidopoihshs antistoixa. */

    private MQTT Handler; private Sound sound; private Flash flash;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate(){
        super.onCreate();
        /* Arxikopoiish xeiristwn twn diaforwn voithitikwn klasewn */
        sound = new Sound(getApplicationContext());
        flash = new Flash(getApplicationContext());
        Handler = null;
        registerReceiver(SettingsReceiver,new IntentFilter("SETSETTINGS"));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle credentials = intent.getExtras(); /* lipsi twn login credentials apo to MainActivity */
        MQTTClient(credentials.getString("SERVER"),credentials.getString("PORT"),ClientID,credentials.getString("USERNAME"),
                credentials.getString("PASSWORD"),credentials.getString("TOPIC"),credentials.getString("QOS"));
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(SettingsReceiver); //kleisimo tou BroadcastReceiver
        if(Handler != null) Handler.closeConnection(); //aposundesh apo ton MQTT Broker
        Toast.makeText(getApplicationContext(),"MQTT Client Shutdown",Toast.LENGTH_LONG).show();
    }

    /* O BroadcastReceiver, einai anagkaios prokeimenou na lamvanei to service entoles gia th
     * diaxeirish tou flash, tou hxou kai ths sundeshs, se opoiadhpote anagkaia stigmi. */
    private BroadcastReceiver SettingsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("SETTINGS");
            if(message.equals("STOP")) switchOffHardware(); //koumpi stop eidopoihsewn
            else if(message.equals("LOGOUT")) logout(); //koumpi logout/aposundeshs apo ton broker
            else if(message.equals("CHECKCONNECTION")) sendConnectionStatus(checkStatus());
            //elegxos an einai sundedemeno to service ston broker
        }
    };

    /* H parakatw sunarthsh, einai ypeuthini gia th sundesh tou service me to MQTT broker. */
    private void MQTTClient(String Server, String Port, String ClientID,
                            String Username, String Password, String Topic, String QOS){

        Handler = new MQTT(getApplicationContext(),Server,Port,ClientID,Username,Password,Topic,QOS);
        Handler.connect();
        Handler.setCallback(new MqttCallbackExtended() {

            /* Parakatw, kaleitai me intent to activity pou emfanizei tis entoles pou lifthikan
             * apo to topic. Epilexthike auth h sunarthsh, prokeimenou to DisplayMessageActivity, na
              * "anoigei" mono otan exoume epityxws syndethei me ton broker. Se periptwsh malista
              * epityxous syndeshs, emfanizetai kai antistoixo mhnuma. Antitheta, an h sundesh den
              * epitygxanetai, tote aplws den kaleitai pote, to antistoixo activity. Dystyxws, de
              * vrika dunatothta mesw twn libraries tou MQTT, na enimerwnw to xrhsth gia to logo tis
              * anepityxous sundeshs, gi auto kai den emfanizetai kapoio mhnuma sthn periptwsh ths.*/
            @Override
            public void connectComplete(boolean b, String s) {
                Toast.makeText(getApplicationContext(),"Connection Successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(),DisplayMessageActivity.class);
                startActivity(intent);
            }

            /* Sthn periptwsh opou xathei h sundesh, emfanizetai katallhlo mhnuma, metavainoume sthn
             mainActivity (login) kai ginetai prospatheia epanasundeshs, h opoia an einai epituxhs,
             automata odhgoumaste kai pali sto DisplayMessageActivity. */
            @Override
            public void connectionLost(Throwable throwable) {
                Toast.makeText(getApplicationContext(),"Connection Lost!", Toast.LENGTH_SHORT).show();
            }

            /* Otan lifthei mhnuma sto service, apo ton MQTT Broker, auto ermhneuetai kai energopoieitai
             * h antistoixh optiki h hxhtikh eidopoiish. Parallhla, me intent, enhmerwnetai katallhla,
              * kai to DisplayMessageActivity. */
            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                String[] Command = mqttMessage.toString().split(" ");
                Intent message = new Intent("GETDATA");
                parseCommand(Command,message);
                sendBroadcast(message);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
            }
        });
    }

    /* Voithitikes Sunarthseis */

    /* H parakatw sunarthsh, diavazei to mhnuma pou elave to service apo ton broker
     * kai pragmatopoiei thn antistoixh energeia. To egkyro mhnuma einai ths morfhs,
      * ENTOLH XX, opou XX einai pedio proairetiko, kai afora th diarkeia anaparagwghs
      * ths eidopoihshs.*/
    private void parseCommand(String[] Command, Intent Preview) {
        int Seconds;
        if(Command[0].equals("Sound")) {
            Seconds = parseSeconds(Command);
            Preview.putExtra("DATA", "Play sound for " + Seconds + " seconds!");
            sound.playSound(Seconds);
        } else if (Command[0].equals("Flash")) {
            Seconds = parseSeconds(Command);
            Preview.putExtra("DATA", "Turn on flashlight for " + Seconds + " seconds!");
            flash.openFlash(Seconds);
        } else if (Command[0].equals("Stop")) {
            if(Command.length == 1) Preview.putExtra("DATA", "Unknown Command");
            else if(Command[1].equals("Sound")) {
                sound.stopSound();
                Preview.putExtra("DATA", "Sound Stopped!");
            }
            else if(Command[1].equals("Flash")) {
                flash.closeFlash();
                Preview.putExtra("DATA", "Flashlight Closed!");
            }
            else if(Command[1].equals("All")) {
                sound.stopSound(); flash.closeFlash();
                Preview.putExtra("DATA", "Commands Stopped!");
            }
            else Preview.putExtra("DATA", "Unknown Command");
        }
        else Preview.putExtra("DATA", "Unknown Command");
    }

    /* H mikrh auth sunarthsh, elegxei to orisma "Diarkeia" pou proairetika sunodeuei kapoies entoles
    * kai to metatrepei apo string se int, h to aporriptei an einai mh egkyrhs morfhs. */
    private int parseSeconds(String[] Command) {
        int Seconds;
        if(Command.length >= 2)
        {
            try{
                Seconds = Integer.parseInt(Command[1]);
                if(Seconds < MIN_DURATION || Seconds > MAX_DURATION) Seconds = DEFAULT_DURATION;
            }catch(NumberFormatException e){
                Seconds = DEFAULT_DURATION;
            }
        }
        else Seconds = DEFAULT_DURATION;
        return Seconds;
    }

    /* Sunarthsh aposundeshs */
    private void logout() {
        switchOffHardware(); //apenergopoihsh eidopoihsewn
        if(Handler != null)
        {
            Handler.closeConnection();
            Handler = null;
        }
    }

    /* Sunarthsh apenergopoihshs hxhtikhs kai optikis eidopoiishs */
    private void switchOffHardware() {
        if(sound != null) sound.stopSound();
        if(flash != null) flash.closeFlash();
    }

    /* Sunarthsh elegxou ths katastashs sundeshs */
    private String checkStatus() {
        String connection;
        if(Handler != null && Handler.isConnected()) connection = "CONNECTED";
        else connection = "DISCONNECTED";
        return connection;
    }

    /* Apostolh, sto mainActivity, ths katastashs sundeshs, prokeimenou
    * na gnorizei an xreiazetai na epanalifthei i diadikasia tou login,
    * i na metavoume kateutheian sto DisplayMessageActivity. */
    private  void sendConnectionStatus(String connection) {
        Intent status = new Intent("GETCONNECTION");
        status.putExtra("CONNECTION",connection);
        sendBroadcast(status);
    }
}