package asp.android.asppagos.utils

import android.content.Context
import asp.android.asppagos.data.models.MyObjectBox
import io.objectbox.BoxStore

object BDObjectBox {
    lateinit var store: BoxStore
    private set

    fun init(context: Context) {
        store = MyObjectBox.builder().androidContext(context.applicationContext).build()
    }
}