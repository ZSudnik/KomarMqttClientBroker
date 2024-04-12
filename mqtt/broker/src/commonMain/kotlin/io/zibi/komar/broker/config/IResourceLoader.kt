package io.zibi.komar.broker.config

import java.io.Reader

interface IResourceLoader {
    fun loadDefaultResource(): Reader?
    fun loadResource(relativePath: String): Reader?
    val name: String

    class ResourceIsDirectoryException(message: String?) : RuntimeException(message) {
        companion object {
            private const val serialVersionUID = 4564569229582764176L
        }
    }
}
