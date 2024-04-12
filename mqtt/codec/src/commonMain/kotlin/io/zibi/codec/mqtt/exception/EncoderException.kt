package io.zibi.codec.mqtt.exception

/**
 * An [io.netty.handler.codec.CodecException] which is thrown by an encoder.
 */
class EncoderException : CodecException {
    /**
     * Creates a new instance.
     */
    constructor()

    /**
     * Creates a new instance.
     */
    constructor(message: String?, cause: Throwable?) : super(message, cause)

    /**
     * Creates a new instance.
     */
    constructor(message: String?) : super(message)

    /**
     * Creates a new instance.
     */
    constructor(cause: Throwable?) : super(cause)

    companion object {
        private const val serialVersionUID = -5086121160476476774L
    }
}
