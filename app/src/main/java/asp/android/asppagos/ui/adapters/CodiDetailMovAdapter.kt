package asp.android.asppagos.ui.adapters


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import asp.android.asppagos.data.models.codi.CodiMovResponse
import asp.android.asppagos.ui.fragments.codi.CodiDetailsBuyerFragment
import asp.android.asppagos.ui.fragments.codi.CodiMovDetailFragment
import asp.android.asppagos.ui.fragments.codi.CodiMovMadeFragment
import asp.android.asppagos.ui.fragments.codi.CodiMovPendingFragment
import java.util.stream.Collectors

class CodiDetailMovAdapter(fragmentManager: FragmentManager, lifeCycle: Lifecycle
                           ,private val codiMovResponse: CodiMovResponse? = null)
    : FragmentStateAdapter(fragmentManager,lifeCycle) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return if (position == 0){
            codiMovResponse?.incomeFrom =""
           CodiMovDetailFragment(codiMovResponse)
        }else{
            codiMovResponse?.incomeFrom ="buyer"
            CodiDetailsBuyerFragment(codiMovResponse)
        }
    }


    fun getCurrentSelected():CodiMovResponse{
        return codiMovResponse!!
    }
}