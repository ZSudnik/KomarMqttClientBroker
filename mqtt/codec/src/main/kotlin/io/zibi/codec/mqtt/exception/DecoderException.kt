package io.zibi.codec.mqtt.exception

/**
 * An [CodecException] which is thrown by a decoder.
 */
open class DecoderException : CodecException {
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
        private const val serialVersionUID = 6926716840699621852L
    }
}
