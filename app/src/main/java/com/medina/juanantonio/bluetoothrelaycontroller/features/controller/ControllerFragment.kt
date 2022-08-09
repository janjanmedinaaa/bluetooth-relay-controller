package com.medina.juanantonio.bluetoothrelaycontroller.features.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import com.medina.juanantonio.bluetoothrelaycontroller.ble.IBluetoothLEManager
import com.medina.juanantonio.bluetoothrelaycontroller.common.extensions.setOnPressedListener
import com.medina.juanantonio.bluetoothrelaycontroller.common.utils.autoCleared
import com.medina.juanantonio.bluetoothrelaycontroller.data.commanders.JDY23Commander
import com.medina.juanantonio.bluetoothrelaycontroller.databinding.FragmentControllerBinding
import com.medina.juanantonio.bluetoothrelaycontroller.features.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ControllerFragment : Fragment() {

    private var binding: FragmentControllerBinding by autoCleared()
    private val viewModel: ControllerViewModel by viewModels()
    private val activityViewModel: MainViewModel by activityViewModels()

    @Inject
    lateinit var bluetoothLeManager: IBluetoothLEManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentControllerBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listenToSwitches()
        listenToButtons()
    }

    private fun listenToSwitches() {
        binding.switchOne.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) sendCommand(JDY23Commander.Command.ON_1)
            else sendCommand(JDY23Commander.Command.OFF_1)
        }

        binding.switchTwo.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) sendCommand(JDY23Commander.Command.ON_2)
            else sendCommand(JDY23Commander.Command.OFF_2)
        }

        binding.switchThree.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) sendCommand(JDY23Commander.Command.ON_3)
            else sendCommand(JDY23Commander.Command.OFF_3)
        }

        binding.switchFour.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) sendCommand(JDY23Commander.Command.ON_4)
            else sendCommand(JDY23Commander.Command.OFF_4)
        }

        binding.switchFive.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) sendCommand(JDY23Commander.Command.ON_5)
            else sendCommand(JDY23Commander.Command.OFF_5)
        }

        binding.switchSix.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) sendCommand(JDY23Commander.Command.ON_6)
            else sendCommand(JDY23Commander.Command.OFF_6)
        }

        binding.switchSeven.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) sendCommand(JDY23Commander.Command.ON_7)
            else sendCommand(JDY23Commander.Command.OFF_7)
        }

        binding.switchEight.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) sendCommand(JDY23Commander.Command.ON_8)
            else sendCommand(JDY23Commander.Command.OFF_8)
        }
    }

    private fun listenToButtons() {
        binding.buttonOne.setOnPressedListener { isPressed ->
            if (isPressed) sendCommand(JDY23Commander.Command.ON_1)
            else sendCommand(JDY23Commander.Command.OFF_1)
        }
        binding.buttonTwo.setOnPressedListener { isPressed ->
            if (isPressed) sendCommand(JDY23Commander.Command.ON_2)
            else sendCommand(JDY23Commander.Command.OFF_2)
        }
        binding.buttonThree.setOnPressedListener { isPressed ->
            if (isPressed) sendCommand(JDY23Commander.Command.ON_3)
            else sendCommand(JDY23Commander.Command.OFF_3)
        }
        binding.buttonFour.setOnPressedListener { isPressed ->
            if (isPressed) sendCommand(JDY23Commander.Command.ON_4)
            else sendCommand(JDY23Commander.Command.OFF_4)
        }
        binding.buttonFive.setOnPressedListener { isPressed ->
            if (isPressed) sendCommand(JDY23Commander.Command.ON_5)
            else sendCommand(JDY23Commander.Command.OFF_5)
        }
        binding.buttonSix.setOnPressedListener { isPressed ->
            if (isPressed) sendCommand(JDY23Commander.Command.ON_6)
            else sendCommand(JDY23Commander.Command.OFF_6)
        }
        binding.buttonSeven.setOnPressedListener { isPressed ->
            if (isPressed) sendCommand(JDY23Commander.Command.ON_7)
            else sendCommand(JDY23Commander.Command.OFF_7)
        }
        binding.buttonEight.setOnPressedListener { isPressed ->
            if (isPressed) sendCommand(JDY23Commander.Command.ON_8)
            else sendCommand(JDY23Commander.Command.OFF_8)
        }
    }

    private fun sendCommand(command: JDY23Commander.Command) {
        viewModel.viewModelScope.launch(Dispatchers.IO) {
            bluetoothLeManager.writeToDevice(
                activityViewModel.selectedDevice.macAddress,
                command.value
            )
        }
    }
}