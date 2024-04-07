package asp.android.asppagos.ui.fragments.credit_payment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import asp.android.aspandroidcore.utils.getAmountFormat
import asp.android.aspandroidmaterial.ui.toolbars.ASPMaterialToolbarMainDashboard
import asp.android.asppagos.R
import asp.android.asppagos.data.models.CreditosActivo
import asp.android.asppagos.databinding.FragmentCreditPaymentInfoBinding
import asp.android.asppagos.databinding.FragmentCreditPaymentMainBinding
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.ui.fragments.credit_payment.CreditPaymentMainFragment.Companion.ACTIVE_CREDIT_DETAIL
import asp.android.asppagos.utils.fromJson
import asp.android.asppagos.utils.showCustomDialogInfo
import asp.android.asppagos.utils.toJson

class CreditPaymentInfoFragment : BaseFragment(),
    ASPMaterialToolbarMainDashboard.ASPMaterialToolbarMainDashboardListeners {

    override var TAG: String = this.javaClass.name

    private var creditInfo: CreditosActivo? = null
    private var amountToPayment: String = ""

    private val binding: FragmentCreditPaymentInfoBinding by lazy {
        FragmentCreditPaymentInfoBinding.inflate(layoutInflater)
    }

    /**
     * Launcher to handle permissions request result
     */
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                moveToCodeSecurity()
            } else {
                showCustomDialogInfo(
                    getString(R.string.information_dialog_text),
                    getString(R.string.dialog_gps_error))
            }
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
        setupInfo()
        setupOnClickListener()
        setupCreditInfo()
    }

    private fun setupCreditInfo() {
        arguments?.let {
            creditInfo = it.getString(ACTIVE_CREDIT_DETAIL)?.fromJson<CreditosActivo>()
            with(binding) {
                tvControlPayment.text = creditInfo?.control ?: ""
                tvPaymentClabe.text = creditInfo?.cuentaClabe ?: ""
                tvCreditName.text = creditInfo?.control ?: ""
                tvAmount.text = getString(R.string.amount, creditInfo?.monto?.getAmountFormat())
            }
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setTitle(getString(R.string.credit_payment_toolbar_title))
        binding.toolbar.setASPMaterialToolbarsListeners(this)
    }

    private fun setupInfo() {
        binding.apply {
            tiAmount.doOnTextChanged { text, _, _, _ ->
                amountToPayment = text.toString()
                binding.btnContinue.isEnabled = text.toString().isNotEmpty()
            }
        }
    }

    private fun checkButtonToMove() {
        if (binding.btnContinue.isEnabled) {
            if (!checkPermissions()) {
                requestLocationPermissions()
            } else {
                if (isLocationEnabled()) {
                    moveToCodeSecurity()
                } else {
                    showCustomDialogInfo(
                        getString(R.string.information_dialog_text),
                        getString(R.string.dialog_gps_error))
                }
            }
        }
    }

    private fun setupOnClickListener() {
        binding.apply {
            btnContinue.setOnClickListener {
                checkButtonToMove()
            }
        }
    }

    private fun moveToCodeSecurity() {
        safeNavigate(
            R.id.creditPaymentCodeSecurityFragment,
            bundleOf(
                Pair(ACTIVE_CREDIT_DETAIL, creditInfo.toJson()),
                Pair(AMOUNT_TO_PAYMENT, amountToPayment)
            )
        )
    }

    override fun onClickBackButton() {
        onClickBackPressed()
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
        const val AMOUNT_TO_PAYMENT = "amount_to_payment"
    }
}