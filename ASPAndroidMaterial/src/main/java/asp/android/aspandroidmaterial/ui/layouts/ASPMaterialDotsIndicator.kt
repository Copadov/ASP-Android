package asp.android.aspandroidmaterial.ui.layouts

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.LinearLayoutCompat
import asp.android.aspandroidmaterial.R
import asp.android.aspandroidmaterial.ui.utils.animateView

class ASPMaterialDotsIndicator : LinearLayoutCompat {

    var dots_count: Int = 1
    var dot_selected: Int = 0
    var dots: Array<ImageView>

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        context.obtainStyledAttributes(attrs, R.styleable.ASPMaterialDotsIndicator).apply {
            dots_count = getInteger(R.styleable.ASPMaterialDotsIndicator_dotsCount, 3)
            dot_selected = getInteger(R.styleable.ASPMaterialDotsIndicator_dotsSelected, 0)
            recycle()
        }

        dots = Array(dots_count) { ImageView(context) }
        init()
    }

    private fun init() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.asp_dots_indicator_layout, this)

        val dotsSlider = findViewById<LinearLayoutCompat>(R.id.sliderDots)

        for ((i, _) in dots.withIndex()) {
            if (i == dot_selected) {
                dots[i].setImageDrawable(
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.dot_selected
                    )
                )
                dots[i].animateView()
            } else {
                if (i > dot_selected){
                    dots[i].setImageDrawable(
                        AppCompatResources.getDrawable(
                            context,
                            R.drawable.empty_dot
                        )
                    )
                }else{
                    dots[i].setImageDrawable(
                        AppCompatResources.getDrawable(
                            context,
                            R.drawable.dot_finished
                        )
                    )
                }
            }

            val layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            layoutParams.setMargins(4, 0, 4, 0)

            dotsSlider.addView(dots[i], layoutParams)
        }
    }

    fun setIndicators(dotsCount: Int, dotSelected: Int) {
        removeAllViews()
        this.dots_count = dotsCount
        this.dot_selected = dotSelected
        dots = Array(dots_count) { ImageView(context) }
        init()
    }
}