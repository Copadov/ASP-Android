package asp.android.asppagos.data.models

import com.google.gson.annotations.SerializedName

data class PendingPaymentPushNotification(
    @SerializedName ("id") val id: String? = null,
    @SerializedName ("hp") val hp: Int? = null,
    @SerializedName ("e") val e: Int? = null,
    @SerializedName ("cc") val cc: String? = null,
    @SerializedName ("mt") val mt: Int? = null,
    @SerializedName ("hs") val hs: Int? = null,
    @SerializedName ("pc") val pc: String? = null,
    @SerializedName ("cr") val cr: String? = null,
    @SerializedName ("c") val c: CClass? = null,
    @SerializedName ("v") val v: VClass? = null,
)

data class CClass(
    @SerializedName ("nc") val nc: String? = null,
    @SerializedName ("dv") val dv: Int? = null,
    @SerializedName ("tc") val tc: Int? = null,
    @SerializedName ("cb") val cb: String? = null,
    @SerializedName ("ci") val ci: Int? = null,
    @SerializedName ("nb") val nb: String? = null,
)

data class VClass(
    @SerializedName ("nc") val nc: String? = null,
    @SerializedName ("dv") val dv: Int? = null,
    @SerializedName ("tc") val tc: Int? = null,
    @SerializedName ("cb") val cb: String? = null,
    @SerializedName ("ci") val ci: Int? = null,
    @SerializedName ("nb") val nb: String? = null,
    @SerializedName ("nb2") val nb2: String? = null,
    @SerializedName ("cb2") val cb2: String? = null,
    @SerializedName ("tc2") val tc2: String? = null,
)

