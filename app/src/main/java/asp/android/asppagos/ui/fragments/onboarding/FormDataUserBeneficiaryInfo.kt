package asp.android.asppagos.ui.fragments.onboarding

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import asp.android.asppagos.R
import asp.android.asppagos.databinding.FragmentFormDataUserBeneficiaryInfoBinding
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.ui.viewmodels.OnboardingViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class FormDataUserBeneficiaryInfo : BaseFragment() {

    override var TAG: String = this.javaClass.name


    private var _binding: FragmentFormDataUserBeneficiaryInfoBinding? = null
    private val binding get() = _binding!!
    val viewModel: OnboardingViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFormDataUserBeneficiaryInfoBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.let {
            it.indicadorDots.setIndicators(dotsCount, 8)
            it.ASPTMaterialToolbarBeneficiaryInfo.setASPMaterialToolbarsListeners(this)

            it.continueButton.setOnClickListener {
                safeNavigate(R.id.action_formDataUserBeneficiaryInfo_to_formDataUserBeneficiary2)
            }


            it.textViewContractLink.movementMethod = LinkMovementMethod.getInstance()
            val spannable = SpannableString(getString(R.string.form_beneficiary_subtitle_text))
            spannable.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(getString(R.string.contract_url_text_link))
                    )
                    startActivity(intent)
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = true
                    ds.color = ContextCompat.getColor(requireContext(), R.color.link_color)
                }
            }, 0, spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            it.textViewContractLink.text = spannable

            it.checkBox.setOnCheckedChangeListener { _, isChecked ->
                it.continueButton.isEnabled = isChecked
            }
        }

        initViewModel()
    }

    override fun onClickBackPressed() {
        super.onClickBackPressed()
        findNavController().popBackStack()
    }

    private fun initViewModel() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}