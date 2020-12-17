package com.mikeblackman.bluetoothplayground.permissions

import android.Manifest
import android.content.Context
import androidx.core.content.PermissionChecker
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


interface CheckRequiredPermissions {
    fun hasRequiredPermissions(): Boolean
}

class CheckRequiredPermissionsImpl @Inject constructor(@ApplicationContext private val context: Context) :
    CheckRequiredPermissions {

    override fun hasRequiredPermissions(): Boolean {
        return isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION) &&
                isPermissionGranted(Manifest.permission.BLUETOOTH_ADMIN) &&
                isPermissionGranted(Manifest.permission.BLUETOOTH) &&
                isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION) &&
                isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE) &&
                isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    private fun isPermissionGranted(permission: String): Boolean {
        val result: Int = PermissionChecker.checkSelfPermission(context, permission)
        return result == PermissionChecker.PERMISSION_GRANTED
    }

}
