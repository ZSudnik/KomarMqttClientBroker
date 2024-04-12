package io.zibi.komar.broker.security

import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.Reader
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.text.ParseException
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Parses the acl configuration file. If a line starts with # it's comment. Blank lines are skipped.
 * The format is "topic [read|write|readwrite] {topic name}"
 */
object ACLFileParser {
    private val LOG = LoggerFactory.getLogger(ACLFileParser::class.java)

    /**
     * Parse the configuration from file.
     *
     * @param file
     * to parse
     * @return the collector of authorizations form reader passed into.
     * @throws ParseException
     * if the format is not compliant.
     */
    @Throws(ParseException::class)
    fun parse(file: File?): AuthorizationsCollector {
        if (file == null) {
            LOG.warn("parsing NULL file, so fallback on default configuration!")
            return AuthorizationsCollector.emptyImmutableCollector()
        }
        if (!file.exists()) {
            LOG.warn(
                String.format(
                    "parsing not existing file %s, so fallback on default configuration!",
                    file.absolutePath
                )
            )
            return AuthorizationsCollector.emptyImmutableCollector()
        }
        return try {
            val reader: Reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)
            parse(reader)
        } catch (fex: IOException) {
            LOG.warn(
                String.format(
                    "parsing not existing file %s, so fallback on default configuration!",
                    file.absolutePath
                ),
                fex
            )
            AuthorizationsCollector.emptyImmutableCollector()
        }
    }

    /**
     * Parse the ACL configuration file
     *
     * @param reader
     * to parse
     * @return the collector of authorizations form reader passed into.
     * @throws ParseException
     * if the format is not compliant.
     */
    @Throws(ParseException::class)
    fun parse(reader: Reader?): AuthorizationsCollector {
        if (reader == null) {
            // just log and return default properties
            LOG.warn("parsing NULL reader, so fallback on default configuration!")
            return AuthorizationsCollector.emptyImmutableCollector()
        }
        val br = BufferedReader(reader)
        var line: String
        val collector = AuthorizationsCollector()
        val emptyLine = Pattern.compile("^\\s*$")
        val commentLine = Pattern.compile("^#.*") // As spec, comment lines should start with '#'
        val invalidCommentLine = Pattern.compile("^\\s*#.*")
        // This pattern has a dependency on filtering `commentLine`.
        val endLineComment = Pattern.compile("^([\\w\\s\\/\\+]+#?)(\\s*#.*)$")
        var endLineCommentMatcher: Matcher
        try {
            while (br.readLine().also { line = it } != null) {
                if (line.isEmpty() || emptyLine.matcher(line).matches() || commentLine.matcher(line)
                        .matches()
                ) {
                    // skip it's a black line or comment
                    continue
                } else if (invalidCommentLine.matcher(line).matches()) {
                    // it's a malformed comment
                    val commentMarker = line.indexOf('#')
                    throw ParseException(line, commentMarker)
                }
                endLineCommentMatcher = endLineComment.matcher(line)
                if (endLineCommentMatcher.matches()) {
                    line = endLineCommentMatcher.group(1) ?: ""
                }
                collector.parse(line)
            }
        } catch (ex: IOException) {
            throw ParseException("Failed to read", 1)
        }
        return collector
    }
}
