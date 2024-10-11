package com.zibi.service.client.util;


public interface Subject {
    void notifyObservers(boolean isRunning);
}