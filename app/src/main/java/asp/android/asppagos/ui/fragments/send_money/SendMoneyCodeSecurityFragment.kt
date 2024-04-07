package asp.android.asppagos.ui.fragments.send_money

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
import asp.android.asppagos.data.models.FavoritosTransferencia
import asp.android.asppagos.data.models.PinRequestTypeTransaction
import asp.android.asppagos.data.models.send_money.SpeiDataResponse
import asp.android.asppagos.ui.fragments.BaseCodeSecurityFragment
import asp.android.asppagos.ui.states.UIStates
import asp.android.asppagos.ui.fragments.send_money.SendMoneyDetailFragment.Companion.AMOUNT_TRANSACTION
import asp.android.asppagos.ui.fragments.send_money.SendMoneyDetailFragment.Companion.CONCEPT_TRANSACTION
import asp.android.asppagos.ui.fragments.send_money.SendMoneyDetailFragment.Companion.FAVORITE_ACCOUNT
import asp.android.asppagos.ui.fragments.send_money.SendMoneyDetailFragment.Companion.REFERENCE_TRANSACTION
import asp.android.asppagos.ui.viewmodels.send_money.SendMoneyCodeSecurityViewModel
import asp.android.asppagos.utils.fromJson
import asp.android.asppagos.utils.showCustomDialogError
import asp.android.asppagos.utils.toJson
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.koin.android.ext.android.inject

class SendMoneyCodeSecurityFragment : BaseCodeSecurityFragment() {

    override var TAG: String = this.javaClass.name

    private val viewModel: SendMoneyCodeSecurityViewModel by inject()

    /**
     * Location provider
     */
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override var shouldAuthenticate = false

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupTransactionInfo()

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
        setupToolbar(R.string.send_money)
        setupObserver()
    }

    override fun continueToFlow(pinSecurity: String) {
        viewModel.checkPinUser(pinSecurity, PinRequestTypeTransaction.SEND_MONEY)
    }

    override fun biometricSuccess() {
        viewModel.checkPinUser(pinRequestTypeTransaction =  PinRequestTypeTransaction.SEND_MONEY, isBiometric = true)
        shouldAuthenticate = false
    }

    override fun onClickBackButton() {
        onClickBackPressed()
    }

    private fun setupTransactionInfo() {
        arguments?.let {
            viewModel.setupTransaction(
                amount = it.getString(AMOUNT_TRANSACTION, ""),
                concept = it.getString(CONCEPT_TRANSACTION, ""),
                reference = it.getString(REFERENCE_TRANSACTION, ""),
                favoriteAccount = it.getString(FAVORITE_ACCOUNT, "")
                    .fromJson<FavoritosTransferencia>()
            )
        }
    }

    private fun setupObserver() {
        viewModel.uiState.observe(viewLifecycleOwner) {
            handleTransaction(it)
        }
    }

    private fun handleTransaction(commonStatusServiceState: UIStates<SpeiDataResponse>) {
        when (commonStatusServiceState) {
            is UIStates.Loading -> dialog.show(childFragmentManager.beginTransaction().remove(dialog), TAG)
            is UIStates.Success -> handleSuccessResponse(commonStatusServiceState.value)
            is UIStates.Error -> {
                dialog.dismissNow()
                showCustomDialogError(
                    message1 = "Información",
                    message2 = commonStatusServiceState.message
                )
                viewModel.resetUiState()
            }
            else -> {}
        }
    }

    private fun handleSuccessResponse(data: SpeiDataResponse?) {
        dialog.dismissNow()
        safeNavigate(
            R.id.sendMoneySuccessInfoFragment, bundleOf(
                Pair(
                    SUCCESS_INFO_TRANSACTION, data.toJson()
                )
            )
        )
        viewModel.resetUiState()
    }

    companion object {
        const val SUCCESS_INFO_TRANSACTION = "success_info_transaction"
    }
}