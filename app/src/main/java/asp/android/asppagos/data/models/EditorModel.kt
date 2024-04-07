package asp.android.asppagos.data.models

import android.os.Parcelable
import asp.android.asppagos.ui.fragments.BaseFragment
import kotlinx.parcelize.Parcelize

@Parcelize
data class DataItem(val name: String, var value: String, val type: BaseFragment.EditorType) :
    Parcelable