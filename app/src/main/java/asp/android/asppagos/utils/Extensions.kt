package asp.android.asppagos.utils

import android.animation.*
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import asp.android.aspandroidmaterial.ui.layouts.ASPMaterialDialogCustom
import asp.android.asppagos.R
import asp.android.asppagos.ui.adapters.MovementType
import com.google.android.material.snackbar.Snackbar
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.*


fun ViewGroup.inflate(@LayoutRes layout: Int): View = LayoutInflater
    .from(context)
    .inflate(layout, this, false)

fun Double.roundOffDecimal(): Double {
    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.CEILING
    return df.format(this).toDouble()
}

fun Context.getAppVersionCode(): Int {
    return try {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        packageInfo.versionCode
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        -1
    }
}

fun Activity.showSingleButtonDialog(title: String, message: String, buttonText: String) {
    runOnUiThread {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton(buttonText) { dialog, _ ->
                // Acción al hacer clic en el botón Aceptar
                dialog.dismiss() // Cierra el diálogo
            }

        val dialog = builder.create()
        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setTextColor(Color.BLACK)
        }

        dialog.show()
    }
}

fun Activity.showSingleButtonDialog(
    title: String,
    message: String,
    buttonText: String,
    onButtonClickListener: () -> Unit // Función de devolución de llamada cuando se hace clic en el botón
) {
    runOnUiThread {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton(buttonText) { dialog, _ ->
                // Acción al hacer clic en el botón Aceptar
                dialog.dismiss() // Cierra el diálogo
                onButtonClickListener() // Llama a la función de devolución de llamada
            }

        val dialog = builder.create()
        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setTextColor(Color.BLACK)
        }

        dialog.show()
    }
}

fun Activity.showToast(message: String, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, message, duration).show()
}

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, message, duration).show()
}

fun TextView.setWebLink(text: String, url: String) {
    this.text = text
    this.setOnClickListener {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }
}

fun String.Companion.generateGUID(): String {
    return UUID.randomUUID().toString()
}

fun TextView.setMaskedWebLink(text: String, maskedText: String, url: String) {
    val spannableString = SpannableString(text)
    val clickableSpan = object : ClickableSpan() {
        override fun onClick(widget: View) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        }
    }
    val startIndex = text.indexOf(maskedText)
    val endIndex = startIndex + maskedText.length
    spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

    this.text = spannableString
    this.movementMethod = LinkMovementMethod.getInstance()
}

fun TextView.append(string: String?, @ColorRes color: Int) {
    if (string == null || string.isEmpty()) {
        return
    }

    val spannable: Spannable = SpannableString(string)
    spannable.setSpan(
        ForegroundColorSpan(ContextCompat.getColor(context, color)),
        0,
        spannable.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    append(spannable)
}

fun <Int> MutableList<Int>.mapInPlace(mutator: (Int) -> (Int)) {
    this.forEachIndexed { i, value ->
        val changedValue = mutator(value)

        this[i] = changedValue
    }
}


fun TextView.appendWithFont(string: String?, @ColorRes color: Int) {
    if (string == null || string.isEmpty()) {
        return
    }

    val spannable: Spannable = SpannableString(string)
    spannable.setSpan(
        ForegroundColorSpan(ContextCompat.getColor(context, color)),
        0,
        spannable.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    spannable.setSpan(
        StyleSpan(
            Typeface.BOLD
        ), 0, spannable.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    append(spannable)
}

fun ImageView.copyTextToClipboard(text: String) {
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText("label", text)
    clipboardManager.setPrimaryClip(clipData)
    Toast.makeText(context, "Copiado al portapapeles", Toast.LENGTH_SHORT).show()
}

fun Double.addAutomaticThousandSeparator(): String {
    return String.format(Locale.US, "%,02d", this).replace(',', '.')
}

fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

fun Double.convertCurrencyFormat(): String {
    val format = DecimalFormat("#,###.00")
    format.isDecimalSeparatorAlwaysShown = false
    return format.format(this).toString()
}

private fun Fragment.goToAppDetailsSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context?.packageName, null)
    }
    activity?.startActivityForResult(intent, 0)
}

