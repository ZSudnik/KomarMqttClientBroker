package com.zibi.mod.common.ui.toast

import android.content.Context
import com.zibi.mod.common.ui.utils.showToast

interface ToastManager {
  fun showToast(message: String)
}

class ToastManagerImpl(
  private val context: Context,
) : ToastManager {

  override fun showToast(message: String) = context.showToast(message)
}
