package io.zibi.komar.broker

import io.zibi.komar.BrokerConstants.ALLOW_ANONYMOUS_PROPERTY_NAME
import io.zibi.komar.BrokerConstants.ALLOW_ZERO_BYTE_CLIENT_ID_PROPERTY_NAME
import io.zibi.komar.BrokerConstants.IMMEDIATE_BUFFER_FLUSH_PROPERTY_NAME
import io.zibi.komar.BrokerConstants.REAUTHORIZE_SUBSCRIPTIONS_ON_CONNECT
import io.zibi.komar.broker.config.IConfig

data class BrokerConfiguration(
    val isAllowAnonymous: Boolean,
    val isAllowZeroByteClientId: Boolean,
    val isReauthorizeSubscriptionsOnConnect: Boolean,
    val isImmediateBufferFlush: Boolean,
) {

    constructor(props: IConfig) : this(
        isAllowAnonymous = props.boolProp(ALLOW_ANONYMOUS_PROPERTY_NAME, true),
        isAllowZeroByteClientId = props.boolProp(ALLOW_ZERO_BYTE_CLIENT_ID_PROPERTY_NAME, false),
        isReauthorizeSubscriptionsOnConnect = props.boolProp(REAUTHORIZE_SUBSCRIPTIONS_ON_CONNECT, false),
        isImmediateBufferFlush = props.boolProp(IMMEDIATE_BUFFER_FLUSH_PROPERTY_NAME, false),
    )

}