private fun mapPermissionsAndResults(
    permissions: Array<out String>, grantResults: IntArray
): Map<String, Int> = permissions.mapIndexedTo(
    mutableListOf<Pair<String, Int>>()
) { index, permission -> permission to grantResults[index] }.toMap()

fun View.runAnimation(animation: Int) {
    AnimatorInflater.loadAnimator(this.context, animation)
        .apply {
            setTarget(this)
            start()
        }
}

fun Context.openURLWithBrowser(url: String) {
    ContextCompat.startActivity(
        this,
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse(url)
        ), Bundle.EMPTY
    )
}


fun RecyclerView.runLayoutAnimation(animation: Int) {
    this.layoutAnimation = AnimationUtils.loadLayoutAnimation(this.context, animation)
    this.adapter!!.notifyDataSetChanged()
    this.scheduleLayoutAnimation()
}

@SuppressLint("HardwareIds")
fun getMac(context: Context): String {
    val manager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    val info = manager.connectionInfo
    return info.macAddress.toUpperCase()
}

fun View.animateView(durationMilis: Long = 1000) {
    val scaleX = ObjectAnimator.ofFloat(this, View.SCALE_X, 0f, 1f)
    val scaleY = ObjectAnimator.ofFloat(this, View.SCALE_Y, 0f, 1f)
    val animatorSet = AnimatorSet().apply {
        interpolator = OvershootInterpolator()
        duration = durationMilis
        playTogether(scaleX, scaleY)
    }
    animatorSet.start()
}

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

fun String.getCurrentDate(): String {
    val currentDate = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return currentDate.format(formatter)
}

fun String.getDateMonthsAgo(n: Int): String {
    val currentDate = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    //val dateMonthsAgo =
    //currentDate.minusMonths(n.toLong()).with(TemporalAdjusters.firstDayOfMonth())
    val dateMonthsAgo = currentDate.minusDays(n.toLong() * 30)
    return dateMonthsAgo.format(formatter)
}

fun Fragment.showCustomDialogError(
    message1: String,
    message2: String,
    option1: String = getString(R.string.dialog_close_option_text),
    iconType: Int = ASPMaterialDialogCustom.DialogIconType.ERROR.id
) {
    ASPMaterialDialogCustom.newInstance(
        message1,
        message2,
        option1,
        iconType,
        visibleAcceptButton = false
    )
        .show(this.parentFragmentManager, this.tag)
}

fun String.enmascararNumeroTarjeta(
    ultimosDigitosSeleccionados: Int,
    numeroParaAgrupar: Int,
    ultimosDigitosNoEnmascarados: Int
): String {
    // Primero, eliminamos todos los caracteres que no sean dígitos
    val digitos = this.filter { it.isDigit() }

    // Tomamos los últimos n dígitos de la cadena
    val ultimosDigitos = digitos.takeLast(ultimosDigitosSeleccionados)

    // Luego, agrupamos los dígitos de la cantidad especificada
    val digitosAgrupados = digitos.dropLast(ultimosDigitosSeleccionados)
        .chunked(numeroParaAgrupar)

    // Calculamos cuántos dígitos se deben enmascarar
    val cantidadDigitosEnmascarados =
        digitos.length - ultimosDigitosNoEnmascarados - ultimosDigitosSeleccionados

    // Por último, reemplazamos todos los dígitos, excepto los últimos n y los que no se deben enmascarar, con una X
    val digitosEnmascarados =
        digitosAgrupados.joinToString(separator = " ") { "X".repeat(numeroParaAgrupar) }
    return "$digitosEnmascarados ${ultimosDigitos}"
}

fun String.formatoUltimos8Digitos(): String {
    // Eliminamos todos los caracteres que no sean dígitos
    val digitos = this.filter { it.isDigit() }

    // Tomamos los últimos 8 dígitos de la cadena
    val ultimos8Digitos = digitos.takeLast(8)

    // Los enmascaramos con X
    val digitosEnmascarados = "X".repeat(4) + " " + ultimos8Digitos.substring(4)

    return digitosEnmascarados
}


