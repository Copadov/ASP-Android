package asp.android.asppagos.ui.fragments.send_money

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import asp.android.aspandroidmaterial.ui.toolbars.ASPMaterialToolbarMainDashboard
import asp.android.aspandroidmaterial.ui.toolbars.ASPTMaterialToolbar
import asp.android.asppagos.R
import asp.android.asppagos.data.models.FavoritosTransferencia
import asp.android.asppagos.databinding.FragmentMainSendMoneyBinding
import asp.android.asppagos.ui.adapters.SendMoneyFavoriteAdapter
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.ui.states.UIStates
import asp.android.asppagos.ui.viewmodels.MainSendMoneyViewModel
import asp.android.asppagos.utils.showCustomDialogError
import asp.android.asppagos.utils.toJson
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainSendMoneyFragment : BaseFragment(), ASPTMaterialToolbar.ASPMaterialToolbarsListeners,
    ASPMaterialToolbarMainDashboard.ASPMaterialToolbarMainDashboardListeners {

    override var TAG: String = this.javaClass.name

    private val binding: FragmentMainSendMoneyBinding by lazy {
        FragmentMainSendMoneyBinding.inflate(layoutInflater)
    }

    private val viewModel: MainSendMoneyViewModel by viewModel()

    private val adapter: SendMoneyFavoriteAdapter by lazy {
        SendMoneyFavoriteAdapter() {
            handleTapFavorite(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getFavoriteList()
        setupToolbar()
        setupRecyclerView()
        setupOnClickListeners()
        setupTextWatcher()
        setupObservable()
    }

    override fun onClickBackButton() {
        onClickBackPressed()
    }

    private fun setupToolbar() {
        binding.toolbar.setTitle(getString(R.string.send_money))
        binding.toolbar.setASPMaterialToolbarsListeners(this)
    }

    private fun setupOnClickListeners() {
        binding.apply {
            containerRegisterAccount.setOnClickListener {
                safeNavigate(
                    R.id.sendMoneyTypeAccountFragment
                )
            }
        }
    }

    private fun handleTapFavorite(favorite: FavoritosTransferencia) {
        safeNavigate(
            R.id.sendMoneyFavoriteDetailFragment, bundleOf(
                USER_TO_SEND_MONEY to favorite.toJson()
            )
        )
    }

    private fun setupObservable() {
        viewModel.favoriteList.observe(viewLifecycleOwner) {
            adapter.submitAll(it)
        }
        viewModel.uiState.observe(viewLifecycleOwner) {
            handleState(it)
        }
    }

    private fun handleState(state: UIStates<Any>) {
        when (state) {
            is UIStates.Loading -> {
                dialog.show(childFragmentManager, TAG)
            }
            is UIStates.Error -> handleError(state)
            is UIStates.Success<Any> -> handleSuccess(state)
            else -> {}
        }
    }

    private fun handleError(error: UIStates.Error) {
        dialog.dismiss()
        //showCustomDialogError(error.message, "")
    }

    private fun handleSuccess(success: UIStates.Success<Any>) {
        dialog.dismiss()
        adapter.submitAll(success.value as MutableList<FavoritosTransferencia>)
    }

    private fun setupRecyclerView() {
        binding.rFavoriteList.adapter = adapter
    }

    private fun setupTextWatcher() {
        binding.etSearch.doOnTextChanged { text, _, _, _ ->
            viewModel.queryFavorite(text.toString())
        }
    }

    override fun onClickBackPressed() {
        findNavController().popBackStack()
    }

    companion object {
        const val USER_TO_SEND_MONEY = "user_to_send_money"
    }

}