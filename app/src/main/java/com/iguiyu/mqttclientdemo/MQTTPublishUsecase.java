package com.iguiyu.mqttclientdemo;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by duant_000 on 2015/12/1.
 */
public class MQTTPublishUsecase implements Usecase<Void> {
    private final String topic;
    private final String message;
    private final MQTTInterface mqttInterface;

    public MQTTPublishUsecase(String topic, String message, MQTTInterface mqttInterface) {
        this.topic = topic;
        this.message = message;
        this.mqttInterface = mqttInterface;
    }

    @Override
    public Observable<Void> execute() throws Exception {
        return mqttInterface.publish(message, topic)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
