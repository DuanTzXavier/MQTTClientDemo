package com.iguiyu.mqttclientdemo;

import org.fusesource.mqtt.client.FutureConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

import java.net.URISyntaxException;

import rx.Observable;

/**
 * Created by duant_000 on 2015/11/30.
 */
public class MQTTClient implements MQTTInterface {
    private final static String BASEURL = "tcp://api.iguiyu.com:1883";
    public final static String TOPIC = "MYTOPIC";

    private final FutureConnection mConnection;

    public MQTTClient() throws URISyntaxException {
        MQTT mqtt = new MQTT();
        mqtt.setClientId("xavier");
        mqtt.setHost(BASEURL);
        mConnection = mqtt.futureConnection();
        Topic[] topics = {new Topic(TOPIC, QoS.AT_LEAST_ONCE)};
        mConnection.subscribe(topics);
    }


    @Override
    public Observable<Message> receive() throws Exception {

        return Observable.just(mConnection
                .receive()
                .await());
    }

    @Override
    public Observable<Void> connect() throws Exception {
        return Observable.just(mConnection
                .connect()
                .await());
    }

    @Override
    public Observable<Void> publish(String message, String topic) throws Exception {
        return Observable.just(mConnection
                .publish(topic, message.getBytes(), QoS.AT_LEAST_ONCE, false)
                .await());
    }
}
