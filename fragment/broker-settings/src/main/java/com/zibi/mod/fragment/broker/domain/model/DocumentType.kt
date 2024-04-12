package com.zibi.mod.fragment.broker.domain.model

enum class DocumentType {
  ID_TYPE_1,
  ID_TYPE_2,
  ID_TYPE_3,
  ID_TYPE_4,
  ID_TYPE_5,
  ID_TYPE_6;

  companion object {
    fun fromString(value: String) =
      values().firstOrNull { documentType -> documentType.name == value } ?: throw Exception()
  }
}