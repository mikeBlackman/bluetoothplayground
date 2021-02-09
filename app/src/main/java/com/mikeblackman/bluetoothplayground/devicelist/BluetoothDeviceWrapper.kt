package com.mikeblackman.bluetoothplayground.devicelist

import android.bluetooth.BluetoothDevice
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class BluetoothDeviceWrapper(val bluetoothDevice: BluetoothDevice, val rssi: Int) :
    Parcelable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as BluetoothDeviceWrapper?
        return bluetoothDevice == that!!.bluetoothDevice
    }

    override fun hashCode(): Int {
        return Objects.hash(bluetoothDevice)
    }

    enum class DeviceType(val value: Int) {
        DEVICE_TYPE_UNKNOWN(0), DEVICE_TYPE_CLASSIC(1), DEVICE_TYPE_LE(2), DEVICE_TYPE_DUAL(3);

        companion object {
            fun from(findValue: Int): DeviceType = values().first { it.value == findValue }
        }
    }

    fun getUUIDs(): String {
        val uuidList = this.bluetoothDevice.uuids
        var output = ""
        if (uuidList != null) {
            val list = uuidList.toList()
            list.forEach {
                output += it.toString() + "\n"
            }
        }
        return output
    }

    fun getType(): String {
        return when (DeviceType.from(this.bluetoothDevice.type)) {
            DeviceType.DEVICE_TYPE_UNKNOWN -> {
                "Unknown"
            }
            DeviceType.DEVICE_TYPE_CLASSIC -> {
                "Bluetooth Classic Only"
            }
            DeviceType.DEVICE_TYPE_DUAL -> {
                "Bluetooth Classic & LE (Dual Mode)"
            }
            DeviceType.DEVICE_TYPE_LE -> {
                "Bluetooth LE Only"
            }
        }
    }

}
