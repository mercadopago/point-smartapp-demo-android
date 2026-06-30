package com.mercadopago.android.point_smartapp_demo.app.view.bluetooth.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.mercadolibre.android.point_integration_sdk.nativesdk.bluetoothclient.provider.entities.BluetoothDeviceModel

internal class BluetoothDevicesAdapter(private val callback: (String) -> Unit) :
    ListAdapter<BluetoothDeviceModel, BluetoothDeviceViewHolder>(DiffCallback()) {

    internal fun addItemDevice(device: BluetoothDeviceModel) {
        val deviceList = currentList.toMutableList()
        deviceList.add(device)
        submitList(deviceList.toList())
    }

    internal fun updateItemDevice(device: BluetoothDeviceModel): Boolean {
        val deviceList = currentList.toMutableList()

        val index = deviceList.indexOf(device)

        if (index != NOT_FOUND) {
            deviceList[index] = device
            notifyItemChanged(index)
            return true
        }
        return false
    }

    internal fun removeItemDevice(device: BluetoothDeviceModel) {
        val deviceList = currentList.toMutableList()
        deviceList.remove(device)
        submitList(deviceList.toList())
    }

    internal fun removeAll() {
        val deviceList = currentList.toMutableList()
        deviceList.clear()
        submitList(deviceList.toList())
    }

    internal fun findDevice(bluetoothDevice: BluetoothDeviceModel) =
        currentList.find {
            it.address == bluetoothDevice.address
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = BluetoothDeviceViewHolder.from(parent)

    override fun onBindViewHolder(holder: BluetoothDeviceViewHolder, position: Int) {
        holder.render(currentList[position], callback)
    }

    private companion object {
        private const val NOT_FOUND = -1
    }
}

private class DiffCallback : DiffUtil.ItemCallback<BluetoothDeviceModel>() {
    override fun areItemsTheSame(oldItem: BluetoothDeviceModel, newItem: BluetoothDeviceModel): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: BluetoothDeviceModel, newItem: BluetoothDeviceModel): Boolean = oldItem == newItem
}
