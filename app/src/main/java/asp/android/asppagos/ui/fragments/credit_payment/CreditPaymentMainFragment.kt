package asp.android.asppagos.ui.fragments.credit_payment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import asp.android.aspandroidmaterial.ui.toolbars.ASPMaterialToolbarMainDashboard
import asp.android.asppagos.R
import asp.android.asppagos.data.models.CreditosActivo
import asp.android.asppagos.databinding.FragmentCreditPaymentMainBinding
import asp.android.asppagos.ui.adapters.UserCreditAdapter
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.ui.states.UIStates
import asp.android.asppagos.ui.viewmodels.credit_payment.CreditPaymentMainViewModel
import asp.android.asppagos.utils.toJson
import org.koin.android.ext.android.inject

class CreditPaymentMainFragment : BaseFragment(),
    ASPMaterialToolbarMainDashboard.ASPMaterialToolbarMainDashboardListeners {

    override var TAG: String = this.javaClass.name

    private val viewModel: CreditPaymentMainViewModel by inject()

    private val binding: FragmentCreditPaymentMainBinding by lazy {
        FragmentCreditPaymentMainBinding.inflate(layoutInflater)
    }

    private val adapter: UserCreditAdapter by lazy {
        UserCreditAdapter() { creditUser ->
            safeNavigate(R.id.creditPaymentInfoFragment, bundleOf(
                Pair(
                    ACTIVE_CREDIT_DETAIL, creditUser.toJson())
            ))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog.show(childFragmentManager, TAG)
        setupToolbar()
        setupRecycler()
        setupObservable()
    }

    override fun onClickBackButton() {
        onClickBackPressed()
    }

    private fun setupToolbar() {
        binding.toolbar.setTitle(getString(R.string.credit_payment_toolbar_title))
        binding.toolbar.setASPMaterialToolbarsListeners(this)
    }

    private fun setupRecycler() {
        binding.rvCreditList.adapter = adapter
    }

    private fun setupObservable() {
        viewModel.uiState.observe(viewLifecycleOwner) {
            handleUIState(it)
        }
    }

    private fun handleUIState(state: UIStates<MutableList<CreditosActivo>>) {
        when (state) {
            is UIStates.Loading -> dialog.show(childFragmentManager, TAG)
            is UIStates.Success -> handleSuccessState(state)
            else -> {}
        }
    }

    private fun handleSuccessState(state: UIStates.Success<MutableList<CreditosActivo>>) {
        dialog.dismiss()
        state.value?.let {
            if(it.isNotEmpty()) {
                adapter.submit(it)
                binding.rvCreditList.isVisible = true
                binding.tvEmptyState.isVisible = false
            } else {
                binding.rvCreditList.isVisible = false
                binding.tvEmptyState.isVisible = true
            }
            binding.cCreditActives.isVisible = true
        }
    }

    companion object {
        const val ACTIVE_CREDIT_DETAIL = "active_credit_detail"
    }
}