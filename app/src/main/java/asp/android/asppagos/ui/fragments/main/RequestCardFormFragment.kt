package asp.android.asppagos.ui.fragments.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import asp.android.aspandroidmaterial.ui.toolbars.ASPMaterialToolbarMainDashboard
import asp.android.asppagos.databinding.FragmentRequestCardFormBinding
import asp.android.asppagos.ui.adapters.DestinationFormPagerAdapter
import asp.android.asppagos.ui.fragments.BaseFragment

class RequestCardFormFragment : BaseFragment(),
    ASPMaterialToolbarMainDashboard.ASPMaterialToolbarMainDashboardListeners {

    private var _binding: FragmentRequestCardFormBinding? = null
    private val binding get() = _binding!!

    override var TAG: String = this.javaClass.name

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRequestCardFormBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.let {
            it.destinationFormPager.adapter = DestinationFormPagerAdapter(childFragmentManager)

            it.ASPMaterialToolbarDashboard.setASPMaterialToolbarsListeners(this)
            it.ASPMaterialToolbarDashboard.setTitle("Solicitud de tarjeta física")
        }
    }

    override fun onClickBackButton() {
        findNavController().popBackStack()
    }
}