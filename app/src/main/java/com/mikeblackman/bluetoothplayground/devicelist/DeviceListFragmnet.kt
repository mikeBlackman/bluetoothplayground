package com.mikeblackman.bluetoothplayground.devicelist

import android.bluetooth.BluetoothDevice
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.mikeblackman.bluetoothplayground.R
import com.mikeblackman.bluetoothplayground.databinding.DeviceListFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeviceListFragmnet : Fragment() {

    private var _binding: DeviceListFragmentBinding? = null
    private val binding get() = _binding!!

    private var adapter: DeviceListAdapter? = null

    private val viewModel: DeviceListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DeviceListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding.deviceList) {
            adapter = DeviceListAdapter(::onItemClick)
            this@DeviceListFragmnet.adapter = adapter as DeviceListAdapter
        }

        val dividerItemDecoration = DividerItemDecoration(
            binding.deviceList.context,
            DividerItemDecoration.VERTICAL
        )
        binding.deviceList.addItemDecoration(dividerItemDecoration)

        viewModel.permissionRequestEvent.observe(viewLifecycleOwner) {
            requestPermissions(it, 1)
        }

        viewModel.deviceLiveData.observe(viewLifecycleOwner) {
            adapter?.addDevice(it)
            viewModel.saveDevice(it)
        }
    }

    private fun onItemClick(device: BluetoothDevice) {
        val bundle = Bundle()
        bundle.putParcelable("device", device)
        viewModel.deviceSelected()
        findNavController().navigate(R.id.action_deviceListFragment_to_deviceFragment, bundle)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        viewModel.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
