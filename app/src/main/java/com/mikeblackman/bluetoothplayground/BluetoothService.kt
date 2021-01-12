package com.mikeblackman.bluetoothplayground

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import java.io.InputStream
import java.io.OutputStream
import java.util.*
import javax.inject.Inject


interface BluetoothService {
    suspend fun connect(device: BluetoothDevice): Boolean
    suspend fun sendPacketForResponse(packet : ByteArray) : ByteArray
    suspend fun writeNoResponse(packet : ByteArray)
    fun isConnected() : Boolean
    fun disconnect()
}

class BluetoothServiceImpl @Inject constructor(
    private val bluetoothAdapter: BluetoothAdapter,
    ) : BluetoothService {

    companion object {
        val GUID_LAK: UUID = UUID.fromString("ABB03DDB-B693-4844-BC79-0E5A54058248")
    }

    private var job = Job()
    private var bluetoothSocket: BluetoothSocket? = null
    private var device: BluetoothDevice? = null
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null

    /// Example of a single thread context for co-routine
    // val context = newSingleThreadContext("BluetoothContext")
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

    override suspend fun connect(device: BluetoothDevice): Boolean {

        job = Job()

        this.device = device

        val state = CoroutineScope(IO + job).async(SupervisorJob(job)) {

            bluetoothSocket = device.connectAsClientAsync(GUID_LAK).await()
            inputStream = bluetoothSocket!!.inputStream
            outputStream = bluetoothSocket!!.outputStream

            // val initialConnectionPacket = bluetoothPacketProvider.encodePacket(0x01, toolVersionPayload())
            // I explicitly want to block here as I don't want to read until this has finished
//             outputStream?.write(byteArrayOf())
//
//            val result = supervisorScope { async { read(inputStream!!) } }
//            result.await().toTypedArray().toByteArray()

            bluetoothSocket?.isConnected ?: false
        }
        return state.await()
    }

    override suspend fun sendPacketForResponse(packet : ByteArray) : ByteArray {
        outputStream?.write(packet)
        val resultBytes = supervisorScope { async { read(inputStream!!) } }
        return resultBytes.await().toTypedArray().toByteArray()
    }

    override suspend fun writeNoResponse(packet : ByteArray) {
        outputStream?.write(packet)
    }

    override fun isConnected(): Boolean {
        return bluetoothSocket != null && bluetoothSocket!!.isConnected
    }

    override fun disconnect() {
        if (isConnected()){
            inputStream?.close()
            outputStream?.close()
            bluetoothSocket?.close()
            job.cancel()
            device = null
        }
    }

    private fun read(inputStream: InputStream): MutableList<Byte> {

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

/**
 * Helper function for printing the contents of a byteArray to command line
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
