package asp.android.asppagos.ui.fragments.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import asp.android.aspandroidmaterial.ui.toolbars.ASPMaterialToolbarMainDashboard
import asp.android.asppagos.databinding.FragmentAccountDetailBinding
import asp.android.asppagos.ui.activities.MainDashboardActivity
import asp.android.asppagos.ui.adapters.AccountDetailOptionsAdapter
import asp.android.asppagos.ui.adapters.AccountOption
import asp.android.asppagos.ui.fragments.BaseFragment

class AccountDetailFragment : BaseFragment(),
    ASPMaterialToolbarMainDashboard.ASPMaterialToolbarMainDashboardListeners {

    private var _binding: FragmentAccountDetailBinding? = null
    private val binding get() = _binding!!

    override var TAG = javaClass.name

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountDetailBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.let {

            it.accountDetailToolbar.setASPMaterialToolbarsListeners(this)

            it.accountDetailToolbar
                .findViewById<TextView>(
                    asp.android.aspandroidmaterial.R.id.textTitleToolbar
                )
                .text = "Detalle de cuenta"

            it.recyclerViewOptionsAccount.adapter = AccountDetailOptionsAdapter(
                listOf(
                    AccountOption(
                        "Producto",
                        MainDashboardActivity.accountData.cuenta.descripcion,
                        false
                    ),
                    AccountOption(
                        "Número de cuenta",
                        MainDashboardActivity.accountData.cuenta.cuenta,
                        true
                    ),
                    AccountOption(
                        "Cuenta CLABE",
                        MainDashboardActivity.accountData.cuenta.clabe,
                        true
                    ),
                    AccountOption(
                        "Tarjeta",
                        MainDashboardActivity.accountData.cuenta.tarjetas
                            .firstOrNull()?.numeroTarjeta
                            ?: "No se encontraron tarjetas en la cuenta",
                        true
                    ),
                    AccountOption(
                        "Celular vinculado",
                        MainDashboardActivity.accountData.cuenta.telefono,
                        false
                    ),
                )
            ) {

            }

            it.recyclerViewOptionsAccount.layoutManager = LinearLayoutManager(requireContext())

        }
    }

    override fun onClickBackButton() {
        findNavController().popBackStack()
    }
}