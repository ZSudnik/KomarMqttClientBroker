package com.zibi.mod.common.permission

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi

enum class Permission(val permissions: Array<String>) {
  EXTERNAL_STORAGE(
    listOf(
      Manifest.permission.READ_EXTERNAL_STORAGE,
      Manifest.permission.WRITE_EXTERNAL_STORAGE
    ).toTypedArray()
  ),
  @RequiresApi(Build.VERSION_CODES.S)
  BLUETOOTH(
    listOf(
      Manifest.permission.BLUETOOTH_CONNECT,
      Manifest.permission.BLUETOOTH_SCAN
    ).toTypedArray()
  ),

  @RequiresApi(Build.VERSION_CODES.S)
  BLUETOOTH_WITH_ADVERTISE(
    listOf(
      Manifest.permission.BLUETOOTH_ADVERTISE,
      Manifest.permission.BLUETOOTH_CONNECT,
      Manifest.permission.BLUETOOTH_SCAN
    ).toTypedArray()
  ),
  CAMERA(
    listOf(
      Manifest.permission.CAMERA
    ).toTypedArray()
  ),
  GPS(
    listOf(
      Manifest.permission.ACCESS_FINE_LOCATION,
      Manifest.permission.ACCESS_COARSE_LOCATION
    ).toTypedArray()
  ),
  NOTIFICATION(
    listOf(
      Manifest.permission.ACCESS_FINE_LOCATION,
    ).toTypedArray()
  )
}