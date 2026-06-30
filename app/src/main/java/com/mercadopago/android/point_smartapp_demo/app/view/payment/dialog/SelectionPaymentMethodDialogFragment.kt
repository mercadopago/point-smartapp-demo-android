package com.mercadopago.android.point_smartapp_demo.app.view.payment.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mercadolibre.android.point_integration_sdk.nativesdk.MPManager
import com.mercadolibre.android.point_integration_sdk.nativesdk.message.utils.doIfError
import com.mercadolibre.android.point_integration_sdk.nativesdk.message.utils.doIfSuccess
import com.mercadopago.android.point_smartapp_demo.app.databinding.PointSmartappDemoAppSelectionPaymentMethodDialogFragmentBinding
import com.mercadopago.android.point_smartapp_demo.app.view.payment.adapter.PaymentMethodAdapter
import com.mercadopago.android.point_smartapp_demo.app.view.payment.models.PaymentMethodModel

class SelectionPaymentMethodDialogFragment : BottomSheetDialogFragment() {

    lateinit var binding: PointSmartappDemoAppSelectionPaymentMethodDialogFragmentBinding

    private var lastPaymentMethodSelected: String? = null

    var onListenerPaymentMethod: (String) -> Unit = {}

    private val paymentMethodAdapter by lazy {
        PaymentMethodAdapter { paymentMethod ->
           lastPaymentMethodSelected = paymentMethod
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = PointSmartappDemoAppSelectionPaymentMethodDialogFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        configPaymentMethodList()
        onClickListenerPaymentMethodSelected()
    }

    private fun onClickListenerPaymentMethodSelected() {
        binding.buttonPaymentMethodSelected.setOnClickListener {
            lastPaymentMethodSelected?.let { paymentMethod ->
                onListenerPaymentMethod(paymentMethod)
                dismiss()
            }
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerviewPaymentMethod.apply {
            layoutManager = LinearLayoutManager(
                requireContext(), LinearLayoutManager.VERTICAL, false
            )
            adapter = paymentMethodAdapter
        }
    }

    private fun configPaymentMethodList() {
        MPManager.paymentMethodsTools.getPaymentMethods { response ->
            response.doIfSuccess { result ->
                val paymentMethodList = result.map { PaymentMethodModel(name = it.name) }
                paymentMethodAdapter.submitList(paymentMethodList)
            }.doIfError { error ->
                Toast.makeText(requireContext(), error.message.orEmpty(), Toast.LENGTH_SHORT).show()
                dismiss()
            }
        }
    }

    companion object {
        fun newInstance(): SelectionPaymentMethodDialogFragment {
            return SelectionPaymentMethodDialogFragment()
        }
    }
}
