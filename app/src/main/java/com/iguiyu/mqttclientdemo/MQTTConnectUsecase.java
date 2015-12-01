package com.iguiyu.mqttclientdemo;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by duant_000 on 2015/12/1.
 */
public class MQTTConnectUsecase implements Usecase<Void>{
    private final MQTTInterface mqttInterface;

    public MQTTConnectUsecase(MQTTInterface mqttInterface) {
        this.mqttInterface = mqttInterface;
    }

    @Override
    public Observable<Void> execute() throws Exception {
        return mqttInterface.connect()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
