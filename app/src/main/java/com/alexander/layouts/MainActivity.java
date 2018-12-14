package com.alexander.layouts;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView firstText;
    private TextView secondText;
    private TextView thirdText;
    private TextView fourthText;

    private Button mButton;

    private CustomBroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;

    private Messenger mService;

    final Messenger mMessenger = new Messenger(new IncomingHandler());



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        init();
        startService(new Intent(MainActivity.this, MyIntentService.class));
    }

    private void initViews(){

        firstText = findViewById(R.id.firstText);
        secondText = findViewById(R.id.secondText);
        thirdText = findViewById(R.id.thirdText);
        fourthText = findViewById(R.id.fourthText);
        mButton = findViewById(R.id.fi);
    }

    private void init() {

        mReceiver = new CustomBroadcastReceiver(new ViewCallback() {
            @Override
            public void onChanged(String status) {
                firstText.setText(status);
                thirdText.setText(status);
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) mButton.getLayoutParams();
                layoutParams.circleAngle+=10;
                mButton.setLayoutParams(layoutParams);

            }
        });

        mIntentFilter = new IntentFilter("com.alexander.SEND_MESSAGES_FILTER");
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            mService = new Messenger(service);
            Message message = Message.obtain(null,
                    MyIntentService.MSG_REGISTER_CLIENT);
            message.replyTo = mMessenger;
            try {
                mService.send(message);
            }
            catch (RemoteException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            mService = null;
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        bindService();
        registerReceiver(mReceiver, mIntentFilter, "com.alexander.SEND_MESSAGES_PERMISSION", null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unBindService();
        unregisterReceiver(mReceiver);
    }

    public void bindService(){

        bindService(new Intent(MainActivity.this, MyIntentService.class), mServiceConnection, Context.BIND_AUTO_CREATE);

    }
    public void unBindService(){
        Message msg = Message.obtain(null,
                MyIntentService. MSG_UNREGISTER_CLIENT);
        msg.replyTo = mMessenger;

        try {
            mService.send(msg);
        } catch (RemoteException e){
            e.printStackTrace();
        }

        unbindService(mServiceConnection);
    }

    private class IncomingHandler extends Handler {

        @Override
        public void handleMessage(Message message){
            super.handleMessage(message);
            switch (message.what){

                case MyIntentService.MSG_SET_VALUE:
                    secondText.setText(message.obj.toString());
                    fourthText.setText(message.obj.toString());
            }
        }

    }


}
