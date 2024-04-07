package asp.android.asppagos.ui.fragments.payment_services

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import asp.android.asppagos.databinding.ModalBottomSheetInfoPaymentServicesBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

private const val DRAWABLE = "drawable"
private const val DESCRIPTION = "description"
private const val TITLE = "title"

class PaymentServicesInfoBottomSheetModal : BottomSheetDialogFragment() {

    private var title: String? = null
    private var imageReferenceId: Int? = null
    private var descriptionReference: String? = null

    private val binding: ModalBottomSheetInfoPaymentServicesBinding by lazy {
        ModalBottomSheetInfoPaymentServicesBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            imageReferenceId = it.getInt(DRAWABLE)
            descriptionReference = it.getString(DESCRIPTION)
            title = it.getString(TITLE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInfo()
        setupOnClickListener()
    }

    private fun setupInfo() {
        binding.apply {
            imageReferenceId?.let {
                ivReference.setImageDrawable(ContextCompat.getDrawable(requireContext(), it))
            }
            descriptionReference?.let {
                tvImportantDescription.text = it
            }
            title?.let {
                tvReferenceTitle.text = it
            }
        }
    }

    companion object {
        const val TAG = "ModalBottomSheetPaymentInfoService"

        @JvmStatic
        fun newInstance(drawableId: Int, description: String, title: String) =
            PaymentServicesInfoBottomSheetModal().apply {
                arguments = Bundle().apply {
                    putString(TITLE, title)
                    putInt(DRAWABLE, drawableId)
                    putString(DESCRIPTION, description)
                }
            }
    }

    private fun setupOnClickListener() {
        binding.btnContinue.setOnClickListener {
            dismiss()
        }
    }

    enum class ReferenceType(val id: Int) {
        CFE(id = 15), TOTAL_PLAY(id = 19), SKY(id = 12), TELMEX(id = 16), NONE(id = 1991)
    }

}