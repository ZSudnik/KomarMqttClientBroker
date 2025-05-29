package io.zibi.komar

import java.io.File

object BrokerConstants {
    const val INTERCEPT_HANDLER_PROPERTY_NAME = "intercept.handler"
    const val PERSISTENT_STORE_PROPERTY_NAME = "persistent_store"
    const val AUTOSAVE_INTERVAL_PROPERTY_NAME = "autosave_interval"
    const val PASSWORD_FILE_PROPERTY_NAME = "password_file"
    const val PORT_PROPERTY_NAME = "port"
    const val HOST_PROPERTY_NAME = "host"
    const val DEFAULT_HOST_ADDRESS = "127.0.0.1"
    const val DEFAULT_MOQUETTE_STORE_H2_DB_FILENAME = "komar_store.h2"
    val DEFAULT_PERSISTENT_PATH = ((System.getProperty("user.dir")?.plus(File.separator) ?: "")
            + DEFAULT_MOQUETTE_STORE_H2_DB_FILENAME)
    const val WEB_SOCKET_PORT_PROPERTY_NAME = "websocket_port"
    const val WSS_PORT_PROPERTY_NAME = "secure_websocket_port"
    const val WEB_SOCKET_PATH_PROPERTY_NAME = "websocket_path"
    const val WEB_SOCKET_MAX_FRAME_SIZE_PROPERTY_NAME = "websocket_max_frame_size"

    /**
     * Defines the SSL implementation to use, default to "JDK".
     */
    const val SSL_PROVIDER = "ssl_provider"
    const val SSL_PORT_PROPERTY_NAME = "ssl_port"
    const val JKS_PATH_PROPERTY_NAME = "jks_path"

    /** @see java.security.KeyStore.getInstance
     */
    const val KEY_STORE_TYPE = "key_store_type"
    const val KEY_STORE_PASSWORD_PROPERTY_NAME = "key_store_password"
    const val KEY_MANAGER_PASSWORD_PROPERTY_NAME = "key_manager_password"
    const val ALLOW_ANONYMOUS_PROPERTY_NAME = "allow_anonymous"
    const val REAUTHORIZE_SUBSCRIPTIONS_ON_CONNECT = "reauthorize_subscriptions_on_connect"
    const val ALLOW_ZERO_BYTE_CLIENT_ID_PROPERTY_NAME = "allow_zero_byte_client_id"
    const val ACL_FILE_PROPERTY_NAME = "acl_file"
    const val AUTHORIZATOR_CLASS_NAME = "authorizator_class"
    const val AUTHENTICATOR_CLASS_NAME = "authenticator_class"
    const val DB_AUTHENTICATOR_DRIVER = "authenticator.db.driver"
    const val DB_AUTHENTICATOR_URL = "authenticator.db.url"
    const val DB_AUTHENTICATOR_QUERY = "authenticator.db.query"
    const val DB_AUTHENTICATOR_DIGEST = "authenticator.db.digest"
    const val PORT = 1883
    const val WEBSOCKET_PORT = 8080
    const val WEBSOCKET_PATH = "/mqtt"
    const val DISABLED_PORT_BIND = "disabled"
    const val HOST = "0.0.0.0"
    const val NEED_CLIENT_AUTH = "need_client_auth"
    const val KOMAR_SO_BACKLOG_PROPERTY_NAME = "komar.so_backlog"
    const val KOMAR_SO_REUSEADDR_PROPERTY_NAME = "komar.so_reuseaddr"
    const val KOMAR_TCP_NODELAY_PROPERTY_NAME = "komar.tcp_nodelay"
    const val KOMAR_SO_KEEPALIVE_PROPERTY_NAME = "komar.so_keepalive"
    const val KOMAR_CHANNEL_TIMEOUT_SECONDS_PROPERTY_NAME = "komar.channel_timeout.seconds"
    const val KOMAR_MAX_BYTES_IN_MESSAGE = "komar.mqtt.message_size"
    const val IMMEDIATE_BUFFER_FLUSH_PROPERTY_NAME = "immediate_buffer_flush"
    const val METRICS_ENABLE_PROPERTY_NAME = "use_metrics"
    const val METRICS_LIBRATO_EMAIL_PROPERTY_NAME = "metrics.librato.email"
    const val METRICS_LIBRATO_TOKEN_PROPERTY_NAME = "metrics.librato.token"
    const val METRICS_LIBRATO_SOURCE_PROPERTY_NAME = "metrics.librato.source"
    const val BUGSNAG_ENABLE_PROPERTY_NAME = "use_bugsnag"
    const val BUGSNAG_TOKEN_PROPERTY_NAME = "bugsnag.token"
    const val STORAGE_CLASS_NAME = "storage_class"
    const val FLIGHT_BEFORE_RESEND_MS = 5000
    const val INFLIGHT_WINDOW_SIZE = 10
    const val ALLOW_TASMOTA = "allow_tasmota"

}
