package io.zibi.codec.mqtt.exception

/**
 * An [Exception] which is thrown by a codec.
 */
open class CodecException : RuntimeException {
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
        private const val serialVersionUID = -1464830400709348473L
    }
}
