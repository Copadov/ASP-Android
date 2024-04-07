package asp.android.asppagos.ui.adapters

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import asp.android.asppagos.ui.fragments.onboarding.OnboardingFragment

class OnBoardingViewPagerAdapter(activity: AppCompatActivity, private val itemsCount : Int) :
    FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return itemsCount
    }

    override fun createFragment(position: Int): Fragment {
        return OnboardingFragment.getInstance(position)
    }
}