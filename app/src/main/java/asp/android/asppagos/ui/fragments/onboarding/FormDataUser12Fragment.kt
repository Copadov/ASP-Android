package asp.android.asppagos.ui.fragments.onboarding

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import asp.android.aspandroidmaterial.ui.layouts.ASPMaterialLoadingScreenRegister
import asp.android.asppagos.R
import asp.android.asppagos.data.models.Geolocalizacion
import asp.android.asppagos.data.models.RegisterAccountResponseData
import asp.android.asppagos.databinding.FragmentCreatePinBinding
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.ui.viewmodels.OnboardingViewModel
import asp.android.asppagos.utils.BENEFICIARY_ACTIVATED
import asp.android.asppagos.utils.PROPERTY_FINGER_TOKEN_REGISTER
import asp.android.asppagos.utils.Prefs
import asp.android.asppagos.utils.ServerErrorCodes
import asp.android.asppagos.utils.decryptData
import asp.android.asppagos.utils.fromJson
import asp.android.asppagos.utils.showCustomDialogError
import asp.android.asppagos.utils.showKeyboard
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class FormDataUser12Fragment : BaseFragment() {

    private var _binding: FragmentCreatePinBinding? = null
    private val binding get() = _binding!!
    val viewModel: OnboardingViewModel by activityViewModel()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var isRegistrySuccessful = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCreatePinBinding.inflate(inflater, container, false)

        return binding.root
    }

    override var TAG: String = this.javaClass.name

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialogRegister = ASPMaterialLoadingScreenRegister()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    viewModel.setGeolocalizacion(
                        Geolocalizacion(
                            longitud = location.longitude.toString(),
                            latitud = location.latitude.toString()
                        )
                    )
                } else {
                    viewModel.setGeolocalizacion(
                        Geolocalizacion(
                            longitud = "",
                            latitud = ""
                        )
                    )
                }
            }
            .addOnFailureListener { exception ->
                showCustomDialogError(
                    getString(R.string.information_dialog_text),
                    getString(R.string.geolocalization_data_not_reached_message),
                )
            }

        binding.let {

            it.appCompatCheckBox.isChecked = true

            it.indicadorDots.setIndicators(dotsCount, 11)

            it.toolbarContent.contentToolbarTitle.text =
                getString(R.string.onboarding_form_title_toolbar_text_pin_creation)

            it.toolbarContent.contentToolbarSubtitle.text =
                getString(R.string.onboarding_form_subtitle_toolbar_text_pin_creation)

            it.toolbarContent.ASPTMaterialToolbarContent.setASPMaterialToolbarsListeners(this)

            it.createPinValidator.requestFocus()
            it.createPinValidator.showKeyboard()

            it.createPinValidator.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    it.continueButton.isEnabled = p0!!.isNotEmpty() && p0.length == 4
                }

                override fun afterTextChanged(p0: Editable?) {
                }
            })

            it.continueButton.setOnClickListener {

                viewModel.setValidatePin(binding.createPinValidator.text.toString())

                dialogRegister.show(requireActivity().supportFragmentManager, TAG)

                viewModel.registerSimpleAccount()
            }
        }

        initViewModel()
    }

    private fun initViewModel() {
        viewModel.let {

            it.registerImageINEResponseData.observe(viewLifecycleOwner){codeResponseData ->
                when (codeResponseData.codigo) {
                    ServerErrorCodes.SUCCESS.ordinal -> {

                    }

                    else -> {
                        showCustomDialogError(
                            getString(R.string.ine_info_title_text),
                            getString(R.string.ine_load_error_message),
                        )
                        if (isRegistrySuccessful) {
                            safeNavigate(R.id.action_formDataUser12Fragment_to_welcomeViewFragment)
                        }
                    }
                }
            }

            it.registerAccountResponseData.observe(viewLifecycleOwner) { codeResponseData ->
                when (codeResponseData.codigo) {
                    ServerErrorCodes.SUCCESS.ordinal -> {

                        if (codeResponseData.data.isEmpty()) {
                            safeNavigate(
                                    R.id.action_formDataUser12Fragment_to_welcomeViewFragment
                                )
                        } else {
                            isRegistrySuccessful = true
                            val responseAccount =
                                Gson().fromJson<RegisterAccountResponseData>(
                                    decryptData(codeResponseData.data)
                                )

                            responseAccount.cuenta.let { account ->
                                viewModel.setAccountNumber(account)

                                viewModel.registerImageIneSimpleAccount()

                                if (Prefs.get(BENEFICIARY_ACTIVATED, false)) {
                                    viewModel.beneficiaryRegister()
                                } else if (binding.appCompatCheckBox.isChecked) {
                                    viewModel.validateFinger()
                                }
                            }
                        }
                    }

                    else -> {
                        isRegistrySuccessful = false
                        showCustomDialogError(
                            getString(R.string.information_dialog_text),
                            codeResponseData.mensaje,
                        )
                    }
                }
            }

            it.beneficiaryResponse.observe(viewLifecycleOwner) { codeResponseData ->
                when (codeResponseData.codigo) {
                    ServerErrorCodes.SUCCESS.ordinal -> {
                        dialogRegister.dismiss()

                        if (binding.appCompatCheckBox.isChecked) {
                            viewModel.validateFinger()
                        } else {
                            safeNavigate(R.id.action_formDataUser12Fragment_to_welcomeViewFragment)
                        }
                    }

                    else -> {
                        dialogRegister.dismiss()
                        safeNavigate(R.id.action_formDataUser12Fragment_to_welcomeViewFragment)
                    }
                }
            }

            it.fingerValidateResponse.observe(viewLifecycleOwner) { codeResponseData ->
                when (codeResponseData.codigo) {
                    ServerErrorCodes.SUCCESS.ordinal -> {

                        dialogRegister.dismiss()

                        Prefs.set(
                            PROPERTY_FINGER_TOKEN_REGISTER, true
                        )

                        safeNavigate(R.id.action_formDataUser12Fragment_to_welcomeViewFragment)
                    }

                    else -> {
                        Prefs.set(
                            PROPERTY_FINGER_TOKEN_REGISTER, true
                        )

                        if (isRegistrySuccessful) {
                            safeNavigate(R.id.action_formDataUser12Fragment_to_welcomeViewFragment)
                        }
                    }
                }
            }

            it.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
                dialogRegister.dismiss()
                showCustomDialogError(
                    getString(R.string.information_dialog_text),
                    errorMessage,
                )

                if (isRegistrySuccessful) {
                    safeNavigate(R.id.action_formDataUser12Fragment_to_welcomeViewFragment)
                }
            }
        }
    }
}