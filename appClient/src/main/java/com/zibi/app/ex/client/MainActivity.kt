package com.zibi.app.ex.client

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.zibi.app.ex.client.view.fragment.AppManagerFragmentNavigator
import com.zibi.app.ex.client.view.fragment.feature.PermissionAndroidFragment
import com.zibi.app.ex.client.view.fragment.feature.SettingFragment
import com.zibi.app.ex.client.view.fragment.feature.StartFragment
import com.zibi.mod.common.navigation.AppGlobalNavigationEvent
import com.zibi.mod.common.navigation.fragments.FragmentNavigator
import com.zibi.mod.common.navigation.global.GlobalNavigationEvent
import com.zibi.mod.common.navigation.global.GlobalNavigationEventHandler
import com.zibi.mod.common.navigation.global.GlobalNavigationManager
import com.zibi.service.client.service.MQTTService
import org.koin.android.ext.android.inject

const val REQUEST_PERMISSION_NOTIFICATION = 1159828952
const val REQUEST_PERMISSION_CAMERA = 1

class MainActivity : AppCompatActivity(), GlobalNavigationEventHandler {

  private var onBackPressedCallback: OnBackPressedCallback? = null

  private val fragmentNavigator: FragmentNavigator = AppManagerFragmentNavigator(this)
  private val globalNavigationManager : GlobalNavigationManager by inject()
//  @Inject
//  lateinit var activityLifecycleConnector: ActivityLifecycleConnector

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setTheme(androidx.appcompat.R.style.Theme_AppCompat_Light_NoActionBar)
    setContentView(R.layout.app_layout)
    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//    onActivityCreate(
//      this,
//      NavigationListener(isFeatureEnabledUseCase, globalNavigationManager),
//      ServerErrorHandler(), PublicCertificatesHandler(),
//      false
//    )

//    activityLifecycleConnector.connect(this)
    globalNavigationManager.register(this)
    initializeBackHandling()
    //////////// permission
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      if(checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED)
        globalNavigationManager.send(AppGlobalNavigationEvent.ToCheckPermission)
      else globalNavigationManager.send(AppGlobalNavigationEvent.ToStart)
    } else globalNavigationManager.send(AppGlobalNavigationEvent.ToStart)

  }

  override fun onStart() {
    super.onStart()
    //create DB during new installation
//    CoroutineScope(Dispatchers.IO).launch {
//      NotificationsProvider.retrieveData(applicationContext)
//    }
  }

  override fun onResume() {
    super.onResume()
    isActivityInForeground = true
  }

  override fun onPause() {
    isActivityInForeground = false
    super.onPause()
  }

  override fun onDestroy() {
    if (isFinishing) {
      globalNavigationManager.unregister(this)
      onBackPressedCallback?.remove()
      MQTTService.end(this)
    }
    super.onDestroy()
  }

  private fun initializeBackHandling() {
    onBackPressedCallback = object : OnBackPressedCallback(true) {
      override fun handleOnBackPressed() {
//        onBackPressed(this@MainActivity)
      }
    }
    onBackPressedDispatcher.addCallback(onBackPressedCallback!!)
  }

  companion object {
    var isActivityInForeground = true
      private set
  }

  /**
   * Activity shouldn't be an intent handler itself! Do not add here new conditions!
   * We do this only for backward compatibility reasons, to handle legacy code.
   */

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    when (requestCode) {
      REQUEST_PERMISSION_NOTIFICATION ->{
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          globalNavigationManager.send(AppGlobalNavigationEvent.ToStart)
        } else {
          finish()
          globalNavigationManager.send(AppGlobalNavigationEvent.ToCheckPermission)
        }
        return;
      }
    } }

  @RequiresApi(Build.VERSION_CODES.TIRAMISU)
  override fun handle(event: GlobalNavigationEvent): Boolean =
    when (event) {
      is AppGlobalNavigationEvent.ToCheckPermission -> {
        fragmentNavigator.navigate(
          fragment = PermissionAndroidFragment.newInstance(),
          tag = PermissionAndroidFragment.TAG_PERMISSION_ANDROID,
        )
        true
      }
      is AppGlobalNavigationEvent.ToStart -> {
        fragmentNavigator.navigate(
          fragment = StartFragment.newInstance(),
          tag = StartFragment.TAG_START,
        )
        true
      }
      is AppGlobalNavigationEvent.ToSetting -> {
        fragmentNavigator.navigate(
          fragment = SettingFragment.newInstance(),
          tag = SettingFragment.TAG_FRAGMENT_ONE,
        )
        true
      }
      else -> false
    }

  private fun registerFragmentLifecycleCallbacks() {
    supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, true)
  }

  private val fragmentLifecycleCallbacks = object : FragmentManager.FragmentLifecycleCallbacks() {
    override fun onFragmentAttached(
      fm: FragmentManager,
      f: Fragment,
      context: Context
    ) {
      super.onFragmentAttached(fm, f, context)
    }

    override fun onFragmentDetached(
      fm: FragmentManager,
      f: Fragment
    ) {
      super.onFragmentDetached(fm, f)
    }
  }

}