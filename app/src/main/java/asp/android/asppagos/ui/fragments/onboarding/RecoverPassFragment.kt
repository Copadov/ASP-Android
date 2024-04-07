package asp.android.asppagos.ui.fragments.onboarding

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import asp.android.asppagos.R
import asp.android.asppagos.databinding.FragmentFormDataUserBinding
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.ui.viewmodels.LoginViewModel
import asp.android.asppagos.utils.ServerErrorCodes
import asp.android.asppagos.utils.showCustomDialogError
import asp.android.asppagos.utils.showCustomDialogInfo
import asp.android.asppagos.utils.showKeyboard
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class RecoverPassFragment : BaseFragment() {

    private var _binding: FragmentFormDataUserBinding? = null
    private val binding get() = _binding!!
    val viewModel: LoginViewModel by activityViewModel()

    override var TAG: String = this.javaClass.name

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFormDataUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.let {
            it.indicadorDots.visibility = View.GONE

            it.textViewTitleViewHead.text =
                "Recupera tu contraseña"

            it.ASPMaterialToolbarFormData.setASPMaterialToolbarsListeners(this)
            it.recyclerViewFormDataUser.visibility = View.GONE

            it.inputForm.visibility = View.VISIBLE
            it.InputForm2.visibility = View.GONE
            it.InputForm3.visibility = View.GONE

            it.inputForm.hint = "Ingresa tu email"
            it.textDescriptive.visibility = View.VISIBLE
            it.textDescriptive.text =
                "Ingresa el correo con el que estás registrado y te enviaremos las instrucciones para el cambio de contraseña."
            it.inputForm.inputType = InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS

            it.inputForm.requestFocus()
            it.inputForm.showKeyboard()

            it.buttonContinueForm.text = "Recuperar"
            it.inputForm.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    it.buttonContinueForm.isEnabled =
                        p0!!.isNotEmpty()
                                &&
                                android.util.Patterns.EMAIL_ADDRESS.matcher(p0).matches()
                }

                override fun afterTextChanged(p0: Editable?) {
                }
            })

            it.buttonContinueForm.setOnClickListener {
                dialog.show(requireActivity().supportFragmentManager, TAG)
                viewModel.setEmail(binding.inputForm.text.toString())
                viewModel.recoverPass()
            }
        }

        initViewModel()
    }

    private fun initViewModel() {

        viewModel.let {
            it.recoverPassResponse.observe(viewLifecycleOwner) { codeResponseData ->
                when (codeResponseData.codigo) {
                    ServerErrorCodes.SUCCESS.ordinal -> {
                        dialog.dismiss()

                        findNavController().popBackStack()

                        showCustomDialogInfo(
                            getString(R.string.information_dialog_text),
                            "Te enviamos un correo con las instrucciones para restablecer tu contraseña.",
                        )
                    }

                    else -> {
                        dialog.dismiss()
                        showCustomDialogError("Información", codeResponseData.mensaje)
                    }
                }
            }
        }

    }
}