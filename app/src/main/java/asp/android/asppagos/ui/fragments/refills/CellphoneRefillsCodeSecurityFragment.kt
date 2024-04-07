package asp.android.asppagos.ui.fragments.refills

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import asp.android.asppagos.R
import asp.android.asppagos.data.models.PinRequestTypeTransaction
import asp.android.asppagos.data.models.payment_services.PaymentServiceDataResponse
import asp.android.asppagos.data.models.send_money.SpeiDataResponse
import asp.android.asppagos.ui.fragments.BaseCodeSecurityFragment
import asp.android.asppagos.ui.fragments.send_money.SendMoneyCodeSecurityFragment.Companion.SUCCESS_INFO_TRANSACTION
import asp.android.asppagos.ui.states.UIStates
import asp.android.asppagos.ui.viewmodels.cellphone_refills.CellphoneRefillsCodeSecurityViewModel
import asp.android.asppagos.utils.showCustomDialogError
import asp.android.asppagos.utils.toJson
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.koin.android.ext.android.inject

class CellphoneRefillsCodeSecurityFragment : BaseCodeSecurityFragment() {

    override var shouldAuthenticate = false

    private val viewModel: CellphoneRefillsCodeSecurityViewModel by inject()

    /**
     * Location provider
     */
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            viewModel.setupInformation(it)
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
        setupToolbar(R.string.cellphone_refills)
    }

    override fun continueToFlow(pinSecurity: String) {
        viewModel.checkPinUser(pinSecurity, PinRequestTypeTransaction.CELLPHONE_REFILL)
    }

    override fun biometricSuccess() {
        viewModel.checkPinUser( pinRequestTypeTransaction = PinRequestTypeTransaction.CELLPHONE_REFILL, isBiometric = true)
        shouldAuthenticate = false
    }

    override var TAG = this.javaClass.name

    override fun onClickBackButton() {
        onClickBackPressed()
    }

    private fun setupObservable() {
        viewModel.apply {
            uiState.observe(viewLifecycleOwner) {
                handle(it)
            }
        }
    }

    private fun handle(state: UIStates<PaymentServiceDataResponse>) {
        when (state) {
            is UIStates.Loading -> dialog.show(childFragmentManager.beginTransaction().remove(dialog), TAG)
            is UIStates.Success -> handleSuccess(state)
            is UIStates.Error -> handleError(state.message)
            else -> {}
        }
    }

    private fun handleSuccess(state: UIStates.Success<PaymentServiceDataResponse>) {
        dialog.dismissNow()
        safeNavigate(
            R.id.cellphoneRefillsSuccessInfoFragment, bundleOf(
                Pair(
                    SUCCESS_INFO_TRANSACTION,
                    SpeiDataResponse(
                        beneficiary = viewModel.cellphoneServiceResponse()?.service,
                        trackingCode = state.value?.numAutorizacion,
                        amount = viewModel.cellphoneServiceAmountResponse()?.amount,
                        reference = viewModel.cellphoneServiceAmountResponse()?.product,
                        isPaymentServicesOperation = true,
                        isRechargeService = true,
                        fifthValue = viewModel.numberToRecharge(),
                        operationStatus = null
                    ).toJson()
                )
            )
        )
        viewModel.resetUiState()
    }

    private fun handleError(message: String) {
        dialog.dismissNow()
        showCustomDialogError(
            message1 = "Información",
            message2 = message
        )
        viewModel.resetUiState()
    }

}