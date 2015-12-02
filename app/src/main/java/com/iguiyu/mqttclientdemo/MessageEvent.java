package com.iguiyu.mqttclientdemo;

import org.fusesource.mqtt.client.Message;

/**
 * Created by duant_000 on 2015/12/2.
 */
public class MessageEvent {
    private String msg;

    public MessageEvent(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
