package asp.android.asppagos.ui.fragments.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import asp.android.aspandroidmaterial.ui.layouts.ASPMaterialDialogCustom
import asp.android.aspandroidmaterial.ui.layouts.ASPMaterialLockCard
import asp.android.aspandroidmaterial.ui.toolbars.ASPMaterialToolbarCarDetail
import asp.android.asppagos.R
import asp.android.asppagos.data.models.ConsultAccountResponseData
import asp.android.asppagos.databinding.FragmentPhysicCardBinding
import asp.android.asppagos.ui.activities.MainDashboardActivity
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.ui.viewmodels.MainDashboardViewModel
import asp.android.asppagos.utils.LOCK_CARD_OPTION_TYPE
import asp.android.asppagos.utils.REPOSITION_PHYSIC_CARD
import asp.android.asppagos.utils.ServerErrorCodes
import asp.android.asppagos.utils.agregarCuenta
import asp.android.asppagos.utils.decryptData
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

class PhysicCardFragment : BaseFragment(),
    ASPMaterialToolbarCarDetail.ASPMaterialToolbarCarDetailListeners,
    ASPMaterialLockCard.OnLockCardListener {

    private var islock = false
    private lateinit var binding: FragmentPhysicCardBinding
    private var _binding: FragmentPhysicCardBinding? = null
    val viewModel: MainDashboardViewModel by activityViewModel()

    override var TAG: String = this.javaClass.name

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhysicCardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.let {
            it.toolbarCardDetail.setASPMaterialToolbarCarDetailListeners(this)

            it.toolbarCardDetail.setASPMaterialToolbarCarDetailListeners(this)

            it.toolbarCardDetail.findViewById<TextView>(
                asp.android.aspandroidmaterial.R.id.textTitleToolbar
            ).text = MainDashboardActivity.accountData.cuenta.cuenta.agregarCuenta()

            it.toolbarCardDetail.findViewById<TextView>(
                asp.android.aspandroidmaterial.R.id.amountText
            ).text = viewModel.balance.value?.formatCurrencyMXN() ?: "0.0"

            it.toolbarCardDetail.findViewById<TextView>(asp.android.aspandroidmaterial.R.id.cardTypeText).text =
                getString(R.string.physic_card_text)

            val tarjetasConTipoF =
                MainDashboardActivity.accountData
                    .cuenta
                    .tarjetas
                    .filter { it.tipoTarjeta == "F" }

            viewModel.setPhysicCard(tarjetasConTipoF.first().numeroTarjeta)

            it.textViewCardNumber.text = tarjetasConTipoF.first().numeroTarjeta.groupByMask(4)
            it.textViewNameUser.text = MainDashboardActivity.accountData.nombre

            it.repositionCard.textViewTitle.text = "Asignar nuevo\n" +
                    " NIP para compras"


            it.repositionCard.root.setOnClickListener {
                val shopNip = ASPMaterialDialogCustom.newInstance(
                    "Asignar nuevo NIP para compras",
                    "Una vez que cambies tu NIP, podrás hacer compras en comercios hasta que completes el proceso de activación, para ésto es necesario acudir a un cajero ATM y realizar una consulta de saldo.\n",
                    "cerrar",
                    ASPMaterialDialogCustom.DialogIconType.INFO.ordinal,
                    true,
                    LOCK_CARD_OPTION_TYPE
                )
                shopNip.setASPMaterialDialogCustomListener(object :
                    ASPMaterialDialogCustom.ASPMaterialDialogCustomListener {
                    override fun onClickAcceptButton(optionType: Int) {
                        shopNip.dismiss()
                        safeNavigate(R.id.action_physicCardFragment_to_pinAsignationFragment, bundleOf("cardNumber" to tarjetasConTipoF.first().numeroTarjeta))
                    }

                    override fun onClickClose() {
                        shopNip.dismiss()
                    }
                })
                shopNip.show(childFragmentManager, ASPMaterialDialogCustom.TAG)
            }

            it.generatPin.root.visibility = View.GONE
            it.generatPin.textViewTitle.text = "Solicitar\n" +
                    "reposición"

            it.unlockCard.imageView11.apply {
                setOnLockCardListener(this@PhysicCardFragment)
                withAutoToggle = false
                isNewRule = true
            }

            it.generatPin.root.setOnClickListener {
                val physicCardReposition = ASPMaterialDialogCustom.newInstance(
                    "Reposición de tarjeta física",
                    "La reposición de tarjeta tiene un costo de \$120.00. ¿Deseas continuar?",
                    "cerrar",
                    ASPMaterialDialogCustom.DialogIconType.INFO.ordinal,
                    true,
                    REPOSITION_PHYSIC_CARD

                )
                physicCardReposition.setASPMaterialDialogCustomListener(object :
                    ASPMaterialDialogCustom.ASPMaterialDialogCustomListener {
                    override fun onClickAcceptButton(optionType: Int) {
                    }

                    override fun onClickClose() {
                    }

                })
                physicCardReposition.show(childFragmentManager, ASPMaterialDialogCustom.TAG)
            }
        }

        initViewModel()
    }

    private fun initViewModel() {

        viewModel.let {

            it.consultAccountPhysicResponseData.observe(viewLifecycleOwner) { codeResponseData ->
                dialog.dismiss()
                when (codeResponseData.codigo) {

                    ServerErrorCodes.SUCCESS.ordinal -> {
                        val dataEncripted = Gson().fromJson<ConsultAccountResponseData>(
                            decryptData(
                                codeResponseData.data
                            )
                        )

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

                    else -> {
                        showCustomDialogError(
                            "Información",
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
                                "Información",
                                "Tu tarjeta se encuentra desbloqueada, ya puedes realizar operaciones.",
                            )
                            islock = false
                        } else {
                            showCustomDialogSuccess(
                                "Información",
                                "Tu tarjeta se encuentra bloqueada, para poder realizar operaciones debes desbloquearla.",
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
                            "Información",
                            codeResponseData.mensage,
                        )
                    }
                }
            }


        }

        dialog.show(requireActivity().supportFragmentManager, "CONSULT")
        viewModel.consultPhysicAccount()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClickBackButton() {
        findNavController().popBackStack()
    }

    override fun onLockCardClicked(isLocked: Boolean) {
        if (islock) {
            val unlockCard = ASPMaterialDialogCustom.newInstance(
                "Bloqueo de tarjeta",
                "Estás bloqueando temporalmente tu tarjeta y no podrás realizar operaciones.\n" +
                        "¿Estás seguro que deseas bloquear tu tarjeta?\n",
                "cerrar",
                ASPMaterialDialogCustom.DialogIconType.INFO.ordinal,
                true,
                LOCK_CARD_OPTION_TYPE
            )
            unlockCard.setASPMaterialDialogCustomListener(object :
                ASPMaterialDialogCustom.ASPMaterialDialogCustomListener {
                override fun onClickAcceptButton(optionType: Int) {
                    unlockCard.dismiss()
                    dialog.show(requireActivity().supportFragmentManager, TAG)
                    viewModel.unlockPhysicCard()
                }

                override fun onClickClose() {
                    unlockCard.dismiss()
                }
            })
            unlockCard.show(childFragmentManager, ASPMaterialDialogCustom.TAG)

        } else {
            val lockCard = ASPMaterialDialogCustom.newInstance(
                "Bloqueo de tarjeta",
                "Estás desbloqueado tu tarjeta, ya podrás realizar operaciones.",
                "cerrar",
                ASPMaterialDialogCustom.DialogIconType.INFO.ordinal,
                true,
                LOCK_CARD_OPTION_TYPE
            )
            lockCard.setASPMaterialDialogCustomListener(object :
                ASPMaterialDialogCustom.ASPMaterialDialogCustomListener {
                override fun onClickAcceptButton(optionType: Int) {
                    lockCard.dismiss()
                    dialog.show(requireActivity().supportFragmentManager, TAG)
                    viewModel.lockPhysicCard()
                }

                override fun onClickClose() {
                    lockCard.dismiss()
                }
            })
            lockCard.show(childFragmentManager, ASPMaterialDialogCustom.TAG)
        }
    }
}