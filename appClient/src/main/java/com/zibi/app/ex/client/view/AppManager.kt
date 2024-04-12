package com.zibi.app.ex.client.view

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.zibi.app.ex.client.R
import com.zibi.app.ex.client.view.fragment.controller.CustomBackStack

object AppManager {
  private lateinit var activity: AppCompatActivity

  @JvmStatic
  fun showFragment(
    activity: Activity,
    frag: Fragment,
    tag: String,
    addToBackStack: Boolean,
    useExistingFragment: Boolean = false
  ) {
    showFragment(
      activity,
      frag,
      tag,
      addToBackStack,
      shouldNameTransaction = true,
      useExistingFragment = useExistingFragment
    )
    //        Handler(Looper.getMainLooper()).postDelayed({ restoreStatusbar(activity) }, 200)
  }

  @JvmStatic
  fun showFragment(
    frag: Fragment,
    tag: String,
    addToBackStack: Boolean
  ) {
    showFragment(
      activity,
      frag,
      tag,
      addToBackStack,
      shouldNameTransaction = true,
      withSlideInAnimation = false
    )
  }

  @JvmStatic
  fun showFragmentWithSlideInAnimation(
    activity: FragmentActivity,
    frag: Fragment,
    tag: String,
    addToBackStack: Boolean
  ) {
    showFragment(
      activity,
      frag,
      tag,
      addToBackStack,
      shouldNameTransaction = true,
      withSlideInAnimation = true
    )
  }

  /**
   * If shouldNameTransaction name is true, you are able to remove given fragment in the following quite convenient
   * and SILENT way:
   * activity.getSupportFragmentManager().popBackStack(givenFragmentTag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
   */
  @JvmStatic
  fun showFragment(
    activity: Activity,
    frag: Fragment,
    tag: String,
    addToBackStack: Boolean,
    shouldNameTransaction: Boolean,
    withSlideInAnimation: Boolean = false,
    useExistingFragment: Boolean = false,
  ) {
    try {


      val transaction = (activity as AppCompatActivity).supportFragmentManager.beginTransaction()
      if (frag.isAdded) {
        transaction.show(frag)
        return
      }

      if (withSlideInAnimation) {
        transaction.setCustomAnimations(
          R.animator.enter_from_right,
          R.animator.exit_to_left,
          R.animator.enter_from_left,
          R.animator.exit_to_right
        )
      }
      val fragment = if (useExistingFragment) {
        activity.supportFragmentManager.findFragmentByTag(tag) ?: frag
      } else {
        frag
      }
      transaction.replace(
        R.id.AppContent,
        fragment,
        tag
      )
      if (addToBackStack) {
        if (shouldNameTransaction) {
          transaction.addToBackStack(tag)
        } else {
          transaction.addToBackStack(null)
        }
      }
      CustomBackStack.addFragment(
        tag,
        frag
      )
      transaction.commit()
    } catch (e: Exception) {
      e.printStackTrace()
      // TODO: handle error
    }
  }

  @JvmStatic
  fun showFragmentWithAnimation(
    activity: AppCompatActivity?,
    frag: Fragment,
    tag: String,
    addToBackStack: Boolean
  ) {
    if (activity == null) return

    showFragmentWithAnimation(
      activity,
      R.id.AppContent,
      frag,
      tag,
      addToBackStack
    )
  }

  @JvmStatic
  fun showFragmentWithAnimation(
    activity: AppCompatActivity?,
    containerViewId: Int,
    frag: Fragment,
    tag: String,
    addToBackStack: Boolean,
  ) {
    if (activity == null) return

    showFragmentWithAnimation(
      activity,
      containerViewId,
      frag,
      tag,
      addToBackStack,
      activity.supportFragmentManager
    )
  }

  @JvmStatic
  fun showFragmentWithAnimation(
    activity: Activity?,
    containerViewId: Int,
    frag: Fragment,
    tag: String,
    addToBackStack: Boolean,
    fragmentManager: FragmentManager,
  ) {
    try {
      if (activity == null) return


      val transaction = fragmentManager.beginTransaction()
      transaction.setCustomAnimations(
        R.animator.enter_from_right,
        R.animator.exit_to_left,
        R.animator.enter_from_left,
        R.animator.exit_to_right
      )
      transaction.replace(
        containerViewId,
        frag,
        tag
      )
      if (addToBackStack) transaction.addToBackStack(null)

      transaction.commit()
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  @JvmStatic
  fun showFragmentWithFastSlideAnimation(
    activity: FragmentActivity?,
    frag: Fragment,
    tag: String,
    addToBackStack: Boolean
  ) {
    try {
      if (activity == null) return


      val fragmentManager = activity.supportFragmentManager
      val transaction = fragmentManager.beginTransaction()
      transaction.setCustomAnimations(
        R.animator.enter_from_right_fast,
        R.animator.exit_to_left_fast,
        R.animator.enter_from_left_fast,
        R.animator.exit_to_right_fast
      )
      transaction.replace(
        R.id.AppContent,
        frag,
        tag
      )
      if (addToBackStack) transaction.addToBackStack(null)

      transaction.commit()
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  @JvmStatic
  fun hideKeyboard(activity: Activity) {
    if (activity.currentFocus == null) {
      return
    }
    try {
      val inputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
      inputMethodManager.hideSoftInputFromWindow(
        activity.currentFocus?.windowToken,
        0
      )
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  @JvmStatic
  fun closeKeyboardFromFragment(
    activity: Activity,
    view: View
  ) { //        val view = binding.root.rootView
    val imm: InputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(
      view.windowToken,
      0
    )
  }

  @JvmStatic
  fun closeKeyboardFromFragment(
    activity: Activity,
    frag: Fragment
  ) {
    val view = frag.view?.rootView
    if (view != null) {
      closeKeyboardFromFragment(
        activity,
        view
      )
    } else {
      hideKeyboard(activity)
    }

  }

  @JvmStatic
  fun removeFragmentByTag(
    activity: AppCompatActivity,
    tag: String
  ) {
    try {
      val fm = activity.supportFragmentManager

      val frag = fm.findFragmentByTag(tag) ?: return

      fm.beginTransaction().remove(frag).commit()

      if (fm.backStackEntryCount > 0) {
        fm.popBackStackImmediate()
      }
    } catch (e: Exception) {
      // TODO: handle error
      e.printStackTrace()
    }
  }
}
