package com.zibi.mod.fragment.start.login.model

sealed interface StartLoginData {
  data class Screen(
      val topMenuTitle: String,
      val labelUser: String,
      val hintUser: String,
      val labelPassword: String,
      val hintPassword: String,
      val bottomButtonText: String,
      val onBottomNavigationButton: (entryUser: String, entryPassword: String) -> Unit,
      val dialogTitle: String,
      val dialogContent: String,
      val dialogButtonLabel: String,
      val onDialogButtonClicked: () -> Unit,
  ) : StartLoginData
}
