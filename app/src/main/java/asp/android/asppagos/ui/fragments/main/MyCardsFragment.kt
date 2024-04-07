package asp.android.asppagos.ui.fragments.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.navigation.fragment.findNavController
import asp.android.aspandroidmaterial.ui.layouts.ASPMaterialDialogCustom
import asp.android.aspandroidmaterial.ui.toolbars.ASPMaterialToolbarMainDashboard
import asp.android.asppagos.R
import asp.android.asppagos.data.events.ActivateCardResponseEvent
import asp.android.asppagos.data.events.DataFormResponseEvent
import asp.android.asppagos.data.models.AssignAccountCardData
import asp.android.asppagos.data.models.Tarjeta
import asp.android.asppagos.databinding.FragmentMyCardsBinding
import asp.android.asppagos.ui.activities.MainDashboardActivity
import asp.android.asppagos.ui.activities.PinInputActivity
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.ui.viewmodels.MainDashboardViewModel
import asp.android.asppagos.utils.ACTIVE_PHYSIC_CARD
import asp.android.asppagos.utils.ACTIVE_VIRTUAL_CARD
import asp.android.asppagos.utils.CONGRATULATIONS_MESSAGE
import asp.android.asppagos.utils.DATA_MODIFIED_CODE
import asp.android.asppagos.utils.PROPERTY_ACCOUNT_ENCRIPTED
import asp.android.asppagos.utils.PROPERTY_NAME_USER_LOGGED
import asp.android.asppagos.utils.Prefs
import asp.android.asppagos.utils.REQUEST_PHYSIC_CARD
import asp.android.asppagos.utils.RxBus
import asp.android.asppagos.utils.ServerErrorCodes
import asp.android.asppagos.utils.addAccountNumberSuffix
import asp.android.asppagos.utils.agregarHola
import asp.android.asppagos.utils.decryptData
import asp.android.asppagos.utils.dividirCadena
import asp.android.asppagos.utils.encriptData
import asp.android.asppagos.utils.enmascararNumeroTarjeta
import asp.android.asppagos.utils.formatCurrencyMXN
import asp.android.asppagos.utils.formatoUltimos8Digitos
import asp.android.asppagos.utils.fromJson
import asp.android.asppagos.utils.showCustomDialogError
import asp.android.asppagos.utils.showCustomDialogInfo
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class MyCardsFragment : BaseFragment(),
    ASPMaterialToolbarMainDashboard.ASPMaterialToolbarMainDashboardListeners {

    var fontSize = 10F
    private var hayTarjetasConTipoV = false
    private var isVirtual: Boolean = false
    private var isActivated: Boolean = false
    private var isRequested: Boolean = false
    private var _binding: FragmentMyCardsBinding? = null
    private val binding get() = _binding!!
    val viewModel: MainDashboardViewModel by activityViewModel()

    override var TAG: String = this.javaClass.name

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyCardsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onResume() {
        super.onResume()

        configureVirtualCard()

        configurePhysicCard()
    }

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        RxBus.listen(DataFormResponseEvent::class.java).subscribe {
            if (it.data) {
                val congratulationsMessage = ASPMaterialDialogCustom.newInstance(
                    getString(R.string.title_congratulations_activation_card_message),
                    getString(R.string.subtitle_card_activation_message_text),
                    getString(R.string.dialog_close_option_text),
                    ASPMaterialDialogCustom.DialogIconType.SUCCESS.ordinal,
                    false,
                    CONGRATULATIONS_MESSAGE
                )

                congratulationsMessage.setASPMaterialDialogCustomListener(object :
                    ASPMaterialDialogCustom.ASPMaterialDialogCustomListener {
                    override fun onClickAcceptButton(optionType: Int) {
                        congratulationsMessage.dismiss()
                    }

                    override fun onClickClose() {
                        congratulationsMessage.dismiss()
                    }
                })

                isRequested = true

                congratulationsMessage.show(
                    childFragmentManager,
                    ASPMaterialDialogCustom.TAG
                )
                binding.physicCardOption.textCardTitle.text =
                    getString(R.string.not_activated_card_yet_message_text)
                binding.physicCardOption.textCardActionTitle.text =
                    getString(R.string.activate_message_button_text)
            }
        }

        RxBus.listen(ActivateCardResponseEvent::class.java).subscribe {
            if (it.data) {
                binding.physicCardOption.chevronicon.visibility = View.VISIBLE
                binding.physicCardOption.imageActionIcon.visibility = View.INVISIBLE
            }
        }

        binding.let {

            it.ASPMaterialToolbarMainDashboard.setASPMaterialToolbarsListeners(this)
            it.ASPMaterialToolbarMainDashboard.setTitle(
                Prefs.get(PROPERTY_NAME_USER_LOGGED, "").split(" ")[0].agregarHola()
            )

            it.myAccountText.text =
                MainDashboardActivity.accountData.cuenta.cuenta.addAccountNumberSuffix(2)

            it.accountAmountText.text = viewModel.balance.value?.formatCurrencyMXN() ?: "0.0"

            it.accountCard.setOnClickListener {
                safeNavigate(R.id.action_myCardsFragment_to_accountDetailFragment)
            }

            Log.i(TAG, "COnfiguracion de fuente: ${resources.configuration.fontScale}")

            if (resources.configuration.fontScale > 1) fontSize = 8F

            if (MainDashboardActivity.accountData.cuenta.mostrarTarjetaAPP == 0){
                it.virtualCardOption.root.visibility = View.GONE
                it.physicCardOption.root.visibility = View.GONE
                it.cardFrame.visibility = View.GONE
            }


            configureVirtualCard()

            configurePhysicCard()
        }

        initViewModel()
    }

    private fun configurePhysicCard() {
        binding.physicCardOption.let {

            val tarjetasConTipoF =
                MainDashboardActivity.accountData
                    .cuenta
                    .tarjetas
                    .filter { it.tipoTarjeta == "F" }

            val hayTarjetasConTipoF = tarjetasConTipoF.isNotEmpty()

            if (!hayTarjetasConTipoF) {
                it.carHolder.visibility = View.GONE
            } else {
                //if (tarjetasConTipoF.first().mostrarTarjetaAPP == 0) {
                    binding.physicCardOption.imageActionIcon.visibility = View.INVISIBLE

                    binding.physicCardOption.chevronicon.visibility = View.VISIBLE
                    binding.physicCardOption.textCardTitle.text =
                        getString(R.string.physic_card_text)

                    viewModel.setPhysicCard(tarjetasConTipoF.first().numeroTarjeta)

                    binding.physicCardOption.textCardActionTitle.text =
                        viewModel.physicCard.value!!.enmascararNumeroTarjeta()

                    it.textCardNumber.text = tarjetasConTipoF.first()
                        .numeroTarjeta.formatoUltimos8Digitos()

                    it.textCardNumber.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize)
                /*} else {
                    val solicitaTarjetaFisica = MainDashboardActivity.accountData.configuracionesASPpago.firstOrNull { item -> item.concepto == "SOLIC_TARJETA_FISICA" }
                    if (solicitaTarjetaFisica != null && solicitaTarjetaFisica.valor != "DISABLED") {
                        binding.physicCardOption.imageActionIcon.visibility = View.VISIBLE

                        binding.physicCardOption.chevronicon.visibility = View.INVISIBLE

                        it.textCardNumber.text = getString(R.string.mask_card_not_activated)
                        it.textCardTitle.text =
                            getString(R.string.physic_card_not_activated_yet_text)
                        it.textCardActionTitle.text =
                            getString(R.string.request_text_message_option)
                        it.imageActionIcon.setOnClickListener {
                            isVirtual = false
                            if (viewModel.balance.value!! > 0) {
                                if (hayTarjetasConTipoV) {
                                    if (isRequested) {
                                        activatePhysicCard()
                                    } else {
                                        requestPhysicCard()
                                    }
                                } else {
                                    showCustomDialogInfo(
                                        "No cuentas con tu tarjeta virtual",
                                        "Para adquirir una tarjeta física, debes contar con una tarjeta virtual activada. Da click en Activar en el apartado de tarjeta virtual.",
                                    )
                                }
                            } else {
                                showCustomDialogError(
                                    "Saldo insuficiente",
                                    "Saldo insuficiente. Te invitamos a fondear tu cuenta e intentarlo de nuevo.",
                                )
                            }
                        }
                    } else {
                        it.carHolder.visibility = View.GONE
                    }
                }*/
            }

            it.lineseparator.visibility = View.INVISIBLE
            it.cardBackground.background =
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.card_physic_background
                )
            it.imageViewIcon.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(), R.drawable.asp_banderas_menu_icon
                )
            )


            it.carHolder.setOnClickListener {
                safeNavigate(R.id.action_myCardsFragment_to_physicCardFragment)
            }
        }
    }

    private fun configureVirtualCard() {
        binding.virtualCardOption.let {
            it.cardBackground.background =
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.card_virtual_background
                )
            it.imageViewIcon.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(), R.drawable.asp_banderas_menu_icon
                )
            )

            val tarjetasConTipoV =
                MainDashboardActivity.accountData
                    .cuenta
                    .tarjetas
                    .filter { it.tipoTarjeta == "V" }

            hayTarjetasConTipoV = tarjetasConTipoV.isNotEmpty()

            if (hayTarjetasConTipoV) {
                isVirtual = true
                binding.virtualCardOption.chevronicon.visibility = View.VISIBLE
                binding.virtualCardOption.imageActionIcon.visibility = View.INVISIBLE
                binding.virtualCardOption.textCardTitle.text =
                    getString(R.string.virtual_card_text)

                viewModel.setVirtualCard(tarjetasConTipoV.first().numeroTarjeta)

                binding.virtualCardOption.textCardActionTitle.text =
                    viewModel.virtualCard.value!!.enmascararNumeroTarjeta()

                it.textCardNumber.text = tarjetasConTipoV.first()
                    .numeroTarjeta.formatoUltimos8Digitos()

                it.textCardNumber.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize)


            } else {
                binding.virtualCardOption.chevronicon.visibility = View.INVISIBLE
                it.textCardNumber.text = getString(R.string.mask_card_not_activated)
                it.textCardTitle.text =
                    getString(R.string.virtual_card_not_activated_yet_message)
                it.textCardActionTitle.text = getString(R.string.activate_message_button_text)
            }

            it.imageActionIcon.setOnClickListener {
                activateVirtualCard()
            }

            it.carHolder.setOnClickListener {

                if (hayTarjetasConTipoV) {
                    safeNavigate(R.id.action_myCardsFragment_to_virtualCardFragment)
                }
            }
        }
    }

    private fun initViewModel() {

        viewModel.let {

            it.assignAccountResponseData.observe(viewLifecycleOwner) { responseData ->
                when (responseData.codigo) {
                    ServerErrorCodes.SUCCESS.ordinal -> {
                        isVirtual = false
                        binding.virtualCardOption.chevronicon.visibility = View.VISIBLE

                        binding.virtualCardOption.imageActionIcon.visibility = View.INVISIBLE

                        binding.virtualCardOption.textCardTitle.text =
                            getString(R.string.virtual_card_text_title)

                        val cardActivated = Gson()
                            .fromJson<AssignAccountCardData>(
                                decryptData(responseData.data)
                            ).tarjeta

                        binding.virtualCardOption.textCardActionTitle.text =
                            cardActivated.enmascararNumeroTarjeta()

                        binding.virtualCardOption.textCardNumber.text =
                            cardActivated.formatoUltimos8Digitos()

                        viewModel.setVirtualCard(cardActivated)

                        MainDashboardActivity.accountData.cuenta.tarjetas.add(
                            Tarjeta(
                                mostrarTarjetaAPP = 0,
                                numeroTarjeta = cardActivated,
                                proveedor = "",
                                tipoTarjeta = "V"
                            )
                        )

                        Prefs.set(
                            PROPERTY_ACCOUNT_ENCRIPTED,
                            encriptData(MainDashboardActivity.accountData)
                        )

                        hayTarjetasConTipoV = true

                        dialog.dismiss()
                    }

                    else -> {
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

    private fun requestPhysicCard() {
        val requestPhysicCard = ASPMaterialDialogCustom.newInstance(
            getString(R.string.request_physic_card_text_title),
            "Adquirir una tarjeta física genera una única comisión de \$120.00.\n" +
                    "Si estás de acuerdo, da click en continuar.\n",
            "cerrar",
            ASPMaterialDialogCustom.DialogIconType.INFO.ordinal,
            true,
            REQUEST_PHYSIC_CARD

        )

        requestPhysicCard.setASPMaterialDialogCustomListener(object :
            ASPMaterialDialogCustom.ASPMaterialDialogCustomListener {
            override fun onClickAcceptButton(optionType: Int) {
                if (!isRequested) {
                    safeNavigate(R.id.action_myCardsFragment_to_requestCardFormFragment)
                } else {
                    intent = Intent(requireContext(), PinInputActivity::class.java)
                    startActivityForResult(intent, DATA_MODIFIED_CODE)
                }
            }

            override fun onClickClose() {
                requestPhysicCard.dismiss()
            }
        })
        requestPhysicCard.show(childFragmentManager, ASPMaterialDialogCustom.TAG)
    }

    private fun activatePhysicCard() {
        val activePhysicCard = ASPMaterialDialogCustom.newInstance(
            "Activar tu tarjeta física",
            "Para activar tu tarjeta es necesario que la tengas a la mano.",
            "cerrar",
            ASPMaterialDialogCustom.DialogIconType.INFO.ordinal,
            true,
            ACTIVE_PHYSIC_CARD
        )
        activePhysicCard.setASPMaterialDialogCustomListener(object :
            ASPMaterialDialogCustom.ASPMaterialDialogCustomListener {
            override fun onClickAcceptButton(optionType: Int) {


            }

            override fun onClickClose() {
                activePhysicCard.dismiss()
            }
        })
        activePhysicCard.show(childFragmentManager, ASPMaterialDialogCustom.TAG)
        isVirtual = false
    }

    private fun activateVirtualCard() {
        val activateVirtualCard = ASPMaterialDialogCustom.newInstance(
            "Activar tarjeta virtual",
            "Obtén tu tarjeta virtual " +
                    "y comienza a realizar compras en comercios electrónicos desde donde estés.",
            "cerrar",
            ASPMaterialDialogCustom.DialogIconType.INFO.ordinal,
            true,
            ACTIVE_VIRTUAL_CARD
        )
        activateVirtualCard.setASPMaterialDialogCustomListener(object :
            ASPMaterialDialogCustom.ASPMaterialDialogCustomListener {
            override fun onClickAcceptButton(optionType: Int) {
                dialog.show(
                    requireActivity().supportFragmentManager,
                    TAG
                )

                viewModel.setAccount(
                    MainDashboardActivity
                        .accountData
                        .cuenta
                        .cuenta
                )
                viewModel.setAccountName(
                    MainDashboardActivity.accountData.nombre.dividirCadena()
                )
                viewModel.setCardType("V")

                viewModel.accountAssign()

                activateVirtualCard.dismiss()
            }

            override fun onClickClose() {
                activateVirtualCard.dismiss()
            }
        })
        activateVirtualCard.show(childFragmentManager, ASPMaterialDialogCustom.TAG)
        isVirtual = true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                safeNavigate(R.id.action_myCardsFragment_to_activePhysicCardInputFragment)
            }

            Activity.RESULT_CANCELED -> {

            }
        }
    }

    override fun onClickBackButton() {
        findNavController().popBackStack()
    }
}