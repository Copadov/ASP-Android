package asp.android.aspandroidmaterial.ui.adapters

import android.content.Context
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import asp.android.aspandroidmaterial.R
import asp.android.aspandroidmaterial.ui.models.CountryDisplayItem

class CountryCodeSpinnerAdapter(
    context: Context,
    list: List<CountryDisplayItem>
) : ArrayAdapter<CountryDisplayItem>(context, 0, list) {
    val layoutInflater: LayoutInflater = LayoutInflater.from(context)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        if (convertView == null) {
            view = layoutInflater.inflate(R.layout.country_code_layout, parent, false)
        } else {
            view = convertView
        }
        getItem(position)?.let { country ->
            setItemForCountry(view, country)
        }
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        if (position == 0) {
            view = layoutInflater.inflate(R.layout.header_country, parent, false)
            view.setOnClickListener {
                val root = parent.rootView
                root.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK))
                root.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK))
            }
        } else {
            view = layoutInflater.inflate(R.layout.item_country_dropdown, parent, false)
            getItem(position)?.let { country ->
                setItemForCountry(view, country)
            }
        }
        return view
    }

    override fun getItem(position: Int): CountryDisplayItem? {
        if (position == 0) {
            return null
        }
        return super.getItem(position - 1)
    }

    override fun getCount() = super.getCount() + 1
    override fun isEnabled(position: Int) = position != 0
    private fun setItemForCountry(view: View, country: CountryDisplayItem) {
        val tvCountry = view.findViewById<TextView>(R.id.countryCodeText)
        val ivCountry = view.findViewById<ImageView>(R.id.flagIcon)
        tvCountry.text = country.countryCode
        ivCountry.setBackgroundResource(country.icon)

    }
}


