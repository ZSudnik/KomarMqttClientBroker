package com.zibi.mod.common.error.data.model

import com.google.gson.annotations.SerializedName

sealed interface PayloadErrorDto {
  data class NewPayloadErrorDto(
    @SerializedName("code") val code: String?,
    @SerializedName("message") val message: String?,
    @SerializedName("action") val action: String?,
  ) : PayloadErrorDto

  data class OldPayloadErrorDtoV1(
    @SerializedName("errorCode") val errorCode: Int?,
    @SerializedName("error") val error: String?,
  ) : PayloadErrorDto

  data class OldPayloadErrorDtoV2(
    @SerializedName("code") val code: Int?,
    @SerializedName("description") val description: String?,
    @SerializedName("details") val details: String?,
    @SerializedName("source") val source: String?,
  ) : PayloadErrorDto
}
