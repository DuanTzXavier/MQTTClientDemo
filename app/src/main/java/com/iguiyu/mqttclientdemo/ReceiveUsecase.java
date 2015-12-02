package com.iguiyu.mqttclientdemo;

import rx.observables.ConnectableObservable;

/**
 * Created by duant_000 on 2015/12/1.
 */
public interface ReceiveUsecase<T> {
    ConnectableObservable<T> execute() throws Exception;
}
