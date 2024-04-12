package com.zibi.mod.common.resources

import android.content.res.Resources
import androidx.annotation.ArrayRes
import androidx.annotation.StringRes

interface StringResolver {
  fun getStringArray(@ArrayRes stringArrayId: Int): List<String>
  fun getString(@StringRes stringId: Int): String
  fun getString(
    @StringRes stringId: Int,
    vararg arg: Any
  ): String
}

class StringResolverImpl(
  private val resources: Resources
) : StringResolver {

  override fun getStringArray(stringArrayId: Int): List<String> =
    resources.getStringArray(stringArrayId).toList()

  override fun getString(stringId: Int): String =
    resources.getString(stringId)
  override fun getString(stringId: Int, vararg arg: Any): String =
    resources.getString(stringId, *arg)
}
