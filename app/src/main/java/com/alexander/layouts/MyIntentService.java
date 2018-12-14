package com.alexander.layouts;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MyIntentService extends IntentService {

    private static final String STATUS = "Status";

    public static final int MSG_REGISTER_CLIENT = 1;
    public static final int MSG_UNREGISTER_CLIENT = 0;
    public static final int MSG_SET_VALUE = 2;


    private List<Messenger> mClients = new ArrayList<>();
    private Messenger mMessenger = new Messenger(new IncomingHandler());

    @Nullable
    @Override
    public IBinder onBind(Intent intent){
        return mMessenger.getBinder();
    }

    public MyIntentService() {
        super("MyIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        int i=0;
        while(true) {
            for (Messenger messenger: mClients){

                try {
                    messenger.send(Message.obtain(null, MSG_SET_VALUE, i));
                    sendBroadcast(String.valueOf(i*3));
                    i++;
                    TimeUnit.SECONDS.sleep(3);
                } catch (RemoteException | InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    }

    private void sendBroadcast(String status){
        Intent broadcastIntent = new Intent("com.alexander.SEND_MESSAGES_FILTER");
        broadcastIntent.putExtra(STATUS, status);
        broadcastIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        sendBroadcast(broadcastIntent);
    }

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_REGISTER_CLIENT:
                    mClients.add(msg.replyTo);
                    break;
                case MSG_UNREGISTER_CLIENT:
                    mClients.remove(msg.replyTo);
                    break;
                case MSG_SET_VALUE:
                    break;
            }
        }
    }
}
