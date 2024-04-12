package io.zibi.komar.broker.config

import io.zibi.komar.broker.config.IResourceLoader.ResourceIsDirectoryException
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.io.Reader
import java.nio.charset.StandardCharsets
import java.nio.file.Files

class FileResourceLoader(
    private val defaultFile: File? = null,
    private val parentPath: String? = System.getProperty("zibi.komar.path", null)
) : IResourceLoader {


    override fun loadDefaultResource(): Reader {
        return defaultFile?.let { loadResource(it) }
            ?: throw IllegalArgumentException("Default file not set!")
    }

    override fun loadResource(relativePath: String): Reader? {
        return loadResource(File(parentPath, relativePath))
    }

    fun loadResource(f: File): Reader? {
        LOG.info("Loading file. Path = {}.", f.absolutePath)
        if (f.isDirectory) {
            LOG.error("The given file is a directory. Path = {}.", f.absolutePath)
            throw ResourceIsDirectoryException("File \"$f\" is a directory!")
        }
        return try {
            Files.newBufferedReader(f.toPath(), StandardCharsets.UTF_8)
        } catch (e: IOException) {
            LOG.error("The file does not exist. Path = {}.", f.absolutePath)
            null
        }
    }

    override val name: String
        get() = "file"

    companion object {
        private val LOG = LoggerFactory.getLogger(FileResourceLoader::class.java)
    }
}
