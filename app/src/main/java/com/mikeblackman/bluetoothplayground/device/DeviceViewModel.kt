package com.mikeblackman.bluetoothplayground.device

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mikeblackman.bluetoothplayground.BluetoothService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeviceViewModel @Inject constructor(
    private val bluetoothService: BluetoothService
) : ViewModel() {

    val response = MutableLiveData<String>()

    fun sendButtonClicked(device: BluetoothDevice, text: String) {
        CoroutineScope(Dispatchers.IO).launch {
            bluetoothService.sendPacketNoResponse("hello".toByteArray())
            response.postValue("Sent packet")
        }
            //val packetResponse = bluetoothService.sendPacketForResponse(text.encodeToByteArray())
            //response.value = connectionResult.toString()
        }

    fun onCreate(device: BluetoothDevice) {
        CoroutineScope(Dispatchers.IO).launch {
            val connectionResult = bluetoothService.connect(device)
        }
    }

    fun onDestroy() {
        bluetoothService.disconnect()
    }
}


