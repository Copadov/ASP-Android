package asp.android.asppagos.ui.fragments.codi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import asp.android.aspandroidmaterial.ui.toolbars.ASPMaterialToolbarMainDashboard
import asp.android.asppagos.databinding.FragmentCodiMovBinding
import asp.android.asppagos.ui.adapters.CodiTransactionsAdapter
import asp.android.asppagos.ui.fragments.BaseFragment
import com.google.android.material.tabs.TabLayout

class CodiMovFragment : BaseFragment(),
    ASPMaterialToolbarMainDashboard.ASPMaterialToolbarMainDashboardListeners {
    private var _binding: FragmentCodiMovBinding? = null
    private val binding get() = _binding!!
    override var TAG: String = this.javaClass.name
    private var adapter:CodiTransactionsAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCodiMovBinding.inflate(inflater, container, false)
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        binding.let {

            it.tabLayoutMovs.addTab(it.tabLayoutMovs.newTab().setText("Realizados"))
            it.tabLayoutMovs.addTab(it.tabLayoutMovs.newTab().setText("Pendientes"))
            adapter = CodiTransactionsAdapter(childFragmentManager,lifecycle,binding.root.context)

            it.viewPage.adapter=adapter

            it.tabLayoutMovs.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    if(tab!=null){
                        it.viewPage.currentItem=tab.position
                    }

                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    if(tab!=null){

                    }
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                    if(tab!=null){
                        it.viewPage.currentItem=tab.position
                    }

                }

            })
            it.viewPage.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    it.tabLayoutMovs.selectTab(it.tabLayoutMovs.getTabAt(position))
                }
            })
        }

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


}