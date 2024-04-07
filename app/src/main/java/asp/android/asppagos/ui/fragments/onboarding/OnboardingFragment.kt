package asp.android.asppagos.ui.fragments.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import asp.android.asppagos.R
import asp.android.asppagos.databinding.FragmentOnboardingBinding

class OnboardingFragment : Fragment() {

    companion object {
        private const val ARG_POSITION = "ARG_POSITION"
        fun getInstance(position: Int) = OnboardingFragment().apply {
            arguments = bundleOf(ARG_POSITION to position)
        }
    }

    private lateinit var binding: FragmentOnboardingBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val position = requireArguments().getInt(ARG_POSITION)
        val onBoardingTitles = requireContext().resources.getStringArray(R.array.on_boarding_titles)
        val onBoardingTexts = requireContext().resources.getStringArray(R.array.on_boarding_texts)
        val onBoardingImages = getOnBoardAssetsLocation()
        with(binding) {
            imageOnboarding.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    onBoardingImages[position]
                )
            )
            onboardingTextTitle.text = onBoardingTitles[position]
            textDescription.text = onBoardingTexts[position]
        }
    }

    private fun getOnBoardAssetsLocation(): List<Int> {
        val onBoardAssets: MutableList<Int> = ArrayList()
        onBoardAssets.add(R.drawable.onb_1)
        onBoardAssets.add(R.drawable.onb_2)
        onBoardAssets.add(R.drawable.onb_3)
        onBoardAssets.add(R.drawable.onb_4)
        onBoardAssets.add(R.drawable.onb_5)
        onBoardAssets.add(R.drawable.onb_6)
        return onBoardAssets
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }
}