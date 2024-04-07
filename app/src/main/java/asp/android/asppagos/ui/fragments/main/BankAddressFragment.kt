package asp.android.asppagos.ui.fragments.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import asp.android.asppagos.R
import asp.android.asppagos.databinding.FragmentBankAddressBinding
import asp.android.asppagos.ui.activities.PinInputActivity
import asp.android.asppagos.ui.adapters.CitySpinnerArrayAdapter
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.ui.viewmodels.MainDashboardViewModel
import asp.android.asppagos.utils.DATA_MODIFIED_CODE
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class BankAddressFragment : BaseFragment() {

    private var _binding: FragmentBankAddressBinding? = null
    private val binding get() = _binding!!
    override var TAG: String = this.javaClass.name
    val viewModel: MainDashboardViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBankAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.let {
            it.continueButton.setOnClickListener {
                intent = Intent(requireContext(), PinInputActivity::class.java)
                startActivityForResult(intent, DATA_MODIFIED_CODE)
            }
        }

        initViewModel()
    }

    private fun initViewModel() {
        viewModel.let {
            it.getBankListResponseData.observe(viewLifecycleOwner) { response ->
                binding.banksList.adapter =
                    CitySpinnerArrayAdapter(
                        requireContext(),
                        R.layout.spinner_city_layout_item,
                        listOf(
                            CitySpinnerArrayAdapter.CityModel(1, "Sucursal 1"),
                            CitySpinnerArrayAdapter.CityModel(2, "Sucursal 2"),
                            CitySpinnerArrayAdapter.CityModel(3, "Sucursal 3"),
                        )
                    )
            }

            it.getBankList()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                findNavController().popBackStack()
            }

            Activity.RESULT_CANCELED -> {

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //RxBus.publish(DataFormResponseEvent(true))
        _binding = null
    }
}