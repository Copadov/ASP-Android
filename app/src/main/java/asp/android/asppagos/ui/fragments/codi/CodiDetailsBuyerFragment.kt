package asp.android.asppagos.ui.fragments.codi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import asp.android.asppagos.R
import asp.android.asppagos.data.models.codi.CodiMovResponse
import asp.android.asppagos.databinding.FragmentCobrarCodiBinding
import asp.android.asppagos.databinding.FragmentCodiDetailsBuyerBinding
import asp.android.asppagos.databinding.FragmentCodiMovDetailBinding
import asp.android.asppagos.ui.activities.MainDashboardActivity


class CodiDetailsBuyerFragment(private val codiMovResponse: CodiMovResponse? = null) : Fragment() {

    private var _binding: FragmentCodiDetailsBuyerBinding? = null
    private val binding get() = _binding!!
    private var TAG: String = this.javaClass.name




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCodiDetailsBuyerBinding.inflate(inflater, container, false)
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.let {
            it.txtInputBuyer.text = codiMovResponse?.owner!!
            it.txtInputVendor.text = codiMovResponse?.vendor!!
            it.txtInputAccvendor.text = codiMovResponse?.acc!!
            it.txtInputAccount.text = MainDashboardActivity.accountData.cuenta.clabe
        }

    }
/*
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =  FragmentCodiDetailsBuyerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.let {

        }
    }*/


}