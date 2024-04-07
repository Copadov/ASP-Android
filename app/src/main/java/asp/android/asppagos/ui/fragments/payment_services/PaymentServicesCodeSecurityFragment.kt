package asp.android.asppagos.ui.fragments.payment_services

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import asp.android.asppagos.R
import asp.android.asppagos.data.models.PinRequestTypeTransaction
import asp.android.asppagos.data.models.payment_services.PaymentServiceDataResponse
import asp.android.asppagos.data.models.payment_services.ServiceDataResponse
import asp.android.asppagos.data.models.send_money.SpeiDataResponse
import asp.android.asppagos.ui.fragments.BaseCodeSecurityFragment
import asp.android.asppagos.ui.fragments.send_money.SendMoneyCodeSecurityFragment.Companion.SUCCESS_INFO_TRANSACTION
import asp.android.asppagos.ui.states.UIStates
import asp.android.asppagos.ui.viewmodels.payment_services.PaymentServiceCodeSecurityViewModel
import asp.android.asppagos.utils.fromJson
import asp.android.asppagos.utils.showCustomDialogError
import asp.android.asppagos.utils.toJson
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.koin.android.ext.android.inject

class PaymentServicesCodeSecurityFragment : BaseCodeSecurityFragment() {

    private val viewModel: PaymentServiceCodeSecurityViewModel by inject()

    /**
     * Location provider
     */
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override var TAG: String = this.javaClass.name

    override var shouldAuthenticate = false

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val reference = it.getString(PaymentServicesInfoFragment.REFERENCE_SERVICE)
            val amount = it.getString(PaymentServicesInfoFragment.AMOUNT_SERVICE)
            val serviceInfo = it.getString(PaymentServicesMainFragment.SERVICE_SELECTED)
                ?.fromJson<ServiceDataResponse>()
            viewModel.setInfoPaymentService(reference, amount, serviceInfo)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        if (checkPermissions()) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location : Location? ->
                    if (location != null) {
                        viewModel.setLocation(location)
                    }
                }
        }

        shouldAuthenticate = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservable()
    }

    override fun continueToFlow(pinSecurity: String) {
        viewModel.checkPinUser(pinSecurity, PinRequestTypeTransaction.PAYMENT_SERVICES)
    }

    override fun biometricSuccess() {
        viewModel.checkPinUser(pinRequestTypeTransaction = PinRequestTypeTransaction.PAYMENT_SERVICES, isBiometric = true)
        shouldAuthenticate = false
    }

    override fun onClickBackButton() {
        onClickBackPressed()
    }

    private fun setupObservable() {
        viewModel.uiState.observe(viewLifecycleOwner) {
            handleUIState(it)
        }
    }

    private fun handleUIState(state: UIStates<PaymentServiceDataResponse>) {
        when (state) {
            is UIStates.Loading -> dialog.show(childFragmentManager.beginTransaction().remove(dialog), TAG)
            is UIStates.Success -> handleSuccess(state)
            is UIStates.Error -> handleError(state)
            else -> {}
        }
    }

    private fun handleSuccess(success: UIStates.Success<PaymentServiceDataResponse>) {
        dialog.dismissNow()
        safeNavigate(
            R.id.paymentServicesSuccessInfoFragment,
            bundleOf(
                Pair(
                    SUCCESS_INFO_TRANSACTION,
                    SpeiDataResponse(
                        beneficiary = viewModel.serviceInfo?.servicio,
                        trackingCode = success.value?.numAutorizacion,
                        amount = viewModel.serviceAmount?.toDouble() ?: 0.0,
                        reference = success.value?.referencia,
                        isPaymentServicesOperation = true,
                        fifthValue = null,
                        operationStatus = null
                    ).toJson()
                )
            )
        )
        viewModel.resetUiState()
    }

    private fun handleError(error: UIStates.Error) {
        dialog.dismissNow()
        showCustomDialogError(message1 = "Información", message2 = error.message)
        viewModel.resetUiState()
    }
}