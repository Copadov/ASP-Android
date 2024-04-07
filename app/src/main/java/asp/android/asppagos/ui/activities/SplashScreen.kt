package asp.android.asppagos.ui.activities

import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import asp.android.aspandroidmaterial.ui.layouts.ASPMaterialDialogCustom
import asp.android.asppagos.R
import asp.android.asppagos.data.models.InitialDataObjectResponse
import asp.android.asppagos.data.models.InitialDataRequestData
import asp.android.asppagos.data.modules.createAppModules
import asp.android.asppagos.utils.ACTIVAR_VALIDACION_OCR
import asp.android.asppagos.utils.BUSCAR_COLONIAS_POR_CP
import asp.android.asppagos.utils.DEVICE_ID_UNIQUE_GUID
import asp.android.asppagos.utils.HTTP_OCRPSS
import asp.android.asppagos.utils.HTTP_OCRUSR
import asp.android.asppagos.utils.HTTP_PASS
import asp.android.asppagos.utils.HTTP_URL
import asp.android.asppagos.utils.HTTP_URL_IOS
import asp.android.asppagos.utils.HTTP_URL_OCR
import asp.android.asppagos.utils.HTTP_USER
import asp.android.asppagos.utils.KEY
import asp.android.asppagos.utils.KEY_CAMBIA_PASS
import asp.android.asppagos.utils.KEY_PASS
import asp.android.asppagos.utils.NFC_REQUEST
import asp.android.asppagos.utils.OBTENER_NUMERO_CELULAR_ASP
import asp.android.asppagos.utils.PROPERTY_REGISTER_SUCCESS
import asp.android.asppagos.utils.Prefs
import asp.android.asppagos.utils.REGISTRA_CUENTA_SIMPLIFICADA_ASP
import asp.android.asppagos.utils.REGISTRA_IMAGENES_CUENTA_SIMPLIFICADA_ASP
import asp.android.asppagos.utils.SAFETY_TOKEN
import asp.android.asppagos.utils.ServerErrorCodes
import asp.android.asppagos.utils.ServiceClient
import asp.android.asppagos.utils.TIME_OUT_SERV_MILIS
import asp.android.asppagos.utils.TRACKING_URL
import asp.android.asppagos.utils.VALIDA_OCR
import asp.android.asppagos.utils.VALIDA_OCR_ASP
import asp.android.asppagos.utils.VERSION_APP
import asp.android.asppagos.utils.decryptInitialData
import asp.android.asppagos.utils.encripRequesttInitialData
import asp.android.asppagos.utils.fromJson
import asp.android.asppagos.utils.getAppVersionCode
import asp.android.asppagos.utils.showSingleButtonDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.core.context.loadKoinModules

class SplashScreen : AppCompatActivity() {

    companion object {
        const val TAG = "SplashScreen"
    }