fun String.dividirCadena(): String {
    val palabras = this.split(" ")
    return palabras.take(2).joinToString(" ")
}

fun String.mask(): String {
    val lastFourDigits = takeLast(4)
    return "X".repeat(length - 4) + lastFourDigits
}

fun String.groupByMask(chunkSize: Int): String {
    val stringBuilder = StringBuilder()
    for (i in indices) {
        if (i > 0 && i % chunkSize == 0) {
            stringBuilder.append(' ')
        }
        stringBuilder.append(this[i])
    }
    return stringBuilder.toString()
}

fun String.enmascararNumeroTarjeta(): String {
    // Primero, eliminamos todos los caracteres que no sean dígitos
    val digitos = this.filter { it.isDigit() }

    // Luego, agrupamos los dígitos de cuatro en cuatro
    val grupos = digitos.chunked(4)

    // Por último, reemplazamos todos los dígitos, excepto los últimos cuatro, con una X
    val ultimosDigitos = grupos.lastOrNull() ?: ""
    val digitosEnmascarados = grupos.dropLast(1)
        .joinToString(separator = " ") { "XXXX" }
    return "$digitosEnmascarados $ultimosDigitos"
}

fun String.groupByThree(): String {
    val regex = "(\\d)(?=(\\d{3})+\$)".toRegex()
    return this.replace(regex, "\$1,")
}

fun String.formatCurrency(currencyCode: String): String {
    val amount = this.toDoubleOrNull() ?: return this
    val currency = Currency.getInstance(currencyCode)
    val formatter = NumberFormat.getCurrencyInstance(Locale.getDefault())
    formatter.currency = currency
    return formatter.format(amount)
}

fun Double.formatCurrency(currencyCode: String): String {
    val currency = Currency.getInstance(currencyCode)
    val formatter = NumberFormat.getCurrencyInstance(Locale.getDefault())
    formatter.currency = currency
    return formatter.format(this)
}

fun String.addAccountNumberSuffix(n: Int): String {
    val lastDigits = this.takeLast(n)
    return "Mi cuenta **$lastDigits"
}

fun String.agregarHola(): String {
    return "¡Hola $this!"
}

/**
 * Function to format and validate string with decimal digits.
 * Validates max digits before and after period, and also converts "." to "0.".
 *
 * @param maxBeforePoint Max digits allowed before period
 * @param maxDecimal Max decimal digits allowed
 *
 * @return formatted String
 */
fun String.formatAmount(maxBeforePoint: Int, maxDecimal: Int): String {
    var str = this
    if (this.isEmpty()) {
        return this
    }
    if (this[0] == '.') {
        str = "0$this"
    }
    val inputSize = str.length
    var finalInput = ""
    var after = false
    var i = 0
    var up = 0
    var decimal = 0
    var t: Char
    while (i < inputSize) {
        t = str[i]
        if (t != '.' && !after) {
            up++
            if (up > maxBeforePoint) return finalInput
        } else if (t == '.') {
            after = true
        } else {
            decimal++
            if (decimal > maxDecimal) return finalInput
        }
        finalInput += t
        i++
    }
    return finalInput
}


fun Fragment.showCustomDialogInfo(
    message1: String,
    message2: String,
    option1: String = getString(R.string.dialog_close_option_text),
    iconType: Int = ASPMaterialDialogCustom.DialogIconType.INFO.id,
    buttonVisible: Boolean = false
) {
    ASPMaterialDialogCustom.newInstance(message1, message2, option1, iconType, buttonVisible)
        .show(this.parentFragmentManager, this.tag)
}

fun Context.showCustomDialogInfo(
    message1: String,
    message2: String,
    option1: String = getString(R.string.dialog_close_option_text),
    iconType: Int = ASPMaterialDialogCustom.DialogIconType.INFO.id,
    buttonVisible: Boolean = false
) {
    ASPMaterialDialogCustom.newInstance(message1, message2, option1, iconType, buttonVisible)
        .show((this as? FragmentActivity)!!.supportFragmentManager, null)
}

