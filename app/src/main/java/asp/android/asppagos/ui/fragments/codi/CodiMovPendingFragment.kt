package asp.android.asppagos.ui.fragments.codi

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import asp.android.asppagos.R
import asp.android.asppagos.data.models.PendingPaymentPushNotificationDBModel
import asp.android.asppagos.data.models.codi.CodiMovResponse
import asp.android.asppagos.data.models.codi.TransactionsRequest
import asp.android.asppagos.data.models.send_money.CommonServiceResponse
import asp.android.asppagos.data.repositories.AspTrackingRepository
import asp.android.asppagos.data.repositories.AspTrackingRepositoryImpl
import asp.android.asppagos.data.repositories.CodiAspRepository
import asp.android.asppagos.databinding.FragmentCodiMovPendingBinding
import asp.android.asppagos.firebase.model.PushNotificationData
import asp.android.asppagos.ui.activities.MainDashboardActivity
import asp.android.asppagos.ui.adapters.ClickListener
import asp.android.asppagos.ui.adapters.CodiMovRecViewAdapter
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.utils.EncryptUtils.decryptByGeneralKey
import asp.android.asppagos.utils.EncryptUtils.encryptByGeneralKey
import asp.android.asppagos.utils.showCustomDialogError
import asp.android.asppagos.utils.toJson
import io.objectbox.Box
import org.koin.android.ext.android.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.stream.Collectors
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async


class CodiMovPendingFragment(private val ls:MutableList<CodiMovResponse> = mutableListOf()) :  BaseFragment() {
    private var _binding: FragmentCodiMovPendingBinding? = null
    private val binding get() = _binding!!
    override var TAG: String = this.javaClass.name
    private var recyclerView: RecyclerView? = null
    private var adapter: CodiMovRecViewAdapter? = null
    private var list = mutableListOf<CodiMovResponse>()
    private val codiAspRepository:CodiAspRepository by inject()
    private val aspTrackingRepository: AspTrackingRepository by inject()
    private val pendingPaymentDb by inject<Box<PendingPaymentPushNotificationDBModel>>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCodiMovPendingBinding.inflate(inflater, container, false)
        recyclerView=binding.codiRecycleViewMade
        adapter = CodiMovRecViewAdapter(list,this.requireContext())
        val layoutManager: RecyclerView.LayoutManager  = LinearLayoutManager(this.context)
        recyclerView?.layoutManager = layoutManager
        adapter?.setOnClickListener(object : ClickListener<CodiMovResponse> {
            override fun onItemClick(data: CodiMovResponse) {
                data.isPending = true
                data.incomeFrom = "pendingPayment"
                safeNavigate(R.id.action_codiMovFragment_to_codiDevPayFragment,
                    bundleOf("data" to data.toJson())
                )
            }
        })
        recyclerView?.adapter = adapter

        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        populateList()
        dialog.let {
            if(!isVisible){
                it.show(childFragmentManager,TAG)
            }
        }
        getPendingMovementsByDb()
    }

    private fun populateList(){
        val request= TransactionsRequest(MainDashboardActivity.accountData.cuenta.clabe)
        var webService = ""
        codiAspApi().getTransactionsCodi(request.encryptByGeneralKey()){
            webService = it
        }.enqueue(object :
            Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                val res = response.body()!!.decryptByGeneralKey<CommonServiceResponse>()
                Log.d(TAG,"${res.toJson()}")
                if(res?.code == 0){
                    adapter!!.run {
                        val r = res?.data?.decryptByGeneralKey<List<CodiMovResponse>>()!!
                        Log.d(TAG,"${r.toJson()}")
                        list.addAll(r.stream()!!.filter {mv->
                            mv.status?.equals("pendiente",true)!!
                        }.collect(Collectors.toList()))
                        this.notifyDataSetChanged()
                        dialog.let {
                            if(isVisible){
                                it.dismiss()
                            }
                        }

                    }
                }
                GlobalScope.async {
                    aspTrackingRepository.consume(
                        webService = webService,
                        typeResponse = AspTrackingRepositoryImpl.ConsumeServiceTypeResponse.SUCCESS,
                        response = response.body()
                    )
                }
            }


            override fun onFailure(call: Call<String>, t: Throwable) {
                GlobalScope.async {
                    aspTrackingRepository.consume(
                        webService = webService,
                        typeResponse = AspTrackingRepositoryImpl.ConsumeServiceTypeResponse.ERROR
                    )
                }
                showCustomDialogError(message1 = "Información", message2 = "Ocurrió un error de comunicación, favor de intentar más tarde.")
            }

        })

    }


    @Override
    override fun onClickBackPressed() {
        parentFragmentManager.executePendingTransactions()
        findNavController().popBackStack()
    }
    private fun codiAspApi(): CodiAspRepository {
        return codiAspRepository
    }

    private fun getPendingMovementsByDb() {
        Log.d("JHMM", "pendingPayments: ${pendingPaymentDb.count()}")
    }


}