package asp.android.asppagos.firebase

import android.content.Context
import android.util.Log
import asp.android.asppagos.data.models.codi.FirebaseData
import asp.android.asppagos.utils.toJson
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.iid.internal.FirebaseInstanceIdInternal
import com.google.firebase.installations.remote.FirebaseInstallationServiceClient
import com.google.firebase.messaging.FirebaseMessaging


class FirebaseInstanceServiceCodi private constructor(){
    private var firebaseBanxico: FirebaseApp? = null
    private var firebasePublic:FirebaseApp? = null


    /*
    *       37600425090	AIzaSyCI62PF98EvaTA-TWljDgVjQzM3VEzVlrI	codi-productivo2
			1012774599182	AIzaSyAdcZV2rVO7Vt9Ln_QUPG_qoZ3fXe_YKyU	codi-productivo5
			378222339434	AIzaSyAVLJT9CNKicKtNxFs_JztsnrcSY1gNOzQ	codi-productivo
			498873124915	AIzaSyACmurfbaio0VDdxbDfC5K6fJ72j-rL3XY	codi-productivo7
			889994019664	AIzaSyCTFLCj7wdPz1yx_yVe0S5R0gT-Sk1uhvI	codi-productivo3
			568932449615	AIzaSyBdMWPvG-Q-Z_FkNMw-4-V2Hj15FcfeZTc	codi-productivo6
			1007063812319	AIzaSyCVxQsTBcmYW_H_DD8v6V723mTMCKEt2Fc	codi-productivo4
    * */

    private var firebaseHashMap = mutableMapOf(
        Pair("37600425090",FirebaseData(
            pId = "codi-productivo2",
            apiKey = "AIzaSyCI62PF98EvaTA-TWljDgVjQzM3VEzVlrI"
        ))
        , Pair("1012774599182",FirebaseData(
            pId = "codi-productivo5",
            apiKey = "AIzaSyAdcZV2rVO7Vt9Ln_QUPG_qoZ3fXe_YKyU"
        ))
        ,Pair("378222339434",FirebaseData(
            pId = "codi-productivo",
            apiKey = "AIzaSyAVLJT9CNKicKtNxFs_JztsnrcSY1gNOzQ"
        )),Pair("498873124915",FirebaseData(
            pId = "codi-productivo7",
            apiKey = "AIzaSyACmurfbaio0VDdxbDfC5K6fJ72j-rL3XY"
        )),Pair("889994019664",FirebaseData(
            pId = "codi-productivo3",
            apiKey = "AIzaSyCTFLCj7wdPz1yx_yVe0S5R0gT-Sk1uhvI"
        )),Pair("568932449615",FirebaseData(
            pId = "codi-productivo6",
            apiKey = "AIzaSyBdMWPvG-Q-Z_FkNMw-4-V2Hj15FcfeZTc"
        )),
        Pair("1007063812319",FirebaseData(
            pId = "codi-productivo4",
            apiKey = "AIzaSyCVxQsTBcmYW_H_DD8v6V723mTMCKEt2Fc"
        )),
        Pair("586294353908", FirebaseData(
            pId = "cobrospei-beta8",
            apiKey = "AIzaSyCOPJLSaXNda0fGm8A07HL-1bJaPpR4B8k"
        )),
        Pair("501487815695", FirebaseData(
            pId = "cobrospei-beta7",
            apiKey = "AIzaSyBxXCvCFVkkrcXkB_Nlu9fP8MTRtlRNMjA"
        )),
        Pair("670058745038", FirebaseData(
            pId= "cobrospei-beta6",
            apiKey = "AIzaSyDVBHoVOQQtjlM7MAD1VhMXMNd76whZ3JQ"
        )),
        Pair("976971810811", FirebaseData(
            pId = "cobrospei-beta5",
            apiKey = "AIzaSyA9_sZ4SvNtl-_XNC4Q0yzN8nTovXX3sg0"
        )),
        Pair("765825704675", FirebaseData(
            pId = "cobrospei-beta4",
            apiKey = "AIzaSyA9rj-2effe8w0xPPk0bSESKXdv27Qw1oc"
        )),
        Pair("504623487161", FirebaseData(
            pId = "cobrospei-beta3",
            apiKey = "AIzaSyD4oGyPA2ILgv4rFzSzkgwgc5yUsA9n2ZI"
        )
        )
    )
    companion object {
        @Volatile
        private var instance: FirebaseInstanceServiceCodi? = null


        fun getInstance(): FirebaseInstanceServiceCodi {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = FirebaseInstanceServiceCodi()
                    }
                }
            }
            return instance!!
        }

    }
    fun getFirebaseInstance(): FirebaseApp {
        return firebaseBanxico!!
    }

    fun getFirebasePublic(): FirebaseApp?{
        return firebasePublic
    }


    fun initFirebase(firebaseData: FirebaseData? = null,context: Context) {

        firebaseData.let {
            val data = firebaseHashMap[it?.gId]
            // firebasePublic = FirebaseApp.initializeApp(context)
            val options =  FirebaseOptions.Builder()
                .setProjectId(data?.pId)
                .setApplicationId("1:${it?.gId}:android:b63c1d8b40c8b20c")
                .setApiKey(data?.apiKey!!)
                .setGcmSenderId(it?.gId)
                .build()
            FirebaseApp.getInstance().delete()
            firebaseBanxico =  FirebaseApp.initializeApp(context,options)
            Log.d(FirebaseInstanceServiceCodi.javaClass.name,"${options.toJson()}")

        }
    }


}



