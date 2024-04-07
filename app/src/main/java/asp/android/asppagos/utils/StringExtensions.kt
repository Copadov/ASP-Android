package asp.android.asppagos.utils

import android.util.Log
import java.lang.StringBuilder

// TODO: Check function this function only work with two words o sentence
fun String.getFirstLetters(): String {
    if (this.isEmpty()) return "NN"
    val getArrayWords = this.split(" ")
    val firstLetters = StringBuilder()
    val getFirstLetterFromFirstWord =  runCatching {
        getArrayWords.first().trim().first().takeIf { (it != ' ') } ?: "N"
    }.getOrElse {
        it.printStackTrace()
        ""
    }
    firstLetters.append(getFirstLetterFromFirstWord)
    if (getArrayWords.size > 1) {
        val getFirstLetterFromSecondWord =  runCatching {
            getArrayWords[1].trim().first().takeIf { (it != ' ') } ?: "N"
        }.getOrElse {
        it.printStackTrace()
            ""
        }
        firstLetters.append(getFirstLetterFromSecondWord)
    }
    return firstLetters.toString()
}