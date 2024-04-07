package asp.android.asppagos.ui.fragments.configurations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import asp.android.aspandroidmaterial.ui.toolbars.ASPMaterialToolbarMainDashboard
import asp.android.asppagos.R
import asp.android.asppagos.databinding.FragmentUpdatePersonalCodeBinding
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.ui.fragments.configurations.UpdatePersonalCodeSmsFragment.Companion.UPDATE_PIN
import asp.android.asppagos.ui.viewmodels.configurations.UpdatePersonalCodeViewModel
import org.koin.android.ext.android.inject

class UpdatePersonalCodeFragment : BaseFragment(),
    ASPMaterialToolbarMainDashboard.ASPMaterialToolbarMainDashboardListeners {

    override var TAG: String = this.javaClass.name

    private val binding: FragmentUpdatePersonalCodeBinding by lazy {
        FragmentUpdatePersonalCodeBinding.inflate(layoutInflater)
    }

    private val viewModel: UpdatePersonalCodeViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupWritingListener()
        setupObservables()
        setupOnClickListener()
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
                safeNavigate(
                    R.id.updatePersonalCodeSmsFragment,
                    bundleOf(UPDATE_PIN to validatePinValidator.text.toString())
                )
            }
            btnCancel.setOnClickListener {
                onClickBackPressed()
            }
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setTitle("Actualiza número de\n" + "seguridad personal")
        binding.toolbar.setASPMaterialToolbarsListeners(this)
    }

    override fun onClickBackButton() {
        onClickBackPressed()
    }

}