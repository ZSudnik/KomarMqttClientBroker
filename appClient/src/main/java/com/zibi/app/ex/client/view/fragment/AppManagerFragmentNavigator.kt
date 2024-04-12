package com.zibi.app.ex.client.view.fragment

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.zibi.mod.common.navigation.fragments.FragmentNavigator
import com.zibi.app.ex.client.view.AppManager

class AppManagerFragmentNavigator(
  context: Context,
) : FragmentNavigator {

  private val activity: AppCompatActivity = (context as? AppCompatActivity)
    ?: throw IllegalArgumentException("Use FragmentNavigator on AppCompatActivity extension only.")

  override fun navigate(
    fragment: Fragment,
    tag: String
  ) {
    AppManager.showFragment(
      activity = activity,
      frag = fragment,
      tag = tag,
      addToBackStack = true,
      shouldNameTransaction = true,
      withSlideInAnimation = true,
    )
  }

  override fun pop(tag: String) =
    AppManager.removeFragmentByTag(
      activity = activity,
      tag = tag,
    )
}