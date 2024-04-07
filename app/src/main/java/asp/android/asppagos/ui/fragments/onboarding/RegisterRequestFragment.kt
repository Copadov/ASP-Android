package asp.android.asppagos.ui.fragments.onboarding

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import asp.android.aspandroidmaterial.ui.toolbars.ASPTMaterialToolbar
import asp.android.asppagos.R
import asp.android.asppagos.databinding.FragmentRegisterRequestBinding
import asp.android.asppagos.utils.hideKeyboard
import asp.android.asppagos.utils.openURLWithBrowser

class RegisterRequestFragment : Fragment(),
    ASPTMaterialToolbar.ASPMaterialToolbarsListeners {

    private var _binding: FragmentRegisterRequestBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRegisterRequestBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireContext().hideKeyboard(view)

        binding.let {
            it.acceptTermsCheck.setOnCheckedChangeListener { _, b ->
                it.registerAccountStartButton.isEnabled = b
            }

            it.textViewTerms.setOnClickListener {
                requireActivity().openURLWithBrowser(getString(R.string.register_url_terms_url))
            }

            it.textViewPrivacy.setOnClickListener {
                requireActivity().openURLWithBrowser(getString(R.string.register_url_priv_url))
            }

            it.textViewTerms.paintFlags = it.textViewTerms.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            it.textViewPrivacy.paintFlags =
                it.textViewPrivacy.paintFlags or Paint.UNDERLINE_TEXT_FLAG

            it.ASPTMaterialToolbarRegisterRequest.setASPMaterialToolbarsListeners(this)

            it.registerAccountStartButton.setOnClickListener {
                safeNavigate(R.id.action_registerRequestFragment_to_ocrDataValidation)
                //.navigate(R.id.action_registerRequestFragment_to_formDataUser11Fragment)
                //.navigate(R.id.action_registerRequestFragment_to_formDataUser10Fragment)
                //.navigate(R.id.action_registerRequestFragment_to_formDataUser8Fragment)

            }
        }
    }

    override fun onClickBackPressed() {
        activity?.onBackPressed()
    }

    override fun onClickWhatsappIcon() {
    }

    /**
     * Method to ensure that the destination is known by the current node.
     * Supports both navigating via an Action and directly to a Destination.
     *
     * @param actionId an action id or a destination id to navigate to.
     */
    fun safeNavigate(@IdRes actionId: Int) = with(findNavController()) {
        if (currentDestination?.getAction(actionId) != null || findDestination(actionId) != null) {
            navigate(actionId)
        }
    }
}