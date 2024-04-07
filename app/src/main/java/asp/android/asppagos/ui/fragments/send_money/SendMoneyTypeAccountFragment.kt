package asp.android.asppagos.ui.fragments.send_money

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import asp.android.aspandroidmaterial.ui.toolbars.ASPMaterialToolbarMainDashboard
import asp.android.asppagos.R
import asp.android.asppagos.databinding.FragmentSendMoneyTypeAccountBinding
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.ui.fragments.send_money.SendMoneyRegisterAccountFragment.Companion.TYPE_REGISTER_ACCOUNT
import asp.android.asppagos.ui.viewbindings.ItemTypeAccount
import com.xwray.groupie.GroupieAdapter

class SendMoneyTypeAccountFragment : BaseFragment(),
    ASPMaterialToolbarMainDashboard.ASPMaterialToolbarMainDashboardListeners {

    override var TAG: String = this.javaClass.name

    private val binding: FragmentSendMoneyTypeAccountBinding by lazy {
        FragmentSendMoneyTypeAccountBinding.inflate(layoutInflater)
    }

    private val adapter: GroupieAdapter by lazy {
        GroupieAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupList()
    }

    private fun setupList() {
        binding.rvTypeAccountList.adapter = adapter
        adapter.clear()
        adapter.addAll(
            mutableListOf(
                ItemTypeAccount(typeName = "Persona física") {
                    moveToRegisterAccount(it)
                },
                ItemTypeAccount(typeName = "Persona moral", isBusiness = true) {
                    moveToRegisterAccount(it)
                })
        )
    }

    private fun setupToolbar() {
        binding.toolbar.setTitle(getString(R.string.send_money))
        binding.toolbar.setASPMaterialToolbarsListeners(this)
    }

    override fun onClickBackButton() {
        onClickBackPressed()
    }

    private fun moveToRegisterAccount(isBusiness: Boolean) {
        safeNavigate(
            R.id.sendMoneyRegisterAccountFragment,
            bundleOf(Pair(TYPE_REGISTER_ACCOUNT, isBusiness))
        )
    }

}