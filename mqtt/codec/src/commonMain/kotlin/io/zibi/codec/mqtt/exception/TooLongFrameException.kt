package io.zibi.codec.mqtt.exception

/**
 * An [io.netty.handler.codec.DecoderException] which is thrown when the length of the frame
 * decoded is greater than the allowed maximum.
 */
class TooLongFrameException : DecoderException {
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
        private const val serialVersionUID = -1995801950698951640L
    }
}
