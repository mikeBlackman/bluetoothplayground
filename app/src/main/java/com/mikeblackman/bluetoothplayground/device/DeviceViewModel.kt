package com.mikeblackman.bluetoothplayground.device

import android.bluetooth.BluetoothDevice
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mikeblackman.bluetoothplayground.BluetoothService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class DeviceViewModel @ViewModelInject constructor(
    private val bluetoothService: BluetoothService
) : ViewModel() {

    val response = MutableLiveData<String>()

    fun sendButtonClicked(device: BluetoothDevice, text: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val connectionResult = bluetoothService.connect(device)
            response.postValue(connectionResult.toString())
            //val packetResponse = bluetoothService.sendPacketForResponse(text.encodeToByteArray())
            //response.value = connectionResult.toString()
        }
    }

}
