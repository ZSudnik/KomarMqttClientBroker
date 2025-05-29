package io.zibi.codec.mqtt

/**
 * See [MQTTV3.1/publish](https://public.dhe.ibm.com/software/dw/webservices/ws-mqtt/mqtt-v3r1.html#publish)
 */
class MqttPublishMessage(
    private val mqttFixedHeader: MqttFixedHeader,
    variableHeader: MqttPublishVariableHeader?,
    payload: ByteArray?
) : MqttMessage(mqttFixedHeader, variableHeader, payload) {//, ByteBufHolder {

    override fun variableHeader(): MqttPublishVariableHeader {
        return super.variableHeader() as MqttPublishVariableHeader
    }

    override fun payload(): ByteArray {
        return content()
    }

    override fun toDecByteArray(mqttVersion: MqttVersion) = mqttFixedHeader.toDecByteArray() +
            remainingLength(
                variableHeader().toDecByteArray(mqttFixedHeader, mqttVersion) +
                        payload()
            )

    fun content(): ByteArray {
        return super.payload() as ByteArray
    }

    companion object{
        fun create(messageId: Int, topic: String, payload: ByteArray? = null, qosValue: Int = 1,
                   isRetain: Boolean = false, isDup: Boolean= false): MqttPublishMessage =
            MqttPublishMessage(
            MqttFixedHeader(MqttMessageType.PUBLISH, isDup, MqttQoS.valueOf(qosValue), isRetain, 0),
            MqttPublishVariableHeader(topic, messageId),
            payload,
        )
    }

//    override fun toString(): String {
//MqttPublishMessage[fixedHeader=MqttFixedHeader[messageType=PUBLISH, isDup=false, qosLevel=AT_LEAST_ONCE, isRetain=false, remainingLength=0], variableHeader=MqttPublishVariableHeader[topicName=cmnd/t_light2/HSBColor, packetId=2], payload=[B@15d6db7] payload: 356,64,100 3
//    }

//    fun copy(): MqttPublishMessage {
//        return content().clone()
//    }
//
//    fun duplicate(): MqttPublishMessage {
//        return replace(content().duplicate())
//    }

//    override fun retainedDuplicate(): MqttPublishMessage {
//        return replace(content().retainedDuplicate())
//    }
//
//    override fun replace(content: ByteArray): MqttPublishMessage {
//        return MqttPublishMessage(fixedHeader, variableHeader(), content)
//    }
//
//    override fun refCnt(): Int {
//        return content().refCnt()
//    }
//
//    override fun retain(): MqttPublishMessage {
//        content().retain()
//        return this
//    }
//
//    override fun retain(increment: Int): MqttPublishMessage {
//        content().retain(increment)
//        return this
//    }
//
//    override fun touch(): MqttPublishMessage {
//        content().touch()
//        return this
//    }
//
//    override fun touch(hint: Any): MqttPublishMessage {
//        content().touch(hint)
//        return this
//    }
//
//    override fun release(): Boolean {
//        return content().release()
//    }
//
//    override fun release(decrement: Int): Boolean {
//        return content().release(decrement)
//    }
}
