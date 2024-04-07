package asp.android.asppagos.ui.fragments.credit_payment

import CreditPaymentCodeSecurityViewModel
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import asp.android.asppagos.R
import asp.android.asppagos.data.models.CreditosActivo
import asp.android.asppagos.data.models.PinRequestTypeTransaction
import asp.android.asppagos.data.models.send_money.SpeiDataResponse
import asp.android.asppagos.ui.fragments.BaseCodeSecurityFragment
import asp.android.asppagos.ui.fragments.credit_payment.CreditPaymentInfoFragment.Companion.AMOUNT_TO_PAYMENT
import asp.android.asppagos.ui.fragments.credit_payment.CreditPaymentMainFragment.Companion.ACTIVE_CREDIT_DETAIL
import asp.android.asppagos.ui.fragments.send_money.SendMoneyCodeSecurityFragment
import asp.android.asppagos.ui.states.UIStates
import asp.android.asppagos.utils.fromJson
import asp.android.asppagos.utils.showCustomDialogError
import asp.android.asppagos.utils.toJson
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.koin.android.ext.android.inject

class CreditPaymentCodeSecurityFragment : BaseCodeSecurityFragment() {

    private val viewModel: CreditPaymentCodeSecurityViewModel by inject()

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
            it.getString(ACTIVE_CREDIT_DETAIL)?.fromJson<CreditosActivo>()?.let { activeCredit ->
                it.getString(AMOUNT_TO_PAYMENT)?.let { amountToPayment ->
                    viewModel.setupInfoCredit(activeCredit, amountToPayment)
                }
            }
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
        setupToolbar(R.string.credit_payment_toolbar_title)
        setupObservable()
    }

    override fun continueToFlow(pinSecurity: String) {
        viewModel.checkPinUser(pinSecurity, PinRequestTypeTransaction.PAYMENT_CREDIT)
    }

    override fun biometricSuccess() {
        viewModel.checkPinUser(pinRequestTypeTransaction =  PinRequestTypeTransaction.PAYMENT_CREDIT, isBiometric = true)
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

    private fun handleUIState(state: UIStates<SpeiDataResponse>) {
        when (state) {
            is UIStates.Success -> handleSuccess(state.value)

            is UIStates.Loading -> dialog.show(childFragmentManager.beginTransaction().remove(dialog), TAG)

            is UIStates.Error -> {
                dialog.dismissNow()
                showCustomDialogError(
                    message1 = "Información",
                    message2 = state.message
                )
                viewModel.resetUiState()
            }

            else -> {}
        }
    }

    private fun handleSuccess(speiDataResponse: SpeiDataResponse?) {
        dialog.dismissNow()
        safeNavigate(
            R.id.creditPaymentSuccessInfoFragment, bundleOf(
                Pair(
                    SendMoneyCodeSecurityFragment.SUCCESS_INFO_TRANSACTION, speiDataResponse.toJson()
                )
            )
        )
        viewModel.resetUiState()
    }

}