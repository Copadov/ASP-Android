package asp.android.asppagos.utils

import com.google.gson.Gson

fun <T> T.toJson() : String {
    return Gson().toJson(this)
}

inline fun <reified T> String.fromJson(): T {
    return Gson().fromJson<T>(this)
}