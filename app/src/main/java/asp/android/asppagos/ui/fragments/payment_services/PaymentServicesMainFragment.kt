package asp.android.asppagos.ui.fragments.payment_services

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import asp.android.aspandroidmaterial.ui.toolbars.ASPMaterialToolbarMainDashboard
import asp.android.asppagos.R
import asp.android.asppagos.data.models.payment_services.ServiceDataResponse
import asp.android.asppagos.databinding.FragmentPaymentServicesMainBinding
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.ui.states.UIStates
import asp.android.asppagos.ui.viewbindings.FavoriteServicesRow
import asp.android.asppagos.ui.viewbindings.ServiceItemRow
import asp.android.asppagos.ui.viewmodels.payment_services.PaymentServicesMainViewModel
import asp.android.asppagos.utils.backgroundColor
import asp.android.asppagos.utils.getDrawable
import asp.android.asppagos.utils.hide
import asp.android.asppagos.utils.hideKeyboard
import asp.android.asppagos.utils.show
import asp.android.asppagos.utils.showCustomDialogError
import asp.android.asppagos.utils.toJson
import com.xwray.groupie.GroupieAdapter
import org.koin.android.ext.android.inject

class PaymentServicesMainFragment : BaseFragment(),
    ASPMaterialToolbarMainDashboard.ASPMaterialToolbarMainDashboardListeners {

    override var TAG: String = this.javaClass.name

    private val viewModel: PaymentServicesMainViewModel by inject()

    private val binding: FragmentPaymentServicesMainBinding by lazy {
        FragmentPaymentServicesMainBinding.inflate(layoutInflater)
    }

    private val favoritesAdapter: GroupieAdapter by lazy {
        GroupieAdapter()
    }

    private val allServicesAdapter: GroupieAdapter by lazy {
        GroupieAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.paymentServices()
        setupToolbar()
        setupOnClickListener()
        setupTextWatcher()
        setupRecyclerView()
        setupObservable()
    }

    private fun setupOnClickListener() {
        binding.containerShowMore.setOnClickListener {
            showMoreServices()
        }
    }

    private fun setupTextWatcher() {
        binding.etSearch.doOnTextChanged { text, _, _, _ ->
            viewModel.searchService(text.toString())
        }
    }

    override fun onClickBackButton() {
        if (viewModel.hasFavoriteListItems() && viewModel.isShowingFavorites.not()) {
            showFavoriteServices()
        } else {
            onClickBackPressed()
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setTitle(getString(R.string.payment_services_toolbar_title))
        binding.toolbar.setASPMaterialToolbarsListeners(this)
    }

    private fun showFavoriteServices() {
        viewModel.setIsShowingFavorites(true)
        binding.apply {
            containerParent.background = getDrawable(R.drawable.asp_bg_degradado)
            cardFavoriteServices.show()
            containerServiceList.hide()
        }
    }

    private fun showMoreServices() {
        viewModel.setIsShowingFavorites(false)
        binding.apply {
            containerParent.backgroundColor(R.color.white)
            cardFavoriteServices.hide()
            containerServiceList.show()
        }
    }

    private fun setupRecyclerView() {
        binding.apply {
            rvAllServices.adapter = allServicesAdapter
            rvFavoriteServices.adapter = favoritesAdapter
        }
    }

    private fun setupObservable() {
        viewModel.uiState.observe(viewLifecycleOwner) {
            handleResult(it)
            checkIfHasFavorite()
        }
        viewModel.favoriteListObservable.observe(viewLifecycleOwner) {
            handleFavorites(it)
            checkIfHasFavorite()
        }
        viewModel.hideKeyword.observe(viewLifecycleOwner) {
            hideKeyword(it)
        }
    }

    private fun hideKeyword(hide: Boolean){
        if (hide) {
            hideKeyboard()
        }
    }

    private fun handleResult(state: UIStates<MutableList<ServiceDataResponse>>) {
        when(state) {
            is UIStates.Loading -> dialog.show(childFragmentManager, TAG)
            is UIStates.Success -> handleSuccess(state.value)
            is UIStates.Error -> showCustomDialogError(message1 = "Información", message2 = state.message)
            else -> {}
        }
    }

    private fun handleSuccess(serviceList: MutableList<ServiceDataResponse>?) {
        serviceList?.let {
            allServicesAdapter.apply {
                clear()
                addAll(it.map {
                    ServiceItemRow(it) { serviceSelected ->
                        safeNavigate(R.id.paymentServicesInfoFragment, bundleOf(
                            Pair(
                                SERVICE_SELECTED, serviceSelected.toJson())
                        ))
                    }
                })
            }
        }
    }

    private fun checkIfHasFavorite() {
        if (viewModel.hasFavoriteListItems() && viewModel.hasQuery.not()) {
            showFavoriteServices()
        } else {
            showMoreServices()
        }
        if (dialog.isVisible) {
            dialog.dismiss()
        }
    }

    private fun handleFavorites(favoriteList: MutableList<ServiceDataResponse>) {
        favoritesAdapter.apply {
            clear()
            addAll(favoriteList.map {
                FavoriteServicesRow(it) { serviceSelected ->
                    safeNavigate(R.id.paymentServicesInfoFragment, bundleOf(
                        Pair(
                            SERVICE_SELECTED, serviceSelected.toJson())
                    ))
                }
            })
        }
    }

    companion object {
        const val SERVICE_SELECTED = "service_selected"
    }
}