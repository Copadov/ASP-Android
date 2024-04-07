package asp.android.asppagos.ui.fragments.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.GridLayoutManager
import asp.android.aspandroidmaterial.ui.layouts.ASPMaterialInfoDialog
import asp.android.aspandroidmaterial.ui.toolbars.ASPMaterialToolbarDashboard
import asp.android.asppagos.R
import asp.android.asppagos.data.models.BalanceDataResponse
import asp.android.asppagos.data.models.PendingPaymentPushNotificationDBModel
import asp.android.asppagos.databinding.FragmentDashboardMainBinding
import asp.android.asppagos.firebase.model.PushNotificationData
import asp.android.asppagos.ui.activities.MainDashboardActivity
import asp.android.asppagos.ui.adapters.DashboardOptionMenuAdapter
import asp.android.asppagos.ui.adapters.MenuOption
import asp.android.asppagos.ui.adapters.TypeMenu
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.ui.viewmodels.MainDashboardViewModel
import asp.android.asppagos.utils.CODI_ENROLLED_KEY
import asp.android.asppagos.utils.NFC_REQUEST
import asp.android.asppagos.utils.PROPERTY_NAME_USER_LOGGED
import asp.android.asppagos.utils.Prefs
import asp.android.asppagos.utils.ServerErrorCodes
import asp.android.asppagos.utils.agregarHola
import asp.android.asppagos.utils.decryptData
import asp.android.asppagos.utils.formatCurrencyMXN
import asp.android.asppagos.utils.fromJson
import asp.android.asppagos.utils.mask
import asp.android.asppagos.utils.showCustomDialogInfo
import com.google.gson.Gson
import io.objectbox.Box
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class DashboardMainFragment : BaseFragment() {

    private var _binding: FragmentDashboardMainBinding? = null
    private val binding get() = _binding!!
    val viewModel: MainDashboardViewModel by activityViewModel()
    private val pendingPaymentDb by inject<Box<PendingPaymentPushNotificationDBModel>>()

    override var TAG: String = this.javaClass.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        getBalance()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.let {

            it.toolbarDashboard.setASPMaterialToolbarsListeners(
                object : ASPMaterialToolbarDashboard.ASPMaterialToolbarDashboardListeners,
                    ASPMaterialInfoDialog.Companion.DialogDismissListener {
                    override fun onClickProfilePicture() {
                    }

                    override fun onClickWhatsappIcon() {
                        val dialogInfo = ASPMaterialInfoDialog.newInstance(this)
                        dialogInfo.show(childFragmentManager, ASPMaterialInfoDialog.TAG)
                    }

                    override fun onClickNotificationsIcon() {

                    }

                    override fun onDialogDismissed() {

                    }
                })

            it.toolbarDashboard
                .findViewById<TextView>(
                    asp.android.aspandroidmaterial.R.id.textGreetings
                ).text =
                Prefs.get(PROPERTY_NAME_USER_LOGGED, "").split(" ")[0].agregarHola()

            it.myAccountDetailDashboard.cardDetailOption.setOnClickListener {

                MainDashboardActivity.bottomNavigation.selectOption(1)

                safeNavigate(R.id.action_dashboardMainFragment_to_myCardsFragment)
            }


            it.mainOptionsDashboard.recyclerViewListOptions.adapter = DashboardOptionMenuAdapter(
                listOf(
                    MenuOption(
                        getString(R.string.dashboard_main_send_money_option_text),
                        R.drawable.asp_envia_dinero_icon, TypeMenu.SENDMONEY
                    ),
                    MenuOption(
                        getString(R.string.dashboard_main_receive_money_option_text),
                        R.drawable.asp_recibe_dinero_icon, TypeMenu.RECEIVEMONEY
                    ),
                    MenuOption(
                        getString(R.string.dashboard_main_pay_asp_credit_option_text),
                        R.drawable.asp_pago_credito_asp_icon,
                        TypeMenu.PAY
                    ),
                    MenuOption(
                        getString(R.string.dashboard_main_pay_services_option_text),
                        R.drawable.asp_pagar_servicios_icon,
                        TypeMenu.PAYSERVICE
                    ),
                    MenuOption(
                        getString(R.string.dashboard_main_phone_charging_option_text),
                        R.drawable.asp_recargas_tel_icon,
                        TypeMenu.PHONERECHARGE
                    ),
                    /*MenuOption(
                        "",
                        R.drawable.asp_mas_icon,
                        TypeMenu.PLUS
                    )*/
                )
            ) {
                handleOptionMenu(typeMenu = it)
            }

            it.mainOptionsDashboard.recyclerViewListOptions
                .layoutManager = GridLayoutManager(requireContext(), 3)

            it.myAccountDetailDashboard.CardNumberText.text =
                MainDashboardActivity.accountData.cuenta.cuenta.mask()

            it.swipeRefreshLayout.setOnRefreshListener {
                getBalance()
            }

            viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
                binding.swipeRefreshLayout.isRefreshing = isLoading
            }
        }

        initViewModel()

        if (viewModel.balance.value == null) {
            getBalance()
        } else {
            binding.myAccountDetailDashboard.AccountTotalAmountText.text =
                viewModel.balance.value!!.formatCurrencyMXN()
        }

        // Valida si el dispositivo está registrado en CoDi cuando recibe un mensaje de cobro via NFC
        val nfcMessage = Prefs[NFC_REQUEST, ""]
        if (nfcMessage.isNotBlank()) {
            val isRegistered = Prefs[CODI_ENROLLED_KEY, false]
            if (isRegistered) {
            safeNavigate(R.id.action_dashboardMainFragment_to_detailPagarCodiFragment, bundleOf("data" to nfcMessage, "isNfcRequest" to true))
            Prefs[NFC_REQUEST] = null
        } else {
                Toast.makeText(
                    requireContext(),
                    "Dispositivo no registrado en CoDi",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun getBalance() {

        viewModel.isLoading.value = true

        viewModel.setAccount(MainDashboardActivity.accountData.cuenta.cuenta)

        viewModel.getBalance()
    }

    private fun initViewModel() {

        viewModel.let {
            viewModel.getBalanceResponseData.observe(viewLifecycleOwner) { responseData ->
                when (responseData.codigo) {
                    ServerErrorCodes.SUCCESS.ordinal -> {
                        viewModel.isLoading.value = false
                        viewModel.setBalance(
                            Gson()
                                .fromJson<BalanceDataResponse>(
                                    decryptData(responseData.data)
                                ).saldo
                        )

                        binding.myAccountDetailDashboard.AccountTotalAmountText.text =
                            viewModel.balance.value?.formatCurrencyMXN() ?: "0.0"
                    }

                    else -> {
                        viewModel.isLoading.value = false
                        dialog.dismiss()
                        showCustomDialogInfo(
                            getString(R.string.information_dialog_text),
                            responseData.mensaje,
                        )
                    }
                }
            }
        }
    }

    private fun handleOptionMenu(typeMenu: TypeMenu) {
        when (typeMenu) {
            TypeMenu.SENDMONEY -> safeNavigate(R.id.mainSendMoneyFragment)
            TypeMenu.RECEIVEMONEY -> safeNavigate(R.id.receiveMoneyMainFragment)
            TypeMenu.PAY -> safeNavigate(R.id.creditPaymentMainFragment)
            TypeMenu.PAYSERVICE -> safeNavigate(R.id.paymentServicesMainFragment)
            TypeMenu.PHONERECHARGE -> safeNavigate(R.id.cellphoneRefillsFragment)
            else -> {}
        }
    }
}