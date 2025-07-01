package com.unplugged.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.unplugged.data.ipc.InterAppContracts.EXTRA_SEARCH_QUERY
import com.unplugged.ui.databinding.ActivityDeviceListBinding
import com.unplugged.ui.viewmodels.DeviceListUiState
import com.unplugged.ui.viewmodels.DeviceListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DeviceListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDeviceListBinding
    private val viewModel: DeviceListViewModel by viewModels()
    private lateinit var deviceAdapter: DeviceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDeviceListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        observeUiState()
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            setIntent(it)
            handleIntent(it)
        }
    }

    private fun handleIntent(intent: Intent) {
        val query = intent.getStringExtra(EXTRA_SEARCH_QUERY)
        viewModel.processIntentQuery(query)
    }

    private fun setupRecyclerView() {
        deviceAdapter = DeviceAdapter { deviceItem ->
            viewModel.handleDeviceItemClick(deviceItem.id)
        }

        binding.recyclerViewDevices.apply {
            adapter = deviceAdapter
            layoutManager = LinearLayoutManager(this@DeviceListActivity)
        }
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is DeviceListUiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.textViewError.visibility = View.GONE
                            binding.recyclerViewDevices.visibility = View.GONE
                        }

                        is DeviceListUiState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.textViewError.visibility = View.GONE
                            binding.recyclerViewDevices.visibility = View.VISIBLE
                            deviceAdapter.submitList(state.devices)
                            binding.textSearchQuery.text = if (state.searchQuery.isBlank()) {
                                "Showing all devices"
                            } else {
                                "Query: \"${state.searchQuery}\""
                            }
                        }

                        is DeviceListUiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.textViewError.visibility = View.VISIBLE
                            binding.recyclerViewDevices.visibility = View.GONE
                            binding.textViewError.text = state.message
                        }
                    }
                }
            }
        }
    }
}