    var versionAppAndroid: Any? = null
    var aspLlaveInicial: Any? = null
    var serviceClient: ServiceClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash_screen)

        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val tableRef: DatabaseReference = database.getReference(getString(R.string.firebase_table))
        tableRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                versionAppAndroid =
                    dataSnapshot.child(getString(R.string.firebase_table_versionApp)).value
                aspLlaveInicial =
                    dataSnapshot.child(getString(R.string.firebase_aes_key)).value

                val aspuser =
                    dataSnapshot.child(getString(R.string.firebase_usuario)).value.toString()
                val asppass = dataSnapshot.child(getString(R.string.firebase_pass)).value.toString()
                val baseurl =
                    dataSnapshot.child(getString(R.string.firebase_baseurl)).value.toString()

                val myServiceClient: ServiceClient by lazy {
                    ServiceClient(
                        applicationContext, aspuser, asppass, baseurl
                    )
                }

                serviceClient = myServiceClient

                if (applicationContext.getAppVersionCode() == versionAppAndroid.toString()
                        .toInt()
                ) {

                    downloadInitialData()

                } else {
                    showSingleButtonDialog(
                        getString(
                            R.string.information_dialog_text
                        ),
                        getString(R.string.app_invalid_version_message_text),
                        getString(R.string.accept)
                    )
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    private fun exitSplashScreen(isTest: Boolean) {
        if (isTest.not()) {
            if (Prefs[PROPERTY_REGISTER_SUCCESS]) {
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                startActivity(Intent(this, OnboardingV2Activity::class.java))
            }
            this.finish()
        } else {
            startActivity(Intent(this, MainDashboardActivity::class.java))
        }
    }

    private fun getInitialData(initialData: InitialDataObjectResponse?, onCompletion: () -> Unit) {
        //Log.d("JHMM", "initialData: ${initialData}")
        Prefs[VERSION_APP] = initialData!!.version
        Prefs[HTTP_USER] = initialData.datos.find { it.clave == HTTP_USER }?.valor
        Prefs[VALIDA_OCR] = initialData.datos.find { it.clave == VALIDA_OCR }?.valor
        Prefs[REGISTRA_IMAGENES_CUENTA_SIMPLIFICADA_ASP] = initialData.datos.find {
            it.clave == REGISTRA_IMAGENES_CUENTA_SIMPLIFICADA_ASP
        }?.valor
        Prefs[REGISTRA_CUENTA_SIMPLIFICADA_ASP] =
            initialData.datos.find { it.clave == REGISTRA_CUENTA_SIMPLIFICADA_ASP }?.valor
        Prefs[VALIDA_OCR_ASP] = initialData.datos.find { it.clave == VALIDA_OCR_ASP }?.valor
        Prefs[OBTENER_NUMERO_CELULAR_ASP] =
            initialData.datos.find { it.clave == OBTENER_NUMERO_CELULAR_ASP }?.valor
        Prefs[HTTP_URL_OCR] = initialData.datos.find { it.clave == HTTP_URL_OCR }?.valor
        Prefs[HTTP_URL] = initialData.datos.find { it.clave == HTTP_URL }?.valor
        Prefs[TRACKING_URL] = initialData.datos.find { it.clave == TRACKING_URL }?.valor
            initialData.datos.find { it.clave == TIME_OUT_SERV_MILIS }?.valor
        Prefs[KEY] = initialData.datos.find { it.clave == KEY }?.valor
        Prefs[HTTP_OCRPSS] = initialData.datos.find { it.clave == HTTP_OCRPSS }?.valor
        Prefs[HTTP_OCRUSR] = initialData.datos.find { it.clave == HTTP_OCRUSR }?.valor
        Prefs[HTTP_PASS] = initialData.datos.find { it.clave == HTTP_PASS }?.valor
        Prefs[BUSCAR_COLONIAS_POR_CP] =
            initialData.datos.find { it.clave == BUSCAR_COLONIAS_POR_CP }?.valor
        Prefs[ACTIVAR_VALIDACION_OCR] =
            initialData.datos.find { it.clave == ACTIVAR_VALIDACION_OCR }?.valor
        Prefs[SAFETY_TOKEN] = initialData.datos.find { it.clave == SAFETY_TOKEN }?.valor
        Prefs[HTTP_URL_IOS] = initialData.datos.find { it.clave == HTTP_URL_IOS }?.valor
        Prefs[KEY_PASS] = initialData.datos.find { it.clave == KEY_PASS }?.valor
        Prefs[KEY_CAMBIA_PASS] = initialData.datos.find { it.clave == KEY_CAMBIA_PASS }?.valor

        onCompletion.invoke()
    }

    private fun showInitialDataErrorDialog(onButtonClickListener: () -> Unit) {
        val dialog = ASPMaterialDialogCustom.newInstance(
            getString(R.string.dialog_error_initial_data_title),
            getString(R.string.dialog_error_initial_data_message),
            getString(R.string.action_retry),
            ASPMaterialDialogCustom.DialogIconType.INFO.id,
            false)
        dialog.setASPMaterialDialogCustomListener(object :
            ASPMaterialDialogCustom.ASPMaterialDialogCustomListener {
            override fun onClickAcceptButton(optionType: Int) {
            }

            override fun onClickClose() {
                onButtonClickListener()
                dialog.dismiss()
            }
        })
        dialog.show(supportFragmentManager, TAG)

    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun downloadInitialData() {
        GlobalScope.launch {
            val encriptedData = encripRequesttInitialData(
                InitialDataRequestData(
                    reqIni = Prefs[DEVICE_ID_UNIQUE_GUID, ""], idCanal = 1
                ), aspLlaveInicial.toString()
            )

            val response = serviceClient!!.getInitialData(encriptedData)

            if (response.codigo == 1) {
                showInitialDataErrorDialog() {
                    finish()
                }
            } else {
                when (response.codigo) {
                    ServerErrorCodes.SUCCESS.ordinal -> {

                        if (response.data.isEmpty()) {
                            showInitialDataErrorDialog() {
                                downloadInitialData()
                            }
                        } else {

                            val decryptedData = decryptInitialData(
                                response.data, aspLlaveInicial.toString()
                            )

                            val initialData =
                                Gson().fromJson<InitialDataObjectResponse>(
                                    decryptedData
                                )

                            getInitialData(initialData) {

                                val appModules = createAppModules()
                                loadKoinModules(appModules)

                                exitSplashScreen(false)
                            }
                        }
                    }

                    else -> {
                        showInitialDataErrorDialog() {
                            downloadInitialData()
                        }
                    }
                }
            }
        }
    }


    public override fun onResume() {
        super.onResume()
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
            processIntent(intent)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        processIntent(intent!!)

    }

    /**
     * Function to parse the NDEF Message from the intent.
     * Saves the QR readed from the message to the app preferences.
     *
     * @param intent NFC Intent with the QR message
     */
    private fun processIntent(intent: Intent) {
        val rawMsgs = intent.getParcelableArrayExtra(
            NfcAdapter.EXTRA_NDEF_MESSAGES
        )
        val msgs = mutableListOf<NdefMessage>()
        if (rawMsgs != null) {
            for (i in rawMsgs.indices) {
                msgs.add(i, rawMsgs[i] as NdefMessage)
            }
            val message = String(msgs[0].records[0].payload)
            Prefs[NFC_REQUEST] = message
        }
    }
}