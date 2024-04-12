package com.zibi.mod.common.permission

import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import com.zibi.mod.common.lifecycle.ActivityForResultManager
import com.zibi.mod.common.lifecycle.ActivityLifecycleConnector
import kotlin.collections.set

interface PermissionManagerActivityLifecycleConnector : ActivityLifecycleConnector

interface PermissionManager {
  fun checkPermission(permission: Permission): PermissionResult
  suspend fun requestPermission(permission: Permission): PermissionResult
}

class PermissionManagerImpl() : //@Inject
  ActivityForResultManager<Array<String>, Map<String, Boolean>>(),
  PermissionManager,
  PermissionManagerActivityLifecycleConnector {

  override val contract: ActivityResultContract<Array<String>, Map<String, Boolean>> =
    ActivityResultContracts.RequestMultiplePermissions()

  override suspend fun requestPermission(permission: Permission): PermissionResult {
    val result = launchForResult(permission.permissions)
    return givePermissionResultFromPermissionsGrantedMap(result)
  }

  override fun checkPermission(permission: Permission): PermissionResult {
    val permissions: MutableMap<String, Boolean> = mutableMapOf()
    permission.permissions.forEach {
      val isGranted = componentActivity?.checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED
      permissions[it] = isGranted
    }
    return givePermissionResultFromPermissionsGrantedMap(permissions)
  }

  private fun givePermissionResultFromPermissionsGrantedMap(
    permissions: Map<String, Boolean>
  ): PermissionResult {
    val permissionResultList: MutableList<PermissionResult> = mutableListOf()
    permissions.forEach {
      val activity = componentActivity ?: return PermissionResult.UnknownStatus
      val shouldShow = activity.shouldShowRequestPermissionRationale(it.key)

      val result = it.value
      when {
        result -> permissionResultList.add(PermissionResult.Granted)
        else -> {
          permissionResultList.add(PermissionResult.NotGranted(shouldShowRationale = shouldShow))
        }
      }
    }
    return when {
      permissionResultList.contains(PermissionResult.NotGranted(shouldShowRationale = false)) ||
        permissionResultList.isEmpty() ->
        PermissionResult.NotGranted(shouldShowRationale = false)
      permissionResultList.contains(PermissionResult.NotGranted(shouldShowRationale = true)) ->
        PermissionResult.NotGranted(shouldShowRationale = true)
      else -> PermissionResult.Granted
    }
  }
}
