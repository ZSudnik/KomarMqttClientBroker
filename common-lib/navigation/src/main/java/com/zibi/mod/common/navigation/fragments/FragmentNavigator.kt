package com.zibi.mod.common.navigation.fragments

import androidx.fragment.app.Fragment

interface FragmentNavigator {

  fun navigate(
    fragment: Fragment,
    tag: String,
  )

  fun pop(tag: String)
}
