package io.zibi.komar.broker

import io.ktor.network.sockets.Socket
import io.zibi.komar.broker.security.IAuthenticator
import io.zibi.codec.mqtt.MqttVersion

class MQTTConnectionFactory(
    private val brokerConfig: BrokerConfiguration,
    private val authenticator: IAuthenticator,
    private val sessionRegistry: SessionRegistry,
    private val postOffice: PostOffice
) {
      fun create( mqttVersion: MqttVersion, socket: Socket, writeChannel: suspend (ByteArray) -> Unit): MQTTConnection =
          MQTTConnection(
              writeChannel = writeChannel,
              brokerConfig = brokerConfig,
              authenticator = authenticator,
              sessionRegistry = sessionRegistry,
              postOffice = postOffice,
              mqttVersion = mqttVersion,
              socket = socket
          )
}
