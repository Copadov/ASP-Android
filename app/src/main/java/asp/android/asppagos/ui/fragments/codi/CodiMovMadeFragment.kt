package asp.android.asppagos.ui.fragments.codi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import asp.android.asppagos.R
import asp.android.asppagos.data.models.codi.CodiMovResponse
import asp.android.asppagos.data.models.codi.TransactionsRequest
import asp.android.asppagos.databinding.FragmentCodiMovMadeBinding
import asp.android.asppagos.ui.activities.MainDashboardActivity
import asp.android.asppagos.ui.adapters.ClickListener
import asp.android.asppagos.ui.adapters.CodiMovRecViewAdapter
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.utils.toJson
import org.koin.androidx.viewmodel.ext.android.viewModel


class CodiMovMadeFragment : BaseFragment() {
    private val binding: FragmentCodiMovMadeBinding by lazy {
        FragmentCodiMovMadeBinding.inflate(layoutInflater)
    }
    override var TAG: String = this.javaClass.name
    private lateinit var adapter: CodiMovRecViewAdapter

    private val codiMovMadeViewModel: CodiMovMadeViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservable()
        populateList()
    }

    private fun setupRecyclerView() {
        adapter = CodiMovRecViewAdapter(mutableListOf(), requireContext()).apply {
            setOnClickListener(object : ClickListener<CodiMovResponse> {
                override fun onItemClick(data: CodiMovResponse) {
                    data.incomeFrom = "madePayment"
                    safeNavigate(
                        R.id.action_codiMovFragment_to_detailMovCodiFragment,
                        bundleOf("data" to data.toJson())
                    )
                }
            })
        }
        binding.codiRecycleViewMade.apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = this@CodiMovMadeFragment.adapter
        }
    }


    private fun populateList() {
        dialog.show(childFragmentManager, TAG)
        val request = TransactionsRequest(MainDashboardActivity.accountData.cuenta.clabe)
        codiMovMadeViewModel.getHistoryMovements(request)
    }

    @Override
    override fun onClickBackPressed() {
        parentFragmentManager.executePendingTransactions()
        findNavController().popBackStack()
    }
    private fun setupObservable() {
        codiMovMadeViewModel.codiMovResponseObservable.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            dialog.dismiss()
        }
    }

}