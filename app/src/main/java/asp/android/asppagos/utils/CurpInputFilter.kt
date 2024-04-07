package asp.android.asppagos.utils

import android.text.InputFilter
import android.text.Spanned


class CurpInputFilter : InputFilter {
    companion object {
        private const val CURP_REGEX =
            "[A-Z]{4}\\d{6}[HM](AS|BC|BS|CC|CS|CH|CL|CM|DF|DG|GR|GT|HG|JC|MC|MN|MS|NE|NL|NT|OC|PL|QT|QR|SP|SL|SR|TC|TL|TS|VZ|YN|ZS)[A-Z]{3}[\\d|A-Z]\\d"
    }

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        val filtered = StringBuilder(end - start)
        for (i in start until end) {
            val currentChar = source[i]
            if (Character.isLetterOrDigit(currentChar)) {
                filtered.append(currentChar)
            }
        }

        val filteredString = filtered.toString().toUpperCase()

        val curpValue =
            dest.subSequence(0, dstart).toString() + filteredString + dest.subSequence(
                dend,
                dest.length
            )
        if (!curpValue.matches(CURP_REGEX.toRegex())) {
            return ""
        }

        return null
    }
}






