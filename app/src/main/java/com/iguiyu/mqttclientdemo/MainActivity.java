package com.iguiyu.mqttclientdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.mqtt.client.Callback;
import org.fusesource.mqtt.client.CallbackConnection;
import org.fusesource.mqtt.client.FutureConnection;
import org.fusesource.mqtt.client.Listener;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

import java.net.URISyntaxException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

public class MainActivity extends AppCompatActivity {

    private MQTTConnectUsecase mMQTTConnectUsecase;
    private MQTTReceiveUsecase mMQTTReceiveUsecase;
    private MQTTPublishUsecase mMQTTPublishUsecase;

    private Subscription mMQTTConnectSubscription;
    private Subscription mMQTTReceiveSubscription;
    private Subscription mMQTTPublishSubscription;

    private MQTTClient mMQTTClient;

    @Bind(R.id.publish)
    EditText publish;

    @Bind(R.id.connect)
    Button connect;

    @Bind(R.id.received)
    TextView received;

    @Bind(R.id.publishbutton) Button publishButton;

    @Bind(R.id.errormessage)
    TextView errorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        publishButton.setEnabled(false);

        initMQTT();

    }

    private void addReceive() {
        Thread thread = new Thread(new Receive());

        thread.start();
    }

    private class Receive implements Runnable{

        @Override
        public void run() {
            try {
                mMQTTReceiveSubscription = mMQTTReceiveUsecase.execute().subscribe(
                        message -> onReceiveMessage(message)
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void onReceiveMessage(Message message) {
        setMessage(new String(message.getPayload()));
        addReceive();
    }

    private void initMQTT() {
        try {
            mMQTTClient = new MQTTClient();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        mMQTTConnectUsecase = new MQTTConnectUsecase(mMQTTClient);
        mMQTTReceiveUsecase = new MQTTReceiveUsecase(mMQTTClient);
    }


    private void setMessage(String s) {
        received.setText(s);
    }

    @OnClick(R.id.publishbutton)
    public void publish(){
        mMQTTPublishUsecase = new MQTTPublishUsecase(
                MQTTClient.TOPIC,
                publish.getText().toString(),
                mMQTTClient
        );

        try {
            mMQTTPublishSubscription = mMQTTPublishUsecase.execute().subscribe(
                    aVoid -> onReceivePublish(aVoid),
                    throwable -> manageError(throwable)
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onReceivePublish(Void aVoid) {
        errorMessage.setText("已发送");
    }

    @OnClick(R.id.connect)
    public void connect(){
        try {
            mMQTTConnectSubscription = mMQTTConnectUsecase.execute().subscribe(
                    aVoid -> onReceiveVoid(aVoid),
                    throwable -> manageError(throwable)
            );
            addReceive();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void manageError(Throwable throwable) {
        errorMessage.setText(throwable.getMessage());
    }

    private void onReceiveVoid(Void aVoid) {
        publishButton.setEnabled(true);
        connect.setEnabled(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!mMQTTConnectSubscription.isUnsubscribed()){
            mMQTTConnectSubscription.unsubscribe();
        }
        if (!mMQTTPublishSubscription.isUnsubscribed()){
            mMQTTPublishSubscription.unsubscribe();
        }
        if (!mMQTTReceiveSubscription.isUnsubscribed()){
            mMQTTReceiveSubscription.unsubscribe();
        }
    }
}
