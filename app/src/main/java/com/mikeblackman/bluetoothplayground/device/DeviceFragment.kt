package com.mikeblackman.bluetoothplayground.device

import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mikeblackman.bluetoothplayground.databinding.DeviceFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeviceFragment : Fragment() {

    private var _binding: DeviceFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DeviceViewModel by viewModels()

    private val device: BluetoothDevice by lazy {
        requireArguments()["device"] as BluetoothDevice
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DeviceFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.sendButton.setOnClickListener {
            if (!binding.textInput.text.toString().isEmpty()) {
                viewModel.sendButtonClicked(device, binding.textInput.text.toString())
                binding.textInput.text = null
            } else {
                binding.textInput.hint = "Enter a message to send"
            }
        }

        viewModel.response.observe(viewLifecycleOwner) {
            binding.output.text = it
        }
    }
}
