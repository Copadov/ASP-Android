package asp.android.asppagos.ui.fragments.codi

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import asp.android.aspandroidcore.utils.encriptaInformacionB64
import asp.android.aspandroidcore.utils.getFolioMensajeCobro
import asp.android.aspandroidmaterial.ui.toolbars.ASPMaterialToolbarMainDashboard
import asp.android.asppagos.R
import asp.android.asppagos.data.models.codi.CoDiASPRequest
import asp.android.asppagos.data.models.codi.CobroRequest
import asp.android.asppagos.data.models.codi.CodiMovResponse
import asp.android.asppagos.data.models.codi.RefundRequest
import asp.android.asppagos.data.models.codi.Vendor
import asp.android.asppagos.data.models.send_money.CommonServiceResponse
import asp.android.asppagos.data.repositories.AspTrackingRepository
import asp.android.asppagos.data.repositories.AspTrackingRepositoryImpl
import asp.android.asppagos.data.repositories.CodiAspRepository
import asp.android.asppagos.databinding.FragmentCodiDevPayBinding
import asp.android.asppagos.ui.activities.MainDashboardActivity
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.utils.CODI_KEY_P_KEY
import asp.android.asppagos.utils.EncryptUtils.decryptByGeneralKey
import asp.android.asppagos.utils.EncryptUtils.encryptByGeneralKey
import asp.android.asppagos.utils.Prefs
import asp.android.asppagos.utils.showCustomDialogError
import asp.android.asppagos.utils.toJson
import com.google.gson.Gson
import org.koin.android.ext.android.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async


