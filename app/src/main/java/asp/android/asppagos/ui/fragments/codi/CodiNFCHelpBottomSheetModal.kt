package asp.android.asppagos.ui.fragments.codi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import asp.android.asppagos.databinding.ModalCodiNfcHelpBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CodiNFCHelpBottomSheetModal : BottomSheetDialogFragment() {

    private val binding: ModalCodiNfcHelpBottomSheetBinding by lazy {
        ModalCodiNfcHelpBottomSheetBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupOnClickListener()
    }

    private fun setupOnClickListener() {
        binding.btnActivate.setOnClickListener {
            dismiss()
        }
    }
    companion object {
        const val TAG = "codi_nfc_help_bottom_sheet_modal"
    }

}