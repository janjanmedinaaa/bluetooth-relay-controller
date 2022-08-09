package com.medina.juanantonio.bluetoothrelaycontroller.features

import androidx.lifecycle.ViewModel
import com.medina.juanantonio.bluetoothrelaycontroller.data.models.BleDevice
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(): ViewModel() {

    lateinit var selectedDevice: BleDevice
}