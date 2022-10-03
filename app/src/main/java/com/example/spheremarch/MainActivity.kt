package com.example.spheremarch

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.spheremarch.adapter.MainAdapter
import com.example.spheremarch.ext.viewModel
import com.example.spheremarch.marcher.MarcherCompute
import com.example.spheremarch.marcher.MarcherComputeNative
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

private const val RESOLUTION = 256

class MainActivity : AppCompatActivity() {

    private lateinit var view: MainActivityView
    private lateinit var viewModel: MainViewModel

    private val controlAdapter = MainAdapter()
    private val compute: MarcherCompute = MarcherComputeNative(RESOLUTION)

    private val localScope = CoroutineScope(
        SupervisorJob() + Dispatchers.Main + CoroutineName("MainActivityScope")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view = MainActivityView(this).apply {
            controlRecycler.adapter = controlAdapter
        }

        viewModel = viewModel<MainViewModelImpl>()
        setContentView(view)
    }


    override fun onResume() {
        super.onResume()
        viewModel.controlListState.onEach(controlAdapter::submitList).launchIn(localScope)
        viewModel.marcherConfigState.onEach(compute::applyConfig).launchIn(localScope)
        view.marchView.start(compute)
    }

    override fun onPause() {
        super.onPause()
        localScope.coroutineContext.cancelChildren()
        view.marchView.stop()
    }
}