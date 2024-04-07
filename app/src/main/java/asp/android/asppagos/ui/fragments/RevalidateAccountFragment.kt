package asp.android.asppagos.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import asp.android.aspandroidmaterial.ui.layouts.ASPMaterialLoadingScreen
import asp.android.asppagos.MainApplication
import asp.android.asppagos.R
import asp.android.asppagos.data.events.SMSEvent
import asp.android.asppagos.data.models.LoginResponseData
import asp.android.asppagos.databinding.FragmentCodePinValidationBinding
import asp.android.asppagos.ui.activities.MainDashboardActivity
import asp.android.asppagos.ui.viewmodels.LoginViewModel
import asp.android.asppagos.utils.PROPERTY_ACCOUNT_ENCRIPTED
import asp.android.asppagos.utils.PROPERTY_ASP_ENCRIPTED
import asp.android.asppagos.utils.PROPERTY_CODI_ENCRIPTED
import asp.android.asppagos.utils.PROPERTY_NAME_USER_LOGGED
import asp.android.asppagos.utils.PROPERTY_PHONE_USER_LOGGED
import asp.android.asppagos.utils.PROPERTY_REGISTER_SUCCESS
import asp.android.asppagos.utils.Prefs
import asp.android.asppagos.utils.RxBus
import asp.android.asppagos.utils.append
import asp.android.asppagos.utils.appendWithFont
import asp.android.asppagos.utils.decryptDataWithAccess
import asp.android.asppagos.utils.encriptData
import asp.android.asppagos.utils.encriptPassword
import asp.android.asppagos.utils.fromJson
import asp.android.asppagos.utils.hideKeyboard
import asp.android.asppagos.utils.showCustomDialogError
import asp.android.asppagos.utils.showCustomDialogInfo
import asp.android.asppagos.utils.showKeyboard
import asp.android.asppagos.utils.toEditable
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class RevalidateAccountFragment : BaseFragment() {

    private var isCounting: Boolean = false
    private lateinit var timer: CountDownTimer
    private var isResend: Boolean = false
    private var _binding: FragmentCodePinValidationBinding? = null
    private val binding get() = _binding!!
    val viewModel: LoginViewModel by activityViewModel()

    override var TAG: String = this.javaClass.name

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCodePinValidationBinding.inflate(inflater, container, false)

        return binding.root
    }

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog = ASPMaterialLoadingScreen()

        timer = object : CountDownTimer(120000, 1000) {
            override fun onTick(millisUntilFinished: Long) {

                // long minutes = (milliseconds / 1000) / 60;
                val minutes = millisUntilFinished / 1000 / 60

                // long seconds = (milliseconds / 1000);
                val seconds = millisUntilFinished / 1000 % 60

                binding.let {

                    it.textViewResendCode.text = ""

                    it.textViewResendCode.append(
                        "El proceso puede tardar ", R.color.text_1
                    )
                    it.textViewResendCode.append(
                        "$minutes:$seconds\n\n", R.color.text_3
                    )

                    it.textViewResendCode.append(
                        "No recibí el código, ", R.color.text_1
                    )

                    it.textViewResendCode.appendWithFont(
                        "reenviar", R.color.text_disable
                    )
                }
            }

            override fun onFinish() {
                binding.let {
                    it.textViewResendCode.text = ""

                    it.textViewResendCode.append(
                        "El proceso puede tardar ", R.color.text_1
                    )
                    it.textViewResendCode.append(
                        "\n\n", R.color.text_3
                    )

                    it.textViewResendCode.append(
                        "No recibí el código, ", R.color.text_1
                    )

                    it.textViewResendCode.appendWithFont(
                        "reenviar", R.color.text_1
                    )
                }

                isCounting = false
            }
        }

        RxBus.listen(SMSEvent::class.java).subscribe {
            binding.codePinValidator.text =
                it.code.replace("\\D".toRegex(), "").toEditable()
        }

        binding.let {

            timer.start()
            isCounting = true

            it.indicadorDots.setIndicators(12, 9)

            it.codePinValidator.requestFocus()
            it.codePinValidator.showKeyboard()

            it.toolbarcontent.contentToolbarTitle.text =
                getString(R.string.onboarding_form_title_toolbar_text_pin_validation)

            it.dataConfirmation.imageViewEditData.visibility = View.INVISIBLE
            it.dataConfirmation.TextViewText.text = viewModel.phone.value!!
            it.dataConfirmation.TextviewTitle.text =
                getString(R.string.onboarding_form_title_phone)

            it.toolbarcontent.ASPTMaterialToolbarContent.setASPMaterialToolbarsListeners(this)

            it.codePinValidator.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    it.continueButton.isEnabled =
                        p0!!.isNotEmpty()
                                && p0.length == 6
                }

                override fun afterTextChanged(p0: Editable?) {
                }
            })

            it.continueButton.setOnClickListener {
                requireActivity().hideKeyboard()
                dialog.show(
                    requireActivity().supportFragmentManager,
                    TAG
                )
                viewModel.setToken(binding.codePinValidator.text.toString())
                viewModel.validatePhone()
            }

            it.textViewResendCode.text = ""

            it.textViewResendCode.append(
                "El proceso puede tardar ", R.color.text_1
            )
            it.textViewResendCode.append(
                "1:00\n\n", R.color.text_3
            )

            it.textViewResendCode.append(
                "No recibí el código, ", R.color.text_1
            )

            it.textViewResendCode.appendWithFont(
                "reenviar", R.color.text_disable
            )

            it.textViewResendCode.setOnClickListener {
                if (!isCounting) {
                    timer.start()

                    dialog.show(
                        requireActivity().supportFragmentManager,
                        TAG
                    )

                    viewModel.sendCode()
                    isResend = true
                }
            }
        }

        initViewModel()
    }

    private fun initViewModel() {
        viewModel.let {
            it.successResponse.observe(viewLifecycleOwner) { codeResponseData ->
                dialog.dismiss()

                when (codeResponseData.codigo) {
                    0 -> {
                        findNavController().popBackStack()
                    }

                    else -> {
                        showCustomDialogError(
                            "Información",
                            codeResponseData.mensaje,
                        )
                    }
                }
            }

            it.loginDataResponse.observe(viewLifecycleOwner) { loginDataResponse ->

                val loginResponse =
                    Gson().fromJson<LoginResponseData>(
                        decryptDataWithAccess(
                            loginDataResponse.data,
                            viewModel.phone.value!!,
                            encriptPassword(viewModel.password.value!!)
                        )
                    )

                Prefs.set(
                    PROPERTY_REGISTER_SUCCESS,
                    true
                )
                Prefs.set(
                    PROPERTY_NAME_USER_LOGGED,
                    loginResponse.nombre
                )
                Prefs.set(
                    PROPERTY_PHONE_USER_LOGGED,
                    viewModel.phone.value!!
                )
                Prefs.set(
                    PROPERTY_CODI_ENCRIPTED,
                    encriptData(loginResponse.urlCoDi)
                )
                Prefs.set(
                    PROPERTY_ASP_ENCRIPTED,
                    encriptData(loginResponse.urlAsp)
                )

                Prefs.set(
                    PROPERTY_ACCOUNT_ENCRIPTED,
                    encriptData(loginResponse)
                )

                MainApplication.phone = viewModel.phone.value!!
                MainApplication.pass = viewModel.password.value!!
            }

            it.loginResponse.observe(viewLifecycleOwner) { codeResponseData ->
                dialog.dismiss()

                when (codeResponseData.codigo) {
                    0 -> {
                        requireActivity().finish()
                        startActivity(
                            Intent(
                                requireContext(),
                                MainDashboardActivity::class.java
                            )
                        )
                    }

                    else -> {
                        showCustomDialogInfo(
                            "Información",
                            codeResponseData.mensaje,
                        )
                    }
                }
            }

            it.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
                dialog.dismiss()
                showCustomDialogError(
                    "Información",
                    errorMessage,
                )
            }
        }

        viewModel.sendCode()
    }
}