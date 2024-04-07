package asp.android.asppagos.ui.fragments.onboarding

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import asp.android.asppagos.R
import asp.android.asppagos.databinding.FragmentFormDataUserBinding
import asp.android.asppagos.ui.adapters.DataConfirmationAdapter
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.ui.viewmodels.OnboardingViewModel
import asp.android.asppagos.utils.*
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import java.util.concurrent.TimeUnit

class FormDataUser8Fragment : BaseFragment() {

    private var isCounting: Boolean = false
    private lateinit var timer: CountDownTimer
    private var isResend: Boolean = false
    private var _binding: FragmentFormDataUserBinding? = null
    private val binding get() = _binding!!
    val viewModel: OnboardingViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFormDataUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override var TAG: String = this.javaClass.name

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        timer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {

                // long minutes = (milliseconds / 1000) / 60;
                val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)

                // long seconds = (milliseconds / 1000);
                val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)

                binding.let {

                    it.textMessageButton.text = ""

                    it.textMessageButton.append(
                        "El proceso puede tardar ", R.color.text_1
                    )
                    it.textMessageButton.append(
                        "$minutes:$seconds\n\n", R.color.text_3
                    )

                    it.textMessageButton.append(
                        "No recibí el correo, ", R.color.text_1
                    )

                    it.textMessageButton.appendWithFont(
                        "reenviar", R.color.text_disable
                    )
                }
            }

            override fun onFinish() {
                binding.let {
                    it.textMessageButton.text = ""

                    it.textMessageButton.append(
                        "El proceso puede tardar ", R.color.text_1
                    )
                    it.textMessageButton.append(
                        "\n\n", R.color.text_3
                    )

                    it.textMessageButton.append(
                        "No recibí el correo, ", R.color.text_1
                    )

                    it.textMessageButton.appendWithFont(
                        "reenviar", R.color.text_1
                    )
                }

                isCounting = false
            }
        }

        binding.let {

            timer.start()
            isCounting = true

            it.indicadorDots.setIndicators(dotsCount, 7)

            it.textViewTitleViewHead.text =
                getString(R.string.onboarding_form_title_toolbar_text_account_activation)

            it.ASPMaterialToolbarFormData.setASPMaterialToolbarsListeners(this)

            it.recyclerViewFormDataUser.visibility = View.GONE

            it.recyclerViewFormDataUser.adapter = DataConfirmationAdapter(
                emptyList()
            ) {}

            it.recyclerViewFormDataUser.layoutManager = LinearLayoutManager(requireContext())

            it.inputForm.visibility = View.GONE
            it.InputForm2.visibility = View.GONE
            it.InputForm3.visibility = View.GONE
            it.textDescriptive.visibility = View.GONE
            it.textInformationMessage.visibility = View.VISIBLE

            it.textInformationMessage.append(
                "Ingresa a tu correo\n", R.color.text_1
            )

            it.textInformationMessage.appendWithFont(
                viewModel.email.value!!, R.color.text_2
            )
            it.textInformationMessage.append(
                "\ny da click en la liga que te enviamos.", R.color.text_1
            )

            it.textMessageButton.append(
                "El proceso puede tardar ", R.color.text_1
            )
            it.textMessageButton.append(
                "1:00\n\n", R.color.text_3
            )

            it.textMessageButton.append(
                "No recibí el correo, ", R.color.text_1
            )

            it.textMessageButton.appendWithFont(
                "reenviar", R.color.text_1
            )

            it.textMessageButton.setOnClickListener {
                if (!isCounting) {
                    timer.start()


                    dialog.show(
                        requireActivity().supportFragmentManager,
                        TAG
                    )
                    isResend = true
                    viewModel.validateEmail("1")
                }
            }

            it.buttonContinueForm.isEnabled = true

            it.buttonContinueForm
                .setOnClickListener {
                    dialog.show(
                        requireActivity().supportFragmentManager,
                        TAG
                    )
                    viewModel.validateEmail("2")
                }
        }

        initViewModel()
    }

    private fun initViewModel() {
        viewModel.let {
            it.successResponse.observe(viewLifecycleOwner) { codeResponse ->
                dialog.dismiss()
                when (codeResponse.codigo) {
                    ServerErrorCodes.SUCCESS.ordinal -> {
                        if (!isResend) {
                            safeNavigate(R.id.action_formDataUser8Fragment_to_formDataUser9Fragment)
                        } else {
                            isResend = false
                            showCustomDialogInfo(
                                "Información",
                                codeResponse.mensaje,
                            )
                        }
                    }

                    else -> {
                        showCustomDialogError(
                            "Información",
                            codeResponse.mensaje,
                        )
                    }
                }
            }

            it.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
                dialog.dismiss()
                requireView().snackbar(errorMessage)
            }
        }
    }
}