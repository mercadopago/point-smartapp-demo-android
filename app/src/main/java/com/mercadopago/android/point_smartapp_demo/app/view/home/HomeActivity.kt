package com.mercadopago.android.point_smartapp_demo.app.view.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.mercadopago.android.point_smartapp_demo.app.ActionsProviderImpl
import com.mercadopago.android.point_smartapp_demo.app.BuildConfig
import com.mercadopago.android.point_smartapp_demo.app.R
import com.mercadopago.android.point_smartapp_demo.app.actions.contract.HomeActions
import com.mercadopago.android.point_smartapp_demo.app.actions.view.HomeActionAdapter
import com.mercadopago.android.point_smartapp_demo.app.databinding.PointMainappDemoAppActivityHomeBinding
import com.mercadopago.android.point_smartapp_demo.app.util.launchActivity

class HomeActivity : AppCompatActivity() {

    private val binding: PointMainappDemoAppActivityHomeBinding by lazy {
        PointMainappDemoAppActivityHomeBinding.inflate(layoutInflater)
    }

    private val actionAdapter: HomeActionAdapter by lazy {
        HomeActionAdapter(::handlerActionItem)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.run { setContentView(root) }
        actionAdapter.submitList(ActionsProviderImpl.getActions(this@HomeActivity))
        setRecyclerView()
        getVersionName()
    }

    private fun getVersionName() {
        val versionName = BuildConfig.VERSION_NAME
        binding.pointMainappDemoAppVersion.text =
            getString(R.string.point_smartapp_demo_app_version_name, versionName)
    }

    private fun setRecyclerView() {
        binding.rvActions.apply {
            layoutManager = LinearLayoutManager(
                this@HomeActivity,
                LinearLayoutManager.VERTICAL,
                false
            )
            adapter = actionAdapter
        }
    }

    private fun handlerActionItem(action: HomeActions) {
        when (action) {
            is HomeActions.LaunchActivity -> launchActivity(action.activity)
            is HomeActions.LaunchBtUi -> action.actionManager.bluetoothUiSettings.launch(this@HomeActivity)
        }
    }
}
