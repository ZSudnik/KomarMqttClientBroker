package io.zibi.komar.broker.config

import org.slf4j.LoggerFactory
import java.io.InputStreamReader
import java.io.Reader
import kotlin.text.Charsets.UTF_8

class ClasspathResourceLoader(
    private val defaultResource: String = IConfig.DEFAULT_CONFIG,
    private val classLoader: ClassLoader? = Thread.currentThread().contextClassLoader
) : IResourceLoader {
    override fun loadDefaultResource(): Reader? {
        return loadResource(defaultResource)
    }

    override fun loadResource(relativePath: String): Reader? {
        LOG.info("Loading resource. RelativePath = {}.", relativePath)
        val inputStream = classLoader?.getResourceAsStream(relativePath)
        return if (inputStream != null) InputStreamReader(inputStream, UTF_8) else null
    }

    override val name: String
        get() = "classpath resource"

    companion object {
        private val LOG = LoggerFactory.getLogger(ClasspathResourceLoader::class.java)
    }
}
