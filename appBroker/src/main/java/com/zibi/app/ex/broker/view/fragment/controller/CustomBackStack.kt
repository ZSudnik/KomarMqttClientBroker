package com.zibi.app.ex.broker.view.fragment.controller

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.zibi.app.ex.broker.R
import com.zibi.app.ex.broker.view.fragment.Util
import com.zibi.app.ex.broker.view.fragment.feature.StartFragment

object CustomBackStack {
  private val backStack: LinkedHashMap<String, Fragment> = LinkedHashMap()

  @JvmStatic
  fun clearBackstack() {
    backStack.clear()
  }

  @JvmStatic
  fun addFragment(
    tag: String,
    fragment: Fragment
  ) {
    backStack[tag] = fragment
  }

  @JvmStatic
  fun removeFragmentByTag(tag: String) {
    backStack.remove(tag)
  }

  @JvmStatic
  fun getFragmentTagToReturn(activity: AppCompatActivity): String {
    val currentFragmentTag = Util.getCurrentFragmentTag(activity)
    val stackAsArray = backStack.keys.toTypedArray()
    if (stackAsArray.isEmpty()) {
      return String()
    } else if (stackAsArray.size == 1) {
      val fragmentTagToReturn = stackAsArray[stackAsArray.size - 1]
      return if (fragmentTagToReturn == currentFragmentTag) {
        String()
      } else {
        fragmentTagToReturn
      }
    } else {
      var fragmentTagToReturn = stackAsArray[stackAsArray.size - 1]
      return if (fragmentTagToReturn == currentFragmentTag) {
        fragmentTagToReturn = stackAsArray[stackAsArray.size - 2]
        removeFragmentByTag(stackAsArray[stackAsArray.size - 1])
        fragmentTagToReturn
      } else {
        fragmentTagToReturn
      }
    }
  }

  @JvmStatic
  fun navigateToFragment(
    activity: AppCompatActivity,
    fragmentTag: String
  ) {
    if (backStack[fragmentTag] != null) {
      Util.getCurrentFragment(activity)
        ?.let { activity.supportFragmentManager.beginTransaction().remove(it).commitNow() }
      val transaction = activity.supportFragmentManager.beginTransaction()
      transaction.replace(
        R.id.AppContent,
        backStack[fragmentTag]!!,
        fragmentTag
      )
      transaction.commit()
    } else {
      navigateHome(activity)
    }
  }

  @JvmStatic
  fun navigateHome(activity: Activity) {
    navigateHome(activity as AppCompatActivity)
  }

  @JvmStatic
  fun navigateHome(
    activity: AppCompatActivity,
  ) {
    val fragmentManager = activity.supportFragmentManager
    val homeFragment = fragmentManager.findFragmentByTag(StartFragment.TAG_START)
    clearBackstack()
    clearRealBackStack(activity)
    if (homeFragment != null) {
      navigate(
        homeFragment,
        StartFragment.TAG_START,
        activity
      )
    }
  }

  private fun navigate(
    fragment: Fragment,
    fragmentTag: String,
    activity: AppCompatActivity
  ) {
    try {

      val transaction = activity.supportFragmentManager.beginTransaction()
      transaction.replace(
        R.id.AppContent,
        fragment,
        fragmentTag
      )
      transaction.commit()
    } catch (e: Exception) {
      e.printStackTrace()
      // TODO: handle error
    }
  }

  private fun clearRealBackStack(activity: AppCompatActivity) {
    // Here we are clearing back stack fragment entries
    val backStackEntry: Int = activity.supportFragmentManager.backStackEntryCount

    if (backStackEntry > 0) {
      for (i in 0 until backStackEntry) {
        activity.supportFragmentManager.popBackStack()
      }
    }

    // Here we are removing all the fragment that are shown here
    if (activity.supportFragmentManager.fragments.size > 0) {
      for (item in activity.supportFragmentManager.fragments) {
        activity.supportFragmentManager.beginTransaction().remove(item).commit()
      }
    }
  }
}