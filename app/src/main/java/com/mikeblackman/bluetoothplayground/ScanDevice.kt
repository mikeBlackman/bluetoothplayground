package com.mikeblackman.bluetoothplayground

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.mikeblackman.bluetoothplayground.devicelist.BluetoothDeviceWrapper
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import java.lang.Short.MIN_VALUE
import javax.inject.Inject

interface ScanDevice {
    fun discoverDevices(): Flow<BluetoothDeviceWrapper>
    fun startDiscovery()
    fun stopDiscovery()
}

class ScanDeviceImpl @Inject constructor(@ApplicationContext private val context: Context) :
    ScanDevice {

    override fun startDiscovery() {
        val bluetoothManager =
            context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        if (!bluetoothAdapter.isDiscovering) {
            bluetoothAdapter.startDiscovery()
        }
    }

    override fun stopDiscovery() {
        val bluetoothManager =
            context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        bluetoothAdapter.cancelDiscovery()
    }

    override fun discoverDevices(): Flow<BluetoothDeviceWrapper> =
        callbackFlow<BluetoothDeviceWrapper> {
            val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    if (BluetoothDevice.ACTION_FOUND == intent?.action) {
                        val device =
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE) as BluetoothDevice?
                        val rssi =
                            intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, MIN_VALUE).toInt()
                        if (device != null) {
                            offer(BluetoothDeviceWrapper(device, rssi))
                        }
                    }
                }
            }
            context.registerReceiver(receiver, filter)
            awaitClose {
                context.unregisterReceiver(receiver)
            }
        }.flowOn(Dispatchers.IO)
}
