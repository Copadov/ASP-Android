package asp.android.asppagos.ui.fragments.refills

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import asp.android.aspandroidmaterial.ui.toolbars.ASPMaterialToolbarMainDashboard
import asp.android.asppagos.R
import asp.android.asppagos.data.models.cellphone_refill.CellphoneRefillServiceResponse
import asp.android.asppagos.data.models.cellphone_refill.CompanyCellphoneRefills
import asp.android.asppagos.databinding.FragmentCellphoneRefillsMainBinding
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.ui.fragments.refills.CellphoneRefillsPlansFragment.Companion.PLAN_MAP
import asp.android.asppagos.ui.fragments.refills.CellphoneRefillsPlansFragment.Companion.PLAN_NAME
import asp.android.asppagos.ui.states.UIStates
import asp.android.asppagos.ui.viewbindings.CellphoneRefillsItem
import asp.android.asppagos.ui.viewmodels.cellphone_refills.CellphoneRefillsViewModel
import asp.android.asppagos.utils.showCustomDialogError
import asp.android.asppagos.utils.toJson
import com.xwray.groupie.GroupieAdapter
import org.koin.android.ext.android.inject

class CellphoneRefillsMainFragment : BaseFragment(),
    ASPMaterialToolbarMainDashboard.ASPMaterialToolbarMainDashboardListeners {

    override var TAG: String = this.javaClass.name

    private val binding: FragmentCellphoneRefillsMainBinding by lazy {
        FragmentCellphoneRefillsMainBinding.inflate(layoutInflater)
    }

    private val viewModel: CellphoneRefillsViewModel by inject()

    private val adapter: GroupieAdapter by lazy {
        GroupieAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupObservable()
        setupRecycler()
        viewModel.getCellphoneRefills()
    }

    private fun setupToolbar() {
        binding.toolbar.setTitle(getString(R.string.cellphone_refills))
        binding.toolbar.setASPMaterialToolbarsListeners(this)
    }

    private fun setupObservable() {
        viewModel.uiState.observe(viewLifecycleOwner) {
            handleResponse(state = it)
        }
    }

    private fun setupRecycler() {
        binding.rCellphoneRefills.adapter = adapter
    }

    override fun onClickBackButton() {
        onClickBackPressed()
    }

    private fun handleResponse(state: UIStates<MutableList<CompanyCellphoneRefills>>) {
        when (state) {
            is UIStates.Success<MutableList<CompanyCellphoneRefills>> -> handleSuccess(state.value as MutableList<CompanyCellphoneRefills>)
            is UIStates.Loading -> dialog.show(childFragmentManager, TAG)
            is UIStates.Error -> {
                dialog.dismiss()
                showCustomDialogError(
                    message1 = "Información",
                    message2 = state.message
                )
            }
            else -> {}
        }
    }

    private fun handleSuccess(serviceList: MutableList<CompanyCellphoneRefills>) {
        adapter.clear()
        adapter.addAll(
            serviceList.map {
                CellphoneRefillsItem(cellphoneRefill = it) { itemTaped ->
                    selectScreenToMove(itemTaped)
                }
            }
        )
        dialog.dismiss()
    }

    private fun selectScreenToMove(companyCellphoneRefills: CompanyCellphoneRefills) {

        // Get map plans
        val companyMapPlans = mutableMapOf<String, CellphoneRefillServiceResponse>()

        companyCellphoneRefills.planList.forEach { plan ->
            companyMapPlans[plan.productType ?: "none"] = plan
        }

        // Check rule

        if (companyMapPlans.size != 1) {
            moveToPlansScreen(companyMapPlans)
        } else {
            companyMapPlans[companyMapPlans.keys.first()]?.let {
                moveToAmountScreen(cellphoneRefillServiceResponse =  it)
            }
        }
    }

    private fun moveToPlansScreen(planMaps: Map<String, CellphoneRefillServiceResponse>) {
        safeNavigate(
            R.id.cellphonePlansRefillFragment, bundleOf(
                Pair(
                    PLAN_MAP, planMaps.toJson()
                )
            )
        )
    }

    private fun moveToAmountScreen(cellphoneRefillServiceResponse: CellphoneRefillServiceResponse) {
        safeNavigate(
            R.id.cellphoneRefillsAmountFragment, bundleOf(
                Pair(
                    SELECTED_REFILL_COMPANY, cellphoneRefillServiceResponse.toJson()
                )
            )
        )
    }

    companion object {
        const val SELECTED_REFILL_COMPANY = "selected_refill_company"
    }

}