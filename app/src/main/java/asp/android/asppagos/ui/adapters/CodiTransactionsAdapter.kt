package asp.android.asppagos.ui.adapters

import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import asp.android.asppagos.data.interfaces.CodiAspAPI
import asp.android.asppagos.data.models.codi.CodiMovResponse
import asp.android.asppagos.ui.fragments.codi.CodiMovMadeFragment
import asp.android.asppagos.ui.fragments.codi.CodiMovPendingFragment
import asp.android.asppagos.utils.EncryptUtils.decryptByGeneralKey
class CodiTransactionsAdapter(fragmentManager:FragmentManager,lifeCycle:Lifecycle,private val context: Context)
    : FragmentStateAdapter(fragmentManager,lifeCycle) {

    private var TAG: String = this.javaClass.name

    private var ls = mutableListOf<CodiMovResponse>()
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        var f:Fragment? = null
        f = if (position == 0){
            CodiMovMadeFragment()
        }else{
            CodiMovPendingFragment(ls)
        }
        return f!!
    }

}