package asp.android.materialapp

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import asp.android.materialapp.databinding.FragmentSecondBinding
import jp.shts.android.storiesprogressview.StoriesProgressView
import kotlin.math.sign

class SecondFragment : Fragment(), StoriesProgressView.StoriesListener {

    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!

    private var counter = 0

    private val resourse = arrayOf(
        R.raw.pantalla2, "Mountain",
        R.raw.animacionmp4, ""
    )

    var pressTime = 0L
    var limit = 500L

    private val onTouchListener: View.OnTouchListener = object : View.OnTouchListener {
        override fun onTouch(v: View?, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    pressTime = System.currentTimeMillis()
                    binding.stories.pause()
                    return false
                }
                MotionEvent.ACTION_UP -> {
                    val now = System.currentTimeMillis()
                    binding.stories.resume()
                    return limit < now - pressTime
                }
            }
            return false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.let {
            it.stories.setStoriesCount(resourse.size)
            it.stories.setStoryDuration(150000L)
            it.stories.setStoriesListener(this)
            it.stories.startStories()

            it.imageview.setVideoURI(
                Uri.parse(
                    "android.resource://"
                            + requireActivity().packageName
                            + "/"
                            + resourse[counter]
                )
            )

            it.reverse.setOnClickListener {
                binding.stories.reverse()
            }
            it.reverse.setOnTouchListener(onTouchListener)

            it.skip.setOnClickListener {
                binding.stories.skip()
            }

            it.skip.setOnTouchListener(onTouchListener)

            it.imageview.setOnPreparedListener { binding.imageview.start() }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onNext() {
        binding.imageview.setVideoURI(
            Uri.parse(
                "android.resource://"
                        + requireActivity().packageName
                        + "/"
                        + resourse[counter]
            )
        )

        counter++
    }

    override fun onPrev() {
        if ((counter - 1) < 0) return

        binding.imageview.setVideoURI(
            Uri.parse(
                "android.resource://"
                        + requireActivity().packageName
                        + "/"
                        + resourse[counter]
            )
        )

        counter--
    }

    override fun onComplete() {
    }
}