fun String.toFormattedDate(): String {
    val formatterInput = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val formatterOutput = DateTimeFormatter.ofPattern("d 'de' MMMM")
    return try {
        val dateTime = LocalDateTime.parse(this.replace(".", "-"), formatterInput)
        dateTime.format(formatterOutput)
    } catch (e: Exception) {
        // si el formato no coincide, regresa una cadena vacía
        ""
    }
}

fun Double.formatCurrencyMXN(): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale.US)
    formatter.currency = Currency.getInstance("USD")
    return formatter.format(this)
}

fun String.formatAsMoney(movementType: MovementType): String {
    val symbol = if (movementType == MovementType.PAY) "-" else ""
    val amount = this.toDouble()
    val formattedAmount = String.format("%,.2f", amount)
    return "$symbol\$$formattedAmount"
}

fun String.agregarCuenta(): String {
    val ultimosCuatro = this.substring(this.length - 4)
    return "Mi cuenta $ultimosCuatro"
}

fun TextView.copyToClipboard() {
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText("label", text)
    clipboardManager.setPrimaryClip(clipData)
    Toast.makeText(context, "Copiado al portapapeles", Toast.LENGTH_SHORT).show()
}

fun Fragment.showCustomDialogSuccess(
    message1: String,
    message2: String,
    option1: String = getString(R.string.dialog_close_option_text),
    iconType: Int = ASPMaterialDialogCustom.DialogIconType.SUCCESS.id
) {
    ASPMaterialDialogCustom.newInstance(
        message1,
        message2,
        option1,
        iconType,
        visibleAcceptButton = false
    )
        .show(this.parentFragmentManager, this.tag)
}

fun View.snackbarSuccess(message: String) {
    val snackbar = Snackbar
        .make(this, message, Snackbar.LENGTH_LONG).also { snackbar ->
            snackbar.setAction("Aceptar") {
                snackbar.dismiss()
            }
        }
    snackbar.view.background = ContextCompat.getDrawable(
        context,
        asp.android.aspandroidmaterial.R.color.asp_linear_button_ripple_color
    )
    snackbar.show()
}

fun View.snackbarError(message: String) {
    val snackbar = Snackbar
        .make(this, message, Snackbar.LENGTH_LONG).also { snackbar ->
            snackbar.setAction("Aceptar") {
                snackbar.dismiss()
            }
        }
    snackbar.view.background = ContextCompat.getDrawable(
        context,
        asp.android.aspandroidmaterial.R.color.asp_flat_button_background_color
    )
    snackbar.show()
}

fun ExpandableListView.calculateHeigth(rows: Int, height: Float) {
    this.minimumHeight = (rows * height).toInt()
}

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun View.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}

fun EditText.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun EditText.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.windowToken, 0)
}

fun View?.removeSelf() {
    this ?: return
    val parentView = parent as? ViewGroup ?: return
    parentView.removeView(this)
}

fun Fragment.getDrawable(@DrawableRes id: Int): Drawable? {
    return ContextCompat.getDrawable(requireContext(), id)
}

fun Fragment.getColor(@ColorRes idColor: Int): Int {
    return ContextCompat.getColor(requireContext(), idColor)
}

fun View.show() {
    isVisible = true
}

fun View.hide() {
    isVisible = false
}

fun View.backgroundColor(@ColorRes color: Int) {
    backgroundTintList = ColorStateList.valueOf(this.context.getColor(color))
}

fun Double.toSpecificLengthFormat(length: Int): String {
    val valueTrunk = this.toInt()
    if (valueTrunk.toString().length >= length) return valueTrunk.toString()
    val zerosRestant = length - valueTrunk.toString().length
    val stringBuilder = StringBuilder()
    for (i in 1..zerosRestant) {
        stringBuilder.append("0")
    }
    stringBuilder.append(valueTrunk.toString())
    return stringBuilder.toString()
}