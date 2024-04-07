package asp.android.asppagos.ui.fragments.codi


import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import asp.android.aspandroidcore.utils.desencriptaInformacionB64
import asp.android.aspandroidcore.utils.encriptaInformacionB64
import asp.android.aspandroidcore.utils.generaConsecutivo
import asp.android.aspandroidcore.utils.generaHmacB64FromKey
import asp.android.aspandroidcore.utils.getFolioMensajeCobro
import asp.android.aspandroidcore.utils.sha512Hex
import asp.android.aspandroidmaterial.ui.toolbars.ASPMaterialToolbarMainDashboard
import asp.android.asppagos.R
import asp.android.asppagos.data.models.codi.AccountCodiData
import asp.android.asppagos.data.models.codi.CobroContainerReq
import asp.android.asppagos.data.models.codi.CobroReq
import asp.android.asppagos.data.models.codi.CobroRequest
import asp.android.asppagos.data.models.codi.Dev
import asp.android.asppagos.data.models.codi.RequestCobro
import asp.android.asppagos.data.models.codi.SerialRequest
import asp.android.asppagos.data.models.codi.SerialResponse
import asp.android.asppagos.data.models.codi.Vendor
import asp.android.asppagos.data.models.send_money.CommonServiceResponse
import asp.android.asppagos.data.repositories.AspTrackingRepository
import asp.android.asppagos.data.repositories.AspTrackingRepositoryImpl
import asp.android.asppagos.data.repositories.CodiAspRepository
import asp.android.asppagos.databinding.FragmentCobrarCodiBinding
import asp.android.asppagos.ui.activities.MainDashboardActivity
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.ui.viewmodels.CobrarQrCodiViewModel
import asp.android.asppagos.utils.CODI_ALIAS_NC_KEY
import asp.android.asppagos.utils.CODI_BENEFICIARIO_KEY
import asp.android.asppagos.utils.CODI_DV_KEY
import asp.android.asppagos.utils.CODI_KEYSORUCE_KEY
import asp.android.asppagos.utils.CODI_KEY_P_KEY
import asp.android.asppagos.utils.EncryptUtils.decryptByGeneralKey
import asp.android.asppagos.utils.EncryptUtils.encryptByGeneralKey
import asp.android.asppagos.utils.Prefs
import asp.android.asppagos.utils.formatAmount
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

