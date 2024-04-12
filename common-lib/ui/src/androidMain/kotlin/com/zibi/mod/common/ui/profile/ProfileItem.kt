package com.zibi.mod.common.ui.profile

data class ProfileItem(
  val name: String,
  val surname: String = "",
  val type: String = "",
  val profileType: ProfileType,
  val showArrowDownIcon: Boolean = false,
  val showUserIcon: Boolean = true,
)

enum class ProfileType {
  ACTIVE, INACTIVE, UNVERIFIED
}