package asp.android.asppagos.ui.fragments.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import asp.android.aspandroidmaterial.ui.layouts.ASPMaterialCVVReveal
import asp.android.aspandroidmaterial.ui.layouts.ASPMaterialDialogCustom
import asp.android.aspandroidmaterial.ui.layouts.ASPMaterialLockCard
import asp.android.aspandroidmaterial.ui.toolbars.ASPMaterialToolbarCarDetail
import asp.android.asppagos.R
import asp.android.asppagos.data.models.CVVResponseData
import asp.android.asppagos.data.models.ConsultAccountResponseData
import asp.android.asppagos.data.models.RepositionCardResponseData
import asp.android.asppagos.data.models.Tarjeta
import asp.android.asppagos.databinding.FragmentVirtualCardBinding
import asp.android.asppagos.ui.activities.MainDashboardActivity
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.ui.viewmodels.MainDashboardViewModel
import asp.android.asppagos.utils.CONGRATULATIONS_MESSAGE
import asp.android.asppagos.utils.LOCK_CARD_OPTION_TYPE
import asp.android.asppagos.utils.PROPERTY_ACCOUNT_ENCRIPTED
import asp.android.asppagos.utils.Prefs
import asp.android.asppagos.utils.ServerErrorCodes
import asp.android.asppagos.utils.UNLOCK_CARD_OPTION_TYPE
import asp.android.asppagos.utils.agregarCuenta
import asp.android.asppagos.utils.decryptData
import asp.android.asppagos.utils.encriptData
import asp.android.asppagos.utils.formatCurrencyMXN
import asp.android.asppagos.utils.fromJson
import asp.android.asppagos.utils.groupByMask
import asp.android.asppagos.utils.showCustomDialogError
import asp.android.asppagos.utils.showCustomDialogSuccess
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class VirtualCardFragment : BaseFragment(),
    ASPMaterialToolbarCarDetail.ASPMaterialToolbarCarDetailListeners,
    ASPMaterialCVVReveal.OnRevealCVVListener, ASPMaterialLockCard.OnLockCardListener {

    private var _binding: FragmentVirtualCardBinding? = null
    private val binding get() = _binding!!
    val viewModel: MainDashboardViewModel by activityViewModel()
    private var isCVVVisible = false
    private var islock = false

    override var TAG: String = javaClass.name

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVirtualCardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.let {
            it.unlockCard.imageView11.apply {
                setOnLockCardListener(this@VirtualCardFragment)
                withAutoToggle = false
                isNewRule = true
            }

            it.toolbarCardDetail.setASPMaterialToolbarCarDetailListeners(this)

            it.toolbarCardDetail.findViewById<TextView>(
                asp.android.aspandroidmaterial.R.id.textTitleToolbar
            ).text = MainDashboardActivity.accountData.cuenta.cuenta.agregarCuenta()

            it.toolbarCardDetail.findViewById<TextView>(
                asp.android.aspandroidmaterial.R.id.amountText
            ).text = viewModel.balance.value?.formatCurrencyMXN() ?: "0.0"

            it.toolbarCardDetail.findViewById<TextView>(
                asp.android.aspandroidmaterial.R.id.cardTypeText
            ).text =
                getString(R.string.virtual_card_text)

            it.textViewCardNumber.text = viewModel.virtualCard.value!!.groupByMask(4)

            it.textViewNameUser.text = MainDashboardActivity.accountData.nombre

            it.revealButton.setOnRevealCVVListener(this)

            it.repositionCard.frameLayout7.setOnClickListener {
                callRepositionDialog()
            }
        }

        initViewModel()
    }

    private fun callRepositionDialog() {
        val repositionCard = ASPMaterialDialogCustom.newInstance(
            getString(R.string.dialog_title_reposition),
            getString(R.string.dialog_reposition_message_description),
            getString(R.string.dialog_close_text),
            ASPMaterialDialogCustom.DialogIconType.INFO.ordinal,
            true,
            UNLOCK_CARD_OPTION_TYPE
        )

        repositionCard.setASPMaterialDialogCustomListener(object :
            ASPMaterialDialogCustom.ASPMaterialDialogCustomListener {
            override fun onClickAcceptButton(optionType: Int) {
                repositionCard.dismiss()

                dialog.show(
                    requireActivity().supportFragmentManager, TAG
                )

                viewModel.setAccountName(MainDashboardActivity.accountData.nombre)

                viewModel.requestReplacement()
            }

            override fun onClickClose() {
                repositionCard.dismiss()
            }
        })

        repositionCard.show(childFragmentManager, ASPMaterialDialogCustom.TAG)
    }

    private fun initViewModel() {
        viewModel.let {

            viewModel.getCVVResponseData.observe(viewLifecycleOwner) { cvvResponse ->
                dialog.dismiss()

                viewModel.setCVVCard(
                    Gson().fromJson<CVVResponseData>(
                        decryptData(
                            cvvResponse.data
                        )
                    ).ValorD2
                )

                validateCVV()

                viewModel.consultAccount()
            }

            viewModel.consultAccountResponseData.observe(viewLifecycleOwner) { codeResponseData ->

                when (codeResponseData.codigo) {
                    ServerErrorCodes.SUCCESS.ordinal -> {

                        val dataEncripted : ConsultAccountResponseData? = Gson().fromJson<ConsultAccountResponseData>(
                            decryptData(
                                codeResponseData.data
                            )
                        )

                        if (dataEncripted == null || dataEncripted.DescripcionStatus.isBlank()) {
                            val errorMessage = ASPMaterialDialogCustom.newInstance(
                                getString(R.string.information_dialog_text),
                                getString(R.string.dialog_error_message_retry),
                                getString(R.string.dialog_close_option_text),
                                ASPMaterialDialogCustom.DialogIconType.ERROR.id,
                                false
                            )

                            errorMessage.setASPMaterialDialogCustomListener(object :
                                ASPMaterialDialogCustom.ASPMaterialDialogCustomListener {
                                override fun onClickAcceptButton(optionType: Int) {
                                }

                                override fun onClickClose() {
                                    errorMessage.dismiss()
                                    onClickBackPressed()
                                }
                            })

                            errorMessage.show(parentFragmentManager, this.TAG)
                        } else {
                            islock = when (dataEncripted.DescripcionStatus) {
                                "BLOQUEADA" -> true
                                else -> false
                            }

                            binding.unlockCard.imageView11.isCardLocked = islock


                            binding.unlockCard.textView2.text =
                                if (islock) "Desbloquear\ntarjeta" else "Bloquear\ntarjeta"

                            val l = LocalDate.parse(dataEncripted.FechaVigencia, DateTimeFormatter.ofPattern("M/d/yyyy hh:mm:ss a", Locale.US))
                            val year = l.year % 100
                            val month = if (l.monthValue >= 10) "${l.monthValue}" else "0${l.monthValue}"

                            val vig = "Vencimiento $month/$year"

                            binding.textViewExpire.text = vig
                        }
                    }

                    else -> {
                        showCustomDialogError(
                            getString(R.string.information_dialog_text),
                            codeResponseData.mensage,
                        )
                    }
                }

            }

            viewModel.lockCardResponseData.observe(viewLifecycleOwner) { codeResponseData ->
                dialog.dismiss()
                when (codeResponseData.code) {
                    ServerErrorCodes.SUCCESS.ordinal -> {
                        if (islock) {
                            showCustomDialogSuccess(
                                getString(R.string.information_dialog_text),
                                getString(R.string.card_unlock_text),
                            )
                            islock = false
                        } else {
                            showCustomDialogSuccess(
                                getString(R.string.information_dialog_text),
                                getString(R.string.card_lock_text),
                            )
                            islock = true
                        }

                        //islock = !islock

                        binding.unlockCard.imageView11.isCardLocked = islock


                        binding.unlockCard.textView2.text =
                            if (islock) "Desbloquear\ntarjeta" else "Bloquear\ntarjeta"

                    }

                    else -> {
                        showCustomDialogError(
                            getString(R.string.information_dialog_text),
                            codeResponseData.mensage,
                        )
                    }
                }
            }

            viewModel
                .requestReplacementResponseData
                .observe(viewLifecycleOwner) { codeResponseData ->
                    dialog.dismiss()
                    when (codeResponseData.code) {
                        ServerErrorCodes.SUCCESS.ordinal -> {

                            val newCard =
                                Gson().fromJson<RepositionCardResponseData>(
                                    decryptData(
                                        codeResponseData.data
                                    )
                                )

                            val newList: MutableList<Tarjeta> =
                                MainDashboardActivity
                                    .accountData.cuenta.tarjetas.mapNotNull { card ->
                                        if (card.tipoTarjeta !=
                                            getString(R.string.card_virtual_type_enum)
                                        ) {
                                            card
                                        } else {
                                            null
                                        }
                                    }.toMutableList()

                            newList.add(
                                Tarjeta(
                                    mostrarTarjetaAPP = 0,
                                    numeroTarjeta = newCard.tarjetaNueva,
                                    proveedor = "",
                                    tipoTarjeta = getString(R.string.card_virtual_type_enum)
                                )
                            )

                            MainDashboardActivity.accountData.cuenta.tarjetas = newList

                            Prefs[PROPERTY_ACCOUNT_ENCRIPTED] =
                                encriptData(MainDashboardActivity.accountData)

                            viewModel.setVirtualCard(newCard.tarjetaNueva)

                            binding.textViewCardNumber.text =
                                newCard.tarjetaNueva.groupByMask(4)

                            isCVVVisible = false
                            binding.revealButton.restart()

                            viewModel.cvvQuery()

                            showCustomDialogSuccess(
                                getString(R.string.request_reposition_success),
                                getString(R.string.request_reposition_success_text_description),
                            )
                        }

                        else -> {
                            showCustomDialogError(
                                getString(R.string.information_dialog_text),
                                codeResponseData.mensage,
                            )
                        }
                    }
                }

            it.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
                dialog.dismiss()
                showCustomDialogError(
                    getString(R.string.information_dialog_text),
                    errorMessage,
                )
            }
        }

        dialog.show(
            requireActivity().supportFragmentManager, TAG
        )
        viewModel.cvvQuery()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClickBackButton() {
        safeNavigate(R.id.action_global_dashboardMainFragment)
    }

    override fun onRevealCVVClicked() {
        validateCVV()
    }

    private fun validateCVV() {
        val cvvText =
            if (isCVVVisible) {
                viewModel.cvvCard.value
            } else {
                getString(R.string.see_CVV_text)
            }

        binding.revealButton.findViewById<TextView>(
            asp.android.aspandroidmaterial.R.id.textViewCVV
        ).text = cvvText

        isCVVVisible = !isCVVVisible
    }

    override fun onLockCardClicked(isLocked: Boolean) {
        binding.unlockCard.textView2.text =
            if (islock) {
                getString(R.string.unlock_card_split_text)
            } else {
                getString(R.string.lock_card_split_text)
            }

        if (islock) {
            val unlockCard = ASPMaterialDialogCustom.newInstance(
                getString(R.string.unlock_card_title_dialog),
                getString(R.string.unlock_card_text_dialog),
                getString(R.string.dialog_close_text),
                ASPMaterialDialogCustom.DialogIconType.INFO.ordinal,
                true,
                UNLOCK_CARD_OPTION_TYPE
            )

            unlockCard.setASPMaterialDialogCustomListener(object :
                ASPMaterialDialogCustom.ASPMaterialDialogCustomListener {
                override fun onClickAcceptButton(optionType: Int) {
                    unlockCard.dismiss()
                    viewModel.unlockCard()
                }

                override fun onClickClose() {
                    unlockCard.dismiss()
                }
            })

            unlockCard.show(childFragmentManager, ASPMaterialDialogCustom.TAG)

        } else {
            val lockCard = ASPMaterialDialogCustom.newInstance(
                getString(R.string.lock_card_title_dialog),
                getString(R.string.lock_card_text_description_dialog),
                getString(R.string.dialog_close_text),
                ASPMaterialDialogCustom.DialogIconType.INFO.ordinal,
                true,
                LOCK_CARD_OPTION_TYPE
            )

            lockCard.setASPMaterialDialogCustomListener(object :
                ASPMaterialDialogCustom.ASPMaterialDialogCustomListener {
                override fun onClickAcceptButton(optionType: Int) {
                    lockCard.dismiss()
                    viewModel.lockCard()
                }

                override fun onClickClose() {
                    lockCard.dismiss()
                }

            })
            lockCard.show(childFragmentManager, ASPMaterialDialogCustom.TAG)

        }
    }
}