package com.mikeblackman.bluetoothplayground.devicelist

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mikeblackman.bluetoothplayground.R

class DeviceListAdapter(
    private val onItemClick: (BluetoothDevice) -> Unit
) : RecyclerView.Adapter<DeviceListAdapter.ViewHolder>() {

    private val devices: MutableList<BluetoothDeviceWrapper> = mutableListOf()
    private val onClickListener: View.OnClickListener = View.OnClickListener { v ->
        val item = v.tag as BluetoothDevice
        onItemClick(item)
    }

    fun addDevice(device: BluetoothDeviceWrapper) {
        if (!devices.contains(device)) {
            devices.add(device)
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.device_cell, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = devices[position]
        holder.macAddress.text = item.bluetoothDevice.address
        holder.name.text = item.bluetoothDevice.name ?: "Undefined"
        holder.rssi.text = item.rssi.toString()
        holder.mView.tag = item.bluetoothDevice
        holder.deviceType.text = item.getType()
        holder.uuids.text = item.getUUIDs()
        holder.mView.setOnClickListener(onClickListener)
        if (item.getUUIDs().isEmpty()) {
            holder.uuids.visibility = View.GONE
            holder.uuidsLabel.visibility = View.GONE
        } else {
            holder.uuids.visibility = View.VISIBLE
            holder.uuidsLabel.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int = devices.size

    class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val name: TextView = mView.findViewById(R.id.name)
        val macAddress: TextView = mView.findViewById(R.id.mac)
        val rssi: TextView = mView.findViewById(R.id.rssi)
        val deviceType: TextView = mView.findViewById(R.id.device_type)
        val uuids: TextView = mView.findViewById(R.id.uuids)
        val uuidsLabel: TextView = mView.findViewById(R.id.uuids_label)
    }
}
