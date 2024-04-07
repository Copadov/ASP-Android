package asp.android.asppagos.ui.fragments.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import asp.android.aspandroidmaterial.ui.toolbars.ASPMaterialToolbarMainDashboard
import asp.android.asppagos.R
import asp.android.asppagos.databinding.FragmentPinSuccessAssignationLayoutBinding
import asp.android.asppagos.ui.fragments.BaseFragment

class AssignationPinSuccessFragment : BaseFragment(),
    ASPMaterialToolbarMainDashboard.ASPMaterialToolbarMainDashboardListeners {

    private var _binding: FragmentPinSuccessAssignationLayoutBinding? = null
    private val binding get() = _binding!!
    override var TAG: String = this.javaClass.name

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPinSuccessAssignationLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.let {

            it.pinSuccessChangeToolbar.setASPMaterialToolbarsListeners(this)
            it.pinSuccessChangeToolbar.setTitle(getString(R.string.pin_success_toolbar_title))


            it.buttonContinue.setOnClickListener {
                safeNavigate(R.id.action_global_myCardsFragment)
            }

            it.step1Layout.imageStep.setImageResource(R.drawable.step_1)
            it.step2Layout.imageStep.setImageResource(R.drawable.step_2)
            it.step3Layout.imageStep.setImageResource(R.drawable.step_3)
            it.step4Layout.imageStep.setImageResource(R.drawable.step_4)

            it.step1Layout.stepText.text =
                getString(R.string.success_pin_step_1_text)
            it.step2Layout.stepText.text =
                getString(R.string.success_pin_step_2)
            it.step3Layout.stepText.text =
                getString(R.string.success_pin_step_3)
            it.step4Layout.stepText.text =
                getString(R.string.success_pin_step_4)
        }
    }

    override fun onClickBackButton() {
        findNavController().popBackStack()
    }
}