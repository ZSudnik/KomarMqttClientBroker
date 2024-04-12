package io.zibi.komar.broker.config

import org.slf4j.LoggerFactory
import java.io.Reader
import java.text.ParseException
import java.util.Properties

/**
 * Configuration that loads config stream from a [IResourceLoader] instance.
 */
class ResourceLoaderConfig(
    resourceLoader: IResourceLoader,
    configName: String? = null
) : IConfig() {
    private val m_properties: Properties
    override val resourceLoader: IResourceLoader

    init {
        LOG.info(
            "Loading configuration. ResourceLoader = {}, configName = {}.", resourceLoader.name, configName)
        this.resourceLoader = resourceLoader
        val confParser = ConfigurationParser()
        m_properties = confParser.properties

        /*
         * If we use a conditional operator, the loadResource() and the loadDefaultResource()
         * methods will be always called. This makes the log traces confusing.
         */
        val configReader: Reader? = if (configName != null) {
            resourceLoader.loadResource(configName)
        } else {
            resourceLoader.loadDefaultResource()
        }
        if (configReader == null) {
            LOG.error(
                "The resource loader returned no configuration reader. ResourceLoader = {}, configName = {}.", resourceLoader.name, configName)
            throw IllegalArgumentException("Can't locate " + resourceLoader.name + " \"" + configName + "\"")
        }
        LOG.info(
            "Parsing configuration properties. ResourceLoader = {}, configName = {}.", resourceLoader.name, configName)
        assignDefaults()
        try {
            confParser.parse(configReader)
        } catch (pex: ParseException) {
            LOG.warn(
                "Unable to parse configuration properties. Using default configuration. "
                        + "ResourceLoader = {}, configName = {}, cause = {}, errorMessage = {}.",
                resourceLoader.name,
                configName,
                pex.cause,
                pex.message
            )
        }
    }

    override fun setProperty(name: String?, value: String?) {
        m_properties.setProperty(name, value)
    }

    override fun getProperty(name: String?): String? {
        return m_properties.getProperty(name)
    }

    override fun getProperty(name: String?, defaultValue: String?): String {
        return m_properties.getProperty(name, defaultValue)
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(ResourceLoaderConfig::class.java)
    }
}
