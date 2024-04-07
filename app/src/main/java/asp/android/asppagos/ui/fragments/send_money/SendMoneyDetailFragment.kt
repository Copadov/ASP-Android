package asp.android.asppagos.ui.fragments.send_money

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import asp.android.aspandroidcore.utils.typeAccountId
import asp.android.aspandroidmaterial.ui.toolbars.ASPMaterialToolbarMainDashboard
import asp.android.asppagos.R
import asp.android.asppagos.data.models.FavoritosTransferencia
import asp.android.asppagos.data.models.send_money.SendMoneyDataRequest
import asp.android.asppagos.databinding.FragmentSendMoneyDetailBinding
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.ui.fragments.send_money.MainSendMoneyFragment.Companion.USER_TO_SEND_MONEY
import asp.android.asppagos.ui.viewmodels.send_money.SendMoneyDetailViewModel
import asp.android.asppagos.utils.formatAmount
import asp.android.asppagos.utils.fromJson
import asp.android.asppagos.utils.getFirstLetters
import asp.android.asppagos.utils.showCustomDialogInfo
import org.koin.androidx.viewmodel.ext.android.viewModel

class SendMoneyDetailFragment : BaseFragment(),
    ASPMaterialToolbarMainDashboard.ASPMaterialToolbarMainDashboardListeners {

    override var TAG: String = this.javaClass.name

    private val binding: FragmentSendMoneyDetailBinding by lazy {
        FragmentSendMoneyDetailBinding.inflate(layoutInflater)
    }

    private var userToSendMoney: FavoritosTransferencia? = null

    private val viewModel: SendMoneyDetailViewModel by viewModel()

    /**
     * Launcher to handle permissions request result
     */
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                safeNavigate(
                    R.id.sendMoneyPersonalCodeSecurityFragment,
                    viewModel.getTransactionBundle(userToSendMoney)
                )
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
        setupTextFields()
        extractBundleInfo()
        setupToolbar()
        setupAccount()
        setupObservable()
        setupOnclickListener()
    }

    override fun onClickBackButton() {
        onClickBackPressed()
    }

    private fun extractBundleInfo() {
        arguments?.getString(USER_TO_SEND_MONEY)?.let {
            userToSendMoney = it.fromJson<FavoritosTransferencia>()
        }
    }

    private fun setupAccount() {
        binding.apply {
            tvFullName.text = userToSendMoney?.nombreBeneficiario
            tvBankName.text = userToSendMoney?.nombreInstitucion
            tvFullNameFirstLetters.text =
                userToSendMoney?.nombreBeneficiario?.getFirstLetters()?.uppercase()
        }
    }

    private fun setupTextFields() {
        binding.apply {
            tiAmount.doOnTextChanged { text, _, _, _ ->
                val finalAmount = text.toString().formatAmount(9, 2)
                if (finalAmount != text.toString()) {
                    tiAmount.setText(finalAmount)
                    tiAmount.setSelection(finalAmount.length)
                }
                viewModel.checkAmount(finalAmount)
            }
            tiConcept.doOnTextChanged { text, _, _, _ ->
                when {
                    text.toString().isEmpty() || text.toString().isBlank() -> tilConcept.setError("Este campo no puede estar vacio")
                    text.toString().matches(viewModel.pattern) -> tilConcept.error = null
                    else -> tilConcept.setError("Verifica que no contenga ningun caracter especial o Ñ")
                }
                viewModel.checkConcept(text.toString())
            }
            tiReference.setupInitialField(
                titleField = "Referencia*",
                hintField = "1234567",
                maxLength = 7
            ) {
                viewModel.checkReference(it)
            }
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setTitle(getString(R.string.send_money))
        binding.toolbar.setASPMaterialToolbarsListeners(this)
    }

    private fun setupObservable() {
        viewModel.enabledButton.observe(viewLifecycleOwner) {
            binding.bContinueSendMoney.isEnabled = it
        }
    }

    private fun setupOnclickListener() {
        binding.apply {
            bContinueSendMoney.setOnClickListener {
                if (!checkPermissions()
                ) {
                    requestLocationPermissions()
                } else {
                    if (isLocationEnabled()) {
                        safeNavigate(
                            R.id.sendMoneyPersonalCodeSecurityFragment,
                            viewModel.getTransactionBundle(userToSendMoney)
                        )
                    } else {
                        showCustomDialogInfo(
                            getString(R.string.information_dialog_text),
                            getString(R.string.dialog_gps_error))
                    }
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
        const val AMOUNT_TRANSACTION = "amount_transaction"
        const val CONCEPT_TRANSACTION = "concept_transaction"
        const val REFERENCE_TRANSACTION = "reference_transaction"
        const val FAVORITE_ACCOUNT = "favorite_account"
    }
}

fun SendMoneyDataRequest.toFavoriteAccount() = FavoritosTransferencia(
    correoBeneficiario = email ?: "",
    fechaHoraRegistro = "",
    id = 0,
    idInstitucionBeneficiario = idBank ?: 0,
    nombreBeneficiario = name ?: "",
    nombreInstitucion = bankName ?: "",
    numeroCuentaBeneficiario = numberAccount ?: "",
    numeroCuentaCoDi = codiNumberAccount ?: "",
    tipoCuentaBeneficiario = numberAccount.typeAccountId()
)