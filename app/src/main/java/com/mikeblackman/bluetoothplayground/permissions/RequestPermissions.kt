package com.mikeblackman.bluetoothplayground.permissions

import android.Manifest
import android.content.Context
import androidx.core.content.PermissionChecker
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface RequestPermissions {

    /**
     * Permissions need to be requested from the fragment.
     * So this method just returns the permission to request
     */
    fun getPermissionToRequest(): Array<String>
}

class RequestPermissionsImpl @Inject constructor(@ApplicationContext private val context: Context) :
    RequestPermissions {

    override fun getPermissionToRequest(): Array<String> {
        return when {
            isPermissionDenied(Manifest.permission.ACCESS_COARSE_LOCATION) -> {
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION)
            }
            isPermissionDenied(Manifest.permission.BLUETOOTH_ADMIN) -> {
                arrayOf(Manifest.permission.BLUETOOTH_ADMIN)
            }
            isPermissionDenied(Manifest.permission.BLUETOOTH) -> {
                arrayOf(Manifest.permission.BLUETOOTH)
            }
            isPermissionDenied(Manifest.permission.READ_EXTERNAL_STORAGE)-> {
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            isPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)-> {
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
            else -> arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun isPermissionDenied(permission: String): Boolean {
        val result: Int = PermissionChecker.checkSelfPermission(context, permission)
        return result != PermissionChecker.PERMISSION_GRANTED
    }

}