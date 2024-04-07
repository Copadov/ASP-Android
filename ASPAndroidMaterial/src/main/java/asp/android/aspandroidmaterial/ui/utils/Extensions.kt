package asp.android.aspandroidmaterial.ui.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.OvershootInterpolator

fun View.animateView(durationMilis: Long = 400) {
    val scaleX = ObjectAnimator.ofFloat(this, View.SCALE_X, 0f, 1f)
    val scaleY = ObjectAnimator.ofFloat(this, View.SCALE_Y, 0f, 1f)
    val animatorSet = AnimatorSet().apply {
        interpolator = OvershootInterpolator()
        duration = durationMilis
        playTogether(scaleX, scaleY)
    }
    animatorSet.start()
}