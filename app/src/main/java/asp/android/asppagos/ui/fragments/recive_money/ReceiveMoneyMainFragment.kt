package asp.android.asppagos.ui.fragments.recive_money

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import asp.android.aspandroidmaterial.ui.toolbars.ASPMaterialToolbarMainDashboard
import asp.android.asppagos.R
import asp.android.asppagos.databinding.FragmentReceiveMoneyMainBinding
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.ui.fragments.recive_money.ReceiveMoneyInfoFragment.Companion.BRANCH
import asp.android.asppagos.ui.fragments.recive_money.ReceiveMoneyInfoFragment.Companion.EASY_WINDOW
import asp.android.asppagos.ui.fragments.recive_money.ReceiveMoneyInfoFragment.Companion.SPEI
import asp.android.asppagos.ui.fragments.recive_money.ReceiveMoneyInfoFragment.Companion.TYPE_SCREEN

class ReceiveMoneyMainFragment : BaseFragment(),
    ASPMaterialToolbarMainDashboard.ASPMaterialToolbarMainDashboardListeners {

    override var TAG: String = this.javaClass.name

    private val binding: FragmentReceiveMoneyMainBinding by lazy {
        FragmentReceiveMoneyMainBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupIconButtons()
        setupOnClickListener()
    }

    override fun onClickBackButton() {
        onClickBackPressed()
    }

    private fun setupIconButtons() {
        binding.apply {
            ivSpei.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_spei_small
                )
            )
            ivBranch.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_branch_small
                )
            )
            ivEasyWindow.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_easy_window_small
                )
            )
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setTitle(getString(R.string.receive_money))
        binding.toolbar.setASPMaterialToolbarsListeners(this)
    }

    private fun setupOnClickListener() {
        binding.apply {
            cSpei.setOnClickListener {
                safeNavigate(
                    R.id.receiveMoneyInfoFragment,
                    bundleOf(TYPE_SCREEN to SPEI)
                )
            }
            cBranch.setOnClickListener {
                safeNavigate(
                    R.id.receiveMoneyInfoFragment,
                    bundleOf(TYPE_SCREEN to BRANCH)
                )
            }
            cEasyWindow.setOnClickListener {
                safeNavigate(
                    R.id.receiveMoneyInfoFragment,
                    bundleOf(TYPE_SCREEN to EASY_WINDOW)
                )
            }
        }
    }
}