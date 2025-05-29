package io.zibi.komar.broker.config

import java.util.Properties

/**
 * Configuration backed by memory.
 */
class MemoryConfig(properties: Properties) : IConfig() {
    private val m_properties = Properties()

    init {
        assignDefaults()
        for ((key, value) in properties) {
            m_properties[key] = value
        }
    }

    // private void createDefaults() {
    // m_properties.put(BrokerConstants.PORT_PROPERTY_NAME, Integer.toString(BrokerConstants.PORT));
    // m_properties.put(BrokerConstants.HOST_PROPERTY_NAME, BrokerConstants.HOST);
    // m_properties.put(BrokerConstants.WEB_SOCKET_PORT_PROPERTY_NAME,
    // Integer.toString(BrokerConstants.WEBSOCKET_PORT));
    // m_properties.put(BrokerConstants.PASSWORD_FILE_PROPERTY_NAME, "");
    // m_properties.put(BrokerConstants.PERSISTENT_STORE_PROPERTY_NAME,
    // BrokerConstants.DEFAULT_PERSISTENT_PATH);
    // m_properties.put(BrokerConstants.ALLOW_ANONYMOUS_PROPERTY_NAME, true);
    // m_properties.put(BrokerConstants.AUTHENTICATOR_CLASS_NAME, "");
    // m_properties.put(BrokerConstants.AUTHORIZATOR_CLASS_NAME, "");
    // }
    override fun setProperty(name: String?, value: String?) {
        m_properties.setProperty(name, value)
    }

    override fun getProperty(name: String?): String? {
        return m_properties.getProperty(name)
    }

    override fun getProperty(name: String?, defaultValue: String?): String {
        return m_properties.getProperty(name, defaultValue)
    }

    override val resourceLoader: IResourceLoader
        get() = FileResourceLoader()
}
