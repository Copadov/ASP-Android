package asp.android.asppagos.ui.fragments.onboarding

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import asp.android.aspandroidmaterial.ui.adapters.CountryCodeSpinnerAdapter
import asp.android.aspandroidmaterial.ui.models.CountryDisplayItem
import asp.android.aspandroidmaterial.ui.toolbars.ASPTMaterialToolbar
import asp.android.asppagos.R
import asp.android.asppagos.databinding.FragmentRegisterLoginBinding
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.ui.viewmodels.LoginViewModel
import asp.android.asppagos.utils.ServerErrorCodes
import asp.android.asppagos.utils.hideKeyboard
import asp.android.asppagos.utils.showCustomDialogError
import asp.android.asppagos.utils.showSingleButtonDialog
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class RegisterLogin : BaseFragment(),
    ASPTMaterialToolbar.ASPMaterialToolbarsListeners {

    private var _binding: FragmentRegisterLoginBinding? = null
    private val binding get() = _binding!!
    val viewModel: LoginViewModel by activityViewModel()

    override var TAG: String = this.javaClass.name

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRegisterLoginBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.let { it ->

            it.editTextPhone.requestFocus()

            it.editTextPhone.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    val nonNumericCharacters = p0?.filterNot { it.isDigit() }

                    if (!nonNumericCharacters.isNullOrEmpty()) {

                        val filteredText = p0.filter { it.isDigit() }
                        it.editTextPhone.setText(filteredText)
                        it.editTextPhone.setSelection(filteredText.length ?: 0)
                    }

                    binding.continueButton.isEnabled =
                        p0!!.isNotEmpty()
                                && android.util.Patterns.PHONE.matcher(p0).matches()
                                && p0.length == 10
                }

                override fun afterTextChanged(p0: Editable?) {
                }
            })

            it.ASPTMaterialToolbarRegisterLogin.setASPMaterialToolbarsListeners(this)

            it.continueButton.setOnClickListener {
                requireActivity().hideKeyboard()
                /*dialog.show(
                    requireActivity().supportFragmentManager,
                    TAG
                )*/
                viewModel.setPhone(binding.editTextPhone.text.toString())
                //viewModel.verifiedPhone()
                safeNavigate(R.id.action_registerLogin_to_registerInfo)
            }

            it.spinnerCountry.adapter = CountryCodeSpinnerAdapter(
                requireContext(),
                listOf(
                    CountryDisplayItem(
                        "+52",
                        "Mexico",
                        asp.android.aspandroidmaterial.R.drawable.flag_icon
                    )
                )
            )

            it.spinnerCountry.setSelection(1, true)
            it.spinnerCountry.isEnabled = false
            it.spinnerCountry.isClickable = false
        }

        initViewModel()
    }

    private fun initViewModel() {
        viewModel.let {
            it.successResponse.observe(viewLifecycleOwner) { codeResponseData ->
                dialog.dismiss()
                when (codeResponseData.codigo) {
                    ServerErrorCodes.SUCCESS.ordinal -> {
                        safeNavigate(R.id.action_registerLogin_to_registerInfo)
                    }

                    else -> {
                        showCustomDialogError(
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

    }

    override fun onClickBackPressed() {
        requireActivity().onBackPressed()
    }

    override fun onClickWhatsappIcon() {
        requireActivity().showSingleButtonDialog(
            "Información",
            "call_center@aspintegraopciones.com\n664 204 1866",
            "Aceptar"
        )
    }
}