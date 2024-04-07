package asp.android.asppagos.ui.fragments.codi

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isEmpty
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import asp.android.aspandroidmaterial.ui.toolbars.ASPMaterialToolbarMainDashboard
import asp.android.asppagos.R
import asp.android.asppagos.data.models.codi.CodiMovResponse
import asp.android.asppagos.data.repositories.AspTrackingRepository
import asp.android.asppagos.data.repositories.AspTrackingRepositoryImpl
import asp.android.asppagos.databinding.FragmentDetailMovCodiBinding
import asp.android.asppagos.ui.adapters.CodiDetailMovAdapter
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.utils.fromJson
import asp.android.asppagos.utils.showSingleButtonDialog
import asp.android.asppagos.utils.toJson
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.koin.android.ext.android.inject


class DetailMovCodiFragment : BaseFragment(), ASPMaterialToolbarMainDashboard.ASPMaterialToolbarMainDashboardListeners {
    override var TAG: String = this.javaClass.name

    private val binding: FragmentDetailMovCodiBinding by lazy {
        FragmentDetailMovCodiBinding.inflate(layoutInflater)
    }
    private val aspTrackingRepository: AspTrackingRepository by inject()

    private var codiDetailMovAdapter: CodiDetailMovAdapter? = null

    private var codeMov: CodiMovResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            codeMov = it.getString("data", "").fromJson<CodiMovResponse>()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        dialog.show(childFragmentManager,TAG)
        binding.let {
            if(!it.tabLayoutDetailMov.isEmpty()){
                it.tabLayoutDetailMov.removeAllTabs()
            }
            it.tabLayoutDetailMov.addTab(
                it.tabLayoutDetailMov.newTab().setText("Detalle de la operación")
            )
            it.tabLayoutDetailMov.addTab(
                it.tabLayoutDetailMov.newTab().setText("Datos adicionales")
            )

            codiDetailMovAdapter =
                CodiDetailMovAdapter(childFragmentManager, lifecycle, codeMov)
            it.viewPageDetail.adapter = codiDetailMovAdapter
            binding.devCodiIcon.visibility= if(codeMov?.isDev!!){
                View.VISIBLE
            }else{
                View.GONE
            }
           /* codeMov?.color.let { s ->
                if(s.equals("verde",true)&&(codeMov?.type=="+")&&(codeMov?.ban == 90659)){

                }else{

                }
            }*/
            it.tabLayoutDetailMov.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    if(tab!=null){
                        it.viewPageDetail.currentItem=tab.position
                    }

                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    if(tab!=null){

                    }
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                    if(tab!=null){
                        it.viewPageDetail.currentItem=tab.position
                    }

                }
            })
            it.viewPageDetail.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){

                override fun onPageSelected(position: Int) {
                   if(!requireParentFragment().childFragmentManager.executePendingTransactions()){
                       super.onPageSelected(position)
                       it.tabLayoutDetailMov.selectTab(it.tabLayoutDetailMov.getTabAt(position))
                   }
                }
            })

            it.devCodiIcon.setOnClickListener {
                GlobalScope.async {
                    aspTrackingRepository.inform(
                        eventAction = AspTrackingRepositoryImpl.EventAction.WEB_SERVICE,
                        ticket = "SOL_ENV_AP",
                        aditionalInfo = "DEVOLVER"
                    )
                }
                codeMov?.incomeFrom = "devPayment"
                safeNavigate(R.id.action_detailMovCodiFragment_to_codiDevPayFragment,
                    bundleOf("data" to codeMov.toJson())
                )
            }
        }
        dialog.dismiss()
    }
    @Override
    override fun onDestroyView() {
        super.onDestroyView()
         binding.viewPageDetail.adapter = null
    }


    private fun setupToolbar() {
        this.binding.accountDetailToolbar.setTitle("Mis Operaciones")
        this.binding.accountDetailToolbar.setASPMaterialToolbarsListeners(this)
    }
    @Override
    override fun onClickBackButton() {
        childFragmentManager.executePendingTransactions()
        onClickBackPressed()
    }

    override fun onClickWhatsappIcon() {
        super.onClickWhatsappIcon()
        requireActivity().showSingleButtonDialog(
            "Información",
            "call_center@aspintegraopciones.com\n664 204 1866",
            "Aceptar"
        )
    }

}