package asp.android.asppagos.utils

import android.text.InputFilter
import android.text.Spanned


class NoSpecialCharacterFilter : InputFilter {
    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence {
        val filtered = StringBuilder()
        for (i in start until end) {
            val character = source[i]
            if (Character.isLetterOrDigit(character) || Character.isSpaceChar(character)) {
                filtered.append(character)
            }
        }
        return filtered.toString()
    }
}