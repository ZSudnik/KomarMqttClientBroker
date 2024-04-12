package io.zibi.codec.mqtt

import io.zibi.codec.mqtt.exception.DecoderException


/**
 * A [MqttUnacceptableProtocolVersionException] which is thrown when
 * a CONNECT request contains unacceptable protocol version.
 */
class MqttUnacceptableProtocolVersionException : DecoderException {
    /**
     * Creates a new instance
     */
    constructor()

    /**
     * Creates a new instance
     */
    constructor(message: String?, cause: Throwable?) : super(message, cause)

    /**
     * Creates a new instance
     */
    constructor(message: String?) : super(message)

    /**
     * Creates a new instance
     */
    constructor(cause: Throwable?) : super(cause)

    companion object {
        private const val serialVersionUID = 4914652213232455749L
    }
}
