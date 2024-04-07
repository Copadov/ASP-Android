package asp.android.asppagos.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import asp.android.asppagos.R
import asp.android.asppagos.databinding.ActivityOnboardingV2Binding
import asp.android.asppagos.utils.DATA_WELCOME_SELECTION
import asp.android.asppagos.utils.DATA_WELCOME_SELECTION_EXTRA_LOGIN
import asp.android.asppagos.utils.DATA_WELCOME_SELECTION_EXTRA_REGISTER
import asp.android.asppagos.utils.PROPERTY_REGISTER_SUCCESS
import asp.android.asppagos.utils.Prefs
import asp.android.asppagos.utils.showCustomDialogInfo
import jp.shts.android.storiesprogressview.StoriesProgressView.StoriesListener

class OnboardingV2Activity : AppCompatActivity(), StoriesListener {

    private lateinit var binding: ActivityOnboardingV2Binding

    private var counter = 0
    private var previous = 0
    private var last = 0

    private val resource = listOf(
        R.raw.pantalla1,
        R.raw.pantalla2,
        R.raw.pantalla3,
        R.raw.pantalla4,
        R.raw.pantalla5,
        R.raw.pantalla6,
    )

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        binding = ActivityOnboardingV2Binding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.let {
            val myIntent = Intent(
                this, MainActivity::class.java
            )

            it.createAccountButton.setOnClickListener {
                myIntent.putExtra(DATA_WELCOME_SELECTION, DATA_WELCOME_SELECTION_EXTRA_REGISTER)
                startActivity(myIntent)
                this.finish()
            }

            it.haveAccountButton.setOnClickListener {
                myIntent.putExtra(DATA_WELCOME_SELECTION, DATA_WELCOME_SELECTION_EXTRA_LOGIN)
                startActivity(myIntent)
                this.finish()
            }

            startStories()

            it.reverse.setOnClickListener {
                binding.stories.reverse()
            }

            it.skip.setOnClickListener {
                binding.stories.skip()
            }

            it.videoContent.setOnPreparedListener { videoContent ->
                videoContent.start()
            }

            it.skip.setOnTouchListener { _, event ->
                when (event!!.action) {
                    MotionEvent.ACTION_DOWN -> {
                        binding.stories.pause()
                        binding.videoContent.pause()
                        false
                    }

                    MotionEvent.ACTION_UP -> {
                        val now = System.currentTimeMillis()
                        binding.stories.resume()
                        binding.videoContent.resume()
                    }
                }
                false
            }

            it.reverse.setOnTouchListener { _, event ->
                when (event!!.action) {
                    MotionEvent.ACTION_DOWN -> {
                        binding.stories.pause()
                        binding.videoContent.pause()
                        false
                    }

                    MotionEvent.ACTION_UP -> {
                        val now = System.currentTimeMillis()
                        binding.stories.resume()
                        binding.videoContent.resume()
                    }
                }
                false
            }
        }

        requestPermissions()

        if (!Prefs[PROPERTY_REGISTER_SUCCESS, false]) {
            showInfoDialog()
        }
    }

    private fun requestPermissions() {
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(
                    Manifest.permission.ACCESS_FINE_LOCATION, false
                ) -> {

                }

                permissions.getOrDefault(
                    Manifest.permission.ACCESS_COARSE_LOCATION, false
                ) -> {

                }

                permissions.getOrDefault(
                    Manifest.permission.RECEIVE_SMS, false
                ) -> {

                }

                permissions.getOrDefault(
                    Manifest.permission.CAMERA, false
                ) -> {

                }

                permissions.getOrDefault(
                    Manifest.permission.POST_NOTIFICATIONS, false
                ) -> {

                }

                permissions.getOrDefault(
                    Manifest.permission.CALL_PHONE, false
                ) -> {

                }

                else -> {
                    Toast.makeText(
                        this,
                        "Active los permisos de localización.",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
            }
        }
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CAMERA,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.CALL_PHONE
            )
        )
    }

    private fun startStories() {
        binding.let {
            it.stories.let {
                it.setStoriesCount(resource.size)
                it.setStoryDuration(10000L)
                it.setStoriesListener(this)
                it.startStories()
            }

            it.videoContent.setVideoURI(
                Uri.parse(
                    "android.resource://"
                            + packageName
                            + "/"
                            + resource[counter]
                )
            )
        }
    }

    override fun onDestroy() {
        binding.stories.destroy()
        super.onDestroy()
    }

    override fun onNext() {
        binding.videoContent.setVideoURI(
            Uri.parse(
                "android.resource://"
                        + packageName
                        + "/"
                        + resource[++counter]
            )
        )
    }

    override fun onPrev() {
        if ((counter - 1) < 0) return else {
            binding.videoContent.setVideoURI(
                Uri.parse(
                    "android.resource://"
                            + packageName
                            + "/"
                            + resource[--counter]
                )
            )
        }
    }

    override fun onComplete() {
        /* finish();
         startActivity(intent);
         overridePendingTransition(
             asp.android.aspandroidmaterial.R.anim.fade_in,
             asp.android.aspandroidmaterial.R.anim.fade_out
         );*/
        recreate()
    }

    /**
     * Show dialog to request users with an ASP account to register in the app.
     */
    private fun showInfoDialog() {
        this.showCustomDialogInfo(
            resources.getString(R.string.onboarding_view_dialog_title),
            resources.getString(R.string.onboarding_view_dialog_message)
        )
    }
}