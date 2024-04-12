package io.zibi.komar.broker.config

import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.Reader
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.text.ParseException
import java.util.Properties

/**
 * Mosquitto configuration parser.
 *
 * A line that at the very first has # is a comment Each line has key value format, where the
 * separator used it the space.
 */
internal class ConfigurationParser {
    val properties = Properties()

    /**
     * Parse the configuration from file.
     */
    @Throws(ParseException::class)
    fun parse(file: File?) {
        if (file == null) {
            LOG.warn("parsing NULL file, so fallback on default configuration!")
            return
        }
        if (!file.exists()) {
            LOG.warn(
                String.format(
                    "parsing not existing file %s, so fallback on default configuration!",
                    file.absolutePath
                )
            )
            return
        }
        try {
            val reader: Reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)
            parse(reader)
        } catch (fex: IOException) {
            LOG.warn(
                "parsing not existing file {}, fallback on default configuration!",
                file.absolutePath,
                fex
            )
        }
    }

    /**
     * Parse the configuration
     *
     * @throws ParseException
     * if the format is not compliant.
     */
    @Throws(ParseException::class)
    fun parse(reader: Reader?) {
        if (reader == null) {
            // just log and return default properties
            LOG.warn("parsing NULL reader, so fallback on default configuration!")
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
                    val delimiterIdx = line.indexOf(' ')
                    val key = line.substring(0, delimiterIdx).trim { it <= ' ' }
                    val value = line.substring(delimiterIdx).trim { it <= ' ' }
                    properties[key] = value
                }
            }
        } catch (ex: IOException) {
            throw ParseException("Failed to read", 1)
        } finally {
            try {
                reader.close()
            } catch (e: IOException) {
                // ignore
            }
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(ConfigurationParser::class.java)
    }
}
