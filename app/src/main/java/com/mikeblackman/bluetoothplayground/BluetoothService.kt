package com.mikeblackman.bluetoothplayground

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import java.io.InputStream
import java.io.OutputStream
import java.util.*
import javax.inject.Inject

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
private suspend fun BluetoothDevice.connectAsClientAsync(uuid: UUID) =
    supervisorScope {
        coroutineScope {
            return@coroutineScope async(IO) {
                val bluetoothSocket = createRfcommSocketToServiceRecord(uuid)
                bluetoothSocket.also { it.connect() }
            }
        }
    }

interface BluetoothService {
    suspend fun connect(device: BluetoothDevice): Boolean
    suspend fun sendPacketForResponse(packet: ByteArray): ByteArray
    suspend fun sendPacketNoResponse(packet: ByteArray)
    fun isConnected(): Boolean
    fun disconnect()
}

class BluetoothServiceImpl @Inject constructor(
) : BluetoothService {

    companion object {
        val GUID_LAK: UUID = UUID.fromString("ABB03DDB-B693-4844-BC79-0E5A54058248")
    }

    private var connectionJob = Job()
    private var bluetoothSocket: BluetoothSocket? = null
    private var device: BluetoothDevice? = null
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null

    /// Example of a single thread context for co-routine
    // val context = newSingleThreadContext("BluetoothContext")

    override suspend fun connect(device: BluetoothDevice): Boolean {
        connectionJob = Job()
        this.device = device

        val state = CoroutineScope(IO + connectionJob).async(SupervisorJob(connectionJob)) {
            bluetoothSocket = device.connectAsClientAsync(GUID_LAK).await()
            inputStream = bluetoothSocket!!.inputStream
            outputStream = bluetoothSocket!!.outputStream
            bluetoothSocket?.isConnected ?: false
        }
        return state.await()
    }

    override suspend fun sendPacketForResponse(packet: ByteArray): ByteArray {
        outputStream?.write(packet)
        val resultBytes = supervisorScope { async { read(inputStream!!) } }
        return resultBytes.await().toTypedArray().toByteArray()
    }

    override suspend fun sendPacketNoResponse(packet: ByteArray) {
        outputStream?.write(packet)
    }

    override fun isConnected(): Boolean {
        return bluetoothSocket != null && bluetoothSocket!!.isConnected
    }

    override fun disconnect() {
        if (isConnected()) {
            inputStream?.close()
            outputStream?.close()
            bluetoothSocket?.close()
            connectionJob.cancel()
            device = null
        }
    }

    private fun read(inputStream: InputStream): MutableList<Byte> {
        /**
         * This function assumes data is delimited by 0xF0 bytes
         * This is will be different depending on protocol
         */
        val result = mutableListOf<Byte>()
        var readFirstByte = false
        var readLastByte = false

        while (!readLastByte) {
            val byte = inputStream.read().toByte()
            result.add(byte)
            if (byte == 0xF0.toByte() && !readFirstByte) {
                readFirstByte = true
            } else if (byte == 0xF0.toByte() && readFirstByte) {
                readLastByte = true
            }
        }
        return result
    }
}
