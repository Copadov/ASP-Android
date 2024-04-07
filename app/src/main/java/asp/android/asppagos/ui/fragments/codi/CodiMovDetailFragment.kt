package asp.android.asppagos.ui.fragments.codi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import asp.android.asppagos.data.models.codi.CodiMovResponse
import asp.android.asppagos.databinding.FragmentCodiMovDetailBinding
import asp.android.asppagos.ui.fragments.BaseFragment

class CodiMovDetailFragment(private val codiMovResponse: CodiMovResponse? = null): Fragment() {
    private var _binding: FragmentCodiMovDetailBinding? = null
    private val binding get() = _binding!!
     var TAG: String = this.javaClass.name


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentCodiMovDetailBinding.inflate(inflater, container, false)

        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.let {
            if (codiMovResponse?.incomeFrom.equals("buyer")){
                it.txtFolio.text = "Nombre Comprador"
                it.txtInputFolio.text = codiMovResponse?.owner
                it.txtAccount.text = "Número de cuenta"
                it.txtInputAccount.text = codiMovResponse?.acc
                it.txtInputConcept.text = codiMovResponse?.concept
                it.linearLayoutDate.visibility= View.GONE
                it.txtInputDate.text = ""
                it.linearLayoutPhone.visibility= View.GONE
                it.txtInputPhone.text = ""
                it.linearLayoutCr.visibility= View.GONE
                it.linearLayoutReference.visibility = View.GONE
            }else{
                it.linearLayoutDate.visibility= View.VISIBLE
                it.linearLayoutPhone.visibility= View.VISIBLE
                it.linearLayoutCr.visibility= View.VISIBLE
                it.linearLayoutReference.visibility = View.VISIBLE
                it.txtInputFolio.text = codiMovResponse?.folio
                it.txtAccount.text = "Monto"
                it.txtInputAccount.text = "$${codiMovResponse?.amount.toString()}"
                it.txtInputConcept.text = codiMovResponse?.concept
                it.txtInputDate.text = codiMovResponse?.requestDate
                it.txtInputPhone.text = codiMovResponse?.processDate
                it.txtInputCr.text = codiMovResponse?.cr
                it.txtInputReference.text = codiMovResponse?.reference
            }
        }

    }

}