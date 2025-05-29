package io.zibi.codec.mqtt

import io.zibi.codec.mqtt.exception.DecoderException

/**
 * A [MqttIdentifierRejectedException] which is thrown when a CONNECT request contains invalid client identifier.
 */
class MqttIdentifierRejectedException : DecoderException {
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
        private const val serialVersionUID = -1323503322689614981L
    }
}
