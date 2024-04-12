package com.zibi.mod.common.error

import com.zibi.mod.common.error.domain.PayloadErrorData

sealed interface DomainError {

  data class Business(
    val title: String,
    val primaryActionLabel: String,
    val secondaryActionLabel: String = "",
  ) : com.zibi.mod.common.error.DomainError

  object CertError : com.zibi.mod.common.error.DomainError

  sealed interface Network : com.zibi.mod.common.error.DomainError {

    object EmptyBody : com.zibi.mod.common.error.DomainError.Network
    object NotConnected : com.zibi.mod.common.error.DomainError.Network
    object Closed : com.zibi.mod.common.error.DomainError.Network
    object Cancelled : com.zibi.mod.common.error.DomainError.Network
    object Timeout : com.zibi.mod.common.error.DomainError.Network
    data class Http(
      val code: com.zibi.mod.common.error.DomainError.Network.Http.Code,
      val message: String,
      val data: PayloadErrorData?,
    ) : com.zibi.mod.common.error.DomainError.Network {

      enum class Code(val code: Int) {
        // Client error responses
        BAD_REQUEST(400),
        UNAUTHORIZED(401),
        PAYMENT_REQUIRED(402),
        FORBIDDEN(403),
        NOT_FOUND(404),
        METHOD_NOT_ALLOWED(405),
        NOT_ACCEPTABLE(406),
        PROXY_AUTHENTICATION_REQUIRED(407),
        REQUEST_TIMEOUT(408),
        CONFLICT(409),
        GONE(410),
        LENGTH_REQUIRED(411),
        PRECONDITION_FAILED(412),
        PAYLOAD_TOO_LARGE(413),
        URI_TOO_LONG(414),
        UNSUPPORTED_MEDIA_TYPE(415),
        RANGE_NOT_SATISFIABLE(416),
        EXPECTATION_FAILED(417),
        TEAPOT(418),
        MISDIRECTED_REQUEST(421),
        UNPROCESSABLE_CONTENT(422),
        LOCKED(423),
        FAILED_DEPENDENCY(424),
        TOO_EARLY(425),
        UPGRADE_REQUIRED(426),
        PRECONDITION_REQUIRED(428),
        TOO_MANY_REQUESTS(429),
        REQUEST_HEADER_FIELDS_TOO_LARGE(431),
        UNAVAILABLE_FOR_LEGAL_REASONS(451),

        // Server error responses
        INTERNAL_SERVER_ERROR(500),
        NOT_IMPLEMENTED(501),
        BAD_GATEWAY(502),
        SERVICE_UNAVAILBLE(503),
        GATEWAY_TIMEOUT(504),
        HTTP_VERSION_NOT_SUPPORTED(505),
        VARIANT_ALSO_NEGOTIATES(506),
        INSUFFICIENT_STORAGE(507),
        LOOP_DETECTED(508),
        NOT_EXTENDED(510),
        NETWORK_AUTHENTICATION_REQUIRED(511),

        // Fallback
        UNKNOWN(-1)
      }
    }
  }

  data class Unknown(val e: Exception) : com.zibi.mod.common.error.DomainError

  /**
   * Null error when network call has been handled internally by mediator.
   *
   * Do not handle this result additionally anywhere else.
   */
  object Handled : com.zibi.mod.common.error.DomainError
}
