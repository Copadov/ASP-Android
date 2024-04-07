package asp.android.asppagos.ui.fragments.payment_services

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import asp.android.aspandroidmaterial.ui.toolbars.ASPMaterialToolbarMainDashboard
import asp.android.asppagos.R
import asp.android.asppagos.data.models.payment_services.ServiceDataResponse
import asp.android.asppagos.databinding.FragmentPaymentServicesInfoBinding
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.ui.fragments.payment_services.PaymentServicesMainFragment.Companion.SERVICE_SELECTED
import asp.android.asppagos.ui.viewmodels.payment_services.PaymentServiceInfoViewModel
import asp.android.asppagos.utils.fromJson
import asp.android.asppagos.utils.showCustomDialogError
import asp.android.asppagos.utils.showCustomDialogInfo
import asp.android.asppagos.utils.toJson
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import org.koin.android.ext.android.inject

class PaymentServicesInfoFragment : BaseFragment(),
    ASPMaterialToolbarMainDashboard.ASPMaterialToolbarMainDashboardListeners {

    private val binding: FragmentPaymentServicesInfoBinding by lazy {
        FragmentPaymentServicesInfoBinding.inflate(layoutInflater)
    }

    private val viewModel: PaymentServiceInfoViewModel by inject()

    private var reference = ""
    private var amount = ""

    override var TAG: String = this.javaClass.name

    private val barCodeLauncher: ActivityResultLauncher<ScanOptions> =
        registerForActivityResult(ScanContract()) { result ->
            if (result.contents != null) {
                checkReference(result.contents)
                handleReference(result.contents)
            }
        }

    /**
     * Launcher to handle permissions request result
     */
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                safeNavigate(
                    R.id.paymentServicesCodeSecurityFragment, bundleOf(
                        Pair(SERVICE_SELECTED, viewModel.serviceInfo().toJson()),
                        Pair(REFERENCE_SERVICE, reference),
                        Pair(AMOUNT_SERVICE, amount)
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
        setupServiceInfo()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupFields()
        setupOnClickListener()
    }

    override fun onClickBackButton() {
        onClickBackPressed()
    }

    private fun setupToolbar() {
        binding.toolbar.setTitle(getString(R.string.payment_services_toolbar_title))
        binding.toolbar.setASPMaterialToolbarsListeners(this)
    }

    private fun setupFields() {
        binding.apply {
            tiReference.apply {
                setupInitialField(
                    titleField = "Referencia o código de barras",
                    hintField = if (viewModel.showScanAndInfoButtons()) "Ingresa o escanea" else "Ingresa",
                    showInfoIcon = viewModel.showScanAndInfoButtons(),
                    showScanIcon = viewModel.showScanAndInfoButtons(),
                    onChange = {
                        reference = it
                        checkButtonEnabled()
                    },
                    tapInfo = {
                        val referenceType = viewModel.getServiceReferenceType()
                        PaymentServicesInfoBottomSheetModal.newInstance(
                            drawableId = setupDrawable(referenceType),
                            description = setupDescription(referenceType),
                            title = setupTitle(referenceType)
                        ).show(
                            childFragmentManager,
                            PaymentServicesInfoBottomSheetModal.TAG
                        )
                    }, tapEndIcon = {
                        barCodeLauncher.launch(ScanOptions())
                    })
            }
            tiAmount.setupInitialField(
                isDecimal = true,
                titleField = "Ingresa monto",
                hintField = "0.00",
                prefix = "$",
                onChange = {
                    amount = it
                    checkButtonEnabled()
                })
        }
    }

    private fun checkReference(reference: String) {
        if (checkLengthReference(viewModel.getServiceReferenceType(), reference).not()) {
            showCustomDialogError("Lectura de codigo incorrecta", "")
        }
    }

    private fun checkButtonEnabled() {
        binding.btnContinue.isEnabled = reference.isNotEmpty() && amount.isNotEmpty()
    }

    private fun handleReference(reference: String) {
        binding.tiReference.setValue(reference)
    }

    private fun setupServiceInfo() {
        arguments?.let {
            val serviceInfo = it.getString(SERVICE_SELECTED)?.fromJson<ServiceDataResponse>()
            viewModel.serviceInfo(serviceInfo)
        }
    }

    private fun setupOnClickListener() {
        binding.btnContinue.setOnClickListener {
            if (!checkPermissions()) {
                requestLocationPermissions()
            } else {
                if (isLocationEnabled()) {
                    safeNavigate(
                        R.id.paymentServicesCodeSecurityFragment, bundleOf(
                            Pair(SERVICE_SELECTED, viewModel.serviceInfo().toJson()),
                            Pair(REFERENCE_SERVICE, reference),
                            Pair(AMOUNT_SERVICE, amount)
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

    private fun setupDrawable(referenceType: PaymentServicesInfoBottomSheetModal.ReferenceType): Int {
        return when (referenceType) {
            PaymentServicesInfoBottomSheetModal.ReferenceType.CFE -> asp.android.aspandroidmaterial.R.drawable.ic_cfe_reference

            PaymentServicesInfoBottomSheetModal.ReferenceType.TOTAL_PLAY -> asp.android.aspandroidmaterial.R.drawable.ic_totalplay_reference

            PaymentServicesInfoBottomSheetModal.ReferenceType.SKY -> asp.android.aspandroidmaterial.R.drawable.ic_sky_reference

            PaymentServicesInfoBottomSheetModal.ReferenceType.TELMEX -> asp.android.aspandroidmaterial.R.drawable.ic_telmex_reference

            else -> asp.android.aspandroidmaterial.R.drawable.ic_cfe_reference
        }
    }

    private fun checkLengthReference(
        referenceType: PaymentServicesInfoBottomSheetModal.ReferenceType,
        reference: String
    ): Boolean {
        val regexNumeric = "([0-9])\\w+".toRegex()
        return when (referenceType) {
            PaymentServicesInfoBottomSheetModal.ReferenceType.CFE -> reference.length == 12 || reference.length == 30 && regexNumeric.matches(reference)
            PaymentServicesInfoBottomSheetModal.ReferenceType.TELMEX -> reference.length == 12 || reference.length == 20 && regexNumeric.matches(reference)
            PaymentServicesInfoBottomSheetModal.ReferenceType.SKY -> reference.length == 12 && regexNumeric.matches(reference)
            PaymentServicesInfoBottomSheetModal.ReferenceType.TOTAL_PLAY -> reference.length == 17 && regexNumeric.matches(reference)
            else -> false
        }
    }

    private fun setupDescription(referenceType: PaymentServicesInfoBottomSheetModal.ReferenceType): String {
        return when (referenceType) {
            PaymentServicesInfoBottomSheetModal.ReferenceType.CFE -> getString(R.string.payment_service_cfe_reference_description)
            PaymentServicesInfoBottomSheetModal.ReferenceType.TOTAL_PLAY -> getString(R.string.payment_service_total_play_reference_description)
            PaymentServicesInfoBottomSheetModal.ReferenceType.SKY -> getString(R.string.payment_service_sky_reference_description)
            PaymentServicesInfoBottomSheetModal.ReferenceType.TELMEX -> getString(R.string.payment_service_telmex_reference_description)
            else -> getString(R.string.payment_service_cfe_reference_description)
        }
    }

    private fun setupTitle(referenceType: PaymentServicesInfoBottomSheetModal.ReferenceType): String {
        return when (referenceType) {
            PaymentServicesInfoBottomSheetModal.ReferenceType.CFE -> "CFE"
            PaymentServicesInfoBottomSheetModal.ReferenceType.TOTAL_PLAY -> "Total Play"
            PaymentServicesInfoBottomSheetModal.ReferenceType.SKY -> "SKY"
            PaymentServicesInfoBottomSheetModal.ReferenceType.TELMEX -> "Telmex"
            else -> getString(R.string.payment_service_cfe_reference_description)
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
        const val REFERENCE_SERVICE = "reference_service"
        const val AMOUNT_SERVICE = "amount_service"
    }
}