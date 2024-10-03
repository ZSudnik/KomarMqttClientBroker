package com.zibi.service.client;

import com.zibi.service.client.Observer;

interface IClientService {
    /**
    *   control service
    */
    void publish(String topic, String message);
    void addObserver(Observer observer);
//    void onChangeConnection();
    void onConnected();
//    void onDisConnected();
}