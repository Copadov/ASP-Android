package asp.android.asppagos.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import asp.android.asppagos.R

class CitySpinnerArrayAdapter(context: Context, resource: Int, var items: List<CityModel>) :
    ArrayAdapter<CitySpinnerArrayAdapter.CityModel>(context, resource, items) {

    val inflater: LayoutInflater = LayoutInflater.from(context)

    data class CityModel(val id: Int, val nameCity: String)


    override fun getItemId(position: Int): Long {
        return items[position].id.toLong()
    }

    override fun getView(position: Int, convertView: View?, container: ViewGroup): View {
        var view: View? = convertView
        if (view == null) {
            view = inflater.inflate(asp.android.aspandroidmaterial.R.layout.header_country, container, false)
        }


        (view?.findViewById(asp.android.aspandroidmaterial.R.id.optionText) as TextView).text =
            getItem(position)!!.nameCity

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view: View? = convertView
        if (view == null) {
            view = inflater.inflate(R.layout.spinner_city_layout_item, parent, false)
        }

        (view?.findViewById(R.id.textViewSpinnerItem) as TextView).text =
            getItem(position)!!.nameCity

        return view
    }
}