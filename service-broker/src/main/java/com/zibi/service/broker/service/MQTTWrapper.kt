package com.zibi.service.broker.service

import com.zibi.mod.data_store.preferences.BrokerProperties
import io.zibi.komar.BrokerConstants
import io.zibi.komar.broker.Server
import io.zibi.komar.broker.config.MemoryConfig
import io.zibi.komar.interception.InterceptHandler
import java.util.Properties


object MQTTWrapper {

    private var mqttBroker: Server? = null

    val clientsConnected: Int
        get() {
            return mqttBroker?.listConnectedClients()?.size ?: 0
        }

    fun startBroker(
        listener: MQTTListener,
        brokerProperties: BrokerProperties
    ) {
        mqttBroker = Server()
        val userHandlers: List<InterceptHandler> = listOf(listener)
        mqttBroker?.startServer(getMemoryConfig(brokerProperties), userHandlers)
    }

    fun stopBroker() {
        mqttBroker?.stopServer()
    }

    private fun getMemoryConfig(brokerProperties: BrokerProperties): MemoryConfig {
        val defaultProperties = Properties()

//        defaultProperties[IConfig.PERSISTENCE_ENABLED_PROPERTY_NAME] = false.toString()

        defaultProperties[BrokerConstants.PORT_PROPERTY_NAME] = brokerProperties.mqttPort.toString()
        defaultProperties[BrokerConstants.HOST_PROPERTY_NAME] = BrokerConstants.HOST
        defaultProperties[BrokerConstants.ALLOW_TASMOTA] = brokerProperties.allowTasmota

        if (brokerProperties.wsEnabled) {
            defaultProperties[BrokerConstants.WEB_SOCKET_PORT_PROPERTY_NAME] = brokerProperties.wsPort.toString()
            defaultProperties[BrokerConstants.WEB_SOCKET_PATH_PROPERTY_NAME] = brokerProperties.wsPath
        } else {
            defaultProperties[BrokerConstants.WEB_SOCKET_PORT_PROPERTY_NAME] = brokerProperties.wsPort
            defaultProperties[BrokerConstants.WEB_SOCKET_PATH_PROPERTY_NAME] = ""
        }

        if (brokerProperties.authEnabled) {
            defaultProperties[BrokerConstants.AUTHENTICATOR_CLASS_NAME] = BasicAuthenticator::class.java.canonicalName
            defaultProperties[BasicAuthenticator.USERNAME] = brokerProperties.userName
            defaultProperties[BasicAuthenticator.PASSWORD] = brokerProperties.password
            defaultProperties[BrokerConstants.ALLOW_ANONYMOUS_PROPERTY_NAME] = false.toString()
        } else {
            defaultProperties[BrokerConstants.ALLOW_ANONYMOUS_PROPERTY_NAME] = true.toString()
            defaultProperties[BrokerConstants.AUTHENTICATOR_CLASS_NAME] = ""
        }

        defaultProperties[BrokerConstants.METRICS_ENABLE_PROPERTY_NAME] = false.toString()

        return MemoryConfig(defaultProperties)
    }
}