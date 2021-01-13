package com.mikeblackman.bluetoothplayground

import android.bluetooth.BluetoothDevice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.supervisorScope
import java.util.*

/**
 * Helper function for printing the contents of a byteArray to the command line
 * Useful for debugging
 */
fun ByteArray.printByteArray(): String? {
    val sb = StringBuilder()
    sb.append("[ ")
    for (b in this) {
        sb.append(String.format("0x%02X ", b))
    }
    sb.append("]")
    return sb.toString()
}

/**
 * Extension function that connects to a device asynchronously
 * and returns a connected socket.
 */
suspend fun BluetoothDevice.connectAsClientAsync(uuid: UUID) =
    supervisorScope {
        coroutineScope {
            return@coroutineScope async(Dispatchers.IO) {
                val bluetoothSocket = createRfcommSocketToServiceRecord(uuid)
                bluetoothSocket.also { it.connect() }
            }
        }
    }