class CobrarCodiFragment : BaseFragment(),
    ASPMaterialToolbarMainDashboard.ASPMaterialToolbarMainDashboardListeners {


    private var _binding: FragmentCobrarCodiBinding? = null
    private val binding get() = _binding!!
    override var TAG: String = this.javaClass.name
    private var _data: CobrarQrCodiViewModel = CobrarQrCodiViewModel();
    private val data get() = _data;

    private var serial: SerialResponse? = SerialResponse("", 6531)

    private var current: String? = ""

    private val codiAspRepository: CodiAspRepository by inject()
    private val aspTrackingRepository by inject<AspTrackingRepository>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCobrarCodiBinding.inflate(inflater, container, false)
        findNavController().enableOnBackPressed(true)
        return binding.root;
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getSerial()
        setupToolbar()
        binding.let { it ->
            it.tiAmount.addTextChangedListener { text ->
                if (!text.isNullOrBlank()) {
                    val finalAmount = text.toString().formatAmount(10, 2)
                    if (finalAmount != text.toString()) {
                        it.tiAmount.setText(finalAmount)
                        it.tiAmount.setSelection(finalAmount.length)
                    }

                    data.setAmount(finalAmount.toDouble())
                    validateBtn()
                }
            }
            it.montoVisual.setOnCheckedChangeListener { buttonView, isChecked ->
                it.tilAmount.isEnabled = !isChecked
                if (!isChecked) {
                    it.tiAmount.setText("")
                    data.setAmount(0.0)
                    validateBtn()
                } else {
                    it.tiAmount.setText("")
                    data.setAmount(0.0)
                    validateBtn()
                }
            }

            it.conceptoInput.addTextChangedListener { text ->
                if (!text.isNullOrEmpty()) {
                    data.setConcept(text.toString())
                }
                validateBtn()
            }
            it.referenciaInput.filters = arrayOf(InputFilter.LengthFilter(7))
            it.referenciaInput.addTextChangedListener { text ->
                if (!text.isNullOrEmpty()) {
                    data.setReference(text.toString())
                }
                validateBtn()
            }

            it.cobrarCodiBtn.setOnClickListener { btn ->

                if (btn.isEnabled) {
                    dialog.show(requireActivity().supportFragmentManager, TAG)
                    val idc = getFolioMensajeCobro()
                    val beneficiaryInfo = Prefs.get(CODI_BENEFICIARIO_KEY, "")
                        .decryptByGeneralKey<AccountCodiData>()!!
                    val dev = "${Prefs.get(CODI_ALIAS_NC_KEY, "")}/${Prefs.get(CODI_DV_KEY, "")}"

                    var vendor = Vendor(
                        NAM = beneficiaryInfo.name,
                        ACC = beneficiaryInfo.cb,
                        BAN = beneficiaryInfo.ci,
                        TYC = beneficiaryInfo.tc,
                        DEV = dev
                    )

                    val cobro = CobroRequest(
                        IDC = idc,
                        IDCN = null,
                        DES = it.conceptoInput.text.toString(),
                        AMO = null,
                        DAT = Calendar.getInstance().timeInMillis,
                        REF = it.referenciaInput.text.toString().toLong(),
                        COM = 1,
                        TYP = 19,
                        v = vendor
                    )
                    if (data.amount != null) {
                        cobro.AMO = data.amount
                    }
                    Log.d(TAG, "COBRO CADENA $cobro")
                    val cspvCadena: String = (idc + Prefs.get(CODI_KEYSORUCE_KEY, "") + serial?.ser)

                    val keysource2 = sha512Hex(cspvCadena)

                    val consecutivo = generaConsecutivo(Prefs.get(CODI_DV_KEY, ""))

                    val cadenaHmac = Prefs.get(
                        CODI_ALIAS_NC_KEY,
                        ""
                    ) + consecutivo + serial?.ser + cobro.toJson()
                    val hmac = generaHmacB64FromKey(cadenaHmac, keysource2!!)

                    val enc = encriptaInformacionB64(keysource2, cobro.toJson())

                    val cobroStr = CobroReq(
                        cry = hmac,
                        TYP = 19,
                        ic = CobroContainerReq(
                            ENC = enc,
                            IDC = idc,
                            SER = serial?.ser
                        ),
                        v = Dev(dev)
                    )
                    Log.d(TAG, "DATA QR ${cobroStr.toJson()}")
                    parentFragmentManager.setFragmentResult(
                        "requestKey",
                        bundleOf("data" to Gson().toJson(cobro))
                    )
                    parentFragmentManager.setFragmentResult(
                        "req", bundleOf(
                            "data" to CobroReq(
                                cry = hmac,
                                TYP = 19,
                                ic = CobroContainerReq(
                                    ENC = enc,
                                    IDC = idc,
                                    SER = serial?.ser
                                ),
                                v = Dev(dev)
                            ).toJson()
                        )
                    )

                    val cobroCifrado: RequestCobro = RequestCobro(
                        "", MainDashboardActivity
                            .accountData.cuenta.cuenta
                    )

                    registarCobro(cobroCifrado, cobroStr, cobro)


                }
            }
        }
    }

    private fun validateBtn() {
        binding.cobrarCodiBtn.isEnabled = false
        if (binding.montoVisual.isChecked) {
            if (!binding.conceptoInput.text.isNullOrEmpty()
                && !binding.referenciaInput.text.isNullOrEmpty()
            ) {
                binding.cobrarCodiBtn.isEnabled = true;
            }
        } else {
            Log.d(TAG, "AMOUNT ELSE : ${data.amount > 0}")
            if (data.amount > 0
                && !binding.referenciaInput.text.isNullOrEmpty()
                && !binding.conceptoInput.text.isNullOrEmpty()
            ) {
                binding.cobrarCodiBtn.isEnabled = true;
            }
        }
    }

    private fun getSerial() {
        val obj = SerialRequest(MainDashboardActivity.accountData.cuenta.cuenta)
        var webService = ""
        codiAspApi().getSerial(obj.encryptByGeneralKey()){
            webService = it
        }.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                val res = response.body()?.decryptByGeneralKey<CommonServiceResponse>()
                if (res?.code == 0) {
                    val mine = desencriptaInformacionB64(Prefs.get(CODI_KEY_P_KEY, ""), res.data!!)
                    mine.let {
                        if (it != null) {
                            serial = Gson().fromJson(it, SerialResponse::class.java)
                            binding.referenciaInput.text?.append(serial?.ref)
                        }
                    }
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
                Log.d(TAG, "ERROR \n${t.message!!}")
            }

        })
    }


    private fun registarCobro(requestCobro: RequestCobro, cobroStr: CobroReq, req: CobroRequest) {
        val keysourcepass = Prefs.get(CODI_KEY_P_KEY, "")
        val dev = "${Prefs.get(CODI_ALIAS_NC_KEY, "")}/${Prefs.get(CODI_DV_KEY, "")}"
        val cspvCadena: String = (req.IDC + keysourcepass + serial?.ser)
        val keysource2 = sha512Hex(cspvCadena)
        val consecutivo = generaConsecutivo(Prefs.get(CODI_DV_KEY, ""))
        cobroStr?.ic?.ENC = encriptaInformacionB64(keysource2!!, req.toJson())
        Log.d(TAG, "DATA ENVIO ASP ${cobroStr.toJson()}")
        requestCobro.payment = cobroStr.toJson()
        Log.d(TAG, "DATA SEND ${requestCobro.toJson()}")
        val request = requestCobro.encryptByGeneralKey()
        var webService = ""
        codiAspApi().registerCbroPayment(request){
            webService = it
        }.enqueue(object : Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                Log.d(TAG, response.body()!!)
                dialog.dismiss()
                if (response.isSuccessful) {
                    val res = response.body()?.decryptByGeneralKey<CommonServiceResponse>()
                    Log.d(TAG, "RES ${res.toJson()}")
                    if (res?.code != 0) {
                        showCustomDialogError(
                            message1 = "Información",
                            message2 = res?.message!!
                        )

                        // Update later

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
                        safeNavigate(R.id.action_cobrarCodiFragment_to_cobrarQrCodi)
                    }
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                GlobalScope.async {
                    aspTrackingRepository.consume(
                        webService = webService,
                        typeResponse = AspTrackingRepositoryImpl.ConsumeServiceTypeResponse.ERROR
                    )
                }
                dialog.dismiss()
                showCustomDialogError(message1 = "Información", message2 = "Ocurrió un error de comunicación, favor de intentar más tarde.")
                Log.d(TAG, t.message!!)
            }
        })
    }

    private fun codiAspApi(): CodiAspRepository {
        return codiAspRepository
    }


    private fun setupToolbar() {
        this.binding.accountDetailToolbar.setTitle("Cobrar con CoDi")
        this.binding.accountDetailToolbar.setASPMaterialToolbarsListeners(this)
    }


    override fun onClickBackButton() {
        findNavController().popBackStack()
    }
}