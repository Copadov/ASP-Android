package asp.android.asppagos.ui.fragments.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import asp.android.aspandroidmaterial.ui.toolbars.ASPTMaterialToolbar
import asp.android.asppagos.databinding.FragmentWelcomeViewBinding
import asp.android.asppagos.ui.activities.MainActivity
import asp.android.asppagos.ui.activities.SplashScreen
import asp.android.asppagos.utils.DATA_WELCOME_SELECTION
import asp.android.asppagos.utils.DATA_WELCOME_SELECTION_EXTRA_LOGIN
import asp.android.asppagos.utils.showSingleButtonDialog

class WelcomeViewFragment : Fragment(),
    ASPTMaterialToolbar.ASPMaterialToolbarsListeners {

    private var _binding: FragmentWelcomeViewBinding? = null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentWelcomeViewBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.let {

            it.continueButton.setOnClickListener {
                startActivity(Intent(requireContext(), MainActivity::class.java).apply {
                    putExtra(DATA_WELCOME_SELECTION, DATA_WELCOME_SELECTION_EXTRA_LOGIN)
                })
            }
        }
    }

    override fun onClickBackPressed() {
        requireActivity().finish()
    }

    override fun onClickWhatsappIcon() {
        requireActivity().showSingleButtonDialog(
            "Información",
            "call_center@aspintegraopciones.com\n664 204 1866",
            "Aceptar"
        )
    }
}