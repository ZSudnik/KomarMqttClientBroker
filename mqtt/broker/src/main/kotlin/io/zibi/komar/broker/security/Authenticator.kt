package io.zibi.komar.broker.security

import io.zibi.komar.BrokerConstants
import io.zibi.komar.broker.Utils
import io.zibi.komar.broker.config.IConfig
import org.slf4j.LoggerFactory

object Authenticator{

    private val LOG = LoggerFactory.getLogger(Authenticator::class.java)

    fun init(props: IConfig): IAuthenticator {
    var authenticator: IAuthenticator? = null
    LOG.debug("Configuring MQTT authenticator")
    val authenticatorClassName = props.getProperty(BrokerConstants.AUTHENTICATOR_CLASS_NAME, "")
    if ( authenticatorClassName.isNotEmpty()) {
        authenticator = Utils.loadClass(
            authenticatorClassName,
            IAuthenticator::class.java,
            IConfig::class.java,
            props
        )
    }
    val resourceLoader = props.resourceLoader
    if (authenticator == null) {
        val passwdPath = props.getProperty(BrokerConstants.PASSWORD_FILE_PROPERTY_NAME, "")
        authenticator = if (passwdPath.isEmpty()) {
            AcceptAllAuthenticator()
        } else {
            ResourceAuthenticator(resourceLoader, passwdPath)
        }
        LOG.info("An {} authenticator instance will be used", authenticator.javaClass.name)
    }
    return authenticator
}

}