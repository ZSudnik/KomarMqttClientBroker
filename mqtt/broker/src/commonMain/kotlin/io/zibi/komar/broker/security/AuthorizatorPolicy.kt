package io.zibi.komar.broker.security

import io.zibi.komar.BrokerConstants
import io.zibi.komar.broker.Utils
import io.zibi.komar.broker.config.IConfig
import org.slf4j.LoggerFactory
import java.text.ParseException

object AuthorizatorPolicy {

    private val LOG = LoggerFactory.getLogger(AuthorizatorPolicy::class.java)

    fun init(
        props: IConfig
    ): IAuthorizatorPolicy {
        var authorizatorPolicy: IAuthorizatorPolicy? = null
        LOG.debug("Configuring MQTT authorizator policy")
        val authorizatorClassName = props.getProperty(BrokerConstants.AUTHORIZATOR_CLASS_NAME, "")
        if ( authorizatorClassName.isNotEmpty()) {
            authorizatorPolicy = Utils.loadClass(
                authorizatorClassName,
                IAuthorizatorPolicy::class.java,
                IConfig::class.java,
                props
            )
        }
        if (authorizatorPolicy == null) {
            val aclFilePath = props.getProperty(BrokerConstants.ACL_FILE_PROPERTY_NAME, "")
            authorizatorPolicy = if ( aclFilePath.isNotEmpty()) {
                try {
                    LOG.info("Parsing ACL file. Path = {}", aclFilePath)
                    ACLFileParser.parse(props.resourceLoader.loadResource(aclFilePath))
                } catch (pex: ParseException) {
                    LOG.error("Unable to parse ACL file. path = {}", aclFilePath, pex)
                    DenyAllAuthorizatorPolicy()
                }
            } else {
                PermitAllAuthorizatorPolicy()
            }
            LOG.info(
                "Authorizator policy {} instance will be used", authorizatorPolicy.javaClass.name
            )
        }
        return authorizatorPolicy
    }

}