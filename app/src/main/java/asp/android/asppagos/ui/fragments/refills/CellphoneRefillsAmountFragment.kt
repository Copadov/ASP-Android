package asp.android.asppagos.ui.fragments.refills

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import asp.android.aspandroidmaterial.ui.toolbars.ASPMaterialToolbarMainDashboard
import asp.android.asppagos.R
import asp.android.asppagos.data.models.cellphone_refill.AmountRefillServiceResponse
import asp.android.asppagos.data.models.cellphone_refill.CellphoneRefillServiceResponse
import asp.android.asppagos.databinding.FragmentCellphoneRefillsAmountBinding
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.ui.fragments.refills.CellphoneRefillsMainFragment.Companion.SELECTED_REFILL_COMPANY
import asp.android.asppagos.ui.states.UIStates
import asp.android.asppagos.ui.viewbindings.ItemAmount
import asp.android.asppagos.ui.viewmodels.cellphone_refills.CellphoneRefillsAmountViewModel
import asp.android.asppagos.utils.fromJson
import asp.android.asppagos.utils.toJson
import com.xwray.groupie.GroupieAdapter
import org.koin.android.ext.android.inject

class CellphoneRefillsAmountFragment : BaseFragment(),
    ASPMaterialToolbarMainDashboard.ASPMaterialToolbarMainDashboardListeners {

    private val binding: FragmentCellphoneRefillsAmountBinding by lazy {
        FragmentCellphoneRefillsAmountBinding.inflate(layoutInflater)
    }

    private val viewModel: CellphoneRefillsAmountViewModel by inject()

    private val adapter: GroupieAdapter by lazy {
        GroupieAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            viewModel.setupBundle(
                it.getString(SELECTED_REFILL_COMPANY)?.fromJson<CellphoneRefillServiceResponse>()
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupRecycler()
        setupObservable()
    }

    private fun setupToolbar() {
        viewModel.cellphoneRefill()?.service?.let {
            binding.toolbar.setTitle(it.lowercase().replaceFirstChar { char -> char.uppercase() })
        }
        binding.toolbar.setASPMaterialToolbarsListeners(this)
    }

    private fun setupRecycler() {
        binding.rCellphoneRefills.adapter = adapter
    }

    private fun setupObservable() {
        viewModel.uiState.observe(viewLifecycleOwner) {
            handle(it)
        }
    }

    override var TAG: String = this.javaClass.name

    override fun onClickBackButton() {
        onClickBackPressed()
    }

    private fun handle(state: UIStates<MutableList<AmountRefillServiceResponse>>) {
        when (state) {
            is UIStates.Success -> handleSuccess(state)
            is UIStates.Error -> {}
            else -> {}
        }
    }

    private fun handleSuccess(successState: UIStates.Success<MutableList<AmountRefillServiceResponse>>) {
        successState.value?.map {
            ItemAmount(it) { amountRefillServiceResponse -> moveTo(amountRefillServiceResponse) }
        }?.let {
            adapter.clear()
            adapter.addAll(it)
        }
    }

    private fun moveTo(amount: AmountRefillServiceResponse) {
        safeNavigate(
            R.id.cellphoneRefillsAddNumberFragment, bundleOf(
                Pair(SELECTED_REFILL_COMPANY, viewModel.cellphoneRefill().toJson()),
                Pair(AMOUNT_SELECTED, amount.toJson())
            )
        )
    }

    companion object {
        const val AMOUNT_SELECTED = "amount_selected"
    }

}