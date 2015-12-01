package com.iguiyu.mqttclientdemo;

import rx.Observable;

/**
 * Created by duant_000 on 2015/12/1.
 */
public interface Usecase<T> {
    Observable<T> execute() throws Exception;
}
