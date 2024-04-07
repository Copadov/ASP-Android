package asp.android.asppagos.ui.fragments.configurations

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import asp.android.aspandroidmaterial.ui.layouts.ASPMaterialDialogCustom
import asp.android.aspandroidmaterial.ui.layouts.ASPMaterialLockCard
import asp.android.aspandroidmaterial.ui.toolbars.ASPMaterialToolbarMainDashboard
import asp.android.asppagos.MainApplication
import asp.android.asppagos.R
import asp.android.asppagos.data.repositories.AspTrackingRepository
import asp.android.asppagos.data.repositories.AspTrackingRepositoryImpl
import asp.android.asppagos.databinding.FragmentConfigurationOptionsBinding
import asp.android.asppagos.ui.activities.MainActivity
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.ui.states.UIStates
import asp.android.asppagos.ui.viewmodels.MainDashboardViewModel
import asp.android.asppagos.ui.viewmodels.configurations.ConfigurationOptionsViewModel
import asp.android.asppagos.utils.IS_USER_LOGIN
import asp.android.asppagos.utils.PROPERTY_FINGER_TOKEN
import asp.android.asppagos.utils.PROPERTY_FINGER_TOKEN_REGISTER
import asp.android.asppagos.utils.PROPERTY_PASSWORD_REGISTER
import asp.android.asppagos.utils.Prefs
import asp.android.asppagos.utils.ServerErrorCodes
import asp.android.asppagos.utils.UNLOCK_CARD_OPTION_TYPE
import asp.android.asppagos.utils.showCustomDialogError
import asp.android.asppagos.utils.showCustomDialogSuccess
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class ConfigurationOptionsFragment : BaseFragment(),
    ASPMaterialToolbarMainDashboard.ASPMaterialToolbarMainDashboardListeners,
    ASPMaterialLockCard.OnLockCardListener {

    private val binding: FragmentConfigurationOptionsBinding by lazy {
        FragmentConfigurationOptionsBinding.inflate(layoutInflater)
    }

    val viewModel: MainDashboardViewModel by activityViewModel()

    override var TAG: String = this.javaClass.name

    private val viewModelConfig: ConfigurationOptionsViewModel by inject()
    private val aspTrackingRepository by inject<AspTrackingRepository>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupOnClickListener()
        setonClickListenerCheck()
        setupToolbar()
        setupBinding()
        setupObservable()
    }

    private fun setupBinding() {
        binding.let {

            it.switchCodiFavoriteAccount.isCardLocked = !viewModelConfig.getIsCodiFavorite()

            val userExist = Prefs.get(PROPERTY_FINGER_TOKEN_REGISTER, false)
            it.switchBiometricActivate.isCardLocked = !userExist

            it.switchBiometricActivate.setOnLockCardListener(object :
                ASPMaterialLockCard.OnLockCardListener {
                override fun onLockCardClicked(isLocked: Boolean) {

                    if (!isLocked) {
                        Prefs.set(
                            PROPERTY_FINGER_TOKEN_REGISTER,
                            false
                        )

                        Prefs.set(
                            PROPERTY_FINGER_TOKEN,
                            ""
                        )
                    } else {
                        dialog.show(requireActivity().supportFragmentManager, TAG)
                        viewModel.validateFinger()
                    }
                }
            })

            it.logoutSession.setOnClickListener {
                calllogoutDialog()
            }
        }

        initViewModel()
    }

    private fun initViewModel() {
        viewModel.let {
            it.fingerValidateResponse.observe(viewLifecycleOwner) { codeResponseData ->
                when (codeResponseData.codigo) {
                    ServerErrorCodes.SUCCESS.ordinal -> {

                        dialog.dismiss()

                        showCustomDialogSuccess(
                            "Información",
                            codeResponseData.mensaje,
                        )
                        Prefs[PROPERTY_FINGER_TOKEN_REGISTER] = true
                        Prefs[PROPERTY_PASSWORD_REGISTER] = MainApplication.pass
                    }

                    else -> {
                        binding.switchCodiFavoriteAccount.isCardLocked = true
                        dialog.dismiss()
                        showCustomDialogError(
                            "Información",
                            codeResponseData.mensaje,
                        )
                    }
                }
            }
        }
    }

    private fun calllogoutDialog() {
        val logoutDialog = ASPMaterialDialogCustom.newInstance(
            "Cerrar sesión",
            "Estás a punto de cerrar sesión.",
            "cerrar",
            ASPMaterialDialogCustom.DialogIconType.INFO.ordinal,
            true,
            UNLOCK_CARD_OPTION_TYPE
        )

        logoutDialog.setASPMaterialDialogCustomListener(object :
            ASPMaterialDialogCustom.ASPMaterialDialogCustomListener {
            override fun onClickAcceptButton(optionType: Int) {
                Prefs.set(IS_USER_LOGIN, false)
                GlobalScope.async {
                    try {
                        aspTrackingRepository.close(AspTrackingRepositoryImpl.USER_CLOSE_APP)
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                    }
                }
                logoutDialog.dismiss()
                requireActivity().finish()
                // Go back to login
                startActivity(
                    Intent(
                        requireContext(),
                        MainActivity::class.java
                    )
                )
            }

            override fun onClickClose() {
                logoutDialog.dismiss()
            }
        })

        logoutDialog.show(childFragmentManager, ASPMaterialDialogCustom.TAG)
    }

    private fun setonClickListenerCheck() {
        binding.switchCodiFavoriteAccount.setOnLockCardListener(this)
    }

    private fun setupToolbar() {
        binding.toolbar.setTitle("Ajustes generales")
        binding.toolbar.setASPMaterialToolbarsListeners(this)
    }


    private fun setupOnClickListener() {
        binding.containerUpdateCode.setOnClickListener {
            safeNavigate(R.id.updatePersonalCodeFragment)
        }
    }

    private fun setupObservable() {
        viewModelConfig.apply {
            uiState.observe(viewLifecycleOwner) {
                handleUIState(it)
            }
        }
    }

    private fun handleUIState(state: UIStates<Any>) {
        when (state) {
            is UIStates.Loading -> dialog.show(childFragmentManager, TAG)
            is UIStates.Success -> handleSuccess(state)
            is UIStates.Error -> handleError(state)
            else -> {}
        }
    }

    private fun handleSuccess(success: UIStates.Success<Any>) {
        dialog.dismiss()
        showCustomDialogSuccess(
            message1 = "ASP con CoDi",
            message2 = "Haz activado como favorito para operaciones CoDi a ASP."
        )
    }

    private fun handleError(error: UIStates.Error) {
        binding.switchCodiFavoriteAccount.isCardLocked = true
        dialog.dismiss()
        showCustomDialogError(error.message, "")
    }

    override fun onClickBackButton() {
        onClickBackPressed()
    }

    override fun onLockCardClicked(isLocked: Boolean) {
        if (isLocked) {
            GlobalScope.async {
                aspTrackingRepository.inform(
                    eventAction = AspTrackingRepositoryImpl.EventAction.WEB_SERVICE,
                    ticket = "REG_APP_OMI",
                    aditionalInfo = "DESPLIEGA_ALERTA"
                )
            }
            viewModelConfig.codiFavoriteAccount()
        }
    }
}