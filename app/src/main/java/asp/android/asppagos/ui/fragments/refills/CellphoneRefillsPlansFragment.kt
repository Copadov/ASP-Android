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
import asp.android.asppagos.databinding.FragmentCellphoneRefillsPlansBinding
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.ui.viewbindings.CellphonePlanItem
import asp.android.asppagos.utils.fromJson
import asp.android.asppagos.utils.toJson
import com.xwray.groupie.GroupieAdapter

class CellphoneRefillsPlansFragment : BaseFragment(),
    ASPMaterialToolbarMainDashboard.ASPMaterialToolbarMainDashboardListeners {

    override var TAG: String = this.javaClass.name

    private val binding: FragmentCellphoneRefillsPlansBinding by lazy {
        FragmentCellphoneRefillsPlansBinding.inflate(layoutInflater)
    }

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
        setupRecycler()
        arguments?.let {
            it.getString(PLAN_MAP)
                ?.fromJson<Map<String, CellphoneRefillServiceResponse>>()
                ?.let { planMap ->
                    setupInfo(planMap)
                }
        }
    }

    override fun onClickBackButton() {
        onClickBackPressed()
    }

    private fun setupRecycler() {
        binding.rCellphoneRefills.adapter = adapter
    }

    private fun setupToolbar() {
        binding.toolbar.setASPMaterialToolbarsListeners(this)
    }

    private fun setupInfo(planMap: Map<String, CellphoneRefillServiceResponse>) {
        planMap.entries.first().value.apply {
            service?.let { title ->
                binding.toolbar.setTitle(title.lowercase().replaceFirstChar { char -> char.uppercase() })
            }
        }
        adapter.clear()
        adapter.addAll(
            planMap.map { (key, value) ->
                CellphonePlanItem(
                    planName = key,
                    planSelected = value
                ) {
                    moveToAmountScreen(it)
                }
            }
        )
    }

    private fun moveToAmountScreen(cellphoneRefillServiceResponse: CellphoneRefillServiceResponse) {
        safeNavigate(
            R.id.cellphoneRefillsAmountFragment, bundleOf(
                Pair(
                    CellphoneRefillsMainFragment.SELECTED_REFILL_COMPANY,
                    cellphoneRefillServiceResponse.toJson()
                )
            )
        )
    }

    companion object {
        const val PLAN_MAP = "plan_map"
        const val PLAN_NAME = "plan_NAME"
    }
}