class CodiDevPayFragment : BaseFragment(),
    ASPMaterialToolbarMainDashboard.ASPMaterialToolbarMainDashboardListeners {

    override var TAG: String = this.javaClass.name
    private var _binding: FragmentCodiDevPayBinding? = null
    private val binding get() = _binding!!
    private var isRefund: Boolean = false
    private var codiMov: CodiMovResponse? = null
    private var keysource: String? = null
    private var amountDev: Double? = 0.0
    private val codiAspRepository: CodiAspRepository by inject()
    private val  aspTrackingRepository: AspTrackingRepository by inject()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCodiDevPayBinding.inflate(inflater, container, false)
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        keysource = Prefs[CODI_KEY_P_KEY, ""]
        binding.let { it ->
            childFragmentManager.setFragmentResultListener("requestKey", this) { key, bundle ->
                val data = Gson().fromJson(bundle.getString("data"), CodiMovResponse::class.java)
                Log.d(TAG, "DATA : $data")
                getMovData(data)
            }
            it.confirmarBtnDetailPagar.setOnClickListener { btn ->

                if (isRefund) {
                    if (amountDev!! > 0.0 && amountDev!! <= codiMov?.amount!!) {
                        refundAmount()
                    }
                } else {
                    processPayment()
                }
            }
            it.cancelDevBtn.setOnClickListener {
                processPayment(2)
                //findNavController().navigate(R.id.action_codiDevPayFragment_to_codiMovFragment)
            }
            arguments?.let {
                val data = Gson().fromJson(it.getString("data"), CodiMovResponse::class.java)
                getMovData(data)
            }
        }
    }

    private fun getMovData(data: CodiMovResponse) {
        codiMov = data
        data.let { d ->
            if (d != null) {
                binding.detailNameScan.text = if (data.isDev == true) d.owner else d.vendor
                binding.amountNumberValue.text = "$ 0.0"
                d.amount.let { monto ->
                    if (monto != null) {
                        amountDev = monto
                        binding.amountNumberValue.text = "$ $monto"
                    }
                }
                binding.conceptValue.text = d.concept
                binding.referencesValue.text = d.reference
                isRefund = d.incomeFrom.equals("devPayment")
                if (isRefund || (d.amount == 0.0)) {
                    binding.editDetailAmount.visibility = View.VISIBLE
                    binding.amountNumberValue.visibility = View.GONE
                    binding.confirmarBtnDetailPagar.isEnabled = false
                    binding.editDetailAmount.setText("${d.amount ?: 0.0}")
                    enableBtn()
                    binding.editDetailAmount.addTextChangedListener { txt ->
                        if (!txt.isNullOrEmpty()) {
                            amountDev = if (txt.isEmpty() || txt.matches(
                                    "[0-9]{1,13}(\\.[0-9]*)?"
                                        .toRegex()
                                )
                            ) {
                                txt.toString()
                                    .replace("$", "")
                                    .trim()
                                    .toDouble()
                            } else {
                                0.0
                            }
                        } else if (txt.isNullOrBlank()) {
                            amountDev = 0.0
                        }
                        validateBtn()
                    }

                }
            }
        }
    }

    private fun enableBtn() {
        if (isRefund) {
            amountDev = codiMov?.amount ?: 0.0
            validateBtn()
        }
    }

    private fun validateBtn() {
        binding.confirmarBtnDetailPagar.isEnabled = false
        if (binding.editDetailAmount.isEnabled) {
            if (isRefund) {
                amountDev.let {
                    if (it != null && (it > 0.0 && it <= codiMov?.amount!!)) {
                        binding.confirmarBtnDetailPagar.isEnabled = it > 0.0
                    }
                }
            } else {
                if (codiMov?.amount!! > 0) {
                    binding.confirmarBtnDetailPagar.isEnabled = true
                } else {
                    amountDev.let {
                        if (it != null && (it > 0.0)) {
                            binding.confirmarBtnDetailPagar.isEnabled = it > 0.0
                        }
                    }
                }
            }
        }
    }

    private fun processPayment(identifier: Int? = 0) {
        val idcn = codiMov?.idc + getFolioMensajeCobro()
        val millis = Calendar.getInstance().timeInMillis
        val cobroReq = CobroRequest(
            IDC = codiMov?.idc,
            IDCN = if (codiMov?.folio?.length!! < 20) {
                idcn
            } else {
                codiMov?.folio
            },
            DES = codiMov?.concept,
            AMO = if (codiMov?.amount!!.equals(0.0)) {
                amountDev!!
            } else {
                codiMov?.amount
            },
            DAT = millis,
            REF = codiMov?.reference?.toLong(),
            COM = 1,
            TYP = codiMov?.typ,
            v = Vendor(
                NAM = codiMov?.vendor,
                ACC = codiMov?.acc,
                BAN = codiMov?.ban,
                TYC = 40,
                DEV = codiMov?.dev
            ),
            qrReader = millis
        )

        // Navigate to pin validation
        parentFragmentManager.setFragmentResult(
            "data",
            bundleOf("dataReq" to cobroReq.encryptByGeneralKey(),
                "dataMovResp" to codiMov.encryptByGeneralKey())
        )
        safeNavigate(R.id.action_codiDevPayFragment_to_payPendingMovFragment)

    }

    private fun refundAmount() {
        dialog.show(childFragmentManager, TAG)
        val refundData = RefundRequest(
            account = MainDashboardActivity.accountData.cuenta.cuenta,
            folio = codiMov?.folio,
            amount = amountDev,
            ownerAccount = codiMov?.acc,
            typeAccount = 40
        )
        Log.d(TAG, "DATA ${refundData.toJson()}")
        var request = CoDiASPRequest(
            accountDevPayment = MainDashboardActivity.accountData.cuenta.cuenta,
            data = encriptaInformacionB64(keysource!!, refundData.toJson())
        )
        var webService = ""
        codiAspApi().doRefund(request.encryptByGeneralKey()){
            webService = it
        }.enqueue(object : Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                dialog.dismiss()
                if (response.isSuccessful) {
                    val res = response?.body()?.decryptByGeneralKey<CommonServiceResponse>()!!
                    if (res.code == 0) {
                        parentFragmentManager.clearFragmentResultListener("requestKey")
                        codiMov?.incomeFrom = "dev"
                        codiMov?.amount = refundData.amount
                        parentFragmentManager.setFragmentResult(
                            "requestKey",
                            bundleOf("data" to codiMov.toJson())
                        )
                        safeNavigate(R.id.action_codiDevPayFragment_to_codiDevDetailFragment)
                        GlobalScope.async {
                            aspTrackingRepository.consume(
                                webService = webService,
                                typeResponse = AspTrackingRepositoryImpl.ConsumeServiceTypeResponse.SUCCESS,
                                response = response.body()
                            )
                        }
                    } else {
                        GlobalScope.async {
                            aspTrackingRepository.consume(
                                webService = webService,
                                typeResponse = AspTrackingRepositoryImpl.ConsumeServiceTypeResponse.ERROR,
                                response = response.body()
                            )
                        }
                        showCustomDialogError("Info", message2 = res.message!!)
                    }
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                showCustomDialogError(message1 = "Información", message2 = "Ocurrió un error de comunicación, favor de intentar más tarde.")
            }

        })

    }


    private fun codiAspApi(): CodiAspRepository {
        return codiAspRepository
    }


    private fun setupToolbar() {
        this.binding.accountDetailToolbar.setTitle("Mis Operaciones")
        this.binding.accountDetailToolbar.setASPMaterialToolbarsListeners(this)
    }


    @Override
    override fun onClickBackButton() {
        childFragmentManager.clearFragmentResult("requestKey")
        childFragmentManager.clearFragmentResultListener("requestKey")
        childFragmentManager.executePendingTransactions()

        parentFragmentManager.clearFragmentResult("requestKey")
        parentFragmentManager.clearFragmentResultListener("requestKey")
        parentFragmentManager.executePendingTransactions()
        onClickBackPressed()
    }

}