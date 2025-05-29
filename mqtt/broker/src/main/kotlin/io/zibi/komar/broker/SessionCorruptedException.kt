package io.zibi.komar.broker

class SessionCorruptedException internal constructor(msg: String?) : RuntimeException(msg) {
    companion object {
        private const val serialVersionUID = 5848069213104389412L
    }
}
