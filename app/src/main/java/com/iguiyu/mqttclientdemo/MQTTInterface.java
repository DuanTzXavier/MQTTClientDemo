package com.iguiyu.mqttclientdemo;

import org.fusesource.mqtt.client.Message;

import rx.Observable;

/**
 * Created by duant_000 on 2015/11/30.
 */
public interface MQTTInterface {

    Observable<Message> receive() throws Exception;

    Observable<Void> connect() throws Exception;

    Observable<Void> publish(String message, String topic) throws Exception;
}
