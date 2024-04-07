package asp.android.asppagos.ui.fragments.send_money

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import asp.android.aspandroidmaterial.ui.layouts.ASPMaterialDialogCustom
import asp.android.aspandroidmaterial.ui.toolbars.ASPMaterialToolbarMainDashboard
import asp.android.asppagos.R
import asp.android.asppagos.databinding.FragmentSendMoneyRegisterAccountBinding
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.ui.states.UIStates
import asp.android.asppagos.ui.viewmodels.send_money.SendMoneyRegisterAccountViewModel
import asp.android.asppagos.utils.showCustomDialogError
import asp.android.asppagos.utils.toJson
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import org.koin.android.ext.android.inject

class SendMoneyRegisterAccountFragment : BaseFragment(),
    ASPMaterialDialogCustom.ASPMaterialDialogCustomListener,
    ASPMaterialToolbarMainDashboard.ASPMaterialToolbarMainDashboardListeners {

    override var TAG: String = this.javaClass.name

    private val binding: FragmentSendMoneyRegisterAccountBinding by lazy {
        FragmentSendMoneyRegisterAccountBinding.inflate(layoutInflater)
    }

    private val viewModel: SendMoneyRegisterAccountViewModel by inject()

    private val showDialogSuccess = ASPMaterialDialogCustom.newInstance(
        title = "Cuenta exitosa",
        subTitle = "",
        textOption1 = "cerrar",
        dialogType = ASPMaterialDialogCustom.DialogIconType.SUCCESS.id,
        visibleAcceptButton = true
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            viewModel.setupIsBusiness(it.getBoolean(TYPE_REGISTER_ACCOUNT, false))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupType()
        setupToolbar()
        setupTextInputs()
        setupObservables()
        setupListenerDialog()
        setupOnClickListener()
        addListener()
        setupBankInfo()
    }

    override fun onClickBackButton() {
        onClickBackPressed()
    }

    private fun setupType() {
        binding.apply {
            tvTypeRegisterAccount.text =
                if (viewModel.getIsBusiness().not()) "Persona fisica" else "Persona moral"
            tiName.isVisible = viewModel.getIsBusiness().not()
            tiLastName.isVisible = viewModel.getIsBusiness().not()
            tiSecondLastName.isVisible = viewModel.getIsBusiness().not()
            tiBusinessName.isVisible = viewModel.getIsBusiness()
        }
    }

    private fun setupListenerDialog() {
        showDialogSuccess.setASPMaterialDialogCustomListener(this)
    }

    private fun setupBankInfo() {
        binding.tvBankSelectable.setOnItemClickListener { _, _, _, _ ->
            viewModel.bankBeneficiary(binding.tvBankSelectable.text.toString())
        }
    }

    private fun setupObservables() {
        viewModel.apply {
            buttonContinue.observe(viewLifecycleOwner) {
                binding.bContinueSendMoney.isEnabled = it
            }
            uiState.observe(viewLifecycleOwner) {
                handleResult(it)
            }
            bankList.observe(viewLifecycleOwner) {
                handleListBank(it)
            }
            bankFilteredByClabe.observe(viewLifecycleOwner) {
                handleBankByClabe(it)
            }
        }
    }

    private fun handleBankByClabe(bankName: String) {
        binding.tvBankSelectable.setText(bankName)
        viewModel.bankBeneficiary(bankName)
    }

    private fun handleListBank(bankList: MutableList<String>) {
        val adapter = ArrayAdapter(requireContext(), R.layout.item_bank, bankList)
        (binding.tvBankSelectable as MaterialAutoCompleteTextView).setAdapter(adapter)
    }

    private fun handleResult(result: UIStates<Any>) {
        when (result) {
            is UIStates.Loading -> dialog.show(childFragmentManager, TAG)
            is UIStates.Success -> showSuccessRegisterAccount()
            is UIStates.Error -> handleError(result.message)
            else -> {}
        }
    }

    private fun handleError(message: String) {
        dialog.dismiss()
        showCustomDialogError(message1 = "Información", message2 = message)
    }

    private fun showSuccessRegisterAccount() {
        dialog.dismiss()
        showDialogSuccess.show(childFragmentManager, ASPMaterialDialogCustom.TAG)
    }

    private fun setupTextInputs() {
        binding.apply {
            tiClabe.setupInitialField(
                titleField = "Ingresa una clabe, celular o tarjeta*",
                hintField = "*****",
                maxLength = 18,
                onChange = { viewModel.accountBeneficiary(it) })
            tiName.setupInitialField(
                titleField = "Nombre*",
                hintField = "Escribe el Nombre",
                allCaps = true,
                onChange = {
                    when {
                        it.isEmpty() || it.isBlank() -> tiName.setError("Este campo no puede estar vacio")
                        it.matches(viewModel.pattern) -> tiName.clearMessage()
                        else -> tiName.setError("Verifica que no contenga ningun caracter especial o Ñ")
                    }
                    viewModel.nameBeneficiary(it)
                })
            tiLastName.setupInitialField(
                titleField = "Primer apellido*",
                hintField = "Escribe el primer apellido",
                allCaps = true,
                onChange = {
                    when {
                        it.isEmpty() || it.isBlank() -> tiLastName.setError("Este campo no puede estar vacio")
                        it.matches(viewModel.pattern) -> tiLastName.clearMessage()
                        else -> tiLastName.setError("Verifica que no contenga ningun caracter especial o Ñ")
                    }
                    viewModel.lastNameBeneficiary(it)
                })
            tiSecondLastName.setupInitialField(
                titleField = "Segundo apellido",
                hintField = "Escribe el segundo apellido",
                allCaps = true,
                onChange = {
                    when {
                        it.isBlank() || it.matches(viewModel.pattern) -> tiSecondLastName.clearMessage()
                        else -> tiSecondLastName.setError("Verifica que no contenga ningun caracter especial o Ñ")
                    }
                    viewModel.secondLastBeneficiary(it)
                })
            tiBusinessName.setupInitialField(
                titleField = "Razón social*",
                allCaps = true,
                hintField = "Escribe la razón social",
                onChange = {
                    when {
                        it.isEmpty() || it.isBlank() -> tiBusinessName.setError("Este campo no puede estar vacio")
                        it.matches(viewModel.pattern) -> tiBusinessName.clearMessage()
                        else -> tiBusinessName.setError("Verifica que no contenga ningun caracter especial o Ñ")
                    }
                    viewModel.businessName(it)
                }
            )
            tiAlias.setupInitialField(
                titleField = "Alias",
                allCaps = true,
                hintField = "Nombre corto para identificarlo",
                onChange = {
                    when {
                        it.isEmpty() || it.isBlank() -> tiAlias.setError("Este campo no puede estar vacio")
                        it.matches(viewModel.pattern) -> tiAlias.clearMessage()
                        else -> tiAlias.setError("Verifica que no contenga ningun caracter especial o Ñ")
                    }
                    viewModel.aliasBeneficiary(it)
                }
            )
            binding.tiAlias.isVisible = false
        }
    }

    private fun setupOnClickListener() {
        binding.apply {
            bContinueSendMoney.setOnClickListener {
                checkContinueFlow()
            }
        }
    }

    private fun checkContinueFlow() {
        if (binding.ctvAddToFavorites.isChecked) {
            viewModel.registerAccount()
        } else {
            moveToSendMoneyDetailTransaction()
            viewModel.restartState()
        }
    }

    private fun moveToSendMoneyDetailTransaction() {
        safeNavigate(
            R.id.sendMoneyFavoriteDetailFragment, bundleOf(
                MainSendMoneyFragment.USER_TO_SEND_MONEY to viewModel.getSendMoneyDataRequest()
                    .toFavoriteAccount().toJson()
            )
        )
    }

    private fun setupToolbar() {
        binding.toolbar.setTitle(getString(R.string.send_money))
        binding.toolbar.setASPMaterialToolbarsListeners(this)
    }

    override fun onClickAcceptButton(optionType: Int) {
        viewModel.restartState()
        moveToSendMoneyDetailTransaction()
    }

    override fun onClickClose() {
        moveToDashboard()
    }

    private fun moveToDashboard() {
        safeNavigate(
            R.id.dashboardMainFragment, args = null, navOptions = NavOptions.Builder().apply {
                setLaunchSingleTop(true)
                setPopUpTo(R.id.dashboardMainFragment, inclusive = true)
            }.build()
        )
    }

    private fun addListener() {
        binding.ctvAddToFavorites.setOnCheckedChangeListener { _, isChecked ->
            //binding.tiAlias.isVisible = isChecked
            binding.tiAlias.isVisible = false
        }
    }

    companion object {
        const val TYPE_REGISTER_ACCOUNT = "type_register_account"
    }

}