package com.medina.juanantonio.bluetoothrelaycontroller.data.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.medina.juanantonio.bluetoothrelaycontroller.R
import com.medina.juanantonio.bluetoothrelaycontroller.data.models.BleDevice
import com.medina.juanantonio.bluetoothrelaycontroller.databinding.ItemBluetoothDeviceBinding

class BluetoothDevicesAdapter(
    private val listener: BleDeviceListener
) : ListAdapter<BleDevice, BluetoothDevicesAdapter.BleDeviceViewHolder>(
    object : DiffUtil.ItemCallback<BleDevice>() {
        override fun areItemsTheSame(
            oldItem: BleDevice,
            newItem: BleDevice
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: BleDevice,
            newItem: BleDevice
        ): Boolean {
            return false
        }
    }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BleDeviceViewHolder {
        val binding = ItemBluetoothDeviceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return BleDeviceViewHolder(binding)
    }

    override fun getItemCount() = currentList.size

    override fun onBindViewHolder(holder: BleDeviceViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class BleDeviceViewHolder(
        private val binding: ItemBluetoothDeviceBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: BleDevice) {
            binding.textViewDeviceName.text = item.getDeviceName()
            binding.textViewDeviceAddress.text = item.macAddress

            val drawable =
                if (item.isConnected) R.drawable.ic_bluetooth_connected
                else R.drawable.ic_bluetooth

            binding.imageViewBluetoothStatus.setImageDrawable(
                ResourcesCompat.getDrawable(
                    binding.root.context.resources,
                    drawable,
                    null
                )
            )

            binding.progressBarBluetoothLoading.isVisible = item.isDeviceLoading

            binding.root.setOnClickListener {
                if (item.isDeviceLoading) return@setOnClickListener
                listener.onDeviceClicked(item)
            }

            binding.imageViewBluetoothStatus.setOnClickListener {
                listener.onBluetoothClicked(item)
            }
        }
    }

    interface BleDeviceListener {
        fun onDeviceClicked(item: BleDevice)
        fun onBluetoothClicked(item: BleDevice)
    }
}
