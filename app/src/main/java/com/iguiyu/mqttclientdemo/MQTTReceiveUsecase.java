package com.iguiyu.mqttclientdemo;

import org.fusesource.mqtt.client.Future;
import org.fusesource.mqtt.client.Message;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by duant_000 on 2015/12/1.
 */
public class MQTTReceiveUsecase implements Usecase<Future<Message>> {
    private final MQTTInterface mqttInterface;

    public MQTTReceiveUsecase(MQTTInterface mqttInterface) {
        this.mqttInterface = mqttInterface;
    }

    @Override
    public Observable<Future<Message>> execute() throws Exception {
        return mqttInterface.receive()
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread());
    }
}
