package asp.android.asppagos.ui.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import asp.android.asppagos.databinding.ActivityOnboardingBinding
import asp.android.asppagos.ui.adapters.OnBoardingViewPagerAdapter
import asp.android.asppagos.utils.DATA_WELCOME_SELECTION
import asp.android.asppagos.utils.DATA_WELCOME_SELECTION_EXTRA_LOGIN
import asp.android.asppagos.utils.DATA_WELCOME_SELECTION_EXTRA_REGISTER

class OnboardingActivity : AppCompatActivity() {

    private var numberOfScreens: Int = 0

    private var onBoardingPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            binding.ASPMaterialDotsIndicator.setIndicators(numberOfScreens, position)
        }
    }

    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        binding = ActivityOnboardingBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.let {

            numberOfScreens = 6

            val onboardingAdapter = OnBoardingViewPagerAdapter(this, numberOfScreens)
            it.viewPager.adapter = onboardingAdapter
            it.viewPager.registerOnPageChangeCallback(onBoardingPageChangeCallback)

            val myIntent = Intent(
                this, MainActivity::class.java
            )

            it.createAccountButton.setOnClickListener {
                myIntent.putExtra(DATA_WELCOME_SELECTION, DATA_WELCOME_SELECTION_EXTRA_REGISTER)
                finish()
                startActivity(myIntent)
            }

            it.haveAccountButton.setOnClickListener {
                myIntent.putExtra(DATA_WELCOME_SELECTION, DATA_WELCOME_SELECTION_EXTRA_LOGIN)
                finish()
                startActivity(myIntent)
            }

            it.viewPager.currentItem = 1
            it.viewPager.currentItem = 0
        }

        requestSmsPermission()
        requestCamera()
    }

    private fun requestCamera() {
        val permission = Manifest.permission.CAMERA
        val grant = ContextCompat.checkSelfPermission(this, permission)
        if (grant != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), 1200)
        }
    }

    private fun requestSmsPermission() {
        val permission = Manifest.permission.RECEIVE_SMS
        val grant = ContextCompat.checkSelfPermission(this, permission)
        if (grant != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), 1200)
        }
    }

    override fun onDestroy() {
        binding.viewPager.unregisterOnPageChangeCallback(onBoardingPageChangeCallback)

        super.onDestroy()
    }
}