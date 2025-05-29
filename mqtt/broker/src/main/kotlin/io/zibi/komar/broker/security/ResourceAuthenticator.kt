package io.zibi.komar.broker.security

import io.zibi.komar.broker.config.IResourceLoader
import io.zibi.komar.broker.config.IResourceLoader.ResourceIsDirectoryException
import org.apache.commons.codec.digest.DigestUtils
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.IOException
import java.io.Reader
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.ParseException

/**
 * Load user credentials from a text resource. Each line of the file is formatted as
 * "[username]:[sha256(password)]". The username mustn't contains : char.
 *
 * To encode your password from command line on Linux systems, you could use:
 *
 * <pre>
 * echo -n "yourpassword" | sha256sum
</pre> *
 *
 * NB -n is important because echo append a newline by default at the of string. -n avoid this
 * behaviour.
 */
open class ResourceAuthenticator(resourceLoader: IResourceLoader, resourceName: String) :
    IAuthenticator {
    private val m_identities: MutableMap<String, String> = HashMap()

    init {
        try {
            MessageDigest.getInstance("SHA-256")
        } catch (nsaex: NoSuchAlgorithmException) {
            LOG.error("Can't find SHA-256 for password encoding", nsaex)
            throw RuntimeException(nsaex)
        }
        LOG.info(String.format("Loading password %s %s", resourceLoader.name, resourceName))
        try {
            val reader = resourceLoader.loadResource(resourceName)
            if (reader == null) {
                LOG.warn(
                    String.format("Parsing not existing %s %s", resourceLoader.name, resourceName)
                )
            } else {
                parse(reader)
            }
        } catch (e: ResourceIsDirectoryException) {
            LOG.warn(String.format("Trying to parse directory %s", resourceName))
        } catch (pex: ParseException) {
            LOG.warn(
                String.format(
                    "Format error in parsing password %s %s",
                    resourceLoader.name,
                    resourceName
                ),
                pex
            )
        }
    }

    @Throws(ParseException::class)
    private fun parse(reader: Reader?) {
        if (reader == null) {
            return
        }
        val br = BufferedReader(reader)
        var line: String
        try {
            while (br.readLine().also { line = it } != null) {
                val commentMarker = line.indexOf('#')
                if (commentMarker != -1) {
                    if (commentMarker == 0) {
                        // skip its a comment
                        continue
                    } else {
                        // it's a malformed comment
                        throw ParseException(line, commentMarker)
                    }
                } else {
                    if (line.isEmpty() || line.matches("^\\s*$".toRegex())) {
                        // skip it's a black line
                        continue
                    }

                    // split till the first space
                    val delimiterIdx = line.indexOf(':')
                    val username = line.substring(0, delimiterIdx).trim { it <= ' ' }
                    val password = line.substring(delimiterIdx + 1).trim { it <= ' ' }
                    m_identities[username] = password
                }
            }
        } catch (ex: IOException) {
            throw ParseException("Failed to read", 1)
        }
    }

    override fun checkValid(clientId: String?, username: String?, password: ByteArray?): Boolean {
        if (username == null || password == null) {
            LOG.info("username or password was null")
            return false
        }
        val foundPwq = m_identities[username] ?: return false
        val encodedPasswd = DigestUtils.sha256Hex(password)
        return foundPwq == encodedPasswd
    }

    companion object {
        protected val LOG = LoggerFactory.getLogger(
            ResourceAuthenticator::class.java
        )
    }
}
