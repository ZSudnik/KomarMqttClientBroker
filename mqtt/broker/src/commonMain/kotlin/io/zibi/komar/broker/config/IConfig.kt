package io.zibi.komar.broker.config

import io.zibi.komar.BrokerConstants.ALLOW_ANONYMOUS_PROPERTY_NAME
import io.zibi.komar.BrokerConstants.AUTHENTICATOR_CLASS_NAME
import io.zibi.komar.BrokerConstants.AUTHORIZATOR_CLASS_NAME
import io.zibi.komar.BrokerConstants.HOST
import io.zibi.komar.BrokerConstants.HOST_PROPERTY_NAME
import io.zibi.komar.BrokerConstants.PASSWORD_FILE_PROPERTY_NAME
import io.zibi.komar.BrokerConstants.PORT
import io.zibi.komar.BrokerConstants.PORT_PROPERTY_NAME


/**
 * Base interface for all configuration implementations (filesystem, memory or classpath)
 */
abstract class IConfig {
    abstract fun setProperty(name: String?, value: String?)

    /**
     * Same semantic of Properties
     *
     * @param name property name.
     * @return property value.
     */
    abstract fun getProperty(name: String?): String?

    /**
     * Same semantic of Properties
     *
     * @param name property name.
     * @param defaultValue default value to return in case the property doesn't exists.
     * @return property value.
     */
    abstract fun getProperty(name: String?, defaultValue: String?): String
    fun assignDefaults() {
        setProperty(PORT_PROPERTY_NAME, PORT.toString())
        setProperty(HOST_PROPERTY_NAME, HOST)
        // setProperty(WEB_SOCKET_PORT_PROPERTY_NAME, WEBSOCKET_PORT.toString());
        setProperty(PASSWORD_FILE_PROPERTY_NAME, "")
        // setProperty(PERSISTENT_STORE_PROPERTY_NAME, DEFAULT_PERSISTENT_PATH)
        setProperty(ALLOW_ANONYMOUS_PROPERTY_NAME, true.toString())
        setProperty(AUTHENTICATOR_CLASS_NAME, "")
        setProperty(AUTHORIZATOR_CLASS_NAME, "")
    }
    
    abstract val resourceLoader: IResourceLoader
    fun intProp(propertyName: String?, defaultValue: Int): Int {
        val propertyValue = getProperty(propertyName) ?: return defaultValue
        return propertyValue.toInt()
    }

    fun boolProp(propertyName: String?, defaultValue: Boolean): Boolean {
        val propertyValue = getProperty(propertyName) ?: return defaultValue
        return java.lang.Boolean.parseBoolean(propertyValue)
    }

    companion object {
        const val DEFAULT_CONFIG = "config/moquette.conf"
    }
}
