package com.medina.juanantonio.bluetoothrelaycontroller.features.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.medina.juanantonio.bluetoothrelaycontroller.R
import com.medina.juanantonio.bluetoothrelaycontroller.ble.BluetoothLEService
import com.medina.juanantonio.bluetoothrelaycontroller.ble.IBluetoothLEManager
import com.medina.juanantonio.bluetoothrelaycontroller.common.utils.autoCleared
import com.medina.juanantonio.bluetoothrelaycontroller.data.adapters.BluetoothDevicesAdapter
import com.medina.juanantonio.bluetoothrelaycontroller.data.models.BleDevice
import com.medina.juanantonio.bluetoothrelaycontroller.databinding.FragmentHomeBinding
import com.medina.juanantonio.bluetoothrelaycontroller.features.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(), BluetoothDevicesAdapter.BleDeviceListener {

    private var binding: FragmentHomeBinding by autoCleared()
    private val viewModel: HomeViewModel by viewModels()
    private val activityViewModel: MainViewModel by activityViewModels()

    private lateinit var bluetoothDevicesAdapter: BluetoothDevicesAdapter

    private val localBroadcastManager by lazy {
        LocalBroadcastManager.getInstance(requireContext())
    }

    private val bluetoothLeServiceReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            @Suppress("UNCHECKED_CAST")
            val scannedDevices = intent?.getSerializableExtra(
                BluetoothLEService.BLE_DEVICES
            ) as? HashMap<String, BleDevice>

            scannedDevices?.let {
                bluetoothDevicesAdapter.submitList(
                    it.map { (_, device) -> device }
                )
            }
        }
    }

    @Inject
    lateinit var bluetoothLeManager: IBluetoothLEManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bluetoothDevicesAdapter = BluetoothDevicesAdapter(this)
        binding.recyclerviewDeviceList.apply {
            adapter = bluetoothDevicesAdapter
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }

        localBroadcastManager.registerReceiver(
            bluetoothLeServiceReceiver,
            IntentFilter(BluetoothLEService.SCANNED_DEVICES)
        )

        if (bluetoothLeManager.scanning)
            bluetoothLeManager.refreshDeviceList()
    }

    override fun onDestroy() {
        localBroadcastManager.unregisterReceiver(bluetoothLeServiceReceiver)
        super.onDestroy()
    }

    override fun onDeviceClicked(item: BleDevice) {
        activityViewModel.selectedDevice = item
        findNavController().navigate(R.id.action_homeFragment_to_controllerFragment)
    }

    override fun onBluetoothClicked(item: BleDevice) {
        viewModel.viewModelScope.launch(Dispatchers.IO) {
            if (!item.isConnected) bluetoothLeManager.connectDevice(item.macAddress)
            else bluetoothLeManager.disconnectDevice(item.macAddress)
        }
    }
}