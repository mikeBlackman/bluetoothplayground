package com.mikeblackman.bluetoothplayground.ui.main

import android.content.pm.PackageManager
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.mikeblackman.bluetoothplayground.ScanDevice
import com.mikeblackman.bluetoothplayground.SingleLiveEvent
import com.mikeblackman.bluetoothplayground.permissions.CheckRequiredPermissions
import com.mikeblackman.bluetoothplayground.permissions.RequestPermissions
import kotlinx.coroutines.ExperimentalCoroutinesApi

class MainViewModel @ViewModelInject constructor(
    private val scanDevice: ScanDevice,
    private val checkRequiredPermissions: CheckRequiredPermissions,
    private val requestPermissions: RequestPermissions,
) : ViewModel() {

    val deviceList = ArrayList<BluetoothDeviceWrapper>()
    val permissionRequestEvent = SingleLiveEvent<Array<String>>()

    @ExperimentalCoroutinesApi
    var deviceLiveData: LiveData<BluetoothDeviceWrapper> =
        scanDevice.discoverDevices().asLiveData()

    init {
        if (checkRequiredPermissions.hasRequiredPermissions()) {
            scanDevice.startDiscovery()
        } else {
            permissionRequestEvent.value = requestPermissions.getPermissionToRequest()
        }
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            // User has declined. Display message and request again.
            permissionRequestEvent.value = permissions
        } else {
            if (checkRequiredPermissions.hasRequiredPermissions()) {
                scanDevice.startDiscovery()
            } else {
                permissionRequestEvent.value = requestPermissions.getPermissionToRequest()
            }
        }
    }

    fun saveDevice(device: BluetoothDeviceWrapper) {
        if (!deviceList.contains(device)) {
            deviceList.add(device)
        }
    }

    fun deviceSelected() {
        scanDevice.stopDiscovery()
    }

}
