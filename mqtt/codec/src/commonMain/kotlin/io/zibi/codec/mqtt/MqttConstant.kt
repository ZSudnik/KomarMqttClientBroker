package io.zibi.codec.mqtt

object MqttConstant {
    /**
     * Default max bytes in message
     */
    const val DEFAULT_MAX_BYTES_IN_MESSAGE = 8092

    /**
     * min client id length
     */
    const val MIN_CLIENT_ID_LENGTH = 1

    /**
     * Default max client id length,In the mqtt3.1 protocol,
     * the default maximum Client Identifier length is 23
     */
    const val DEFAULT_MAX_CLIENT_ID_LENGTH = 23
}
