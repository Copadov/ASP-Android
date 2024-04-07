package asp.android.aspandroidcore.utils

import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import java.text.DecimalFormat

fun View.snackbar(message: String) {
    val snackbar = Snackbar
        .make(this, message, Snackbar.LENGTH_LONG).also { snackbar ->
            snackbar.setAction("Aceptar") {
                snackbar.dismiss()
            }
        }
    snackbar.view.background = ContextCompat.getDrawable(context, android.R.color.black)
    snackbar.show()
}

fun String?.typeAccountId(): Int {
    return when (this?.length) {
        16 -> 3 // CUENTA
        18 -> 40 // CLABE
        else -> 10 // NUMERO TELEFONICO
    }
}

fun Double.getAmountFormat(): String {
    val simpleMoneyFormat = DecimalFormat("###,###,###.##")
    val doubleFormatter = simpleMoneyFormat.format(this)
    return if (doubleFormatter.contains(".")) {
        doubleFormatter
    } else {
        "${doubleFormatter}.00"
    }
}