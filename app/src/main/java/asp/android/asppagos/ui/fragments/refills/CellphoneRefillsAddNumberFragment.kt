package asp.android.asppagos.ui.fragments.refills

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import asp.android.aspandroidmaterial.ui.toolbars.ASPMaterialToolbarMainDashboard
import asp.android.asppagos.R
import asp.android.asppagos.data.models.cellphone_refill.AmountRefillServiceResponse
import asp.android.asppagos.data.models.cellphone_refill.CellphoneRefillServiceResponse
import asp.android.asppagos.databinding.FragmentCellphoneRefillsAddNumberBinding
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.ui.fragments.payment_services.PaymentServicesInfoFragment
import asp.android.asppagos.ui.fragments.payment_services.PaymentServicesMainFragment
import asp.android.asppagos.ui.fragments.refills.CellphoneRefillsAmountFragment.Companion.AMOUNT_SELECTED
import asp.android.asppagos.ui.fragments.refills.CellphoneRefillsMainFragment.Companion.SELECTED_REFILL_COMPANY
import asp.android.asppagos.ui.viewmodels.cellphone_refills.CellphoneRefillsAddNumberViewModel
import asp.android.asppagos.utils.fromJson
import asp.android.asppagos.utils.showCustomDialogInfo
import asp.android.asppagos.utils.toJson
import org.koin.android.ext.android.inject

class CellphoneRefillsAddNumberFragment : BaseFragment(),
    ASPMaterialToolbarMainDashboard.ASPMaterialToolbarMainDashboardListeners {

    private val binding: FragmentCellphoneRefillsAddNumberBinding by lazy {
        FragmentCellphoneRefillsAddNumberBinding.inflate(layoutInflater)
    }

    private val viewModel: CellphoneRefillsAddNumberViewModel by inject()

    override var TAG: String = this.javaClass.name

    /**
     * Launcher to handle permissions request result
     */
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                safeNavigate(
                    R.id.cellphoneRefillsCodeSecurityFragment,
                    bundleOf(
                        Pair(SELECTED_REFILL_COMPANY, viewModel.cellphoneRefill().toJson()),
                        Pair(AMOUNT_SELECTED, viewModel.amountRefill().toJson()),
                        Pair(NUMBER_RECHARGE, viewModel.numberToRecharge())
                    )
                )
            } else {
                showCustomDialogInfo(
                    getString(R.string.information_dialog_text),
                    getString(R.string.dialog_gps_error))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            viewModel.setupBundle(
                it.getString(SELECTED_REFILL_COMPANY)?.fromJson<CellphoneRefillServiceResponse>(),
                it.getString(AMOUNT_SELECTED)?.fromJson<AmountRefillServiceResponse>()
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupTextView()
        setupObservable()
        setupOnClickListener()
    }

    override fun onClickBackButton() {
        onClickBackPressed()
    }

    private fun setupToolbar() {
        binding.toolbar.setTitle(viewModel.cellphoneRefill()?.service ?: "")
        binding.toolbar.setASPMaterialToolbarsListeners(this)
    }

    private fun setupTextView() {
        binding.apply {
            tvAddNumber.setupInitialField(
                titleField = "Ingresa los 10 dígitos del celular",
                hintField = "5555555555",
                maxLength = 10,
                onChange = {
                    viewModel.setNumberRecharge(it)
                })
            tvAddNumberVerification.setupInitialField(
                titleField = "Confirma los 10 dígitos del celular",
                hintField = "5555555555",
                maxLength = 10,
                onChange = {
                    viewModel.setNumberRechargeVerification(it)
                })
        }
    }

    private fun setupObservable() {
        viewModel.apply {
            enabledButton.observe(viewLifecycleOwner) {
                binding.btnContinue.isEnabled = it
            }
        }
    }

    private fun setupOnClickListener() {
        binding.btnContinue.setOnClickListener {
            if (!checkPermissions()) {
                requestLocationPermissions()
            } else {
                if (isLocationEnabled()) {
                    safeNavigate(
                        R.id.cellphoneRefillsCodeSecurityFragment,
                        bundleOf(
                            Pair(SELECTED_REFILL_COMPANY, viewModel.cellphoneRefill().toJson()),
                            Pair(AMOUNT_SELECTED, viewModel.amountRefill().toJson()),
                            Pair(NUMBER_RECHARGE, viewModel.numberToRecharge())
                        )
                    )
                } else {
                    showCustomDialogInfo(
                        getString(R.string.information_dialog_text),
                        getString(R.string.dialog_gps_error))
                }
            }
        }
    }

    /**
     * Method to request needed permissions, according to OS version.
     */
    private fun requestLocationPermissions() {
        val permissionsList : MutableList<String> = ArrayList()
        permissionsList.add(Manifest.permission.ACCESS_FINE_LOCATION)
        permissionsList.add(Manifest.permission.ACCESS_COARSE_LOCATION)

        locationPermissionLauncher.launch(permissionsList.toTypedArray())
    }

    companion object {
        const val NUMBER_RECHARGE = "number_recharge"
    }

}