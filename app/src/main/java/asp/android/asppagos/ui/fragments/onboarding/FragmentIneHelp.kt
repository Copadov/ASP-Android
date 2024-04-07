package asp.android.asppagos.ui.fragments.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import asp.android.asppagos.databinding.ModalIneHelpBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FragmentIneHelp : BottomSheetDialogFragment() {

    private var imageResource: Int = 0

    private val binding: ModalIneHelpBinding by lazy {
        ModalIneHelpBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            imageResource = it.getInt(ARG_IMAGE_RESOURCE, 0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            ivReference.setImageResource(imageResource)
            btnContinue.setOnClickListener {
                dismiss()
            }
        }
    }

    companion object {
        private const val ARG_IMAGE_RESOURCE = "arg_image_resource"

        fun newInstance(imageResource: Int): FragmentIneHelp {
            val fragment = FragmentIneHelp()
            val args = Bundle().apply {
                putInt(ARG_IMAGE_RESOURCE, imageResource)
            }
            fragment.arguments = args
            return fragment
        }
    }

}