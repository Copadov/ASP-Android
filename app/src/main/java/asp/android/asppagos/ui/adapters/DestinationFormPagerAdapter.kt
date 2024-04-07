package asp.android.asppagos.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import asp.android.asppagos.ui.fragments.main.BankAddressFragment
import asp.android.asppagos.ui.fragments.onboarding.FormAddressFragment

class DestinationFormPagerAdapter (fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> FormAddressFragment()
            1 -> BankAddressFragment()
            else -> throw IllegalStateException("Unexpected position $position")
        }
    }

    override fun getCount(): Int {
        return 2 // Número de páginas
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> "Domicilio"
            1 -> "Sucursal"
            else -> throw IllegalStateException("Unexpected position $position")
        }
    }
}