package asp.android.asppagos.ui.fragments.recive_money

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import asp.android.aspandroidmaterial.ui.toolbars.ASPMaterialToolbarMainDashboard
import asp.android.asppagos.R
import asp.android.asppagos.databinding.FragmentReceiveMoneyInfoBinding
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.ui.fragments.recive_money.adapters.ReceiveStepInfoAdapter

class ReceiveMoneyInfoFragment : BaseFragment(),
    ASPMaterialToolbarMainDashboard.ASPMaterialToolbarMainDashboardListeners {

    override var TAG: String = this.javaClass.name

    private lateinit var typeInfoScreen: String

    private val binding: FragmentReceiveMoneyInfoBinding by lazy {
        FragmentReceiveMoneyInfoBinding.inflate(layoutInflater)
    }
    private val adapter by lazy {
        ReceiveStepInfoAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        typeInfoScreen = arguments?.getString(TYPE_SCREEN) ?: ""
        setupRecycler()
        setupInfo()
        setupToolbar()
    }

    override fun onClickBackButton() {
        onClickBackPressed()
    }

    private fun setupToolbar() {
        binding.toolbar.setTitle(getTitleScreen(typeInfoScreen))
        binding.toolbar.setASPMaterialToolbarsListeners(this)
    }

    private fun setupInfo() {
        binding.apply {
            tvInfoTitle.text = title(typeInfoScreen)
            adapter.submit(steps(typeInfoScreen))
            ivInfo.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    infoImage(typeInfoScreen)
                )
            )
        }
    }

    private fun setupRecycler() {
        binding.rOptionsInfo.adapter = adapter
    }

    companion object {
        const val TYPE_SCREEN = "type_screen"
        const val SPEI = "spei"
        const val BRANCH = "branch"
        const val EASY_WINDOW = "easy_window"
    }

    private fun getTitleScreen(typeInfo: String) =
        when (typeInfo) {
            BRANCH -> "Sucursal"
            EASY_WINDOW -> "Ventanilla Fácil"
            else -> "SPEI"
        }

    private fun title(typeInfo: String) = when (typeInfo) {
        BRANCH -> "Sucursal"
        EASY_WINDOW -> "Depósito en Ventanilla Fácil"
        else -> "SPEI"
    }

    private fun steps(typeInfo: String) = when (typeInfo) {
        BRANCH -> mutableListOf(
            "Acude a tu sucursal ASP más cercana.",
            "Presenta el número de cuenta, del beneficiario.",
            "Haz tu trámite."
        )

        EASY_WINDOW -> mutableListOf(
            "Acude a tu Ventanilla Fácil autorizada.",
            "Presenta la referencia del beneficiario del depósito.",
            "Solicita al comisionista que realice el proceso."
        )

        else -> mutableListOf(
            "Haz una transferencia desde cualquier aplicación bancaria.",
            "Captura tu Clabe interbancaria de 18 dígitos.",
            "Ingresa el monto.",
            "Confirma."
        )
    }

    private fun infoImage(typeInfo: String) = when (typeInfo) {
        BRANCH -> R.drawable.ic_branch_small
        EASY_WINDOW -> R.drawable.ic_easy_window_small
        else -> R.drawable.ic_spei
    }
}