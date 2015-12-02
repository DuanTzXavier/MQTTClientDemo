package com.iguiyu.mqttclientdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.fusesource.mqtt.client.Future;
import org.fusesource.mqtt.client.FutureConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

import java.net.URISyntaxException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.Subscription;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private MQTTConnectUsecase mMQTTConnectUsecase;
    private MQTTReceiveUsecase mMQTTReceiveUsecase;
    private MQTTPublishUsecase mMQTTPublishUsecase;

    private Subscription mMQTTConnectSubscription;
    private Subscription mMQTTReceiveSubscription;
    private Subscription mMQTTPublishSubscription;

    private ConnectableObservable<Message> mMQTTReceiveObservable;
    private ConnectableObservable<Future<Message>> mTestObservable;

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

        EventBus.getDefault().register(this);
        publishButton.setEnabled(false);

        initMQTT();
//        try {
//            creatObservable();
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
    }

    private void addReceive() {
        try {
            mMQTTReceiveSubscription = mMQTTReceiveUsecase.execute().subscribe(
                    message -> onReceiveMessage(message),
                    throwable -> manageError(throwable)
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private void testRecive(){
//        try {
//            mMQTTReceiveObservable = mMQTTReceiveUsecase.execute().publish();
//            mMQTTReceiveSubscription = mMQTTReceiveObservable.subscribe(
//                    message -> onReceiveMessage(message),
//                    throwable -> manageError(throwable)
//            );
//            mMQTTReceiveObservable.connect();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private class Receive implements Runnable{

        @Override
        public void run() {

        }
    }

    private void onReceiveMessage(Future<Message> message) {
        try {
            Message msg = message.await();
            String send = new String(msg.getPayload());
            msg.ack();
            EventBus.getDefault().post(new MessageEvent(send));
        } catch (Exception e) {
            e.printStackTrace();
        }

        addReceive();
//        testRecive();
    }

    public void onEventMainThread(MessageEvent event){
        setMessage(event.getMsg());
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
//            testRecive();
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

    private final static String BASEURL = "tcp://api.iguiyu.com:1883";
    public final static String TOPIC = "MYTOPIC";

    private FutureConnection mConnection;

    private void creatObservable() throws URISyntaxException {
        MQTT mqtt = new MQTT();
        mqtt.setClientId("xavier");
        mqtt.setHost(BASEURL);
        mConnection = mqtt.futureConnection();
        Topic[] topics = {new Topic(TOPIC, QoS.AT_LEAST_ONCE)};
        mConnection.subscribe(topics);

        try {
            mConnection.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mTestObservable = Observable.just(mConnection.receive()).publish();
        mTestObservable.subscribeOn(Schedulers.newThread()).observeOn(Schedulers.newThread()).subscribe(s -> {

            try {
                Message message = s.await();
                errorMessage.append(new String(message.getPayload()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    @OnClick(R.id.test)
    public void test(){
//        mTestObservable.connect();
    }
}
