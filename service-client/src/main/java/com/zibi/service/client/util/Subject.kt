package com.zibi.service.client.util


interface Subject {
    fun notifyObservers(isRunning: Boolean)
}