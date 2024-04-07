package asp.android.aspandroidmaterial.ui.layouts

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.LinearLayoutCompat
import asp.android.aspandroidmaterial.R
import asp.android.aspandroidmaterial.ui.utils.animateView

class ASPMaterialBottomNavigation : LinearLayoutCompat, View.OnClickListener {

    private lateinit var option1: LinearLayoutCompat
    private lateinit var option2: LinearLayoutCompat
    private lateinit var option3: LinearLayoutCompat
    private lateinit var option4: LinearLayoutCompat
    private lateinit var menuHome: LinearLayoutCompat
    private var customValue: Int = 0

    private var aspMaterialBottomNavigationListeners:
            ASPMaterialBottomNavigationListener? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        init()
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init()
    }

    fun resetStates() {
        init()
    }

    fun setCustomValue(value: Int) {
        when (value) {
            0 -> {
                option1.findViewById<TextView>(
                    R.id.bottomOptionItemText
                )
                    .setTextColor(Color.parseColor("#D9D9D9"))
                option1
                    .findViewById<ImageView>(R.id.bottomOptionItemImage)
                    .setImageDrawable(
                        AppCompatResources.getDrawable(
                            context,
                            R.drawable.asp_mis_tarjetas_icon_disabled
                        )
                    )
                option1.isEnabled = false
            }
        }
    }

    private fun init() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.asp_material_bottom_navigation_layout, this)

        option1 = findViewById(R.id.option1)
        option1.setOnClickListener(this)

        option2 = findViewById(R.id.option2)
        option2.setOnClickListener(this)

        option3 = findViewById(R.id.option3)
        option3.setOnClickListener(this)

        option4 = findViewById(R.id.option4)
        option4.setOnClickListener(this)

        menuHome = findViewById(R.id.aspHomeIcon)
        menuHome.setOnClickListener {
            it.animateView()
            aspMaterialBottomNavigationListeners?.onClickHome()
        }


        option1.findViewById<TextView>(R.id.bottomOptionItemText).text =
            resources.getString(R.string.dashboard_main_my_cards_option_text)
        option2.findViewById<TextView>(R.id.bottomOptionItemText).text =
            resources.getString(R.string.dashboard_main_my_movements_option_text)
        option3.findViewById<TextView>(R.id.bottomOptionItemText).text =
            resources.getString(R.string.dashboard_main_codi_option_text)
        option4.findViewById<TextView>(R.id.bottomOptionItemText).text =
            resources.getString(R.string.dashboard_main_config_option_text)

        option1.findViewById<ImageView>(R.id.bottomOptionItemImage)
            .setImageDrawable(
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.asp_mis_tarjetas_icon
                )
            )

        option2.findViewById<ImageView>(R.id.bottomOptionItemImage)
            .setImageDrawable(
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.asp_mis_movimientos_icon
                )
            )

        option3.findViewById<ImageView>(R.id.bottomOptionItemImage)
            .setImageDrawable(
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.asp_codi_icon
                )
            )

        option4.findViewById<ImageView>(R.id.bottomOptionItemImage)
            .setImageDrawable(
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.asp_mis_ajustes_icon
                )
            )


    }

    fun setASPMaterialBottomNavigationListener(
        aspMaterialBottomNavigationListener: ASPMaterialBottomNavigationListener
    ) {
        this.aspMaterialBottomNavigationListeners = aspMaterialBottomNavigationListener
    }

    enum class TypeMenu {
        UNDEFINED,
        CARDS,
        MOVEMENTS,
        CODI,
        CONFIGURATION,
        SENDMONEY,
        RECEIVEMONEY,
        PAY,
        PAYSERVICE,
        PHONERECHARGE,
        PLUS
    }

    fun selectOption(option: Int) {

        option1
            .findViewById<ImageView>(R.id.bottomOptionItemImage)
            .setImageDrawable(
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.asp_mis_tarjetas_icon
                )
            )

        option2
            .findViewById<ImageView>(R.id.bottomOptionItemImage)
            .setImageDrawable(
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.asp_mis_movimientos_icon
                )
            )
        option3
            .findViewById<ImageView>(R.id.bottomOptionItemImage)
            .setImageDrawable(
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.asp_codi_icon
                )
            )
        option4
            .findViewById<ImageView>(R.id.bottomOptionItemImage)
            .setImageDrawable(
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.asp_mis_ajustes_icon
                )
            )

        when (option) {
            1 -> {
                option1
                    .findViewById<ImageView>(R.id.bottomOptionItemImage)
                    .setImageDrawable(
                        AppCompatResources.getDrawable(
                            context,
                            R.drawable.asp_mis_tarjetas_activo_icon
                        )
                    )
            }

            2 -> {
                option2
                    .findViewById<ImageView>(R.id.bottomOptionItemImage)
                    .setImageDrawable(
                        AppCompatResources.getDrawable(
                            context,
                            R.drawable.asp_mis_movimientos_activo_icon
                        )
                    )
            }

            3 -> {
                option3
                    .findViewById<ImageView>(R.id.bottomOptionItemImage)
                    .setImageDrawable(
                        AppCompatResources.getDrawable(
                            context,
                            R.drawable.asp_codi_activo_icon
                        )
                    )
            }

            4 -> {
                option4
                    .findViewById<ImageView>(R.id.bottomOptionItemImage)
                    .setImageDrawable(
                        AppCompatResources.getDrawable(
                            context,
                            R.drawable.asp_mis_ajustes_activo_icon
                        )
                    )
            }
        }
    }

    override fun onClick(v: View?) {

        option1
            .findViewById<ImageView>(R.id.bottomOptionItemImage)
            .setImageDrawable(
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.asp_mis_tarjetas_icon
                )
            )

        option2
            .findViewById<ImageView>(R.id.bottomOptionItemImage)
            .setImageDrawable(
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.asp_mis_movimientos_icon
                )
            )
        option3
            .findViewById<ImageView>(R.id.bottomOptionItemImage)
            .setImageDrawable(
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.asp_codi_icon
                )
            )
        option4
            .findViewById<ImageView>(R.id.bottomOptionItemImage)
            .setImageDrawable(
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.asp_mis_ajustes_icon
                )
            )



        when (v?.id) {
            R.id.option1 -> {
                option1.animateView()
                option1
                    .findViewById<ImageView>(R.id.bottomOptionItemImage)
                    .setImageDrawable(
                        AppCompatResources.getDrawable(
                            context,
                            R.drawable.asp_mis_tarjetas_activo_icon
                        )
                    )
                aspMaterialBottomNavigationListeners?.onClickOption(TypeMenu.CARDS)
            }

            R.id.option2 -> {
                option2.animateView()
                option2
                    .findViewById<ImageView>(R.id.bottomOptionItemImage)
                    .setImageDrawable(
                        AppCompatResources.getDrawable(
                            context,
                            R.drawable.asp_mis_movimientos_activo_icon
                        )
                    )
                aspMaterialBottomNavigationListeners?.onClickOption(TypeMenu.MOVEMENTS)
            }

            R.id.option3 -> {
                option3.animateView()
                option3
                    .findViewById<ImageView>(R.id.bottomOptionItemImage)
                    .setImageDrawable(
                        AppCompatResources.getDrawable(
                            context,
                            R.drawable.asp_codi_activo_icon
                        )
                    )
                aspMaterialBottomNavigationListeners?.onClickOption(TypeMenu.CODI)
            }

            R.id.option4 -> {
                option4.animateView()
                option4
                    .findViewById<ImageView>(R.id.bottomOptionItemImage)
                    .setImageDrawable(
                        AppCompatResources.getDrawable(
                            context,
                            R.drawable.asp_mis_ajustes_activo_icon
                        )
                    )
                aspMaterialBottomNavigationListeners?.onClickOption(TypeMenu.CONFIGURATION)
            }
        }
    }

    interface ASPMaterialBottomNavigationListener {
        fun onClickHome()
        fun onClickOption(cards: TypeMenu)
    }
}