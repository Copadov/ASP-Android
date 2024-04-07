package asp.android.asppagos.ui.fragments.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import asp.android.aspandroidmaterial.ui.toolbars.ASPMaterialToolbarMainDashboard
import asp.android.asppagos.R
import asp.android.asppagos.databinding.FragmentUpdatePersonalCodeBinding
import asp.android.asppagos.ui.activities.PinInputActivity
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.ui.viewmodels.MainDashboardViewModel
import asp.android.asppagos.utils.PROPERTY_PIN_VALUE
import asp.android.asppagos.utils.ServerErrorCodes
import asp.android.asppagos.utils.showCustomDialogInfo
import org.koin.android.ext.android.inject

class PinAsignationFragment : BaseFragment(),
    ASPMaterialToolbarMainDashboard.ASPMaterialToolbarMainDashboardListeners {

    override var TAG: String = this.javaClass.name
    private lateinit var pinInputLauncher: ActivityResultLauncher<Intent>
    private val binding: FragmentUpdatePersonalCodeBinding by lazy {
        FragmentUpdatePersonalCodeBinding.inflate(layoutInflater)
    }

    val viewModel: MainDashboardViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = binding.root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupWritingListener()
        setupObservables()
        setupOnClickListener()
        initViewModel()

        binding.let {
            it.addNewCode.text = getString(R.string.pin_assignation_new_nip_button_text)
            it.addConfirmCode.text = getString(R.string.pin_assignation_confirm_nip_button_text)
        }

        // Asigna numero de tarjeta física
        val cardNumber = arguments?.getString("cardNumber")
        if (cardNumber != null) {
            viewModel.setPhysicCard(cardNumber)
        }
    }

    private fun initViewModel() {
        viewModel.let {
            it.validateAuthorizationCodeResponse.observe(viewLifecycleOwner) { codeResponseData ->
                when (codeResponseData.codigo) {
                    ServerErrorCodes.SUCCESS.ordinal -> {
                        viewModel.assignNip()
                    }

                    else -> {
                        dialog.dismiss()
                        showCustomDialogInfo(
                            getString(R.string.information_dialog_text),
                            codeResponseData.mensaje,
                        )
                    }
                }
            }

            it.assignNipCodeResponseData.observe(viewLifecycleOwner) { codeResponseData ->
                when (codeResponseData.codigo) {
                    ServerErrorCodes.SUCCESS.ordinal -> {
                        dialog.dismiss()
                        safeNavigate(R.id.action_pinAsignationFragment_to_assignationPinSuccessFragment)
                    }

                    else -> {
                        dialog.dismiss()
                        showCustomDialogInfo(
                            getString(R.string.information_dialog_text),
                            codeResponseData.mensaje,
                        )
                    }
                }
            }
        }
    }

    private fun setupObservables() {
        viewModel.enabledButtons.observe(viewLifecycleOwner) {
            binding.apply {
                btnCancel.isEnabled = it
                btnAccept.isEnabled = it
            }
        }
    }

    private fun setupWritingListener() {
        binding.apply {
            createPinValidator.addTextChangedListener {
                viewModel.codeSecurity(it.toString())
            }
            validatePinValidator.addTextChangedListener {
                viewModel.codeSecurityConfirmation(it.toString())
            }
        }
    }

    private fun setupOnClickListener() {
        binding.apply {
            btnAccept.setOnClickListener {
                intent = Intent(requireContext(), PinInputActivity::class.java)
                        // indica que debe solicitar el biometrico
                    .putExtra(PinInputActivity.SHOW_BIOMETRIC, true)
                pinInputLauncher.launch(intent)
            }
            btnCancel.setOnClickListener {
                onClickBackPressed()
            }
        }

        pinInputLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val pinValue = result.data?.getStringExtra(PROPERTY_PIN_VALUE)
                    val isBiometricSuccess = result.data?.getBooleanExtra(PinInputActivity.IS_BIOMETRIC_SUCCESS, false)
                    // Si se autentica con PIN
                    if (pinValue != null) {
                        dialog.show(
                            requireActivity().supportFragmentManager, TAG
                        )
                        viewModel.setPincode(pinValue)
                        viewModel.validateAuthorizationCode()
                    } else if (isBiometricSuccess == true) { // Si se autentica con biometrico de forma exitosa
                        dialog.show(
                            requireActivity().supportFragmentManager, TAG
                        )
                        viewModel.assignNip()
                    } else { // Si ocurre un error al autenticar con biometrico
                        val msg = result.data?.getStringExtra(PinInputActivity.BIOMETRIC_MSG) ?: ""
                        showCustomDialogInfo(
                            getString(R.string.information_dialog_text),
                            msg,
                        )
                    }
                } else if (result.resultCode == Activity.RESULT_CANCELED){
                    Toast.makeText(requireContext(), "Cancelado", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun setupToolbar() {
        binding.toolbar.setASPMaterialToolbarsListeners(this)
        binding.toolbar.setTitle(getString(R.string.pin_assignation_toolbar_title))
    }

    override fun onClickBackButton() {
        onClickBackPressed()
    }
}